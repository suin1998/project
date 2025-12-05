package org.zerock.project.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.OutfitRequestDto;
import org.zerock.project.dto.OutfitResponseDto;
import org.zerock.project.entity.Closet;
import org.zerock.project.service.AiCoordiService;
import org.zerock.project.service.ClosetService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/AI")
public class AiCoordiController {
    private final AiCoordiService aiCoordiService;


    @PostMapping("/coordi")
    public ResponseEntity<OutfitResponseDto> generateAiCoordi(@ModelAttribute OutfitRequestDto outfitRequestDto) {
        try{
            OutfitResponseDto result = aiCoordiService.getAiCoordi(outfitRequestDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("AI 요청 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
