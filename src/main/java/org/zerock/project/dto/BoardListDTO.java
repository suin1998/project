package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 목록 조회 시 사용되는 간소화된 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDTO {

    private String id;
    private String title;
    private String userStyle;
    private String mainImageUrl;
    private String userNickname;
    private LocalDateTime regDate;
    private Long viewCount;
    private Integer likeCount;
}