package org.zerock.project.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNumber;

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
