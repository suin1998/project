package org.zerock.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String userId;

    private String password;
    private String nickname;
    private String email;
    private String username;
    private String gender;
    private Integer age;
    private boolean isActivated;
    private boolean isVerified;
    private String verificationCode;
    private LocalDateTime codeExpiryDate;

    @Builder.Default
    private boolean activated = true;

    public void updateVerificationCode(String code, LocalDateTime expiryDate) {
        this.verificationCode = code;
        this.codeExpiryDate = expiryDate;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void deactivate() {
        this.activated = false;
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
