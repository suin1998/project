package org.zerock.project.service;

import org.zerock.project.dto.*;
import org.zerock.project.entity.Board;
import org.zerock.project.entity.User;

/**
 * 게시판 CRUD 비즈니스 로직 정의
 * 각 메서드는 목적에 맞는 전용 DTO를 사용합니다.
 */
public interface BoardService {

    /**
     * 게시글을 등록합니다. (BoardRegisterDTO 사용)
     * @param registerDTO 등록 요청 정보
     * @return 생성된 게시글의 ID
     */
    String register(BoardRegisterDTO registerDTO);

    /**
     * 게시글 목록을 페이징 처리하여 조회합니다. (BoardListDTO 사용)
     * @param pageRequestDTO 페이지 요청 정보
     * @return BoardListDTO를 포함하는 페이지 응답
     */
    PageResponseDTO<BoardListDTO, Board> getList(PageRequestDTO pageRequestDTO);

    /**
     * 특정 ID의 게시글 상세 정보를 조회합니다. (BoardDTO 사용)
     * @param id 게시글 ID
     * @return 게시글 상세 정보 DTO
     */
    BoardDTO get(String id);

    /**
     * 게시글을 수정합니다. (BoardModifyDTO 사용)
     * @param id 수정할 게시글 ID
     * @param modifyDTO 수정 요청 정보 (제목, 내용 등)
     */
    void modify(String id, BoardModifyDTO modifyDTO);

    /**
     * 게시글을 소프트 삭제합니다.
     * @param id 삭제할 게시글 ID
     */
    void remove(String id);

    // --- DTO <-> Entity 변환 관련 Default/Helper 메서드 ---

    /**
     * BoardRegisterDTO -> Board Entity 변환 (게시글 등록 시 사용)
     */
    default Board dtoToEntity(BoardRegisterDTO dto, User writer) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .userStyle(dto.getUserStyle())
                .mainImageUrl(dto.getMainImageUrl())
                .writer(writer)
                .build();
    }

    /**
     * Board Entity -> BoardDTO 변환을 위한 기본 메서드 (상세 조회 시 사용)
     */
    BoardDTO entityToDto(Board entity);

    /**
     * Board Entity -> BoardListDTO 변환을 위한 기본 메서드 (목록 조회 시 사용)
     */
    BoardListDTO entityToListDto(Board entity);
}