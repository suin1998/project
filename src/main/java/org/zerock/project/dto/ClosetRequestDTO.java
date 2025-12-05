package org.zerock.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.project.entity.Category;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosetRequestDTO {
    @NotNull
    private Category category;
}

