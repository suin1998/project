package org.zerock.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter

public class SavedSelectedRequestDTO {
    private String userId;
    private String aiId;
    private List<String> tags;
    private List<String> imageUrl;
    private LocalDate targetDate;
    private String content;
}
