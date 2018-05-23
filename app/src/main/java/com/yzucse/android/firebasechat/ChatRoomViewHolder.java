package com.yzucse.android.firebasechat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
    TextView chatroomTextView;
    TextView chatroomNameView;
    TextView chatroomTimestampView;
    CircleImageView chatroomImageView;

    public ChatRoomViewHolder(View v) {
        super(v);
        chatroomNameView = itemView.findViewById(R.id.roomNameView);
        chatroomImageView = itemView.findViewById(R.id.roomImageView);
        chatroomTextView = itemView.findViewById(R.id.roomMSGTextView);
        chatroomTimestampView = itemView.findViewById(R.id.roomTime);
    }
}
