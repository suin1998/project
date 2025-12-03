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
    private String outContent;
}
