package org.zerock.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.project.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {
    private String id;
    private String email;
    private String nickname;
    private String role;
    private String regDate;

    public static UserListDTO fromEntity(User user) {

        String creationDate = (user.getCreatedAt() != null)
                ? user.getCreatedAt().toString().substring(0, 10)
                : "";

        return UserListDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole() != null ? user.getRole().name() : "USER")
                .regDate(creationDate)
                .build();
    }
}