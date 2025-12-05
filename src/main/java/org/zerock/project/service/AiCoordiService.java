package org.zerock.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.project.dto.OutfitRequestDto;
import org.zerock.project.dto.OutfitResponseDto;
import org.zerock.project.dto.WeatherRequestDto;
import org.zerock.project.dto.WeatherResponseDto;

import org.zerock.project.entity.AiCoordi;
import org.zerock.project.entity.Closet;
import org.zerock.project.entity.User;
import org.zerock.project.entity.User.Gender;
import org.zerock.project.repository.AiCoordiRepository;
import org.zerock.project.repository.ClosetRepository;
import org.zerock.project.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
@Log4j2
public class AiCoordiService {

    private final AiCoordiRepository aiCoordiRepository;
    private final ClosetRepository closetRepository;
    private final UserRepository userRepository;

    @Value("${gemini.api.key}")
    private String aiKey;
    @Value("${gemini.api.url}")
    private String ai_api_url;
    @Value("{app.upload.dir}")
    private String upload_dir;
    @Value("${app.upload.url}")
    private String serverFileUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    public OutfitResponseDto getAiCoordi(OutfitRequestDto outfitRequestDto) throws Exception {

        User user = userRepository.findById(outfitRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Closet> userClothes = closetRepository.findByUser(user);

        String prompt = buildPrompt(outfitRequestDto, user);

        OutfitResponseDto result = getAiRecommend(prompt, userClothes);


        return  result;

    }


    private String buildPrompt(OutfitRequestDto outfitRequestDto, User user ) {
        WeatherRequestDto weatherReq = outfitRequestDto.getWeatherRequestDto();
        WeatherResponseDto weatherResp = outfitRequestDto.getWeatherResponseDto();

        String sido = weatherReq.getSiDo();
        String sigungu = weatherReq.getSiGunGu();
        String dong = weatherReq.getDong();
        LocalDate targetDate = weatherReq.getInputDate();

        Double tMax = weatherResp.getTempMax();
        Double tMin = weatherResp.getTempMin();
        Double pop = weatherResp.getRainProbability();

        Gender userGender = user.getGender();
        Integer age = user.getAge();
        List<String> fashionStyles = outfitRequestDto.getFashionStyle();
        List<String> tempStyles = outfitRequestDto.getTempStyle();
        String tpo = outfitRequestDto.getTpo();



        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("당신은 전문 패션 스타일리스트 입니다. 다음 조건을 충족하는 코디를 추천해주세요.\n");

        promptBuilder.append("---[날씨 조건]---\n");
        promptBuilder.append("날짜: ").append(targetDate + "\n");
        promptBuilder.append("지역: ").append(sido+" ").append(sigungu+" ").append(dong+"\n");
        promptBuilder.append("최고기온: ").append(tMax+"°C, ").append("최저기온: ").append(tMin+"°C\n");
        promptBuilder.append("강수확률: ").append(pop).append("%.\n");

        promptBuilder.append("---[사용자 조건]---\n");
        promptBuilder.append("사용자 성별: ").append(userGender).append(", 연령: ").append(age+"\n");
        promptBuilder.append("요청 스타일: ").append(String.join(", ", fashionStyles)).append(", 느낌: ").append(String.join(", ", tempStyles)).append(".\n");
        promptBuilder.append("상황(TPO): ").append(tpo).append("\n");
        promptBuilder.append("제공된 이미지들은 사용자의 옷장입니다. 이를 활용하여 코디 이미지를 2~3개 생성해주세요");

        promptBuilder.append("\n응답은 반드시 JSON 형식으로, 다음 구조를 따르세요.\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"recommendation\": \"전체 코디에 대한 한 줄 설명\",\n");
        promptBuilder.append("  \"reason\": \"날짜 정보 및 추천 이유 및 날씨 대응 전략 (간략하게 2~3 문장으로)\"\n");
        promptBuilder.append("}\n");

        String textPrompt = promptBuilder.toString();
        log.info("Gemini text prompt: {}", textPrompt);
        return textPrompt;
    }



    private OutfitResponseDto getAiRecommend(String prompt, List<Closet> closets) throws Exception {

        List<Map<String, Object>> imgParts = new ArrayList<>();

        for (Closet cloth : closets) {
            File file = new File(upload_dir + cloth.getImageUrl());
            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);

            imgParts.add(Map.of(
                    "inlineData",
                    Map.of("data", base64, "mimeType", "image/png")
            ));
        }

            // 요청 JSON 구성
        Map<String, Object> jsonBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", mergeParts(
                                            List.of(Map.of("text", prompt)),
                                            imgParts
                                )
                        )
                )

        );

        URL url = new URL(ai_api_url + aiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        conn.getOutputStream().write(mapper.writeValueAsBytes(jsonBody));

        JsonNode root = mapper.readTree(conn.getInputStream());

        log.info("Gemini response = {}", root);

        // 1) 텍스트(JSON)
        String jsonText = root.at("/candidates/0/content/parts/0/text").asText();
        JsonNode parsed = mapper.readTree(jsonText);


        // 2) 이미지(Base64)
        // Gemini는 아래처럼 이미지가 parts[*].inlineData로 포함됨
        List<String> imgBase64List = new ArrayList<>();

        JsonNode parts = root.at("/candidates/0/content/parts");
        for (JsonNode p : parts) {
            if (p.has("inlineData")) {
                imgBase64List.add(p.get("inlineData").get("data").asText());
            }
        }

        List<String> savedUrls = new ArrayList<>();
        for (String base64 : imgBase64List) {
            savedUrls.add(saveBase64Images(base64));
        }

        String ai_id = UUID.randomUUID().toString();


        return new OutfitResponseDto(ai_id,
                parsed.get("recommendation").asText(),
                parsed.get("reason").asText(),
                savedUrls
        );
    }


    private List<Map<String, Object>> mergeParts(
            List<Map<String, Object>> text,
            List<Map<String, Object>> img){
        List<Map<String, Object>> merged = new ArrayList<>(text);
        merged.addAll(img);
        return merged;
    }

    private String saveBase64Images(String base64) throws IOException{

        byte[] bytes = Base64.getDecoder().decode(base64);

        String FileName = "ai_" + (int)(Math.random()*9999) + ".png";
        File file = new File(upload_dir + FileName);
        Files.write(file.toPath(), bytes);

        String aiUrl = serverFileUrl + FileName;

        return aiUrl;
    }

}
