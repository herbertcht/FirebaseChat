package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.Strings;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaticValue {
    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final String MESSAGES_CHILD = "Messages";
    public static final String MESSAGES = "messages";
    public static final String GROUP = "group";
    public static final String CHAT = "chat";
    public static final String FRIEND = "friends";
    public static final String LASTMSG = "lastMsg";
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

    static public String MaxLengthText(String string){
        String returnMSG = null;
        int maxLength = 50;
        if(string !=null){
            returnMSG = string.replaceAll("\n", " ");
            if(string.length() >= maxLength)
                returnMSG = returnMSG.substring(0, maxLength) + "...";
        }
        return returnMSG;
    }

    static public void setTextViewText(TextView view, String text) {
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
        if (!Strings.isEmptyOrWhitespace(uri)) {
            Glide.with(activity)
                    .load(uri)
                    .into(view);
        } else
            view.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_account_circle_black_36dp));
    }

    static public boolean isNullorEmptyMap(Map<String, ?> map)
    {
        if(map == null) return true;
        return map.isEmpty();
    }

    static boolean isNullorWhitespace(CharSequence charSequence){
        if(TextUtils.isEmpty(charSequence)) return true;
        return Strings.isEmptyOrWhitespace(charSequence.toString());
    }
}
