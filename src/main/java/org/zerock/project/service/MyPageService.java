//package org.zerock.project.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.zerock.project.dto.AuthResponseDTO;
//import org.zerock.project.dto.BoardDTO;
//import org.zerock.project.dto.MyPageResponseDTO;
//import org.zerock.project.dto.MyPageUpdateRequestDTO;
//import org.zerock.project.entity.Board;
//import org.zerock.project.entity.User;
//import org.zerock.project.repository.BoardRepository;
////import org.zerock.project.repository.ClosetRepository;
//import org.zerock.project.repository.UserRepository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MyPageService {
//
//    private final UserRepository userRepository;
//    private final BoardRepository boardRepository;
////    private final ClosetRepository closetRepository;
//
//    // ------------------------------------------------------------------------------------------------
//    // 마이페이지 데이터 조회
//    // ------------------------------------------------------------------------------------------------
//
//    /**
//     * 특정 사용자의 마이페이지 정보를 로드합니다.
//     * @param userId 사용자 ID (String)
//     * @return 마이페이지 응답 DTO
//     */
//    @Transactional(readOnly = true)
//    public MyPageResponseDTO getMyPageInfo(String userId) {
//
//        // 1. 사용자 정보 로드
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("사용자(ID: " + userId + ")를 찾을 수 없습니다."));
//
//        // 2. AuthResponseDTO.UserInfo로 변환
//        AuthResponseDTO.UserInfo userInfo = userToUserInfoDTO(user);
//
//        // 3. 사용자가 작성한 게시글 로드 (최신순 5개 등 Pageable 적용 가능)
//        // 여기서는 일단 모든 게시글을 가져오고, DTO로 변환
//        List<Board> myBoards = boardRepository.findByWriter_IdAndDeletedFalse(userId);
//        List<BoardDTO> myBoardDTOs = myBoards.stream()
//                .map(this::boardEntityToDto)
//                .collect(Collectors.toList());
//
//        // 4. 옷장 아이템 개수 로드 (ClosetRepository에 countByUser(User user) 메서드가 필요)
//        // Long totalClosetItems = closetRepository.countByUser(user);
//        // Repository에 해당 메서드가 정의되어 있지 않을 수 있으므로, 임시로 0L로 처리하거나 주석 처리합니다.
//        Long totalClosetItems = 0L; // 실제 구현 시: closetRepository.countByUser(user);
//
//        // 5. 최종 MyPageResponseDTO 생성 및 반환
//        return MyPageResponseDTO.builder()
//                .userInfo(userInfo)
//                .myBoardPosts(myBoardDTOs)
//                .build();
//    }
//
//    // ------------------------------------------------------------------------------------------------
//    // 사용자 정보 수정
//    // ------------------------------------------------------------------------------------------------
//
//    /**
//     * 마이페이지에서 사용자 정보를 수정합니다. (닉네임 등)
//     * @param userId 사용자 ID (String)
//     * @param updateDto 수정 요청 DTO
//     * @return 수정된 사용자 정보 DTO
//     */
//    @Transactional
//    public AuthResponseDTO.UserInfo updateUserInfo(String userId, MyPageUpdateRequestDTO updateDto) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("사용자(ID: " + userId + ")를 찾을 수 없습니다."));
//
//        // 닉네임 업데이트
//        if (updateDto.getNickname() != null && !updateDto.getNickname().isEmpty()
//                && !user.getNickname().equals(updateDto.getNickname())) {
//
//            // 닉네임 중복 체크
//            if(userRepository.existsByNickname(updateDto.getNickname())) {
//                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
//            }
//            user.setNickname(updateDto.getNickname());
//        }
//
//        // 비밀번호 업데이트 (실제로는 현재 비밀번호 검증 로직이 Controller나 Service에 필요합니다)
//        // if (updateDto.getNewPassword() != null) {
//        //     // user.setPassword(passwordEncoder.encode(updateDto.getNewPassword())); // PasswordEncoder 필요
//        // }
//
//        // @Transactional에 의해 커밋 시 변경사항이 자동 반영됩니다.
//        return userToUserInfoDTO(user);
//    }
//
//
//    // ------------------------------------------------------------------------------------------------
//    // 헬퍼 메서드 (Entity to DTO 변환)
//    // ------------------------------------------------------------------------------------------------
//
//    /**
//     * User 엔티티를 AuthResponseDTO.UserInfo로 변환
//     */
//    private AuthResponseDTO.UserInfo userToUserInfoDTO(User user) {
//        return AuthResponseDTO.UserInfo.builder()
//                .id(user.getId())
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .nickname(user.getNickname())
//                .birthDate(user.getBirthDate())
//                .gender(user.getGender().toString())
//                .emailVerified(user.getEmailVerified())
//                .build();
//    }
//
//    /**
//     * Board 엔티티를 BoardDTO로 변환
//     */
//    private BoardDTO boardEntityToDto(Board entity) {
//        // BoardDTO의 ID 필드명을 String id로 변경했다고 가정하고 id 필드에 String ID를 넣습니다.
//        return BoardDTO.builder()
//                .id(entity.getId()) // BoardDTO에 id 필드가 String 타입으로 존재한다고 가정
//                .userId(UserId)
//                .userNickname(entity.getUserNickname())
//                .title(entity.getTitle())
//                .content(entity.getContent())
//                .userStyle(entity.getUserStyle())
//                .regDate(entity.getRegDate())
//                .viewCount(entity.getViewCount())
//                .likeCount(entity.getLikeCount())
//                .dislikeCount(entity.getDislikeCount())
//                .deleted(entity.isDeleted())
//                .build();
//    }
//}