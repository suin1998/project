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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClosetService {

    private final ClosetRepository closetRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService; // 이미지 저장용 서비스

    // ---------------- 등록 ----------------
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
                .id(dto.getUserId())
                .user(user)
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : imageUrl)
                .color(dto.getColor())
                .brand(dto.getBrand())
                .tags(dto.getTags())
                .build();

        Closet saved = closetRepository.save(closet);
        return toDTO(saved);
    }

    // ---------------- 단일 카테고리 조회 ----------------
    public List<ClosetResponseDTO> getCloset(String userId, Category category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUserAndCategory(user, category)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---------------- 전체 그룹 조회 ----------------
    public Map<Category, List<ClosetResponseDTO>> getGroupedCloset(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUser(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.groupingBy(ClosetResponseDTO::getCategory));
    }

    // ---------------- 수정 ----------------
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

    // ---------------- 태그 검색 ----------------
    public List<ClosetResponseDTO> searchByTags(String userId, List<String> tags) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return closetRepository.findByUserAndTagsIn(user, tags)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ---------------- DTO 변환 ----------------
    private ClosetResponseDTO toDTO(Closet closet) {
        return ClosetResponseDTO.builder()
                .id(closet.getId())
                .userId(closet.getUser().getUserId())
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
