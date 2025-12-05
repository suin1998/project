package org.zerock.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.project.dto.*;
import org.zerock.project.entity.Board;
import org.zerock.project.entity.User;
import org.zerock.project.repository.BoardRepository;
import org.zerock.project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * BoardRegisterDTO -> Board Entity 변환
     * (게시글 등록 시 사용)
     */
    @Override
    public Board dtoToEntity(BoardRegisterDTO dto, User writer) {
        // ID는 DB에서 자동 생성되거나 UUID로 설정될 것이므로 DTO에는 없음
        return Board.builder()
                .writer(writer)
                .title(dto.getTitle())
                .content(dto.getContent())
                .userStyle(dto.getUserStyle())
                .mainImageUrl(dto.getMainImageUrl())
                .build();
    }

    /**
     * Board Entity -> BoardDTO 변환 (상세 조회 시 사용)
     */
    @Override
    public BoardDTO entityToDto(Board entity) {
        User writer = entity.getWriter();

        return BoardDTO.builder()
                .id(entity.getId())
                .userId(writer.getId()) // User 엔티티의 String 식별자(username)를 DTO의 userId에 설정
                .userNickname(writer.getNickname()) // User 닉네임을 DTO에 설정
                .title(entity.getTitle())
                .content(entity.getContent())
                .userStyle(entity.getUserStyle())
                .mainImageUrl(entity.getMainImageUrl())
                .regDate(entity.getRegDate())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .dislikeCount(entity.getDislikeCount())
                .deleted(entity.isDeleted())
                .build();
    }

    /**
     * Board Entity -> BoardListDTO 변환 (목록 조회 시 사용)
     */
    @Override
    public BoardListDTO entityToListDto(Board entity) {
        User writer = entity.getWriter();

        return BoardListDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .userStyle(entity.getUserStyle())
                .mainImageUrl(entity.getMainImageUrl())
                .userNickname(writer.getNickname())
                .regDate(entity.getRegDate())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .build();
    }


    /**
     * 게시글 등록 기능 (BoardRegisterDTO 사용)
     */
    @Override
    @Transactional
    public String register(BoardRegisterDTO registerDTO) {
        log.info("게시글 등록 요청: {}", registerDTO);

        // 1. 작성자 찾기 (Long 타입 ID 사용)
        User writer = userRepository.findById(registerDTO.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("작성자 ID(" + registerDTO.getWriterId() + ")를 찾을 수 없습니다."));

        // 2. DTO -> Entity 변환
        Board entity = dtoToEntity(registerDTO, writer);

        // 3. 초기값 설정
        // UUID를 ID로 사용한다고 가정합니다. (Controller에서 String으로 받고 있으므로)
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        entity.setViewCount(0L);
        entity.setLikeCount(0);
        entity.setDislikeCount(0);
        entity.setDeleted(false);
        // RegDate는 Entity에서 @CreationTimestamp 등으로 처리하는 것이 일반적이나, 여기서는 명시적으로 설정
        entity.setRegDate(LocalDateTime.now());

        // 4. 저장
        boardRepository.save(entity);
        log.info("게시글 등록 완료: ID={}", entity.getId());

        return entity.getId();
    }

    /**
     * 게시글 목록을 페이징 처리하여 조회합니다. (BoardListDTO 사용)
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardListDTO, Board> getList(PageRequestDTO pageRequestDTO) {
        log.info("전체 게시글 목록 조회 요청: {}", pageRequestDTO);
        // 최신순 정렬 (regDate 기준 내림차순)
        Pageable pageable = pageRequestDTO.getPageable();
        // deleted = false 인 게시글만 조회
        Page<Board> result = boardRepository.findAll(pageable);

        return new PageResponseDTO<>(result, this::entityToListDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BoardListDTO, Board> getMyList(PageRequestDTO pageRequestDTO, String writerId) {
        log.info("내 게시글 목록 조회 요청: Writer ID={}, {}", writerId, pageRequestDTO);

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new EntityNotFoundException("작성자(ID: " + writerId + ")를 찾을 수 없습니다."));
        // 정렬 기준 설정 (예: 최신순 regDate 기준 내림차순)
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("regDate").descending());

        // 🚨 Repository 메서드 호출: writerId와 deleted=false인 게시글만 조회
        Page<Board> result = boardRepository.findAllByWriter_IdAndDeletedFalse(writerId, pageable);

        Function<Board, BoardListDTO> fn = (entity -> BoardListDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .userStyle(entity.getUserStyle())
                .mainImageUrl(entity.getMainImageUrl())
                // ⭐ 수정: writer 필드에서 닉네임을 가져옵니다.
                .userNickname(entity.getWriter().getNickname())
                .regDate(entity.getRegDate())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .build());

        // PageResponseDTO 생성 및 반환
        return new PageResponseDTO<>(result, fn);
    }

    /**
     * 특정 ID의 게시글 상세 정보를 조회합니다. (BoardDTO 사용)
     */
    @Override
    @Transactional
    public BoardDTO get(String id) {
        Optional<Board> result = boardRepository.findByIdAndDeletedFalse(id);

        Board board = result.orElseThrow(() -> new EntityNotFoundException("해당 게시물(ID: " + id + ")을 찾을 수 없습니다."));

        // 조회수 증가 로직 (오류 발생 부분 수정)
        // Board 엔티티에 increaseViewCount() 메서드가 정의되어 있어야 합니다.
        board.increaseViewCount();
        log.info("게시글 조회수 증가: ID={}, New Count={}", id, board.getViewCount());

        return entityToDto(board);
    }

    /**
     * 게시글의 제목, 내용, 스타일, 이미지 URL을 수정합니다. (BoardModifyDTO 사용)
     */
//    @Override
//    @Transactional
//    public void modify(String id, BoardModifyDTO modifyDTO) {
//        Board entity = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException
//                ("수정할 게시물(ID :" + id + ")을 찾을 수 없습니다."));
//
//        // [추가] modifyDTO.getModifierId()를 사용하여 권한 체크 로직을 여기에 추가해야 합니다.
//        // 예: if (!entity.getWriter().getId().equals(modifyDTO.getModifierId())) throw new SecurityException("수정 권한이 없습니다.");
//
//        // 수정할 필드만 업데이트
//        entity.setTitle(modifyDTO.getTitle());
//        entity.setContent(modifyDTO.getContent());
//        entity.setMainImageUrl(modifyDTO.getMainImageUrl());
//        log.info("게시글 수정 완료: ID={}", id);
//
//        // @Transactional에 의해 변경 감지(Dirty Checking)로 자동 저장됩니다.
//    }

    /**
     * 게시글을 소프트 삭제합니다.
     */
    @Override
    @Transactional
    public void remove(String id, String removerId) {
        Board entity = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException
                ("삭제할 게시물(ID :" + id + ")을 찾을 수 없습니다."));

        if (!entity.getWriter().getId().equals(removerId)) {
            throw new SecurityException("삭제 권한이 없습니다. (작성자 ID 불일치)");
        }

        if (entity.isDeleted()) {
            throw new EntityNotFoundException("이미 삭제된 게시물(ID: " + id + ")입니다.");
        }

        entity.setDeleted(true); // 소프트 삭제 처리
        log.info("게시글 소프트 삭제 완료: ID={}", id);
    }

}