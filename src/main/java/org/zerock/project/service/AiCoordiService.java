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

import org.zerock.project.entity.Closet;
import org.zerock.project.entity.User;
import org.zerock.project.entity.User.Gender;
import org.zerock.project.repository.AiCoordiRepository;
import org.zerock.project.repository.ClosetRepository;
import org.zerock.project.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public OutfitResponseDto getAiCoordi(OutfitRequestDto outfitRequestDto) throws IOException{

//        User user = userRepository.findById(outfitRequestDto.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));


//        Closet userClothes = closetRepository.findByUser(user);

//        String imgUrl = userClothes.getImg_Url();
//
//        String prompt = buildPrompt(outfitRequestDto, user);
//
//        GeminiResponse geminiResponse = callGeminiApi(prompt, userClothes);
//
//        String recommendation = geminiResponse.getText();
//        String reason = geminiResponse.getReason();
//
//        String savedImageUrl = saveImage(geminiResponse.getImageBase64());


        return null;

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
        promptBuilder.append("  \"reason\": \"추천 이유 및 날씨 대응 전략 (간략하게 2~3 문장으로)\"\n");
        promptBuilder.append("}\n");

        String textPrompt = promptBuilder.toString();
        log.info("Gemini text prompt: {}", textPrompt);
        return null;
    }



    private OutfitResponseDto getAiRecommend(String prompt, List<MultipartFile> images) throws IOException {

        try(CloseableHttpClient client = HttpClients.createDefault()){
            String apiUrl = ai_api_url + "/v1beta/models/gemini-2.5-flash-latest:generateContent?key=" + aiKey;
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Content-Type", "application/json");

            // 이미지 Base64 변환
            List<Map<String, Object>> imgList = new ArrayList<>();
            for (MultipartFile mf : images) {
                String base64 = Base64.getEncoder().encodeToString(mf.getBytes());
                imgList.add(Map.of(
                        "inlineData", Map.of(
                                "data", base64,
                                "mimeType", mf.getContentType()))
                );
            }

            // 요청 JSON 구성
            Map<String, Object> jsonBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", mergeParts(
                                            List.of(Map.of("text", prompt)),
                                            imgList
                                    )
                            )
                    ),
                    "generationConfig",
                    Map.of(
                            "response_mime_type", "image/*",
                            "candidate_count", 3)

                    );

            post.setEntity(new StringEntity(mapper.writeValueAsString(jsonBody)));

            var response = client.execute(post);

            JsonNode root = mapper.readTree(response.getEntity().getContent());
            log.info("Gemini response = {}", root);

            // 1) 텍스트(JSON)
            String jsonText = root.get("candidates").get(0)
                    .get("content").get("parts").get(0).get("text").asText();

            JsonNode json = mapper.readTree(jsonText);

            // 2) 이미지(Base64)
            // Gemini는 아래처럼 이미지가 parts[*].inlineData로 포함됨
            List<String> base64Images = new ArrayList<>();

            JsonNode candidates = root.get("candidates");

            for (JsonNode cand : candidates) {
                if (cand.has("content")) {
                    JsonNode partsNode = cand.get("content").get("parts");
                    for (JsonNode partNode : partsNode) {
                        if (partNode.has("inlineData")) {
                            String base64 = partNode.get("inlineData").get("data").asText();
                            base64Images.add(base64);
                        }
                    }
                }
            }
            log.info("Extracted Images Count = {}", base64Images.size());


            return OutfitResponseDto.builder()
                    .recommendation(json.get("recommendation").asText())
                    .reason(json.get("reason").asText())
//                    .imageDescription(json.get("image_prompt").asText())
//                    .generatedImageBase64(base64Images)
                    .build();


        }catch(Exception e){
            log.error("Gemini error: ", e);
            return null;
        }

    }


    private List<Map<String, Object>> mergeParts(
            List<Map<String, Object>> text,
            List<Map<String, Object>> img){
        List<Map<String, Object>> merged = new ArrayList<>(text);
        merged.addAll(img);
        return merged;
    }

    public List<String> saveBase64Images(List<String> base64List) {
        List<String> savedPaths = new ArrayList<>();

        try {

            String dir = "C:/project/uploads";
            File folder = new File(dir);
            if (!folder.exists()) folder.mkdirs();

            for (String base64 : base64List) {
                byte[] decoded = Base64.getDecoder().decode(base64);

                String path = dir + "AI_" + System.currentTimeMillis()
                        + "_" + (int)(Math.random()*9999) + ".png";

                try (FileOutputStream fos = new FileOutputStream(path)) {
                    fos.write(decoded);
                }

                savedPaths.add(path);
                log.info("Saved image = {}", path);
            }
        } catch (Exception e) {
            log.error("Image save error", e);
        }

        return savedPaths;
    }



}
