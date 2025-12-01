//package org.zerock.project.service;
//
//import org.zerock.project.entity.Category;
//import org.zerock.project.entity.Closet;
//import org.zerock.project.dto.ClosetRequestDTO;
//import org.zerock.project.dto.ClosetResponseDTO;
//import org.zerock.project.repository.ClosetRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ClosetService {
//
//    private final ClosetRepository closetRepository;
//
//    // 등록
//    public ClosetResponseDTO save(ClosetRequestDTO dto) {
//        Closet closet = Closet.builder()
//                .userId(dto.getUserId())
//                .category(dto.getCategory())
//                .imageUrl(dto.getImageUrl())
//                .color(dto.getColor())
//                .brand(dto.getBrand())
//                .tags(dto.getTags())
//                .build();
//
//        Closet saved =  closetRepository.save(closet);
//        return toDTO(saved);
//    }
//    // 단일 유저 + 카테고리 조합
//    public List<ClosetResponseDTO> getCloset(Long userId, Category category) {
//        return closetRepository.findByUserIdAndCategory(userId, category)
//                .stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//    // 유저 전체 카테고리별 그룹 조회
//    public Map<Category, List<ClosetResponseDTO>> getGroupedCloset(Long userId) {
//        return closetRepository.findByUserId(userId)
//                .stream()
//                .map(this::toDTO)
//                .collect(Collectors.groupingBy(ClosetResponseDTO::getCategory));
//    }
//
//    // 수정
//    public ClosetResponseDTO update(Long id, ClosetRequestDTO dto) {
//        Closet closet = closetRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Closet not found"));
//
//        closet.setCategory(dto.getCategory());
//        closet.setImageUrl(dto.getImageUrl());
//        closet.setColor(dto.getColor());
//        closet.setBrand(dto.getBrand());
//        closet.setTags(dto.getTags());
//
//        Closet updated = closetRepository.save(closet);
//        return toDTO(updated);
//    }
//    // 태그 검색
//    public List<ClosetResponseDTO> searchByTags(Long userId, List<String> tags) {
//        return closetRepository.findByUserIdAndTagsIn(userId, tags)
//                .stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//    // DTO 변환
//    private ClosetResponseDTO toDTO(Closet closet) {
//        return ClosetResponseDTO.builder()
//                .id(closet.getId())
//                .userId(closet.getUserId())
//                .category(closet.getCategory())
//                .imageUrl(closet.getImageUrl())
//                .color(closet.getColor())
//                .brand(closet.getBrand())
//                .tags(closet.getTags())
//                .createdAt(closet.getCreatedAt())
//                .updatedAt(closet.getUpdatedAt())
//                .build();
//    }
//}
