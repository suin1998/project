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
public class WeatherRequestDto {
    private LocalDate inputDate;
    private String siDo;
    private String siGunGu;
    private String dong;
}
