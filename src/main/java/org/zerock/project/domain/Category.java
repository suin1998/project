package org.zerock.project.domain;

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
}

