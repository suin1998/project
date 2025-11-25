package org.zerock.project.dto;

import lombok.Data;

@Data
public class OutfitRequestDto {
    private WeatherResponseDto weatherResponseDto;
    private String gender;
    private double userage;
    private String style1; //list
    private String style2; //list
    private String tpo;

    private String top;
    private String bottom;
    private String shoes;
    private String bag;
    private String accessory;
    private String hat;

}
