//package org.zerock.project.controller;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.zerock.project.dto.AuthResponseDTO;
//import org.zerock.project.dto.MyPageResponseDTO;
//import org.zerock.project.dto.MyPageUpdateRequestDTO;
//import org.zerock.project.service.MyPageService;
//
//@Controller // 뷰 이름(String)을 반환하는 컨트롤러
//@RequestMapping({"/mypage", "/MyPage"}) // 기본 경로를 소문자로 설정
//@RequiredArgsConstructor
//@Slf4j
//public class MyPageController {
//
//    private final MyPageService myPageService;
//
//    /**
//     * [GET] 마이페이지 뷰 로드 및 데이터 전달 (SSR 방식)
//     * URL: /mypage
//     * @param authentication 현재 로그인 사용자 정보
//     * @param model 뷰로 전달할 데이터 객체
//     * @return 뷰 이름 (예: user/myPage.html)
//     */
//    @GetMapping
//    public String getMyPageView(Authentication authentication, Model model) {
//        log.info("GET /mypage 요청: 마이페이지 데이터 로드 시작");
//
//        // 1. 인증 확인
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login"; // 인증되지 않았다면 로그인 페이지로 리다이렉트
//        }
//
//        try {
//            // 2. Security Context에서 사용자 ID (String)를 가져옴
//            String userId = authentication.getName();
//
//            // 3. MyPageService를 통해 모든 데이터 로드
//            MyPageResponseDTO myPageData = myPageService.getMyPageInfo(userId);
//
//            // 4. 데이터를 모델에 담아 뷰로 전달 (myPageData 라는 이름으로 접근 가능)
//            model.addAttribute("myPageData", myPageData);
//
//            // 5. 뷰 이름 반환 (src/main/resources/templates/user/myPage.html을 찾게 됨)
//            return "user/mypage";
//
//        } catch (EntityNotFoundException e) {
//            log.error("마이페이지 로드 중 사용자 정보를 찾을 수 없음: {}", e.getMessage());
//            // 사용자 정보를 찾지 못하는 경우 에러 처리
//            return "redirect:/error";
//        } catch (Exception e) {
//            log.error("마이페이지 로드 중 예상치 못한 오류 발생", e);
//            return "redirect:/error";
//        }
//    }
//
//
//    /**
//     * [PUT] 사용자 정보 수정 (REST API 방식)
//     * URL: /mypage/update
//     * @param authentication 현재 로그인 사용자 정보
//     * @param updateDto 수정 요청 DTO (닉네임, 비밀번호 등)
//     * @return 수정된 사용자 정보 DTO (JSON)
//     */
//    @PutMapping("/update")
//    @ResponseBody // JSON 데이터를 반환하기 위해 @ResponseBody 사용
//    public ResponseEntity<?> updateUserInfo(
//            Authentication authentication,
//            @RequestBody MyPageUpdateRequestDTO updateDto) {
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
//        }
//
//        try {
//            String userId = authentication.getName();
//
//            AuthResponseDTO.UserInfo updatedInfo = myPageService.updateUserInfo(userId, updateDto);
//
//            return ResponseEntity.ok(updatedInfo);
//
//        } catch (IllegalArgumentException e) {
//            log.warn("사용자 정보 수정 실패: {}", e.getMessage());
//            // 닉네임 중복 등 비즈니스 로직 오류
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다.");
//        } catch (Exception e) {
//            log.error("사용자 정보 수정 중 예상치 못한 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
//        }
//    }
//}