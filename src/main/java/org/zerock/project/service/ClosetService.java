package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClosetService {

    private final ClosetRepository closetRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService; // 이미지 저장용 서비스

    // 등록 (DTO + MultipartFile 받아서 Closet 엔티티 생성, 이미지 저장 서비스 호출 후 URL 세팅, User 존재 여부 확인 후 저장)
    public ClosetResponseDTO save(ClosetRequestDTO dto, MultipartFile image) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = fileStorageService.storeFile(image); // 이미지 저장 후 URL 반환
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        Closet closet = Closet.builder()
                .user(user)
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : imageUrl)
                .color(dto.getColor())
                .brand(dto.getBrand())
                .tags(dto.getTags() != null ? dto.getTags() : List.of())
                .build();

        Closet saved = closetRepository.save(closet);
        return toDTO(saved);
    }

    // 카테고리별 조회 (로그인 사용자(userId) + Category 기준 조회, DTO 변환 후 리스트 반환)
    public List<ClosetResponseDTO> getCloset(String userId, Category category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUserAndCategory(user, category)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 전체 그룹 조회 (로그인 사용자 기준 전체 Closet 조회, 카테고리별 그룹화 후 Map반환)
    public Map<Category, List<ClosetResponseDTO>> getGroupedCloset(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUser(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.groupingBy(ClosetResponseDTO::getCategory));
    }

    // 수정 (closetId 기준 Closet 조회, 이미지가 존재하면 저장, 아니면 DTO에 있는 URL 적용, 색상.브랜드.카테고리.태그 갱신, 수정 시간 업데이트 후 저장)
    public ClosetResponseDTO update(String closetId, ClosetRequestDTO dto, MultipartFile image) {
        Closet closet = closetRepository.findById(closetId)
                .orElseThrow(() -> new RuntimeException("Closet not found"));

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = fileStorageService.storeFile(image);
                closet.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        } else if (dto.getImageUrl() != null) {
            closet.setImageUrl(dto.getImageUrl());
        }

        closet.setCategory(dto.getCategory());
        closet.setColor(dto.getColor());
        closet.setBrand(dto.getBrand());
        closet.setTags(dto.getTags());
        closet.setUpdatedAt(LocalDateTime.now());

        Closet updated = closetRepository.save(closet);
        return toDTO(updated);
    }

    // 태그 검색 (로그인 사용자 + 태그 리스트 기준 검색)
    public List<ClosetResponseDTO> searchByTags(String userId, List<String> tags) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUserAndTagsIn(user, tags)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // DTO 변환
    private ClosetResponseDTO toDTO(Closet closet) {
        return ClosetResponseDTO.builder()
                .id(closet.getId())
                .userId(closet.getUser().getId())
                .category(closet.getCategory())
                .imageUrl(closet.getImageUrl())
                .color(closet.getColor())
                .brand(closet.getBrand())
                .tags(closet.getTags())
                .createdAt(closet.getCreatedAt())
                .updatedAt(closet.getUpdatedAt())
                .build();
    }
}
