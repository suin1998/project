package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.project.domain.Category;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosetRequestDTO {
    private Long userId;
    private Category category;
    private String imageUrl;
    private String color;
    private String brand;
    private List<String> tags;
}
