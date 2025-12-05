package org.zerock.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiCoordi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String ai_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // User 테이블의 ID 컬럼을 참조
    private User userId;

    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @Column(updatable = false)
    private LocalDate regDate = LocalDate.now();;

    private LocalDate targetDate;
    private List<String> aiImage_url;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
