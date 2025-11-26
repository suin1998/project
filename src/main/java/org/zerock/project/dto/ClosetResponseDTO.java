package org.zerock.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ClosetResponseDTO {
    private Long id;
    private String userId;
    private String imageUrl;
    private String category;
    private String color;
    private String brand;
    private List<String> tags;
    private Instant createdAt;
}
