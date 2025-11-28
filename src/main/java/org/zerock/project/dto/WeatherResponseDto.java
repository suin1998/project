package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WeatherResponseDto {

    private LocalDate date;
    private ShortTermWeather shortTerm;
    private MidTermWeather midTerm;

    @Data
    @AllArgsConstructor
    public static class ShortTermWeather{
        private Double sky;
        private Double precipitationType;
        private Double rainProbability;
        private Integer tempMin;
        private Integer tempMax;
    }

    @Data
    @AllArgsConstructor
    public static class MidTermWeather {
        private Double rainProbability;
        private Integer tempMin;
        private Integer tempMax;
    }
}
