package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 등록(POST) 요청 시 사용되는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRegisterDTO {

    private String title;
    private String content;
    private String userStyle;
    private String mainImageUrl;

    // 게시글 작성자의 ID (실제로는 Spring Security를 통해 자동으로 얻어와야 함)
    private String writerId;
}