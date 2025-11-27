package org.zerock.project.util;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.zerock.project.model.GridLocation;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class GridLoader {
    public Map<String, GridLocation> loadGridData() {
        Map<String, GridLocation> gridMap = new HashMap<>();
        ZipSecureFile.setMinInflateRatio(0.0d);
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream("sub_file/kma_shrt_grid_202504.xlsx");
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum()==0) continue;

                String sido = row.getCell(2).getStringCellValue();
                Cell sigunguCell = row.getCell(3);
                String sigungu = "";

                if ( sigunguCell != null){
                    sigungu = row.getCell(3).getStringCellValue();
                }else{
                    sigungu = null;
                }

                Cell dongCell = row.getCell(4);
                String dong = "";

                if(dongCell != null){
                    dong = row.getCell(4).getStringCellValue();

                }else{
                    dong = null;
                }

                int nx = (int) row.getCell(5).getNumericCellValue();
                int ny = (int) row.getCell(6).getNumericCellValue();
                String key = buildKey(sido, sigungu, dong);
                gridMap.put(key, new GridLocation(sido, sigungu, dong, nx, ny));
            }

            workbook.close();
    }catch (Exception e){
            e.printStackTrace();
        }
        return gridMap;
    }
    private String buildKey(String sido, String sigungu, String dong){
        return (sido + "-" +sigungu + "-"+dong);
    }

}
