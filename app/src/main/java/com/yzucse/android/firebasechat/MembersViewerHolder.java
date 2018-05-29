package com.yzucse.android.firebasechat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersViewerHolder extends RecyclerView.ViewHolder {
    TextView friendNameView;
    CircleImageView friendImageView;
    Button kickOutButtonView;
    LinearLayout itemLayout;

    public MembersViewerHolder(View v) {
        super(v);
        friendNameView = itemView.findViewById(R.id.friendNameView);
        friendImageView = itemView.findViewById(R.id.friendImageView);
        kickOutButtonView = itemView.findViewById(R.id.friendKickOutView);
        itemLayout = itemView.findViewById(R.id.itemLayout);
    }
}

