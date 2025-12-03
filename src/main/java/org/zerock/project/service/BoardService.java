package org.zerock.project.service;

import org.zerock.project.dto.*;
import org.zerock.project.entity.Board;
import org.zerock.project.entity.User;

public interface BoardService {

    //게시물 등록
    String register(BoardRegisterDTO registerDTO);

    //전체 게시물 조회
    PageResponseDTO<BoardListDTO, Board> getList(PageRequestDTO pageRequestDTO);

    //특정 작성자 게시물 조회
    PageResponseDTO<BoardListDTO, Board> getMyList(PageRequestDTO pageRequestDTO, String writerId);

    //특정 id 게시물 조회
    BoardDTO get(String id);

    //게시물 삭제
    void remove(String id, String removerId);

    // dto -> entity 변환
    default Board dtoToEntity(BoardRegisterDTO dto, User writer) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .userStyle(dto.getUserStyle())
                .mainImageUrl(dto.getMainImageUrl())
                .writer(writer)
                .build();
    }

    //entity -> dto변환
    BoardDTO entityToDto(Board entity);

    //dto -> entity 변환
    BoardListDTO entityToListDto(Board entity);
}