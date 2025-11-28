package org.zerock.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.AuthResponseDTO;
import org.zerock.project.dto.LoginRequestDTO;
import org.zerock.project.dto.SignupRequestDTO;
import org.zerock.project.dto.VerificationRequestDTO;
import org.zerock.project.service.EmailService;
import org.zerock.project.service.UserService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    /**
     * 인증번호 발송
     */
    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(
            @RequestBody Map<String, String> request) {

        log.info("=== Send Verification Code Request ===");

        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "이메일을 입력해주세요."
            ));
        }

        try {
            emailService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "인증 코드가 이메일로 발송되었습니다."
            ));
        } catch (Exception e) {
            log.error("Failed to send verification code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "인증 코드 발송에 실패했습니다."
            ));
        }
    }

    /**
     * 인증번호 확인
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequestDTO request) {

        String message = userService.verifyCode(request.getEmail(), request.getCode());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message
        ));
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@Valid @RequestBody SignupRequestDTO request) {

        log.info("=== Signup Request ===");
        AuthResponseDTO responseDTO = userService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        log.info("=== Login Request ===");
        AuthResponseDTO response = userService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {

        log.info("=== User Deletion Request ===");
        userService.deleteUser(userId);

        return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "회원 탈퇴가 완료되었습니다."
        ));
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AI Fashion Auth Service",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
