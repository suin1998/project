package org.zerock.project.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.WeatherRequestDto;
import org.zerock.project.dto.WeatherResponseDto;
import org.zerock.project.service.WeatherService;

@RestController
@RequestMapping("/main/AI")
@RequiredArgsConstructor
@Log4j2

public class WeatherController {
    private final WeatherService weatherService;

   @GetMapping
    public ResponseEntity<WeatherResponseDto> getWeatherForecast(@ModelAttribute WeatherRequestDto request) {

       log.info("날씨 조회 요청: {}", request);

       WeatherResponseDto response = weatherService.getWeather(request);
       // 3. 응답 반환
       if (response != null) {
           return ResponseEntity.ok(response);
       } else {
           // API 호출 실패 또는 데이터 없는 경우
           return ResponseEntity.status(500).build();
       }
   }

}
