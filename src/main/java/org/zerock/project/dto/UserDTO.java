package org.zerock.project.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String userId;
    private String password;
    private String nickname;
    private String email;
    private String username;
    private String gender;
    private Integer age;
}
