package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zerock.project.dto.WeatherResponseDto;
import org.zerock.project.model.GridLocation;
import org.zerock.project.util.getRegionCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Log4j2

public class WeatherService {

    private final getRegionCode getRegionCode;
    //    private final RestTemplate restTemplate;
    @Value("${weather.api.key}")
    private String serviceKey;

    private static final String short_api_url = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst";
    private static final String mid_temp_api_url = "https://apihub.kma.go.kr/api/typ02/openApi/MidFcstInfoService/getMidTa?";
    private static final String mid_sky_api_url = "https://apihub.kma.go.kr/api/typ02/openApi/MidFcstInfoService/getMidLandFcst?";


    private final GridService gridService;
    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherResponseDto getWeather(String sido, String sigungu, String dong, String inputdate) {
        GridLocation location = gridService.getGridLocation(sido, sigungu, dong);
//        String regionCode = getRegionCode(req.getSido(), req.getSigungu());

        int nx = location.getNx();
        int ny = location.getNy();

        LocalDate targetDate = LocalDate.parse(inputdate);

        WeatherResponseDto.ShortTermWeather shortTerm = null;
        WeatherResponseDto.MidTermWeather midTerm = null;

        if(targetDate.isBefore(LocalDate.now().plusDays(3))) {
            shortTerm = getShortTermForecast(nx, ny, targetDate);

        }else{
//            midTerm = getMidTermForecast(targetDate, regionCode);
        }
        return new WeatherResponseDto(targetDate.toString(), shortTerm, midTerm);
    }

    private JSONObject callJson(String urlStr){
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();

            return new JSONObject(sb.toString());


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private WeatherResponseDto.ShortTermWeather getShortTermForecast(int nx, int ny, LocalDate targetDate) {
        String baseDate =  LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = "0500";

        String url = short_api_url + "?"
                +"authKey="+serviceKey
                +"numOfRows=1000"
                +"&pageNo=1"
                +"&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        JSONObject json = callJson(url);

        JSONArray items = json.getJSONArray("items");

        String sky = null;
        String pty = null;
        String rainProb = null;
        String tMin = null;
        String tMax = null;

        for(int i = 0; i < items.length(); i++){
            JSONObject obj = items.getJSONObject(i);
            String targetDateStr =  targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            if (targetDateStr.equals(obj.getString("fcstDate"))){

                switch (obj.getString("category")){
                    case "SKY": sky = obj.getString("fcstValue"); break;
                    case "PTY": pty = obj.getString("fcstValue"); break;
                    case "POP": rainProb = obj.getString("fcstValue"); break;
                    case "TMN": tMin = obj.getString("fcstValue"); break;
                    case "TMX": tMax = obj.getString("fcstValue"); break;
                }
            }
            else{
                return null;
            }
        }

        return new WeatherResponseDto.ShortTermWeather(sky, pty, rainProb, tMin, tMax);

    }


//    private WeatherResponseDto.MidTermWeather getMidTermForecast(LocalDate targetDate, String regionCode) {
//        int dayDiff = (int) ChronoUnit.DAYS.between(targetDate, LocalDate.now());
//
//        if (dayDiff < 4 || dayDiff > 10) throw new IllegalArgumentException("중기예보는 D+4~D+10일만 제공됩니다.");
//
//        String skyUrl = mid_sky_api_url + "?"
//                +"authKey="+serviceKey
//                +"&dataType=JSON"
//                +"&regId=" + regionCode;
//    }



}
