package org.zerock.project.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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

       if (request.getSiDo() == null || request.getInputDate() == null) {
           log.warn("날씨 조회 요청 실패: 필수 파라미터 (siDo 또는 inputDate) 누락");
           // 400 Bad Request 반환
           return ResponseEntity.badRequest().build();
       }
       log.info("날씨 조회 요청: {}", request);

       try{
           WeatherResponseDto response = weatherService.getWeather(request);
           if (response != null) {
               log.info("날씨 조회 성공: {}", response);
               return ResponseEntity.ok(response);
           } else {
               log.error("날씨 조회 실패: Service에서 null 응답 반환");
               return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content 또는 500
           }

       }catch (IllegalArgumentException e) {
           // Service 코드에서 throw new IllegalArgumentException("중기예보는 D+4~D+10일만 제공됩니다.") 등의 예외 처리
           log.error("날씨 조회 실패 (잘못된 요청): {}", e.getMessage());
           return ResponseEntity.badRequest().body(null); // 400 Bad Request
       } catch (Exception e) {
           // 그 외 서비스 내부 오류 (DB 연결, JSON 파싱 오류 등)
           log.error("날씨 조회 중 서버 오류 발생:", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
       }

       // 3. 응답 반환

   }

}
