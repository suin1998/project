package org.zerock.project.util;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.zerock.project.model.GridLocation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class GridLoader {
    public Map<String, GridLocation> loadGridData() {
        Map<String, GridLocation> gridMap = new HashMap<>();
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream("sub_file/kma_shrt_grid_202504.xlsx");
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum()==0) continue;

                String sido = row.getCell(2).getStringCellValue().trim();
                String sigungu = row.getCell(3).getStringCellValue().trim();
                String dong = row.getCell(4).getStringCellValue().trim();
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
