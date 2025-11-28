package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.zerock.project.dto.WeatherRequestDto;
import org.zerock.project.dto.WeatherResponseDto;
import org.zerock.project.model.GridLocation;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Log4j2

public class WeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    @Value("${weather.api.url}")
    private String url;

    private final GridService gridService;
    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherResponseDto getWeather(String sido, String sigungu, String dong, String inputdate) {
        GridLocation location = gridService.getGridLocation(sido, sigungu, dong);

        int nx = location.getNx();
        int ny = location.getNy();
        LocalDate targetDate = LocalDate.parse(inputdate);

        WeatherResponseDto result = new WeatherResponseDto();

        if(targetDate.isBefore(LocalDate.now().plusDays(4))) {
            ShortTermWeatherDto shortTerm = getShortTermForecast(nx, ny);
            parseShortTerm(shortTerm, targetDate, result);
        }else{
            MidTermWeatherDto midTerm = getMidTermForecast(sido);
            parseMidTerm(midTerm, targetDate, result);
        }
        return result;
    }

    private void parseShortTerm(ShortTremWeatherDto shortTerm, LocalDate targetDate, WeatherResponseDto result){
        List<ShortTermweatherDto.Item> items = shortTerm.getResponse().getBody().getItems().getitem();

        double tempSum = 0;
        int countTmp = 0;
        double popMax = 0;
        String sky = null;
        String pty = null;

        String targetDateStr = targetDate.toString().replace("-", "");

        for(ShortTermWeatherDto.item item: items){
            if(!item.getFcstDate().equals(targetDateStr)) continue;

            switch (item.getCategory()){
                case "TMP":
                    tempSum += Double.parseDouble(item.getFcstValue());
                    countTmp++;
                    break;

                case "POP":
                    popMax = Math.max(popMax, Double.parseDouble(item.getFcstValue()));
                    break;

                case "SKY":
                    sky = item.getFcstValue();
                    break;

                case "PTY":
                    pty = item.getFcstValue();
                    break;

            }

        }

        result.setTempAvg(countTmp > 0 ? tempSum / countTmp : 0);
        result.setPrecipitationProb(popMax);
        result.setSkyState(sky);
        result.setPrecipitationType(pty);


    }
    private void parseMidTerm(MidTermWeatherDto midTerm, LocalDate targetDate, WeatherResponseDto result){
        int dayDiff = targetDate.getDayOfYear() - LocalDate.now().getDayOfYear() ;

        if (dayDiff < 4 || dayDiff > 10){

        }
    }



}
