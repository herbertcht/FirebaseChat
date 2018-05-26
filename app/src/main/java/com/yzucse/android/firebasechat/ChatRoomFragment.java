package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChatRoomFragment extends Fragment {
    public GlobalData globalData;
    private FirebaseRecyclerAdapter<ChatRoom, ChatRoomViewHolder>
            mFirebaseAdapter;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private TextView noitemText;
    private String TAG = "ChatRoomFragment";

    public ChatRoomFragment() {
    }

    public GlobalData getGlobalData() {
        return globalData;
    }

    public void setGlobalData(GlobalData globalData) {
        this.globalData = new GlobalData(globalData);
        //this.globalData = globalData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseAdapter != null) mFirebaseAdapter.startListening();

        View view = getView();
        if (view != null)
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        // handle back button's click listener
                        if (mMessageRecyclerView != null) {
                            if (mMessageRecyclerView.getVerticalScrollbarPosition() != 0)
                                mMessageRecyclerView.smoothScrollToPosition(0);
                        }
                        return true;
                    }
                    return false;
                }
            });
    }

    @Override
    public void onPause() {
        if (mFirebaseAdapter != null) mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_list_fragment, container, false);

        mProgressBar = view.findViewById(R.id.listprogressBar);
        mMessageRecyclerView = view.findViewById(R.id.chatroomRecyclerView);
        noitemText = view.findViewById(R.id.noItem);
        noitemText.setVisibility(View.INVISIBLE);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        ChatRoomInit();
        if (mFirebaseAdapter != null) mFirebaseAdapter.startListening();

        return view;
    }

    public void ChatRoomInit() {
        //getActivity().setContentView(R.layout.chat_list);

        if (StaticValue.isNullorEmptyMap(globalData.getmUser().getChatrooms())) {
            noitemText.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }

        // New child entries
        SnapshotParser<ChatRoom> parser = new SnapshotParser<ChatRoom>() {
            @Override
            public ChatRoom parseSnapshot(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(ChatRoom.class);
            }
        };
        Query messagesRef = globalData.getmChatRoomDBR().orderByChild(StaticValue.USERID + "/" + globalData.getmUser().getUserID()).equalTo(true);

        FirebaseRecyclerOptions<ChatRoom> options =
                new FirebaseRecyclerOptions.Builder<ChatRoom>()
                        .setQuery(messagesRef, parser)
                        .build();

        //mLinearLayoutManager.setStackFromEnd(false);
        //mLinearLayoutManager.setStackFromEnd(true);
        //mLinearLayoutManager.setReverseLayout(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatRoom, ChatRoomViewHolder>(options) {
            @Override
            public ChatRoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ChatRoomViewHolder(inflater.inflate(R.layout.item_chat_list, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final ChatRoomViewHolder viewHolder,
                                            int position,
                                            final ChatRoom chatroom) {
                if (chatroom == null) return;
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                noitemText.setVisibility(View.INVISIBLE);

                Log.e("ChatRoom", chatroom.toString());

                String chatroomName = globalData.getmUser().getChatroomName(chatroom);
                StaticValue.setTextViewText(viewHolder.chatroomNameView, globalData.getmUser().getFriendsName(chatroomName, chatroomName));

                String lastMSG = chatroom.getLastMsg();
                if (!Strings.isEmptyOrWhitespace(lastMSG)) {
                    DatabaseReference messagedbr = globalData.getmChatRoomDBR().child(chatroom.getChatroomID())
                            .child(StaticValue.MESSAGES).child(chatroom.getLastMsg());

                    if (messagedbr != null) {
                        messagedbr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final FriendlyMessage msg = dataSnapshot.getValue(FriendlyMessage.class); // it might be null, so can't just assign it
                                if (msg != null) {
                                    StaticValue.setTextViewText(viewHolder.chatroomTextView, msg.getText());
                                    StaticValue.setTextViewText(viewHolder.chatroomTimestampView,
                                            StaticValue.getTimeByFormat(msg.getTimestamp(), globalData.getTIMEFORMAT()));
                                    if (Strings.isEmptyOrWhitespace(msg.getText())) {
                                        globalData.getmUsersDBR().child(msg.getSenderID())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        User sender = dataSnapshot.getValue(User.class);
                                                        String sendetStr = !sender.getUserID().equals(globalData.getmUser().getUserID()) ?
                                                                globalData.getmUser().getFriendsName(sender.getUserID(), sender.getUsername()) :
                                                                getString(R.string.you);
                                                        if (!Strings.isEmptyOrWhitespace(msg.getImageUrl())) {
                                                            StaticValue.setTextViewText(viewHolder.chatroomTextView, sendetStr + getString(R.string.send_image));
                                                        } else if (!Strings.isEmptyOrWhitespace(msg.getStickerID())) {
                                                            StaticValue.setTextViewText(viewHolder.chatroomTextView, sendetStr + getString(R.string.send_sticker));
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                if (globalData.getmUser().hasFriend(chatroomName)) {
                    globalData.getmUsersDBR().child(chatroomName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User friend = dataSnapshot.getValue(User.class);
                                    StaticValue.setAccountImage(viewHolder.chatroomImageView, friend.getPhotoUrl(), getActivity());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else
                    StaticValue.setAccountImage(viewHolder.chatroomImageView, chatroom.getPhotoUrl(), getActivity());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        globalData.setmChatroom(chatroom);
                        mFirebaseAdapter.stopListening();
                        Activity thisAct = getActivity();
                        thisAct.findViewById(R.id.mainLayout).setVisibility(View.INVISIBLE);
                        thisAct.findViewById(R.id.chatlayout).setVisibility(View.VISIBLE);
                        ChatFragment mChatFragment = new ChatFragment();
                        mChatFragment.setGlobalData(globalData);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.chatlayout, mChatFragment)
                                .commit();
                        return;
                    }
                });
            }

        }

        ;

        /*mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatroomCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // mUser is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatroomCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });*/

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
