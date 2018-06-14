package com.yzucse.android.firebasechat;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;

public class GlobalData implements Serializable {
    private String mPhotoUrl;
    private User mUser;
    private ChatRoom mChatroom;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mUsersDBR;
    private DatabaseReference mChatRoomDBR;
    private DatabaseReference mGroupDBR;
    private String TIMEFORMAT;

    public GlobalData() {
    }

    public GlobalData(GlobalData globalData) {
        setmUser(globalData.getmUser());
        setmFirebaseDatabaseReference(globalData.getmFirebaseDatabaseReference());
        setmPhotoUrl(globalData.getmPhotoUrl());
        setmChatroom(globalData.getmChatroom());
        TIMEFORMAT = globalData.getTIMEFORMAT();
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

    public DatabaseReference getmGroupDBR() {
        return mGroupDBR;
    }

    public void setmGroupDBR(DatabaseReference mGroupDBR) {
        this.mGroupDBR = mGroupDBR;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public boolean AllFriendsInChatRoom() {
        if (StaticValue.isNullorEmptyMap(this.mUser.getFriends()) || StaticValue.isNullorEmptyMap(this.mChatroom.getUserID()))
            return true;
        for (String friendId : this.mUser.getFriends().keySet())
            if (!this.mChatroom.getUserID().containsKey(friendId))
                return false;
        return true;
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
        this.mGroupDBR = mFirebaseDatabaseReference.child(StaticValue.GROUP);
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
        this.mUser.setGroups(mUser.getGroups());
        this.mUser.setCanMessage(mUser.getCanMessage());
        this.mUser.setOpenCan(mUser.isOpenCan());
    }

    public String getTIMEFORMAT() {
        return TIMEFORMAT;
    }

    public void setTIMEFORMAT(String TIMEFORMAT) {
        this.TIMEFORMAT = TIMEFORMAT;
    }
}
