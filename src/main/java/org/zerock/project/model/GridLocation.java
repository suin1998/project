package org.zerock.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GridLocation {
    private String sido;
    private String sigungu;
    private String dong;
    private int nx;
    private int ny;
}
