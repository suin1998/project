package org.zerock.project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class BoardDTO {

    private String id;
    private String userId;
    private String userNickname;
    private String title;
    private String content;
    private String userStyle;
    private String mainImageUrl;
    private LocalDateTime regDate;
    private Long viewCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private boolean deleted;

}
