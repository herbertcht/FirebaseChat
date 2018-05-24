package com.yzucse.android.firebasechat;

import java.util.List;
import java.util.Map;

public class StaticValue {
    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final String MESSAGES_CHILD = "Messages";
    public static final String MESSAGES = "messages";
    public static final String GROUP = "group";
    public static final String CHAT = "chat";
    public static final String FRIEND = "friends";
    public static final String BLOCKADE = "blockade";
    public static final String STICKERS = "stickers";
    public static final String STATUS = "status";
    public static final String SIGN = "sign";
    public static final String CHATROOM = "chatrooms";
    public static final String PHOTO = "photoUrl";
    public static final String USERNAME = "username";
    public static final String USERID = "userID";
    public static final String Users = "Users";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 3000;
    public static final String ANONYMOUS = "anonymous";
    public static final String TAG = "MainActivity";
    public static final int REQUEST_INVITE = 1;
    public static final int REQUEST_IMAGE = 2;
    public static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";


    /****************************** Static Utilities **********************************/
    public static String list2String(final List<String> item) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        int i = 0;
        if (item != null)
            if (!item.isEmpty())
                for (String str : item) {
                    if (sb.length() > 2) sb.append(", ");
                    sb.append("'").append(i++).append("' : '").append(str).append("' ");
                }
        sb.append(" }");
        return sb.toString();
    }

    static public String Map2String(Map<String, ?> itemList) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        if (itemList != null) {
            if (!itemList.isEmpty())
                for (Map.Entry<String, ?> item : itemList.entrySet()) {
                    if (sb.length() > 2) sb.append(", ");
                    sb.append("'").append(item.getKey()).append("' : '").append(item.getValue().toString()).append("'");
                }
        }
        sb.append(" }");
        return sb.toString();
    }
}
