package org.zerock.project.dto;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutfitRequestDto {
    public WeatherResponseDto weatherResponseDto;
    public WeatherRequestDto weatherRequestDto;
    private Gender gender;
    private Double userAge;
    private String fashion;
    private String tempStyle;
    private String tpo;
    private String cloths;
}


