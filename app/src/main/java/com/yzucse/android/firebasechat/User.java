package com.yzucse.android.firebasechat;

import com.google.android.gms.common.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String username;
    private String userID;
    private List<String> stickers; // this structure might be changed
    private Map<String, String> friends;
    private Map<String, Boolean> blockade;
    private Map<String, String> chatrooms;
    private String sign;
    private Boolean online;
    private String photoUrl;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userID, String username, String email, String photoUrl) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public User(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getStickers() {
        return stickers;
    }

    public void setStickers(List<String> stickers) {
        if (stickers != null) {
            this.stickers = new ArrayList<>();
            this.stickers.addAll(stickers);
        }
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        if (friends != null)
            if (!friends.isEmpty()) {
                this.friends = new HashMap<>();
                this.friends.putAll(friends);
            }
    }

    public void addFriend(String id, String name) {
        if (Strings.isEmptyOrWhitespace(id) || Strings.isEmptyOrWhitespace(name)) return;
        if (friends == null) friends = new HashMap<>();
        friends.put(id, name);
        addBlockade(id);
    }

    public Map<String, Boolean> getBlockade() {
        return blockade;
    }

    public void setBlockade(Map<String, Boolean> blockade) {
        if (blockade != null)
            if (!blockade.isEmpty()) {
                this.blockade = new HashMap<>();
                this.blockade.putAll(blockade);
            }
    }

    public void addBlockade(String id) {
        if (Strings.isEmptyOrWhitespace(id)) return;
        if (this.blockade == null) this.blockade = new HashMap<>();
        this.blockade.put(id, false);
    }

    public Map<String, String> getChatrooms() {
        return chatrooms;
    }

    public void setChatrooms(Map<String, String> chatrooms) {
        if (chatrooms != null)
            if (!chatrooms.isEmpty()) {
                this.chatrooms = new HashMap<>();
                this.chatrooms.putAll(chatrooms);
            }
    }

    public String getChatroomName(ChatRoom chatRoom) {
        if (chatRoom != null) {
            if (!Strings.isEmptyOrWhitespace(chatRoom.getChatroomID()) && !StaticValue.isNullorEmptyMap(chatrooms)) {
                String name = chatrooms.get(chatRoom.getChatroomID());
                if (!Strings.isEmptyOrWhitespace(name)) return name;
            }
        }
        return chatRoom.getChatroomName();
    }

    public String getFriendsName(String friendID, String defaultName) {
        if (!Strings.isEmptyOrWhitespace(friendID)) {
            if (!StaticValue.isNullorEmptyMap(friends)) {
                if(friends.containsKey(friendID)) return friends.get(friendID);
            }
        }
        return defaultName;
    }

    public void addChatroom(String id, String name) {
        if (Strings.isEmptyOrWhitespace(id) || Strings.isEmptyOrWhitespace(name)) return;
        if (chatrooms == null) chatrooms = new HashMap<>();
        chatrooms.put(id, name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return " { " +
                "'online' : '" + online + "'" + ", " + "'name' : '" + username + "'"
                + ", " + "'id' : '" + userID + "'" + ", " + "'chatrooms' : " + StaticValue.Map2String(chatrooms)
                + ", " + "'friends' : " + StaticValue.Map2String(friends) + ", " + "'blockade' : " + StaticValue.Map2String(blockade)
                + ", " + "'stickers' : " + StaticValue.list2String(stickers) + ", " + "'sign' : '" + sign + "'" + ", " + "'photoUrl' : '" + photoUrl + "' "
                + ", " + "'email' : '" + email + "'"
                + " }";
    }

}
