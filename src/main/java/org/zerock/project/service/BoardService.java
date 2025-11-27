package org.zerock.project.service;

import org.zerock.project.dto.BoardDTO;
import org.zerock.project.dto.PageRequestDTO;
import org.zerock.project.dto.PageResponseDTO;
import org.zerock.project.entity.Board;

public interface BoardService {

    Long register(BoardDTO dto);

    BoardDTO get(Long boardNumber);

    void modify(BoardDTO dto);

    default BoardDTO entityToDto(Board entity) {
        return null;
    }

    Board dtoToEntity(BoardDTO dto);

    void remove(Long boardNumber);

    PageResponseDTO<BoardDTO, Board> getList(PageRequestDTO pageRequestDTO);
}
