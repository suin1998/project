package org.zerock.project.dto;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


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
    private List<String> fashionStyle;
    private List<String> tempStyle;
    private String tpo;
    private List<MultipartFile> clothesImages;
}


