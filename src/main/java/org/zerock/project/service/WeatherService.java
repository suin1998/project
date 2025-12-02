package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zerock.project.dto.WeatherRequestDto;
import org.zerock.project.dto.WeatherResponseDto;
import org.zerock.project.model.GridLocation;
import org.zerock.project.model.StnLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


@Service
@RequiredArgsConstructor
@Log4j2

public class WeatherService {
    //    private final RestTemplate restTemplate;
    @Value("${weather.api.key}")
    private String serviceKey;

    private static final String short_api_url = "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst";
    private static final String mid_temp_api_url = "https://apihub.kma.go.kr/api/typ02/openApi/MidFcstInfoService/getMidTa";
    private static final String mid_sky_api_url = "https://apihub.kma.go.kr/api/typ02/openApi/MidFcstInfoService/getMidLandFcst";


    private final GridService gridService;
    private final StnService stnService;
//    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherResponseDto getWeather(WeatherRequestDto  weatherRequestDto) {
        String sido = weatherRequestDto.getSiDo();
        String sigungu = weatherRequestDto.getSiGunGu();
        String dong = weatherRequestDto.getDong();
        LocalDate targetDate = weatherRequestDto.getInputDate();

        GridLocation location = gridService.getGridLocation(sido, sigungu, dong);
        StnLocation stnLocation = stnService.getStnLocation(sido, sigungu);

        int nx = location.getNx();
        int ny = location.getNy();
        String regionCode =  stnLocation.getRegionCode();

        Double tMin = null;
        Double tMax = null;
        Double pop = null;

        WeatherResponseDto responseDto = null;

        if(targetDate.isBefore(LocalDate.now().plusDays(4))) {
            responseDto = getShortTermForecast(nx, ny, targetDate);

        }else{
            responseDto = getMidTermForecast(regionCode, targetDate);
        }
        return responseDto;
    }

    public JSONObject callJson(String urlStr){
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


    private WeatherResponseDto getShortTermForecast(int nx, int ny, LocalDate targetDate) {
        String baseDate =  LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = "0500";

        String url = short_api_url + "?"
                +"authKey="+serviceKey
                +"&numOfRows=1000"
                +"&pageNo=1"
                +"&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        JSONObject json = callJson(url);


        JSONObject response = json.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray itemArrays = items.getJSONArray("item");
        String targetDateStr =  targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        ArrayList<Double> rainList = new ArrayList<>();
        double tMin = 0;
        double tMax = 0;


        double rainProb = 0;

        for(int i = 0; i < itemArrays.length(); i++){
            JSONObject obj = itemArrays.getJSONObject(i);

            if (!targetDateStr.equals(obj.getString("fcstDate"))) {
                continue;
            }
            switch (obj.getString("category")) {
                case "POP":
                    rainList.add(obj.getDouble("fcstValue"));
                    break;
                case "TMN":
                    tMin = obj.getDouble("fcstValue");
                    break;
                case "TMX":
                    tMax = obj.getDouble("fcstValue");
                    break;
            }
        }
        rainProb = Collections.max(rainList);

        return new WeatherResponseDto(targetDate, rainProb, tMin, tMax);

    }


    private WeatherResponseDto getMidTermForecast(String regionCode, LocalDate targetDate) {
        int dayDiff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDate);

        if (dayDiff < 4 || dayDiff > 10) throw new IllegalArgumentException("중기예보는 D+4~D+10일만 제공됩니다.");

        String baseDate =  LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetDateStr =  targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String skyUrl = mid_sky_api_url + "?"
                +"authKey="+serviceKey
                +"&dataType=JSON"
                +"&regId=" + regionCode
                +"&pageNo=1"
                +"&numOfRows=10"
                +"&tmFc="+baseDate+"0600";

        JSONObject json = callJson(skyUrl);

        JSONObject response = json.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray itemArrays = items.getJSONArray("item");

        double rainProb = 0;
        ArrayList<Double> sumRainList = new ArrayList<>();

        JSONObject obj = itemArrays.getJSONObject(0);

        Iterator<String> keys = obj.keys();
        String searchPattern = "rnSt" + dayDiff;

        while(keys.hasNext()){
            String key = keys.next();
            if(key.contains(searchPattern)){
                try{
                    if (dayDiff >= 4 || dayDiff <= 7){
                        sumRainList.add(obj.getDouble(key));
                    }else {
                        rainProb = obj.getDouble(key);
                    }

                }catch(Exception e) {
                    log.warn("RainProb key found but value is not a number or parsing failed for key: {}", key);
                }

            }

        }
        if (dayDiff >= 4 || dayDiff <= 7) {
            rainProb = Collections.max(sumRainList);
        }

//        log.info(rainProb);
        String tempUrl = mid_temp_api_url + "?"
                +"authKey="+serviceKey
                +"&dataType=JSON"
                +"&regId=" + regionCode
                +"&pageNo=1"
                +"&numOfRows=10"
                +"&tmFc="+baseDate+"0600";

        JSONObject temp = callJson(tempUrl);

        JSONObject temp_response = temp.getJSONObject("response");
        JSONObject temp_body = temp_response.getJSONObject("body");
        JSONObject temp_items = temp_body.getJSONObject("items");
        JSONArray temp_itemArrays = temp_items.getJSONArray("item");

        double tMin = 0;
        double tMax = 0;

        String tMinKey = "taMin" + dayDiff;
        String tMaxKey = "taMax" + dayDiff;
        JSONObject forCast = temp_itemArrays.getJSONObject(0);
        tMin = forCast.getDouble(tMinKey);
        tMax = forCast.getDouble(tMaxKey);

        return new WeatherResponseDto(targetDate, rainProb, tMin, tMax);

    }

}
