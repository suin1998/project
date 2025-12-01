package org.zerock.project.dto;

import lombok.Data;

@Data
public class MyPageUpdateRequestDTO {

    private String nickname;
    private String newPassword;
    private String password;
    private String email;
}