//package org.zerock.project.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.zerock.project.entity.Category;
//import org.zerock.project.dto.ClosetRequestDTO;
//import org.zerock.project.dto.ClosetResponseDTO;
//import org.zerock.project.service.ClosetService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/closet")
//@RequiredArgsConstructor
//public class ClosetController {
//
//    private final ClosetService closetService;
//
//    // 옷 등록
//    @PostMapping
//    public ResponseEntity<ClosetResponseDTO> uploadClothes(@RequestBody ClosetRequestDTO dto) {
//        return ResponseEntity.ok(closetService.save(dto));
//    }
//    // 유저 + 카테고리 조회
//    @GetMapping("/{userId}/{category}")
//    public ResponseEntity<List<ClosetResponseDTO>> getCloset(
//            @PathVariable Long userId,
//            @PathVariable Category category) {
//        return ResponseEntity.ok(closetService.getCloset(userId, category));
//    }
//    // 카테고리 별 그룹 조회
//    @GetMapping("/group/{userId}")
//    public ResponseEntity<Map<Category, List<ClosetResponseDTO>>> getGroupedCloset(
//            @PathVariable Long userId) {
//        return ResponseEntity.ok(closetService.getGroupedCloset(userId));
//    }
//    // 옷 수정
//    @PutMapping("/{closetId}")
//    public ResponseEntity<ClosetResponseDTO> updateCloset(
//            @PathVariable Long closetId,
//            @RequestBody ClosetRequestDTO dto) {
//        return ResponseEntity.ok(closetService.update(closetId, dto));
//    }
//    // 태그 검색
//    @GetMapping("/search/tags")
//    public ResponseEntity<List<ClosetResponseDTO>> searchByTags(
//            @RequestParam Long userId,
//            @RequestParam List<String> tags) {
//        return ResponseEntity.ok(closetService.searchByTags(userId, tags));
//    }
//}
//
//
