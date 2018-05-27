package com.yzucse.android.firebasechat;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageTextView;
    ImageView messageImageView;
    TextView messengerTextView;
    TextView messengerTimestampView;
    TextView messagerDateTextView;
    CircleImageView messengerImageView;
    TextView userMessageTextView;
    ImageView userMessageImageView;
    TextView userMessengerTimestampView;
    LinearLayout otherMSGLayout;
    RelativeLayout userMSGLayout;
    LinearLayout userChatBubbleLayout;
    LinearLayout friendChatBubbleLayout;
    CardView userMSGImageCardView;
    CardView friendMSGImageCardView;

    public MessageViewHolder(View v) {
        super(v);
        messageTextView = itemView.findViewById(R.id.messageTextView);
        messageImageView = itemView.findViewById(R.id.messageImageView);
        messengerTextView = itemView.findViewById(R.id.messengerTextView);
        messengerTimestampView = itemView.findViewById(R.id.timestamp);
        messengerImageView = itemView.findViewById(R.id.messengerImageView);
        messagerDateTextView = itemView.findViewById(R.id.MsgdateText);
        userMessageTextView = itemView.findViewById(R.id.userMSGTextView);
        userMessageImageView = itemView.findViewById(R.id.userMSGImageView);
        userMessengerTimestampView = itemView.findViewById(R.id.userMSGtimestamp);
        otherMSGLayout = itemView.findViewById(R.id.otherMSGLayout);
        userMSGLayout = itemView.findViewById(R.id.userMSGLayout);
        userMSGImageCardView = itemView.findViewById(R.id.userMSGCardImageView);
        friendMSGImageCardView = itemView.findViewById(R.id.friendMSGCardImageView);
        userChatBubbleLayout = itemView.findViewById(R.id.userChatBubbleLayout);
        friendChatBubbleLayout = itemView.findViewById(R.id.friendChatBubbleLayout);
    }
}