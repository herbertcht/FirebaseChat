package com.yzucse.android.firebasechat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageTextView;
    ImageView messageImageView;
    TextView messengerTextView;
    TextView messengerTimestampView;
    TextView messagerDateTextView;
    CircleImageView messengerImageView;

    public MessageViewHolder(View v) {
        super(v);
        messageTextView = itemView.findViewById(R.id.messageTextView);
        messageImageView = itemView.findViewById(R.id.messageImageView);
        messengerTextView = itemView.findViewById(R.id.messengerTextView);
        messengerTimestampView = itemView.findViewById(R.id.timestamp);
        messengerImageView = itemView.findViewById(R.id.messengerImageView);
        messagerDateTextView = itemView.findViewById(R.id.MsgdateText);
    }
}