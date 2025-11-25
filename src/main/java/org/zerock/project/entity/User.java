package org.zerock.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Builder.Default
    private boolean activated = true;

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void changeGender(String newGender) {
        this.gender = newGender;
    }

    public void changeAge(Integer newAge) {
        this.age = newAge;
    }

    public void deactivate() {
        this.activated = false;
    }
}
