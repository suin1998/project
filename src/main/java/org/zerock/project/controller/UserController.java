package org.zerock.project.controller;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.EmailCodeDTO;
import org.zerock.project.dto.LoginDTO;
import org.zerock.project.dto.LoginResponseDTO;
import org.zerock.project.dto.UserDTO;
import org.zerock.project.service.EmailService;
import org.zerock.project.service.UserService;
import org.zerock.project.util.VerificationCodeGenerator;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            return new ResponseEntity<>("이메일 주소가 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            String code = VerificationCodeGenerator.generateCode(6);
            userService.saveVerificationCode(email, code);
            emailService.sendVerificationEmail(email, code);

            log.info("인증 코드 발송 성공: 이메일={}", email);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (RuntimeException e) {
            log.error("인증 코드 발송 실패: 이메일={}, 오류={}", email, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody EmailCodeDTO dto) {
        if (dto.getEmail() == null || dto.getCode() == null) {
            return new ResponseEntity<>("이메일과 코드가 모두 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            boolean success = userService.verifyCodeAndActivate(dto.getEmail(), dto.getCode());

            if (success) {
                log.info("이메일 인증 성공: 이메일={}", dto.getEmail());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                log.warn("이메일 인증 실패: 이메일={}", dto.getEmail());
                return new ResponseEntity<>("인증 코드가 일치하지 않거나 만료되었습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (RuntimeException e) {
            log.error("인증 처리 중 오류 발생: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO dto) {
        try {
            UserDTO registeredUser = userService.register(dto);
            log.info("회원가입 성공: userId={}", registeredUser.getUserId());
            return new ResponseEntity<>(registeredUser, HttpStatus.OK);

        } catch (IllegalStateException e) {
            log.warn("회원가입 비즈니스 오류: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("회원가입 서버 오류: {}", e.getMessage());
            return new ResponseEntity<>("회원가입 중 알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PostMapping("/register-request")
//    public ResponseEntity<Void> registerRequest(@RequestBody UserDTO userDTO) {
//        try {
//            userService.registerRequest(userDTO);
//            return new ResponseEntity<>(HttpStatus.ACCEPTED);
//        } catch (RuntimeException e) {
//
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping("/verify")
//    public ResponseEntity<Void> verifyEmail(@RequestParam String userId, @RequestParam String code) {
//        try {
//            userService.verifyEmail(userId, code);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = userService.authenticate(loginDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserDTO> modifyUser(@PathVariable String userId, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.modify(userId, userDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (EntityNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> withdrawUser(@PathVariable String userId) {
        try {
            userService.withdraw(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
