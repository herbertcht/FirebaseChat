package com.yzucse.android.firebasechat;

public class FriendlyMessage {

    private String id;
    private String text;
    private String senderID;
    private String senderName;
    private String imageUrl;
    private String stickerID;
    private long timestamp;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, User sender, String imageUrl, String stickerID) {
        this.text = text;
        this.senderID = sender.getUserID();
        this.senderName = sender.getUsername();
        this.imageUrl = imageUrl;
        this.stickerID = stickerID;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStickerID() {
        return stickerID;
    }

    public void setStickerID(String stickerID) {
        this.stickerID = stickerID;
    }

    @Override
    public String toString() {
        return "{" +
                "'id' : '" + id + "'" + ", " + "'text' : '" + text + "'" + ", " + "'senderID' : '" + senderID + "'"
                + ", " + "'senderName' : '" + senderName + "'" + ", " + "'imageUrl' : '" + imageUrl + "'"
                + ", " + "'stickerID' : '" + stickerID + "'" + ", " + "'timestamp' : '" + timestamp + "'"
                + "}";
    }
}
