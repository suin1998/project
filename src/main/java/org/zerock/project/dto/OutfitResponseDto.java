package org.zerock.project.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class OutfitResponseDto {
    private List<String> outfitImgUrl;
    private String recommendation;
    private String reason;
}
