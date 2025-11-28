package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class WeatherRequestDto {
    private LocalDate inputDate;
    private String siDo;
    private String siGunGu;
    private String dong;
}
