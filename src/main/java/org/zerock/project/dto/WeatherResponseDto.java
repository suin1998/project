package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherResponseDto {

    private String date;
    private ShortTermWeather shortTerm;
    private MidTermWeather midTerm;

    @Data
    @AllArgsConstructor
    public static class ShortTermWeather{
        private String sky;
        private String precipitationType;
        private String rainProbability;
        private String tempMin;
        private String tempMax;
    }

    @Data
    @AllArgsConstructor
    public static class MidTermWeather {
        private String sky;
        private String rainProbability;
        private String tempMin;
        private String tempMax;
    }
}
