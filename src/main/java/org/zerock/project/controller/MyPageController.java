package org.zerock.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.MyPageResponseDTO;
import org.zerock.project.dto.MyPageUpdateRequestDTO;
import org.zerock.project.service.MyPageService;

@RestController
@RequestMapping("/api/myPage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 전체 데이터 조회 API
    @GetMapping
    public ResponseEntity<MyPageResponseDTO> getMyPageInfo(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String userId = authentication.getName();
        MyPageResponseDTO data = myPageService.getMyPageInfo(userId);

        return ResponseEntity.ok(data);
    }

    // 마이페이지 정보 수정 API
    @PutMapping
    public ResponseEntity<?> updateUserInfo(
            Authentication authentication,
            @Valid @RequestBody MyPageUpdateRequestDTO updateDto,
            BindingResult bindingResult) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        String userId = authentication.getName();
        var updated = myPageService.updateUserInfo(userId, updateDto);

        return ResponseEntity.ok(updated);
    }
}