package org.zerock.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    @JoinColumn(name = "userId", nullable = false) // User 테이블의 ID 컬럼을 참조
    private User userId;

    private List<String> tags;
    private LocalDateTime regDate;
    private String aiImage_url;
}
