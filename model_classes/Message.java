package com.example.mmtapp.model_classes;

public class Message {
    String content;
    String sender;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    String senderUID;
    String receiverUID;
    String time;
    String date;
    String userType;
    boolean isMe;

     public Message() {
    }

    public Message(String content, String sender, String senderUID, String receiverUID, String time, String date, String userType, boolean isMe) {
        this.content = content;
        this.sender = sender;
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.time = time;
        this.date = date;
        this.userType = userType;
        this.isMe = isMe;
    }
}
