package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.project.dto.ClosetRequestDTO;
import org.zerock.project.dto.ClosetResponseDTO;
import org.zerock.project.entity.Category;
import org.zerock.project.entity.Closet;
import org.zerock.project.entity.User;
import org.zerock.project.repository.ClosetRepository;
import org.zerock.project.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosetService {

    private final ClosetRepository closetRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService; // 이미지 저장용 서비스

    // 등록: 로그인된 userId + 카테고리 + 이미지 파일만으로 저장
    public ClosetResponseDTO save(String userId, ClosetRequestDTO dto, MultipartFile image) {
        // 1) 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) 이미지 필수 체크
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("이미지 파일이 필요합니다.");
        }

        // 3) 이미지 저장 후 URL 얻기
        String imageUrl;
        try {
            imageUrl = fileStorageService.storeFile(image); // 이미지 저장 후 URL 반환
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }

        // 4) Closet 엔티티 생성 (카테고리 + 이미지 URL만)
        Closet closet = Closet.builder()
                .user(user)
                .category(dto.getCategory())
                .imageUrl(imageUrl)
                .build();

        Closet saved = closetRepository.save(closet);
        return toDTO(saved);
    }

    // 카테고리별 조회 (로그인 사용자(userId) + Category 기준 조회, DTO 변환 후 리스트 반환)
    @Transactional(readOnly = true)
    public List<ClosetResponseDTO> getCloset(String userId, Category category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUserAndCategory(user, category)
                .stream()
                .map(ClosetResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 전체 그룹 조회 (로그인 사용자 기준 전체 Closet 조회, 카테고리별 그룹화 후 Map반환)
    @Transactional(readOnly = true)
    public Map<Category, List<ClosetResponseDTO>> getGroupedCloset(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUser(user)
                .stream()
                .map(ClosetResponseDTO::fromEntity)
                .collect(Collectors.groupingBy(ClosetResponseDTO::getCategory));
    }

    // 수정: 이미지 새로 올리면 변경, 카테고리도 변경 가능
    public ClosetResponseDTO update(String closetId, ClosetRequestDTO dto, MultipartFile image) {
        Closet closet = closetRepository.findById(closetId)
                .orElseThrow(() -> new RuntimeException("Closet not found"));

        // 1) 새 이미지가 있으면 저장 후 URL 변경
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = fileStorageService.storeFile(image);
                closet.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        // 2) 카테고리 변경 (null이면 그대로 유지)
        if (dto.getCategory() != null) {
            closet.setCategory(dto.getCategory());
        }

        // @LastModifiedDate를 쓰지 않는다면 직접 수정 시간 관리
        closet.setUpdatedAt(LocalDateTime.now());

        Closet updated = closetRepository.save(closet);
        return toDTO(updated);
    }

    private ClosetResponseDTO toDTO(Closet closet) {
        return ClosetResponseDTO.builder()
                .id(closet.getId())
                .userId(closet.getUser().getId())
                .category(closet.getCategory())
                .imageUrl(closet.getImageUrl())
                .createdAt(closet.getCreatedAt())
                .updatedAt(closet.getUpdatedAt())
                .build();
    }

}
