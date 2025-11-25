package org.zerock.project.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private static final String FROM_EMAIL = "ramalstyle1534@gmail.com";

    public void sendVerificationEmail(String toEmail, String code) {
        String subject = "[나만의 옷장 만들기] 회원가입 이메일 인증 코드";
        String content = "안녕하세요. 나만의 옷장 만들기에 가입해 주셔서 감사합니다.<br><br>"
                + "회원님의 **이메일 인증 코드**는 다음과 같습니다.<br>"
                + "<h2 style='color: #007bff;'>" + code + "</h2><br>"
                + "이 코드를 인증 화면에 입력하여 회원가입을 완료해 주세요.";

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);

        } catch (Exception e) {
            System.err.println("이메일 전송 실패: " + e.getMessage());
            throw new RuntimeException("이메일 전송에 실패했습니다. 이메일 주소를 확인해 주세요.", e);
        }
    }
}
