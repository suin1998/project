package org.zerock.project.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.AuthResponseDTO;
import org.zerock.project.dto.MyPageResponseDTO;
import org.zerock.project.dto.MyPageUpdateRequestDTO;
import org.zerock.project.service.MyPageService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * [GET] /myCloset
     * SSR(서버 렌더링) 방식으로 myCloset.html을 보여주는 엔드포인트
     */
    @GetMapping
    public String getMyPageView(Authentication authentication, Model model, HttpServletRequest request) {
        log.info("GET /myCloset");

        // 인증 확인 (로그인 안 되어있으면 로그인 페이지로 이동)
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String userId = authentication.getName();

        try {
            // 1. 마이페이지 전체 데이터 조회
            MyPageResponseDTO myPageData = myPageService.getMyPageInfo(userId);

            // 2. 뷰로 전달
            model.addAttribute("myPageData", myPageData);

            // 3. templates/user/myCloset.html 렌더링
            return "user/myCloset";

        } catch (EntityNotFoundException e) {
            log.warn("마이페이지 로드 실패: 사용자 없음");
            return "error/404";
        } catch (Exception e) {
            log.error("마이페이지 로드 중 오류", e);
            return "error/500";
        }
    }


    /**
     * [PUT] /myCloset/update
     * 마이페이지 정보 수정 (REST API)
     */
    @PutMapping
    @ResponseBody
    public ResponseEntity<?> updateUserInfo(
            Authentication authentication,
            @Valid @RequestBody MyPageUpdateRequestDTO updateDto,
            BindingResult bindingResult) {

        // 인증되지 않았으면 401
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자입니다.");
        }

        // DTO 유효성 검사 실패 → 400 반환
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String userId = authentication.getName();

            // 서비스에 수정 요청
            AuthResponseDTO.UserInfo updatedInfo =
                    myPageService.updateUserInfo(userId, updateDto);

            return ResponseEntity.ok(updatedInfo);

        } catch (IllegalArgumentException e) {
            log.warn("수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("수정 처리 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }
}
