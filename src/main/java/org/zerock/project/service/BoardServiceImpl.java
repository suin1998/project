package org.zerock.project.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.project.dto.BoardDTO;
import org.zerock.project.dto.PageRequestDTO;
import org.zerock.project.dto.PageResponseDTO;
import org.zerock.project.entity.Board;
import org.zerock.project.repository.BoardRepository;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;

    @Override
    public Board dtoToEntity(BoardDTO dto) {
        return Board.builder()
                .boardNumber(dto.getBoardNumber())
                .userId(dto.getUserId())
                .userNickname(dto.getUserNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .userStyle(dto.getUserStyle())
                .regDate(dto.getRegDate())
                .viewCount(dto.getViewCount())
                .likeCount(dto.getLikeCount())
                .dislikeCount(dto.getDislikeCount())
                .deleted(dto.isDeleted())
                .build();
    }

    @Override
    public BoardDTO entityToDto(Board entity) {

        BoardDTO dto = new BoardDTO(
                entity.getBoardNumber(),
                entity.getUserId(),
                entity.getUserNickname(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUserStyle(),
                entity.getMainImageUrl(),
                entity.getRegDate(),
                entity.getViewCount(),
                entity.getLikeCount(),
                entity.getDislikeCount(),
                entity.isDeleted()
        );

        return dto;
    }

    @Override
    public Long register(BoardDTO dto) {
        Board entity = Board.builder()
                .userId(dto.getUserId())
                .userNickname(dto.getUserNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .userStyle(dto.getUserStyle())
                .regDate(LocalDateTime.now())
                .viewCount(0L)
                .likeCount(0)
                .dislikeCount(0)
                .deleted(false)
                .build();

        boardRepository.save(entity);

        return entity.getBoardNumber();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDTO get(Long boardNumber) {
        Optional<Board> result = boardRepository.findByBoardNumberAndDeletedFalse(boardNumber);

        return result.map(this::entityToDto)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물(번호: " + boardNumber + ")을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void modify(BoardDTO dto) {
        Board entity = boardRepository.findById(dto.getBoardNumber()).orElseThrow(() -> new EntityNotFoundException
                ("수정할 게시물(번호 :" + dto.getBoardNumber() + ")을 찾을 수 없습니다."));

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setUserStyle(dto.getUserStyle());
        entity.setDeleted(dto.isDeleted());
    }

    @Override
    @Transactional
    public void remove(Long boardNumber) {
        Board entity = boardRepository.findById(boardNumber)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 게시물을 찾을 수 없습니다."));

        entity.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardDTO, Board> getList(PageRequestDTO pageRequestDTO) {
       Pageable pageable = pageRequestDTO.getPageable(Sort.by("boardNumber").descending());
       Page<Board> result = boardRepository.findAllByDeletedFalse(pageable);
       Function<Board, BoardDTO> fn = this::entityToDto;
       return new PageResponseDTO<>(result, fn);
    }
}
