package com.studder.model;

public class Message {
    String message;
    String userName;
    String imageUri;
    Long createdAt;
    Long userId;

    public Message(String message, String userName, String imageUri, Long createdAt, Long userId) {
        this.message = message;
        this.userName = userName;
        this.imageUri = imageUri;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
