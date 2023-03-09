package com.example.letschat.Models;

public class ChatData {

    String userId,message;
    String timeStamp;
    String messageStatus;

    public ChatData(String userId, String message, String timeStamp, String messageStatus) {
        this.userId = userId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.messageStatus = messageStatus;
    }

    public ChatData(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ChatData() {
    }
}
