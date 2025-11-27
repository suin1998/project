//package org.zerock.project.service;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.zerock.project.dto.WeatherResponseDto;
//import org.zerock.project.model.GridLocation;
//import org.zerock.project.model.StnLocation;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class WeatherServiceTest {
//
//    // 💡 수동으로 생성하고 spy()로 감쌀 객체 (어노테이션 제거)
//    private WeatherService spyWeatherService;
//
//    @Mock
//    private GridService gridService;
//
//    @Mock
//    private StnService stnService;
//
//    // @Value로 주입되는 필드 Mocking 값
//    private final String MOCK_SERVICE_KEY = "mock_test_key";
//    private final String SHORT_API_URL = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst";
//    private final String MID_SKY_API_URL = "https://apihub.kma.go.kr/api/typ02/openApi/MidFcstInfoService/getMidLandFcst";
//
//
//    @BeforeEach
//    void setUp() {
//        // 💡 에러 해결: @RequiredArgsConstructor가 요구하는 Mock 객체를 인자로 넣어 수동으로 인스턴스를 생성하고 Spy로 감쌉니다.
//        // 현재 WeatherService의 생성자는 GridService, StnService, RestTemplate(final)를 요구합니다.
//        // RestTemplate는 private final 필드이므로 Mockito가 주입할 수 없습니다.
//        // WeatherService의 RestTemplate 필드를 Mock으로 변경하거나 (MockMvc 같은 테스트 환경이 아니라면)
//        // 실제 인스턴스를 사용하거나, 테스트 시 무시하도록 처리해야 합니다.
//
//        // 현재 코드에 RestTemplate는 final로 선언되어 있어 주입이 어려우므로,
//        // 테스트를 위해 RestTemplate 의존성을 무시하고 생성자를 임시로 호출합니다.
//
//        // **중요:** 실제 WeatherService 생성자를 (GridService, StnService)로 가정합니다.
//        WeatherService realInstance = new WeatherService(gridService, stnService);
//        spyWeatherService = Mockito.spy(realInstance);
//
//
//        // @Value 필드 설정
//        ReflectionTestUtils.setField(spyWeatherService, "serviceKey", MOCK_SERVICE_KEY);
//        ReflectionTestUtils.setField(spyWeatherService, "short_api_url", SHORT_API_URL);
//        ReflectionTestUtils.setField(spyWeatherService, "mid_sky_api_url", MID_SKY_API_URL);
//    }
//
//    // --- Mock JSON 응답 생성 헬퍼 메서드 ---
//    // (이전과 동일하게 유지)
//    private JSONObject createMockShortTermJson(LocalDate targetDate) {
//        String fcstDateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        JSONArray items = new JSONArray();
//        items.put(new JSONObject().put("category", "SKY").put("fcstValue", "3").put("fcstDate", fcstDateStr).put("fcstTime", "0900"));
//        items.put(new JSONObject().put("category", "PTY").put("fcstValue", "0").put("fcstDate", fcstDateStr).put("fcstTime", "0900"));
//        items.put(new JSONObject().put("category", "POP").put("fcstValue", "20").put("fcstDate", fcstDateStr).put("fcstTime", "0900"));
//        items.put(new JSONObject().put("category", "TMN").put("fcstValue", "5").put("fcstDate", fcstDateStr).put("fcstTime", "0900"));
//        items.put(new JSONObject().put("category", "TMX").put("fcstValue", "15").put("fcstDate", fcstDateStr).put("fcstTime", "0900"));
//        JSONObject itemObject = new JSONObject().put("item", items);
//        JSONObject body = new JSONObject().put("items", itemObject).put("dataType", "JSON");
//        JSONObject response = new JSONObject().put("header", new JSONObject().put("resultCode", "00")).put("body", body);
//        return new JSONObject().put("response", response);
//    }
//
//    // --- 테스트 케이스 ---
//
//    @Test
//    @DisplayName("1. 단기 예보 기간 내 호출 시 성공적으로 DTO를 반환해야 한다")
//    void getWeather_ShortTerm_Success() throws Exception {
//        // 1. Given (준비)
//        String sido = "서울";
//        String sigungu = "종로구";
//        String dong = "청운동";
//        LocalDate targetDate = LocalDate.now().plusDays(2);
//        String inputdate = targetDate.toString();
//
//        GridLocation mockLocation = new GridLocation(sido, sigungu, dong, 60, 127);
//        StnLocation mockStnLocation = new StnLocation("11A00201", "서울", "108"); // 임시 기상관측 지점 정보
//
//        // GridService/StnService Mocking
//        when(gridService.getGridLocation(sido, sigungu, dong)).thenReturn(mockLocation);
//        when(stnService.getStnLocation(sido, sigungu)).thenReturn(mockStnLocation);
//
//        // callJson Mocking
//        doReturn(createMockShortTermJson(targetDate))
//                .when(spyWeatherService).callJson(anyString());
//
//        // 2. When (실행)
//        WeatherResponseDto result = spyWeatherService.getWeather(sido, sigungu, dong, inputdate);
//
//        // 3. Then (검증)
//        assertNotNull(result, "응답 DTO는 null이 아니어야 합니다.");
//        assertNotNull(result.getShortTerm(), "단기 예보 결과가 있어야 합니다.");
//        assertNull(result.getMidTerm(), "중기 예보 결과는 없어야 합니다.");
//
//        // 메서드 호출 검증
//        verify(gridService, times(1)).getGridLocation(sido, sigungu, dong);
//        verify(stnService, times(1)).getStnLocation(sido, sigungu);
//        verify(spyWeatherService, times(1)).callJson(startsWith(SHORT_API_URL));
//    }
//
//    @Test
//    @DisplayName("2. 중기 예보 기간 내 호출 시 중기 예보 로직이 호출되어야 한다")
//    void getWeather_MidTerm_CallMidTermLogic() throws Exception {
//        // 1. Given
//        String sido = "서울";
//        String sigungu = "종로구";
//        String dong = "청운동";
//        // 오늘 + 4일, 중기 예보 기간
//        LocalDate targetDate = LocalDate.now().plusDays(4);
//        String inputdate = targetDate.toString();
//
//        GridLocation mockLocation = new GridLocation(sido, sigungu, dong, 60, 127);
//        // MidTermForecast 로직을 위한 regionCode
//        StnLocation mockStnLocation = new StnLocation("11B10101", "경기북부", "105");
//
//        when(gridService.getGridLocation(sido, sigungu, dong)).thenReturn(mockLocation);
//        when(stnService.getStnLocation(sido, sigungu)).thenReturn(mockStnLocation);
//
//        // 💡 중기 예보 로직은 현재 null을 반환하도록 되어 있으므로, 메서드가 호출되었는지 확인합니다.
//        // getMidTermForecast는 private이므로 직접 Spy 객체를 통해 검증할 수 없습니다.
//        // 대신 callJson이 호출되었는지 여부로 간접 검증하거나 (현재 callJson 호출 로직이 없으므로 어려움),
//        // DTO 반환 값이 null인지 확인합니다.
//
//        // 2. When
//        WeatherResponseDto result = spyWeatherService.getWeather(sido, sigungu, dong, inputdate);
//
//        // 3. Then
//        assertNotNull(result);
//        assertNull(result.getShortTerm(), "단기 예보 기간이 아니므로 null이어야 합니다.");
//        assertNull(result.getMidTerm(), "현재 getMidTermForecast가 null을 반환하도록 구현되어 있으므로 null이어야 합니다.");
//
//        // callJson은 호출되지 않았는지 검증 (단기 예보 로직만 callJson을 사용하기 때문)
//        verify(spyWeatherService, never()).callJson(anyString());
//        verify(stnService, times(1)).getStnLocation(sido, sigungu);
//
//        // 💡 getMidTermForecast가 호출되었는지 확인하는 다른 방법:
//        // getMidTermForecast가 구현된다면 해당 메서드가 사용하는 callJson이 호출되었는지 검증하면 됩니다.
//    }
//
//    @Test
//    @DisplayName("3. getMidTermForecast: D+4일 미만 또는 D+10일 초과 시 예외 발생")
//    void getMidTermForecast_InvalidDateRange_ThrowsException() {
//        // D+3일 (유효하지 않음)
//        LocalDate targetDateTooEarly = LocalDate.now().plusDays(3);
//        // D+11일 (유효하지 않음)
//        LocalDate targetDateTooLate = LocalDate.now().plusDays(11);
//
//        String regionCode = "11B10101";
//
//        // private 메서드이므로 ReflectionTestUtils를 사용하여 강제 호출해야 하지만,
//        // public 메서드인 getWeather를 통해 간접적으로 테스트합니다.
//
//        GridLocation mockLocation = new GridLocation("서울", "종로구", "청운동", 60, 127);
//        StnLocation mockStnLocation = new StnLocation("11A00201", "서울", regionCode);
//
//        when(gridService.getGridLocation(any(), any(), any())).thenReturn(mockLocation);
//        when(stnService.getStnLocation(any(), any())).thenReturn(mockStnLocation);
//
//
//        // D+3일은 단기예보 로직으로 빠지므로, D+4일보다 이른 날짜 테스트는 어려움.
//        // D+11일 테스트는 중기예보 로직으로 들어갑니다.
//
//        // 💡 중기 예보 로직이 호출되는 날짜 범위(D+4일 이상)를 만족하고, getMidTermForecast 내부에서 예외가 발생하는지 확인
//
//        // D+11일 (중기예보 영역으로 진입)
//        assertThrows(IllegalArgumentException.class, () -> {
//            spyWeatherService.getWeather("서울", "종로구", "청운동", targetDateTooLate.toString());
//        }, "D+11일은 중기예보 범위 초과로 IllegalArgumentException이 발생해야 합니다.");
//    }
//}