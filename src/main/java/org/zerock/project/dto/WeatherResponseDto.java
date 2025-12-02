package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Getter
@Setter
public class WeatherResponseDto {

    private LocalDate date;
    private Double rainProbability;
    private Double tempMin;
    private Double tempMax;


}
