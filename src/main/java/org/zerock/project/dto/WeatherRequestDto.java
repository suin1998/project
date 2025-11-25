package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherRequestDto {
    private String inputdate;
    private String sido;
    private String sigungu;
    private String dong;
    private int nx;
    private int ny;
}
