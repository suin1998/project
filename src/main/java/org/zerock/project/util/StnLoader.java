package org.zerock.project.util;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.zerock.project.model.StnLocation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class StnLoader {
    public Map<String, StnLocation> loadStnData() {
        Map<String, StnLocation> stnMap = new HashMap<>();
        ZipSecureFile.setMinInflateRatio(0.0d);
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream("static/sub_file/mid_stn_info.xlsx");
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() < 4) continue;
                String sido = row.getCell(0).getStringCellValue();
                String sigun = row.getCell(1).getStringCellValue();
                String regionCode = row.getCell(2).getStringCellValue();

                String key = buildKey(sido, sigun);
                stnMap.put(key, new StnLocation(sido, sigun, regionCode));

            }

            workbook.close();
    }catch (Exception e){
            e.printStackTrace();
        }
        return stnMap;

    }
    private String buildKey(String sido, String sigun){

        return (sido+ "-" +sigun);
    }

}
