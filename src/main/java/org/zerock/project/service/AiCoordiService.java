package org.zerock.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zerock.project.dto.OutfitRequestDto;
import org.zerock.project.dto.OutfitResponseDto;
import org.zerock.project.dto.WeatherRequestDto;
import org.zerock.project.dto.WeatherResponseDto;
import com.nimbusds.openid.connect.sdk.claims.Gender;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Log4j2

public class AiCoordiService {

    @Value("${gemini.api.key}")
    private String aiKey;
    @Value("${gemini.api.url")
    private String ai_api_url;


    public OutfitResponseDto getAiCoordi(OutfitRequestDto outfitRequestDto){

        WeatherRequestDto weatherReq = outfitRequestDto.getWeatherRequestDto();
        WeatherResponseDto weatherResp = outfitRequestDto.getWeatherResponseDto();

        String sido = weatherReq.getSiDo();
        String sigungu = weatherReq.getSiGunGu();
        String dong = weatherReq.getDong();
        LocalDate targetDate = weatherReq.getInputDate();

        Double tMax = weatherResp.getTempMax();
        Double tMin = weatherResp.getTempMin();
        Double pop = weatherResp.getRainProbability();

        Gender userGender = outfitRequestDto.getGender();
        Double age = outfitRequestDto.getUserAge();
//        String fashion = outfitRequestDto.getFashion();
//        String tempstyle = outfitRequestDto.getTempStyle();
        String tpo = outfitRequestDto.getTpo();
//        String cloth = outfitRequestDto.getCloths();

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("당신은 전문 패션 스타일리스트 입니다. 다음 조건을 충족하는 코디를 추천해주세요.\n");

        promptBuilder.append("---[날씨 조건]---\n");
        promptBuilder.append("날짜: ").append(targetDate + "\n");
        promptBuilder.append("지역: ").append(sido+" ").append(sigungu+" ").append(dong+"\n");
        promptBuilder.append("최고기온: ").append(tMax+"°C, ").append("최저기온: ").append(tMin+"°C\n");
        promptBuilder.append("강수확률: ").append(pop).append("%.\n");

        promptBuilder.append("---[사용자 조건]---\n");
        promptBuilder.append("사용자 성별: ").append(userGender).append(", 연령: ").append(age+"\n");
//        promptBuilder.append("요청 스타일: ").append(fashion).append("느낌: ").append(tempstyle+"\n");
        promptBuilder.append("상황(TPO): ").append(tpo).append("\n");

        
        return null;
    }






}
