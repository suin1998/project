package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.project.dto.LoginRequestDTO;
import org.zerock.project.entity.User;
import org.zerock.project.exception.InvalidCredentialsException;
import org.zerock.project.exception.UserAlreadyExistsException;
import org.zerock.project.repository.UserRepository;
import org.zerock.project.dto.SignupRequestDTO;
import org.zerock.project.dto.AuthResponseDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;


    @Transactional
    public String verifyCode(String email, String code) {
        String savedCode = emailService.getCode(email);

        if (savedCode == null) {
            throw new IllegalArgumentException("인증 코드가 존재하지 않습니다.");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        // 인증 완료 → 코드 삭제
        emailService.deleteCode(email);

        return "이메일 인증이 완료되었습니다!";
    }


    @Transactional
    public AuthResponseDTO signup(SignupRequestDTO request) {
        log.info("Signup attempt for username: {}", request.getUsername());

        // 1. 비밀번호 확인 검증
        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 아이디 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("이미 사용 중인 아이디입니다.");
        }

        // 3. 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("이미 사용 중인 이메일입니다.");
        }

        // 4. 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserAlreadyExistsException("이미 사용 중인 닉네임입니다.");
        }

        // 5. 이메일 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();

        // 6. 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .role(User.UserRole.USER)
                .emailVerified(false)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        // 이메일 인증 토큰 설정 (24시간)
        user.setEmailVerificationToken(verificationToken, 24);

        // 7. 사용자 저장
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {} ({})", savedUser.getUsername(), savedUser.getEmail());

        // 8. 이메일 인증 메일 발송
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            // 이메일 발송 실패해도 회원가입은 완료
        }

        // 9. JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(savedUser.getUsername());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());

        // 10. 응답 생성
        AuthResponseDTO.UserInfo userInfo = AuthResponseDTO.UserInfo.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .birthDate(savedUser.getBirthDate())
                .gender(savedUser.getGender().name())
                .emailVerified(savedUser.getEmailVerified())
                .age(savedUser.getAge())
                .build();

        return AuthResponseDTO.success(
                "회원가입이 완료되었습니다. 이메일을 확인하여 인증을 완료해주세요.",
                accessToken,
                refreshToken,
                userInfo
        );
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // 1. 사용자 조회
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 2. 계정 활성화 확인
        if (!user.getEnabled()) {
            throw new InvalidCredentialsException("비활성화된 계정입니다.");
        }

        // 3. 계정 잠금 확인
        if (!user.getAccountNonLocked()) {
            throw new InvalidCredentialsException("잠긴 계정입니다. 관리자에게 문의하세요.");
        }

        // 4. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 5. 마지막 로그인 시간 업데이트
        user.updateLastLogin();
        userRepository.save(user);

        // 6. JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        log.info("User logged in successfully: {}", user.getUsername());

        // 7. 응답 생성
        AuthResponseDTO.UserInfo userInfo = AuthResponseDTO.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .gender(user.getGender().name())
                .emailVerified(user.getEmailVerified())
                .age(user.getAge())
                .build();

        return AuthResponseDTO.success(
                "로그인에 성공했습니다.",
                accessToken,
                refreshToken,
                userInfo
        );
    }

    @Transactional
    public String resendVerificationEmail(String username) {
        log.info("Resend verification email request for: {}", username);

        // 1. 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 이미 인증된 경우
        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("이미 인증된 이메일입니다.");
        }

        // 3. 새로운 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken, 24);
        userRepository.save(user);

        // 4. 인증 메일 발송
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        log.info("Verification email resent to: {}", username);

        return "인증 메일이 재발송되었습니다.";
    }

    @Transactional
    public void deleteUser(String userId) {
        log.info("User deletion request for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}