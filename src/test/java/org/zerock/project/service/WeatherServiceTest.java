package org.zerock.project.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.zerock.project.dto.WeatherRequestDto;
import org.zerock.project.dto.WeatherResponseDto;
import org.zerock.project.model.GridLocation;
import org.zerock.project.model.StnLocation;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static reactor.netty.http.HttpConnectionLiveness.log;

@ExtendWith(MockitoExtension.class)
@DisplayName("WeatherService 테스트")
class WeatherServiceTest {

    @Mock
    private GridService gridService;

    @Mock
    private StnService stnService;

    @Spy
    @InjectMocks
    private WeatherService weatherService;

    private static final String TEST_API_KEY = "lJVhA4k9SEKVYQOJPdhCBQ";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "serviceKey", TEST_API_KEY);
    }

    @Test
    @DisplayName("단기예보 조회 성공 테스트 (D+0 ~ D+2)")
    void testGetWeather_ShortTerm_Success() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(7);
        WeatherRequestDto request = new WeatherRequestDto(
                targetDate,
                "서울특별시",
                "강남구",
                "역삼동"
        );

        GridLocation gridLocation = new GridLocation("서울특별시", "강남구", "역삼동", 60, 127);
        StnLocation stnLocation = new StnLocation("서울특별시", "강남구", "11B10101");

        when(gridService.getGridLocation("서울특별시", "강남구", "역삼동"))
                .thenReturn(gridLocation);
        when(stnService.getStnLocation("서울특별시", "강남구"))
                .thenReturn(stnLocation);

//         Mock JSON response for short-term forecast
        JSONObject mockJson = createMockShortTermJson(targetDate);
        doReturn(mockJson).when(weatherService).callJson(anyString());

        // When
        WeatherResponseDto response = weatherService.getWeather(request);
        System.out.println(response);
        // Then
        assertNotNull(response);
        assertEquals(targetDate, response.getDate());
        assertNotNull(response.getShortTerm());
        assertNull(response.getMidTerm());

        verify(gridService, times(1)).getGridLocation("서울특별시", "강남구", "역삼동");
        verify(stnService, times(1)).getStnLocation("서울특별시", "강남구");
    }

    @Test
    @DisplayName("중기예보 조회 성공 테스트 (D+4 ~ D+10)")
    void testGetWeather_MidTerm_Success() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(5);
        WeatherRequestDto request = new WeatherRequestDto(
                targetDate,
                "서울특별시",
                "강남구",
                "역삼동"
        );

        GridLocation gridLocation = new GridLocation("서울특별시", "강남구", "역삼동", 60, 127);
        StnLocation stnLocation = new StnLocation("서울특별시", "강남구", "11B10101");

        when(gridService.getGridLocation("서울특별시", "강남구", "역삼동"))
                .thenReturn(gridLocation);
        when(stnService.getStnLocation("서울특별시", "강남구"))
                .thenReturn(stnLocation);

        // Mock JSON response for mid-term forecast
        JSONObject mockJson = createMockMidTermJson();
        doReturn(mockJson).when(weatherService).callJson(anyString());

        // When
        WeatherResponseDto response = weatherService.getWeather(request);
        System.out.println(response);

        // Then
        assertNotNull(response);
        assertEquals(targetDate, response.getDate());
        assertNull(response.getShortTerm());
        assertNotNull(response.getMidTerm());

        verify(gridService, times(1)).getGridLocation("서울특별시", "강남구", "역삼동");
        verify(stnService, times(1)).getStnLocation("서울특별시", "강남구");
    }

    @Test
    @DisplayName("중기예보 범위 초과 예외 테스트 (D+3)")
    void testGetWeather_MidTerm_InvalidDateRange() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(3);
        WeatherRequestDto request = new WeatherRequestDto(
                targetDate,
                "서울특별시",
                "강남구",
                "역삼동"
        );

        GridLocation gridLocation = new GridLocation("서울특별시", "강남구", "역삼동", 60, 127);
        StnLocation stnLocation = new StnLocation("서울특별시", "강남구", "11B10101");

        when(gridService.getGridLocation(anyString(), anyString(), anyString()))
                .thenReturn(gridLocation);
        when(stnService.getStnLocation(anyString(), anyString()))
                .thenReturn(stnLocation);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeather(request);
        });
    }

    @Test
    @DisplayName("단기예보 데이터 파싱 테스트")
    void testShortTermForecast_DataParsing() {
        // Given
        LocalDate targetDate = LocalDate.now();
        WeatherRequestDto request = new WeatherRequestDto(
                targetDate,
                "서울특별시",
                "강남구",
                "역삼동"
        );

        GridLocation gridLocation = new GridLocation("서울특별시", "강남구", "역삼동", 60, 127);
        StnLocation stnLocation = new StnLocation("서울특별시", "강남구", "11B10101");

        when(gridService.getGridLocation(anyString(), anyString(), anyString()))
                .thenReturn(gridLocation);
        when(stnService.getStnLocation(anyString(), anyString()))
                .thenReturn(stnLocation);

        JSONObject mockJson = createMockShortTermJson(targetDate);
        doReturn(mockJson).when(weatherService).callJson(anyString());

        // When
        WeatherResponseDto response = weatherService.getWeather(request);

        // Then
        assertNotNull(response.getShortTerm());
        WeatherResponseDto.ShortTermWeather shortTerm = response.getShortTerm();

        assertNotNull(shortTerm.getSky());
        assertNotNull(shortTerm.getPrecipitationType());
        assertNotNull(shortTerm.getRainProbability());
        assertNotNull(shortTerm.getTempMin());
        assertNotNull(shortTerm.getTempMax());
    }

    @Test
    @DisplayName("GridLocation null 처리 테스트")
    void testGetWeather_NullGridLocation() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(1);
        WeatherRequestDto request = new WeatherRequestDto(
                targetDate,
                "서울특별시",
                "강남구",
                "역삼동"
        );

        when(gridService.getGridLocation(anyString(), anyString(), anyString()))
                .thenReturn(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            weatherService.getWeather(request);
        });
    }

    // Mock JSON 생성 메서드
    private JSONObject createMockShortTermJson(LocalDate targetDate) {
        String targetDateStr = targetDate.toString().replace("-", "");

        JSONObject item1 = new JSONObject();
        item1.put("category", "SKY");
        item1.put("fcstDate", targetDateStr);
        item1.put("fcstValue", 1);

        JSONObject item2 = new JSONObject();
        item2.put("category", "PTY");
        item2.put("fcstDate", targetDateStr);
        item2.put("fcstValue", 0);

        JSONObject item3 = new JSONObject();
        item3.put("category", "POP");
        item3.put("fcstDate", targetDateStr);
        item3.put("fcstValue", 30);

        JSONObject item4 = new JSONObject();
        item4.put("category", "TMN");
        item4.put("fcstDate", targetDateStr);
        item4.put("fcstValue", 10);

        JSONObject item5 = new JSONObject();
        item5.put("category", "TMX");
        item5.put("fcstDate", targetDateStr);
        item5.put("fcstValue", 20);

        JSONArray itemArray = new JSONArray();
        itemArray.put(item1);
        itemArray.put(item2);
        itemArray.put(item3);
        itemArray.put(item4);
        itemArray.put(item5);

        JSONObject items = new JSONObject();
        items.put("item", itemArray);

        JSONObject body = new JSONObject();
        body.put("items", items);

        JSONObject response = new JSONObject();
        response.put("body", body);

        JSONObject json = new JSONObject();
        json.put("response", response);

        return json;
    }

    private JSONObject createMockMidTermJson() {
        JSONObject item = new JSONObject();
        item.put("rnSt5Am", 30);
        item.put("rnSt5Pm", 40);
        item.put("taMin5", 10);
        item.put("taMax5", 20);

        JSONArray itemArray = new JSONArray();
        itemArray.put(item);

        JSONObject items = new JSONObject();
        items.put("item", itemArray);

        JSONObject body = new JSONObject();
        body.put("items", items);

        JSONObject response = new JSONObject();
        response.put("body", body);

        JSONObject json = new JSONObject();
        json.put("response", response);

        return json;
    }
}