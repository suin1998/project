package org.zerock.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO {

    private Boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String id;
        private String username;
        private String email;
        private String nickname;
        private LocalDate birthDate;
        private String gender;
        private Boolean emailVerified;
        private Integer age;
    }

    // 성공 응답 생성
    public static AuthResponseDTO success(String message, String accessToken, String refreshToken, UserInfo user) {
        return AuthResponseDTO.builder()
                .success(true)
                .message(message)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

    // 실패 응답 생성
    public static AuthResponseDTO failure(String message) {
        return AuthResponseDTO.builder()
                .success(false)
                .message(message)
                .build();
    }
}