package org.zerock.project.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Closet {
    @Id
    private String id;

    @NotBlank
    private String userId;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String category; // top, bottom, outer, shoes, bag, accessory, hat

    private String color;
    private String brand;
    private List<String> tags;
    private Instant createdAt = Instant.now();

}
