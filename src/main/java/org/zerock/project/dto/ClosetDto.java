package org.zerock.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClosetDto {
    private String id;
    @NotBlank
    private String userId;
    @NotBlank
    private String imageUrl;
    @NotBlank
    private String category;
    private String color;
    private String brand;
    private List<String> tags;
}
