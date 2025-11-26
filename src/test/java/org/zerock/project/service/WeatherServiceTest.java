package org.zerock.project.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.zerock.project.dto.WeatherResponseDto;
import org.zerock.project.model.GridLocation;
import org.zerock.project.util.getRegionCode;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private GridService gridService;

    @Mock
    private getRegionCode getRegionCode; // 사용되지 않더라도 의존성 주입을 위해 Mocking

    @Mock
    private RestTemplate restTemplate; // final 필드 Mocking

    // private 메서드인 callJson을 Mocking하기 위한 Spy 객체
    @Spy
    private WeatherService spyWeatherService;

    // API Key와 URL 설정을 위한 값 주입
    private final String MOCK_SERVICE_KEY = "lJVhA4k9SEKVYQOJPdhCBQ";
    private final String SHORT_API_URL = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst";


    @BeforeEach
    void setUp() {
        // @Value 필드에 Mock 값 주입 (Reflection 사용)
        ReflectionTestUtils.setField(weatherService, "serviceKey", MOCK_SERVICE_KEY);
        ReflectionTestUtils.setField(weatherService, "short_api_url", SHORT_API_URL);

        // Spy 객체에도 @Value 필드 설정
        ReflectionTestUtils.setField(spyWeatherService, "serviceKey", MOCK_SERVICE_KEY);
        ReflectionTestUtils.setField(spyWeatherService, "short_api_url", SHORT_API_URL);
        ReflectionTestUtils.setField(spyWeatherService, "gridService", gridService);
        ReflectionTestUtils.setField(spyWeatherService, "getRegionCode", getRegionCode);
    }

    // --- private 메서드 테스트용 Mocking된 응답 생성 ---

    /**
     * 단기 예보 API에서 반환될 것으로 예상되는 Mock JSON 응답을 생성합니다.
     * 실제 로직은 TMN, TMX, SKY, PTY, POP 값을 찾습니다.
     */
    private JSONObject createMockShortTermJson(LocalDate targetDate) {
        String fcstDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 날씨 예보 데이터 항목 목록 (items)
        JSONArray items = new JSONArray();

        // SKY (하늘 상태: 맑음(1), 구름 많음(3), 흐림(4))
        items.put(new JSONObject().put("category", "SKY").put("fcstValue", "3").put("fcstDate", fcstDateStr));
        // PTY (강수 형태: 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4))
        items.put(new JSONObject().put("category", "PTY").put("fcstValue", "0").put("fcstDate", fcstDateStr));
        // POP (강수 확률: %)
        items.put(new JSONObject().put("category", "POP").put("fcstValue", "20").put("fcstDate", fcstDateStr));
        // TMN (최저 기온)
        items.put(new JSONObject().put("category", "TMN").put("fcstValue", "5").put("fcstDate", fcstDateStr));
        // TMX (최고 기온)
        items.put(new JSONObject().put("category", "TMX").put("fcstValue", "15").put("fcstDate", fcstDateStr));

        // API 응답 구조 (Response -> Body -> Items -> Item)
        JSONObject body = new JSONObject().put("items", items);
        JSONObject response = new JSONObject().put("header", new JSONObject()).put("body", body);
        JSONObject root = new JSONObject().put("response", response);

        return root;
    }

    // --- 공개 메서드 테스트 ---

    @Test
    @DisplayName("단기 예보 기간 내 호출 시 단기 예보 DTO 반환 테스트")
    void getWeather_ShortTerm_Success() throws Exception {
        // 1. Given (준비)
        String sido = "서울";
        String sigungu = "종로구";
        String dong = "청운동";
        LocalDate targetDate = LocalDate.now().plusDays(2); // 단기 예보 범위 (오늘+3일 이내)
        String inputdate = targetDate.toString();

        GridLocation mockLocation = new GridLocation(sido, sigungu, dong, 60, 127); // NX=60, NY=127

        // GridService Mocking (좌표 반환)
        when(gridService.getGridLocation(sido, sigungu, dong)).thenReturn(mockLocation);

        // callJson() 메서드 Mocking (Spy 객체 사용)
        // spyWeatherService의 callJson이 호출되면 Mock JSON 응답을 반환하도록 설정
        doReturn(createMockShortTermJson(targetDate))
                .when(spyWeatherService).callJson(anyString());

        // 2. When (실행)
        WeatherResponseDto result = spyWeatherService.getWeather(sido, sigungu, dong, inputdate);

        // 3. Then (검증)
        assertNotNull(result);
        assertEquals(inputdate, result.getTargetDate());
        assertNotNull(result.getShortTerm(), "단기 예보 결과가 있어야 합니다.");
        assertNull(result.getMidTerm(), "중기 예보 결과는 없어야 합니다.");

        // 단기 예보 상세 값 검증 (Mocking 데이터 기반)
        WeatherResponseDto.ShortTermWeather shortTerm = result.getShortTerm();
        assertEquals("3", shortTerm.getSky());      // 구름 많음
        assertEquals("0", shortTerm.getPty());      // 강수 형태 없음
        assertEquals("20", shortTerm.getRainProb()); // 강수 확률 20%
        assertEquals("5", shortTerm.getTMin());
        assertEquals("15", shortTerm.getTMax());

        // gridService가 정확히 한 번 호출되었는지 확인
        verify(gridService, times(1)).getGridLocation(sido, sigungu, dong);
        // callJson이 정확히 한 번 호출되었는지 확인
        verify(spyWeatherService, times(1)).callJson(startsWith(SHORT_API_URL));
    }

    @Test
    @DisplayName("중기 예보 기간 내 호출 시 단기 예보 결과가 null이어야 함")
    void getWeather_MidTerm_ShortTerm_Null() throws Exception {
        // 1. Given
        String sido = "서울";
        String sigungu = "종로구";
        String dong = "청운동";
        // 중기 예보 기간 (오늘+3일 이후)
        LocalDate targetDate = LocalDate.now().plusDays(4);
        String inputdate = targetDate.toString();

        GridLocation mockLocation = new GridLocation(sido, sigungu, dong, 60, 127);
        when(gridService.getGridLocation(sido, sigungu, dong)).thenReturn(mockLocation);

        // callJson은 호출되지 않아야 하므로 Mocking 불필요

        // 2. When
        WeatherResponseDto result = weatherService.getWeather(sido, sigungu, dong, inputdate);

        // 3. Then
        assertNotNull(result);
        assertEquals(inputdate, result.getTargetDate());
        assertNull(result.getShortTerm(), "단기 예보 기간이 아니므로 null이어야 합니다.");
        assertNull(result.getMidTerm(), "현재 중기 예보 로직이 구현되지 않았으므로 null이어야 합니다.");

        // 단기 예보 로직이 실행되지 않았으므로 callJson이 호출되지 않았는지 확인
        verify(spyWeatherService, never()).callJson(anyString());
    }
}
