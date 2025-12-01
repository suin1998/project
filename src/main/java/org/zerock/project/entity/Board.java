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
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 50)
    private String userId;

    private String userNickname;
    private String title;
    private String content;
    private String userStyle;
    private String mainImageUrl;

    @Builder.Default
    private LocalDateTime regDate = LocalDateTime.now();

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer dislikeCount = 0; // 기본값 설정

    @Builder.Default
    private boolean deleted = false;

}
