package org.zerock.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.project.entity.Category;
import org.zerock.project.entity.Closet;
import org.zerock.project.entity.User;

import java.util.ArrayList;
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

    // DTO -> Entity 변환
    public Closet toEntity(User user) {
        return Closet.builder()
                .user(user)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .color(this.color)
                .brand(this.brand)
                .tags(this.tags != null ? this.tags : new ArrayList<>())
                .build();
    }

}

