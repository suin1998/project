package org.zerock.project.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sort = "latest";

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page -1, size, sort);
    }

    public Pageable getPageable() {
        Sort s = Sort.by("regDate").descending();

        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "view":
                    s = Sort.by("viewCount").descending(); // 조회순
                    break;
                case "like":
                    s = Sort.by("likeCount").descending(); // 추천순
                    break;
                case "latest":
                default:
                    s = Sort.by("regDate").descending(); // 최신순
                    break;
            }
        }

        return PageRequest.of(page - 1, size, s); // 💡 수정된 Pageable 반환
    }
}
