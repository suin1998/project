package org.zerock.project.dto;

import lombok.Data;

@Data
public class ShortTermWeatherDto {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private Items items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }

    @Data
    public static class Items {
        private java.util.List<Item> item;
    }

    @Data
    public static class Item {
        private String category;   // TMP, POP, SKY 등
        private String fcstDate;
        private String fcstTime;
        private String fcstValue;
    }
}

