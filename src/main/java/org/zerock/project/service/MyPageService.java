package org.zerock.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.project.dto.AuthResponseDTO;
import org.zerock.project.dto.BoardDTO;
import org.zerock.project.dto.MyPageResponseDTO;
import org.zerock.project.dto.MyPageUpdateRequestDTO;
import org.zerock.project.entity.Board;
import org.zerock.project.entity.User;
import org.zerock.project.repository.BoardRepository;
import org.zerock.project.repository.ClosetRepository;
import org.zerock.project.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ClosetRepository closetRepository;

    // ------------------------------------------------------------------------------------------------
    // 마이페이지 조회
    // ------------------------------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public MyPageResponseDTO getMyPageInfo(String userId) {

        // 1) 사용자 로드
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자(ID: " + userId + ") 를 찾을 수 없습니다."));

        // 2) UserInfo DTO 변환
        AuthResponseDTO.UserInfo userInfo = userToUserInfoDTO(user);

        // 3) 사용자가 작성한 게시글 조회
        List<Board> myBoards = boardRepository.findByUserIdAndDeletedFalse(userId);
        List<BoardDTO> myBoardDTOs = myBoards.stream()
                .map(this::boardEntityToDto)
                .collect(Collectors.toList());

        // 4) 옷장 아이템 개수 조회
        Long totalClosetItems = closetRepository.countByUser(user);

        // 5) 반환
        return MyPageResponseDTO.builder()
                .userInfo(userInfo)
                .myBoardPosts(myBoardDTOs)
                .totalClosetItems(totalClosetItems)
                .build();
    }


    // ------------------------------------------------------------------------------------------------
    // 사용자 정보 수정
    // ------------------------------------------------------------------------------------------------

    @Transactional
    public AuthResponseDTO.UserInfo updateUserInfo(String userId, MyPageUpdateRequestDTO updateDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자(ID: " + userId + ") 를 찾을 수 없습니다."));

        // 닉네임 변경
        if (updateDto.getNickname() != null && !updateDto.getNickname().isBlank()
                && !user.getNickname().equals(updateDto.getNickname())) {

            if (userRepository.existsByNickname(updateDto.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }

            user.setNickname(updateDto.getNickname());
        }

        return userToUserInfoDTO(user);
    }

    // ------------------------------------------------------------------------------------------------
    // 변환 메서드 (Entity → DTO)
    // ------------------------------------------------------------------------------------------------

    private AuthResponseDTO.UserInfo userToUserInfoDTO(User user) {
        return AuthResponseDTO.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .gender(user.getGender().toString())
                .emailVerified(user.getEmailVerified())
                .build();
    }

    private BoardDTO boardEntityToDto(Board entity) {
        return BoardDTO.builder()
                .id(entity.getId())  // String ID 그대로 넣음
                .title(entity.getTitle())
                .content(entity.getContent())
                .userStyle(entity.getUserStyle())
                .regDate(entity.getRegDate())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .dislikeCount(entity.getDislikeCount())
                .deleted(entity.isDeleted())
                .build();
    }
}
