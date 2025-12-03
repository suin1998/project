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
@ToString(exclude = "writer") // writer 필드는 무한 루프 방지를 위해 제외
public class Board {

    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // 🚨 [수정 1] 작성자 정보를 User 엔티티와 ManyToOne 관계로 매핑
    // FetchType.LAZY: 필요할 때만 User 정보를 로드하여 성능 최적화
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", nullable = false) // User 테이블의 ID 컬럼을 참조
    private User writer;

    // 기존의 String userId와 userNickname 필드는 삭제

    @Column(nullable = false, length = 100) // title 길이를 좀 더 넉넉하게 수정
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT") // content는 TEXT 타입 권장
    private String content;

    private String userStyle;
    private String mainImageUrl;

    @Builder.Default
    @Column(updatable = false) // 🚨 [수정 2] 등록 시간은 수정 불가능하게 설정
    private LocalDateTime regDate = LocalDateTime.now();

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer dislikeCount = 0;

    @Builder.Default
    private boolean deleted = false;

    // 게시글 내용 수정 메서드 (관리자가 사용할 수 있도록)
    public void changeBoard(String title, String content, String mainImageUrl) {
        this.title = title;
        this.content = content;
        this.mainImageUrl = mainImageUrl;
    }
}