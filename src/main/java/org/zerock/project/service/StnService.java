package org.zerock.project.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.project.model.StnLocation;
import org.zerock.project.util.StnLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class StnService {

    private final StnLoader stnLoader;
    private Map<String, StnLocation> stnMap;

    @PostConstruct
    public void load(){
        stnMap = stnLoader.loadStnData();
        System.out.println("총 로드된 지역 수:" + stnMap.size());
    }
    public StnLocation getStnLocation(String sido, String sigungu){

        final String searchSido;
        if (sido.endsWith("시")){
            searchSido = sido != null && sido.length() >= 2 ? sido.substring(0, 2) : sido;
        }else{
            searchSido = sido;
        }

        String searchSigungu = sigungu != null && sigungu.length() >= 2 ? sigungu.substring(0, 2) : sigungu;

        log.info(searchSido, searchSigungu);
        List<String> matchingKeys = stnMap.keySet().stream()
                    // 키에 searchSido가 포함되어 있는지 확인합니다.
                    .filter(key -> key.contains(searchSido))
                    .collect(Collectors.toList());

        for (String key : matchingKeys) {
            int dashIndex = key.indexOf("-");
            String dashAfterKey = key.substring(dashIndex+1);

            if (sido.endsWith("시")){
                if(dashAfterKey.equals(searchSido)){
                    return stnMap.get(key);
                }
            }
            else if (dashAfterKey.contains(searchSigungu)){
                return stnMap.get(key);
            }
        }

        return null;

    }
}
