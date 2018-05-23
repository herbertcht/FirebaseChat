package com.yzucse.android.firebasechat;

import java.io.Serializable;
import java.util.Map;

public class IDAndName implements Serializable {
    Map<String, String> chatroom;
    Map<String, String> friends;

    public IDAndName(){

    }

    public Map<String, String> getChatroom() {
        return chatroom;
    }

    public void setChatroom(Map<String, String> chatroom) {
        this.chatroom = chatroom;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    /*@Override
    public String toString() {
        return "'id' : '" + this.id + "', 'name' : '" + this.name + "'";
    }*/
}
