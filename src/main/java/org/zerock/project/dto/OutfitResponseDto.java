package org.zerock.project.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class OutfitResponseDto {
    private String outfitImgUrl;
    private String recommendation;
    private String reason;
    private String imageDescription;
    private String generatedImageBase64;
}
