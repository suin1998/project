package org.zerock.project.dto;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String userId;

    private List<String> fashionStyle;
    private List<String> tempStyle;
    private String tpo;
    private List<MultipartFile> clothesImages;
}


