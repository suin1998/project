//package org.zerock.project.service;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.zerock.project.model.GridLocation;
//import org.zerock.project.util.GridLoader;
//
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class GridService {
//
//    private final GridLoader gridLoader;
//    private Map<String, GridLocation> gridMap;
//
//    @PostConstruct
//    public void load(){
//        gridMap = gridLoader.loadGridData();
//        System.out.println("총 로드된 지역 수:" + gridMap.size());
//    }
//    public GridLocation getGridLocation(String sido, String sigungu, String dong){
//        String key = sido + "-" + sigungu +"-"+dong;
//
//        if(!gridMap.containsKey(key)){
//            throw new RuntimeException("해당 지역의 격자 정보를 찾을 수 없습니다.:" + key);
//        }
//        return gridMap.get(key);
//    }
//}
