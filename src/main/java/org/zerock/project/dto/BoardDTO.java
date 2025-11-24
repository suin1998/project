package org.zerock.project.dto;


import java.time.LocalDateTime;


public class BoardDTO {

    private Long boardNumber;
    private String userId;
    private String userNickname;
    private String title;
    private String content;
    private String userStyle;
    private LocalDateTime regDate;
    private Long viewCount;
    private Integer likeCount;
    private Integer dislikeCount;

    public BoardDTO(Long boardNumber, String userId, String userNickname, String title, String content,
                    String userStyle, LocalDateTime regDate, Long viewCount, Integer likeCount, Integer dislikeCount) {

        this.boardNumber = boardNumber;
        this.userId = userId;
        this.userNickname = userNickname;
        this.title = title;
        this.content = content;
        this.userStyle = userStyle;
        this.regDate = regDate;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    public Long getBoardNumber() {
        return boardNumber;
    }

    public void setBoardNumber(Long boardNumber){
        this.boardNumber = boardNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserStyle() {
        return userStyle;
    }

    public void setUserStyle(String userStyle) {
        this.userStyle = userStyle;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}
