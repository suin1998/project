package org.zerock.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.project.entity.Category;
import org.zerock.project.dto.ClosetRequestDTO;
import org.zerock.project.dto.ClosetResponseDTO;
import org.zerock.project.service.ClosetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // 🔑 추가
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/closet")
@RequiredArgsConstructor
public class ClosetController {

    private final ClosetService closetService;

    // 옷 등록 (Multipart/Form-data 방식)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ClosetResponseDTO> uploadClothes(
            @RequestPart("data") ClosetRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal // 🔑 로그인 사용자 정보
    ) {
        String userId = principal.getName(); // 로그인된 userId
        dto.setUserId(userId); // DTO에 userId 세팅
        return ResponseEntity.ok(closetService.save(dto, image));
    }

    // 로그인된 사용자 옷장 조회 (userId를 URL로 전달 X)
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ClosetResponseDTO>> getByCategory(
            @PathVariable Category category,
            Principal principal
    ) {
        String userId = principal.getName();
        return ResponseEntity.ok(closetService.getCloset(userId, category));
    }

    // 그룹 조회
    @GetMapping("/group")
    public ResponseEntity<Map<Category, List<ClosetResponseDTO>>> getGrouped(
            Principal principal
    ) {
        String userId = principal.getName();
        return ResponseEntity.ok(closetService.getGroupedCloset(userId));
    }

    // 옷 수정
    @PutMapping(value = "/{closetId}", consumes = "multipart/form-data")
    public ResponseEntity<ClosetResponseDTO> updateClothes(
            @PathVariable String closetId,
            @RequestPart("data") ClosetRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) {
        String userId = principal.getName();
        dto.setUserId(userId); // DTO에 userId 세팅
        return ResponseEntity.ok(closetService.update(closetId, dto, image));
    }

    // 태그 검색
    @GetMapping("/search/tags")
    public ResponseEntity<List<ClosetResponseDTO>> searchByTags(
            @RequestParam List<String> tags,
            Principal principal
    ) {
        String userId = principal.getName();
        return ResponseEntity.ok(closetService.searchByTags(userId, tags));
    }
}
