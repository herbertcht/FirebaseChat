package com.yzucse.android.firebasechat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom implements Serializable {
    private Map<String, Boolean> userID;
    private Boolean group;
    private String chatroomName;
    private String chatroomID;
    private String photoUrl;
    private String lastMsg;

    public ChatRoom() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ChatRoom(Map<String, Boolean> userID, String chatroomName, String photoUrl) {
        setUserID(userID);
        this.chatroomName = chatroomName;
        this.photoUrl = photoUrl;

        // under this line will generate roomID
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

    public Boolean getGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public void addMember(String memberID) {
        this.userID.put(memberID, true);
    }

    @Override
    public String toString() {
        return chatroomID + " { "
                + "'chatroomID' : '" + chatroomID + "'" + ", " + "'chatroomName' : '" + chatroomName + "'"
                + ", " + "'lastMsg' : '" + lastMsg + "'" + ", " + "'userID' : " + StaticValue.Map2String(userID)
                + ", " + "'group' : '" + group + "'"
                + " }";
    }
}
