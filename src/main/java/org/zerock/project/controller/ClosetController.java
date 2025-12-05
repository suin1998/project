package org.zerock.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.project.dto.ClosetRequestDTO;
import org.zerock.project.dto.ClosetResponseDTO;
import org.zerock.project.entity.Category;
import org.zerock.project.service.ClosetService;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/closet")
@RequiredArgsConstructor
public class ClosetController {

    private final ClosetService closetService;

    /**
     * 옷 등록 (카테고리 + 이미지 파일만)
     * 프론트: FormData에
     *  - "data": { "category": "TOP" }
     *  - "image": (파일)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ClosetResponseDTO> uploadClothes(
            @RequestPart("data") ClosetRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) {
        String userId = principal.getName();
        ClosetResponseDTO response = closetService.save(userId, dto, image);
        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리별 옷 목록 조회
     * GET /api/closet/category/TOP
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ClosetResponseDTO>> getByCategory(
            @PathVariable Category category,
            Principal principal
    ) {
        String userId = principal.getName();
        return ResponseEntity.ok(closetService.getCloset(userId, category));
    }

    /**
     * 유저의 옷장을 카테고리별로 묶어서 조회
     * GET /api/closet/group
     */
    @GetMapping("/group")
    public ResponseEntity<Map<Category, List<ClosetResponseDTO>>> getGrouped(
            Principal principal
    ) {
        String userId = principal.getName();
        return ResponseEntity.ok(closetService.getGroupedCloset(userId));
    }

    /**
     * 카테고리 목록 (코드 + 한글 라벨) 조회
     * GET /api/closet/categories
     * 응답 예:
     * [
     *   { "code": "TOP", "label": "상의" },
     *   { "code": "BOTTOM", "label": "하의" },
     *   ...
     * ]
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> list = Arrays.stream(Category.values())
                .map(cat -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", cat.name());
                    map.put("label", cat.getLabel());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    /**
     * 옷 정보 수정 (카테고리/이미지 재설정)
     * PUT /api/closet/{closetId}
     */
    @PutMapping(value = "/{closetId}", consumes = "multipart/form-data")
    public ResponseEntity<ClosetResponseDTO> updateClothes(
            @PathVariable String closetId,
            @RequestPart("data") ClosetRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) {
        String userId = principal.getName();
        // 필요하면 이 userId로 소유자 검증까지 추가 가능
        ClosetResponseDTO response = closetService.update(closetId, dto, image);
        return ResponseEntity.ok(response);
    }
}
