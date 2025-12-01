package org.zerock.project.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Category {

    TOP("상의"),
    BOTTOM("하의"),
    OUTER("아우터"),
    SHOES("신발"),
    BAG("가방"),
    ACCESSORY("악세사리"),
    HAT("모자");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // 전체 카테고리를 리스트로 반환
    public static List<Category> getAll() {
        return List.of(values()); // 불변 리스트 반환
    }

    // 라벨 목록만 추출 기능
    public static List<String> getLabelList() {
        return Arrays.stream(values())
                .map(Category::getLabel)
                .toList();
    }

    // 라벨 → enum 변환
    public static Category fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category label: " + label));
    }
}
