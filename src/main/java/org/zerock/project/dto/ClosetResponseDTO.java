package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.project.entity.Category;
import org.zerock.project.entity.Closet;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosetResponseDTO {
    private String id;
    private String userId;
    private Category category;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClosetResponseDTO fromEntity( Closet closet) {
        return ClosetResponseDTO.builder()
                .id(closet.getId())
                .userId(closet.getUser().getId())
                .category(closet.getCategory())
                .imageUrl(closet.getImageUrl())
                .createdAt(closet.getCreatedAt())
                .updatedAt(closet.getUpdatedAt())
                .build();
    }

}

