package org.zerock.project.service;

import org.zerock.project.dto.BoardDTO;
import org.zerock.project.dto.PageRequestDTO;
import org.zerock.project.dto.PageResponseDTO;
import org.zerock.project.entity.Board;

public interface BoardService {

    String register(BoardDTO dto);

    BoardDTO get(String id);

    void modify(BoardDTO dto);

    default BoardDTO entityToDto(Board entity) {
        return null;
    }

    Board dtoToEntity(BoardDTO dto);

    void remove(String id);

    PageResponseDTO<BoardDTO, Board> getList(PageRequestDTO pageRequestDTO);
}
