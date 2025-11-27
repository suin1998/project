package org.zerock.project.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageResponseDTO<DTO, EN> {

    private List<DTO> dtoList;

    private int totalPages;
    private int page;
    private int size;
    private int start;
    private int end;
    private boolean prev, next;

    public PageResponseDTO(Page<EN> result, Function<EN, DTO> fn) {
        dtoList = result.stream().map(fn).collect(Collectors.toList());

        totalPages = result.getTotalPages();
        page = result.getNumber() + 1;
        size = result.getSize();

        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;

        start = tempEnd - 9;
        end = totalPages > tempEnd ? tempEnd : totalPages;

        prev = start > 1;
        next = totalPages > tempEnd;
    }
}
