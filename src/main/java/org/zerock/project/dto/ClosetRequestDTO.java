package org.zerock.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.project.entity.Category;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosetRequestDTO {

    @NotNull
    private String userId;

    @NotNull
    private Category category;

    @NotBlank
    private String imageUrl;

    private String color;
    private String brand;

    private List<String> tags;
}

