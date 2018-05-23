package com.yzucse.android.firebasechat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewerHolder extends RecyclerView.ViewHolder {
    TextView friendSignView;
    TextView friendNameView;
    TextView friendStatusView;
    CircleImageView friendImageView;

    public FriendsViewerHolder(View v) {
        super(v);
        friendNameView = itemView.findViewById(R.id.friendNameView);
        friendImageView = itemView.findViewById(R.id.friendImageView);
        friendSignView = itemView.findViewById(R.id.friendSignView);
        friendStatusView = itemView.findViewById(R.id.friendStatusView);
    }
}

