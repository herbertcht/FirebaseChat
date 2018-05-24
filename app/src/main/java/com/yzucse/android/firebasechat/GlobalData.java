package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GlobalData implements Serializable {
    private String mPhotoUrl;
    private User mUser;
    private ChatRoom mChatroom;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mUsersDBR;
    private DatabaseReference mChatRoomDBR;
    private String TIMEFORMAT;

    public GlobalData() {
    }

    public GlobalData(GlobalData globalData) {
        setmUser(globalData.getmUser());
        setmFirebaseDatabaseReference(globalData.getmFirebaseDatabaseReference());
        setmPhotoUrl(globalData.mPhotoUrl);
        setmChatroom(globalData.mChatroom);
        TIMEFORMAT = globalData.TIMEFORMAT;
    }

    public DatabaseReference getmChatRoomDBR() {
        return mChatRoomDBR;
    }

    public void setmChatRoomDBR(DatabaseReference mChatRoomDBR) {
        this.mChatRoomDBR = mChatRoomDBR;
    }

    public DatabaseReference getmUsersDBR() {
        return mUsersDBR;
    }

    public void setmUsersDBR(DatabaseReference mUsersDBR) {
        this.mUsersDBR = mUsersDBR;
    }

    public void setUserStatus(final Boolean isOnline) {
        mUser.setOnline(isOnline);
        mUsersDBR.child(mUser.getUserID()).updateChildren(new HashMap<String, Object>() {{
            put("online", isOnline);
        }});
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public ChatRoom getmChatroom() {
        return mChatroom;
    }

    public void setmChatroom(ChatRoom mChatroom) {
        if (mChatroom == null) return;
        this.mChatroom = new ChatRoom();
        this.mChatroom.setChatroomID(mChatroom.getChatroomID());
        this.mChatroom.setLastMsg(mChatroom.getLastMsg());
        this.mChatroom.setChatroomName(mChatroom.getChatroomName());
        this.mChatroom.setUserID(mChatroom.getUserID());
        this.mChatroom.setPhotoUrl(mChatroom.getPhotoUrl());
    }

    public DatabaseReference getmFirebaseDatabaseReference() {
        return mFirebaseDatabaseReference;
    }

    public void setmFirebaseDatabaseReference(DatabaseReference mFirebaseDatabaseReference) {
        this.mFirebaseDatabaseReference = mFirebaseDatabaseReference;
        this.mUsersDBR = mFirebaseDatabaseReference.child(StaticValue.Users);
        this.mChatRoomDBR = mFirebaseDatabaseReference.child(StaticValue.MESSAGES_CHILD);
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        if (mUser == null) return;
        this.mUser = new User();
        this.mUser.setUsername(mUser.getUsername());
        this.mUser.setUserID(mUser.getUserID());
        this.mUser.setOnline(mUser.getOnline());
        this.mUser.setPhotoUrl(mUser.getPhotoUrl());
        this.mUser.setSign(mUser.getSign());
        this.mUser.setStickers(mUser.getStickers());
        this.mUser.setFriends(mUser.getFriends());
        this.mUser.setChatrooms(mUser.getChatrooms());
        this.mUser.setBlockade(mUser.getBlockade());
    }

    public String getTIMEFORMAT() {
        return TIMEFORMAT;
    }

    public void setTIMEFORMAT(String TIMEFORMAT) {
        this.TIMEFORMAT = TIMEFORMAT;
    }
}
