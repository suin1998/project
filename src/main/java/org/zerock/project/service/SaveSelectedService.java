package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.project.entity.AiCoordi;
import org.zerock.project.entity.User;
import org.zerock.project.repository.AiCoordiRepository;
import org.zerock.project.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveSelectedService {

    private final AiCoordiRepository aiCoordiRepository;
    private final UserRepository userRepository;

    public void saveSelectedImage(
            String user_Id,
            String aiId,
            List<String> tags,
            List<String> imageUrl,
            LocalDate targetDate,
            String content
    ){
        User user = userRepository.findById(user_Id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        AiCoordi aiCoordi = AiCoordi.builder()
                .ai_id(aiId)
                .userId(user)
                .tags(tags)
                .targetDate(targetDate)
                .aiImage_url(imageUrl)
                .content(content)
                .build();

        aiCoordiRepository.save(aiCoordi);

    }

}
