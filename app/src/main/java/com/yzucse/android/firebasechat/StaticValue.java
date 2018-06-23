package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.Strings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaticValue {
    public static final String CAN_MESSAGE="canMessage";
    public static final String OPEN_CAN="openCan";
    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final String MESSAGES_CHILD = "Messages";
    public static final String MESSAGES = "messages";
    public static final String GROUP = "Group";
    public static final String GROUPS = "groups";
    public static final String CHAT = "chat";
    public static final String FRIEND = "friends";
    public static final String LASTMSG = "lastMsg";
    public static final String BLOCKADE = "blockade";
    public static final String STICKERS = "stickers";
    public static final String STATUS = "status";
    public static final String SIGN = "sign";
    public static final String EDITGROUPFRAGMENT = "EditGroupFragment";
    public static final String CHATROOM = "chatrooms";
    public static final String CHATROOMID = "chatroomID";
    public static final String PHOTO = "photoUrl";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String USERID = "userID";
    public static final String ANONYMOUSID = "INnFMnIOxDU3pk2mQb4Ivg3pZBD2";
    public static final String Users = "Users";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 3000;
    public static final String ANONYMOUS = "anonymous";
    public static final int REQUEST_INVITE = 1;
    public static final int REQUEST_IMAGE = 2;
    public static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    public static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_API_KEY = "AAAAMzQ4iTc:APA91bGlOPmQ4GOMlO3IzhFOJe-lC8d4ZUaPO41OZdl_CaPxEvkejNvO4DoYjPtt2zy2CC43qKoKPIZ4c6OyaangLBScOVcLDPLzdN3iusBmhHMZOhRqO5u7MErouybL8WWHJGYGZOTl";

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

    static public String MaxLengthText(String string) {
        String returnMSG = null;
        int maxLength = 40;
        if (string != null) {
            returnMSG = string.replaceAll("\n", " ");
            if (string.length() >= maxLength)
                returnMSG = returnMSG.substring(0, maxLength) + "...";
        }
        return returnMSG;
    }

    static public void setTextViewText(TextView view, String text) {
        if (!Strings.isEmptyOrWhitespace(text) && view != null)
            view.setText(text);
    }

    static public void setButtonText(Button view, String text) {
        if (!Strings.isEmptyOrWhitespace(text) && view != null)
            view.setText(text);
    }

    static public void setViewVisibility(View view, int type) {
        if (view != null) {
            view.setVisibility(type);
        }
    }

    static public String getTimeByFormat(Object time, String format) {
        return new SimpleDateFormat(format).format(time);
    }

    static public void setAccountImage(CircleImageView view, String uri, Activity activity) {
        if (view == null || activity == null) return;
        if (!Strings.isEmptyOrWhitespace(uri)) {
            Glide.with(activity)
                    .load(uri)
                    .into(view);
        } else {
            try {
                view.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_account_circle_black_36dp));
            }catch (Exception e){}
        }
    }

    static public boolean isNullorEmptyMap(Map<String, ?> map) {
        if (map == null) return true;
        return map.isEmpty();
    }

    static public boolean isNullorWhitespace(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) return true;
        return Strings.isEmptyOrWhitespace(charSequence.toString());
    }

    static public List<String> getKeysByValue(Map<String, String> map, String value) {
        List<String> keys = new ArrayList<>();
        if (!isNullorEmptyMap(map) && !Strings.isEmptyOrWhitespace(value))
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (value.equals(entry.getValue())) {
                    keys.add(entry.getKey());
                }
            }
        return keys;
    }
}
