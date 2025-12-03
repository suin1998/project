package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 수정(PUT) 요청 시 사용되는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardModifyDTO {

    // 수정 시에는 게시글 ID는 URL PathVariable로 받으므로 여기서는 생략 가능

    private String title;
    private String content;
    private String mainImageUrl;

    // 수정 요청자의 ID (권한 체크를 위해 필요)
    private Long modifierId;
}