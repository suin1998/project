package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    /**
     * 이메일 인증 메일 발송
     */
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String subject = "AI Fashion - 이메일 인증";
        String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=" + verificationToken;

        String message = String.format(
                "안녕하세요!\n\n" +
                        "AI Fashion 서비스에 가입해주셔서 감사합니다.\n\n" +
                        "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n" +
                        "%s\n\n" +
                        "이 링크는 24시간 동안 유효합니다.\n\n" +
                        "본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.\n\n" +
                        "감사합니다.\n",
                verificationUrl
        );

        sendSimpleEmail(toEmail, subject, message);
    }


    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "AI Fashion - 비밀번호 재설정";
        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;

        String message = String.format(
                "안녕하세요!\n\n" +
                        "비밀번호 재설정을 요청하셨습니다.\n\n" +
                        "아래 링크를 클릭하여 비밀번호를 재설정해주세요:\n" +
                        "%s\n\n" +
                        "이 링크는 1시간 동안 유효합니다.\n\n" +
                        "본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.\n\n" +
                        "감사합니다.\n",
                resetUrl
        );

        sendSimpleEmail(toEmail, subject, message);
    }

    private void sendSimpleEmail(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", toEmail, e);
        }
    }

    public void sendVerificationCode(String email) {

        // 6자리 랜덤 코드 생성
        String code = String.format("%06d", (int)(Math.random() * 1000000));
        verificationCodes.put(email, code);

        String subject = "AI Fashion - 이메일 인증번호";
        String message =
                "안녕하세요!\n\n" +
                        "요청하신 이메일 인증번호는 다음과 같습니다:\n\n" +
                        "인증번호: " + code + "\n\n" +
                        "해당 인증번호는 3분 동안만 유효합니다.\n\n" +
                        "감사합니다.";

        sendSimpleEmail(email, subject, message);
    }

    public String getCode(String email) {
        return verificationCodes.get(email);
    }

    public void deleteCode(String email) {
        verificationCodes.remove(email);
    }

    public boolean verifyCode(String email, String code) {

        String savedCode = verificationCodes.get(email);

        if (savedCode == null) {
            return false;  // 인증코드 없음
        }

        boolean isValid = savedCode.equals(code);

        if (isValid) {
            verificationCodes.remove(email);  // 1회용 코드 → 검증 후 삭제
        }

        return isValid;
    }

}