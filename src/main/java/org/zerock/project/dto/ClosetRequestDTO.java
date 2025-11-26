package org.zerock.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClosetRequestDTO {
    private String userId;
    private String category;
    private String color;
    private String brand;

    private List<String> tags;
}
