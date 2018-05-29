package com.yzucse.android.firebasechat;

import com.google.android.gms.common.util.Strings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom implements Serializable {
    private Map<String, Boolean> userID;
    private String chatroomName;
    private String chatroomID;
    private String photoUrl;
    private String lastMsg;

    public ChatRoom() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ChatRoom(Map<String, Boolean> userID, String chatroomID, String chatroomName) {
        setUserID(userID);
        this.chatroomID = chatroomID;
        this.chatroomName = chatroomName;
    }

    public String getChatroomID() {
        return chatroomID;
    }

    public void setChatroomID(String chatroomID) {
        this.chatroomID = chatroomID;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Map<String, Boolean> getUserID() {
        return userID;
    }

    public void setUserID(Map<String, Boolean> userID) {
        if (userID != null) {
            this.userID = new HashMap<>();
            this.userID.putAll(userID);
        }
    }

    public boolean hasMember(String id) {
        if (StaticValue.isNullorEmptyMap(userID)) return false;
        return userID.containsKey(id);
    }

    public void eraseMember(String id) {
        if (Strings.isEmptyOrWhitespace(id) || StaticValue.isNullorEmptyMap(this.userID)) return;
        if (this.userID.containsKey(id))
            this.userID.remove(id);
    }

    public void addMember(String memberID) {
        this.userID.put(memberID, true);
    }

    @Override
    public String toString() {
        return " { "
                + "'chatroomID' : '" + chatroomID + "'" + ", " + "'chatroomName' : '" + chatroomName + "'"
                + ", " + "'lastMsg' : '" + lastMsg + "'" + ", " + "'userID' : " + StaticValue.Map2String(userID)
                + ", "
                + " }";
    }
}
