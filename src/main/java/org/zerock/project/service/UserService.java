package org.zerock.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.project.dto.LoginDTO;
import org.zerock.project.dto.LoginResponseDTO;
import org.zerock.project.dto.UserDTO;
import org.zerock.project.entity.User;
import org.zerock.project.entity.VerificationCode;
import org.zerock.project.repository.UserRepository;
import org.zerock.project.repository.VerificationCodeRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    private User dtoToEntity(UserDTO dto) {
        return User.builder()
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .age(dto.getAge())
                .activated(true)
                .build();
    }

    private UserDTO entityToDto(User entity) {
        return UserDTO.builder()
                .userId(entity.getUserId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .gender(entity.getGender())
                .age(entity.getAge())
                .build();
    }

    @Transactional
    public void registerRequest(UserDTO dto) {
        if (userRepository.existsById(dto.getUserId())) {
            throw new RuntimeException("이미 존재하는 ID입니다.");
        }
        User entity = dtoToEntity(dto);
        entity.setActivated(false);
        entity.encryptPassword(passwordEncoder);
        userRepository.save(entity);

        String verificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        VerificationCode codeEntity = VerificationCode.builder()
                .userId(dto.getUserId())
                .code(verificationCode)
                .expiryDate(expiryTime)
                .build();
        verificationCodeRepository.save(codeEntity);

        emailService.sendVerificationEmail(dto.getEmail(), verificationCode);

    }

    @Transactional
    public void verifyEmail(String userId, String code) {
        LocalDateTime now = LocalDateTime.now();

        Optional<VerificationCode> result = verificationCodeRepository
                .findByUserIdAndCodeAndExpiryDateAfterAndVerifiedFalse(userId, code, now);

        if(result.isEmpty()) {
            throw new RuntimeException("유효하지 않거나 만료된 인증 코드입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("인증 할 사용자를 찾을 수 없습니다."));
        user.setActivated(true);
        userRepository.save(user);

        result.get().markVerified();
        verificationCodeRepository.save(result.get());
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO authenticate(LoginDTO dto) {
        User user = userRepository.findByUserIdAndActivatedTrue(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = "FAKE_JWT_TOKEN_FOR_" + user.getUserId();

        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .token(token)
                .build();
    }

    @Transactional
    public UserDTO modify(String userId, UserDTO dto) {
        User user = userRepository.findByUserIdAndActivatedTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("수정할 사용자를 찾을 수 없습니다."));

        if (dto.getNickname() != null) {
            user.changeNickname(dto.getNickname());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
        return entityToDto(user);
    }

    @Transactional
    public void saveVerificationCode(String email, String code) {

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent() && existingUser.get().isActivated()) {
            throw new IllegalStateException("이미 가입된(활성화된) 이메일 주소입니다.");
        }

        User user = existingUser.orElse(
                User.builder().email(email).build()
        );

        user.updateVerificationCode(code, LocalDateTime.now().plusMinutes(5));

        if (existingUser.isEmpty()) {
            userRepository.save(user);
        }

        log.info("인증 코드 저장/업데이트 완료: 이메일={}, 코드={}", email, code);
    }

    @Transactional
    public boolean verifyCodeAndActivate(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일로 요청된 인증 정보를 찾을 수 없습니다."));

        if (user.getCodeExpiryDate() == null || user.getCodeExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!user.getVerificationCode().equals(code)) {
            return false;
        }

        user.setIsVerified(true);
        user.updateVerificationCode(null, null);
        log.info("이메일 인증 최종 성공: 이메일={}", email);
        return true;
    }

    @Transactional
    public UserDTO register(UserDTO dto) {

        userRepository.findByUserId(dto.getUserId()).ifPresent(user -> {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        });

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalStateException("인증 요청된 이메일 정보를 찾을 수 없습니다."));

        if (!user.isVerified()) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        user.setUserId(dto.getUserId());
        user.setNickname(dto.getNickname());
        user.setGender(dto.getGender());
        user.setAge(dto.getAge());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActivated(true); // 사용자 활성화

        log.info("최종 회원가입 및 활성화 완료: userId={}", user.getUserId());

        return entityToDto(user);
    }

    @Transactional
    public void withdraw(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("탈퇴할 사용자를 찾을 수 없습니다."));

        user.deactivate();
    }
}
