package org.zerock.project.dto;

import lombok.Builder;
import lombok.Data;
import org.zerock.project.dto.AuthResponseDTO.UserInfo;
import java.util.List;

@Data
@Builder
public class MyPageResponseDTO {

    private UserInfo userInfo;

    private List<BoardDTO> myBoardPosts;
}
