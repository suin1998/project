package org.zerock.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherResponseDto {
    private double temp;
    private double precipitationProb;
    private double precipitationType;
    private double sky;

    private double tempMax;
    private double tempMin;
}
