package com.yzucse.android.firebasechat;

public class FriendlyMessage {

    private String id;
    private String text;
    private String senderID;
    private String senderName;
    private String photoUrl;
    private String imageUrl;
    private long timestamp;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, User sender, String photoUrl, String imageUrl) {
        this.text = text;
        this.senderID = sender.getUserID();
        this.senderName = sender.getUsername();
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
}
