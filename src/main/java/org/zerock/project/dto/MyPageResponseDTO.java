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

    private List<ClosetResponseDTO> closetItems;

    private Long totalClosetItems; // 옷장 아이템 개수 (추가됨)
}
