package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    private final String TAG = "FriendsFragment";
    public FirebaseRecyclerAdapter<User, FriendsViewerHolder>
            mFriendsAdapter;
    public FirebaseRecyclerAdapter<ChatRoom, FriendsViewerHolder>
            mGroupsAdapter;
    private RecyclerView mFriendsRecyclerView;
    private RecyclerView mGroupsRecyclerView;
    private LinearLayout mFriendsLayout;
    private LinearLayout mGroupsLayout;
    private ProgressBar mProgressBar;
    private CircleImageView mAddBtn;
    private TextView noitemText;
    private GlobalData globalData;
    private String STATUS[];

    public FriendsFragment() {
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
        if (mFriendsAdapter != null) mFriendsAdapter.startListening();
        if (mGroupsAdapter != null) mGroupsAdapter.startListening();
    }

    @Override
    public void onPause() {
        if (mFriendsAdapter != null) mFriendsAdapter.stopListening();
        if (mGroupsAdapter != null) mGroupsAdapter.stopListening();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friend_list_fragment, container, false);

        mProgressBar = view.findViewById(R.id.friendsProgressBar);
        mFriendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        mGroupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        mFriendsLayout = view.findViewById(R.id.friendsLayout);
        mGroupsLayout = view.findViewById(R.id.groupsLayout);
        mAddBtn = view.findViewById(R.id.add_button);
        noitemText = view.findViewById(R.id.noItem);
        StaticValue.setViewVisibility(noitemText, View.INVISIBLE);
        STATUS = new String[]{getString(R.string.offline), getString(R.string.online)};
        FriendsInit();
        if (mFriendsAdapter != null) mFriendsAdapter.startListening();
        if (mGroupsAdapter != null) mGroupsAdapter.startListening();

        return view;
    }

    final private ChatRoom generateChat(final User friend) {
        ChatRoom friendChat = new ChatRoom();
        String chatRoomID = globalData.getmUser().getUserID() + friend.getUserID();
        friendChat.setChatroomID(chatRoomID);
        friendChat.setChatroomName(StaticValue.CHAT);
        Map<String, Boolean> savedata = new HashMap<>();
        savedata.put(globalData.getmUser().getUserID(), true);
        savedata.put(friend.getUserID(), true);
        friendChat.setUserID(savedata);
        globalData.getmChatRoomDBR().child(chatRoomID).setValue(friendChat);
        globalData.setmChatroom(friendChat);
        globalData.getmUser().addChatroom(friendChat.getChatroomID(), friend.getUserID());
        friend.addChatroom(friendChat.getChatroomID(), globalData.getmUser().getUserID());
        Map<String, Object> updatecharoom = new HashMap<>();
        updatecharoom.put(StaticValue.CHATROOM, globalData.getmUser().getChatrooms());
        globalData.getmUsersDBR().child(globalData.getmUser().getUserID()).updateChildren(updatecharoom);
        updatecharoom.put(StaticValue.CHATROOM, friend.getChatrooms());
        globalData.getmUsersDBR().child(friend.getUserID()).updateChildren(updatecharoom);
        return friendChat;
    }

    private void InitFriendsRecyclerView() {
        // New child entries
        SnapshotParser<User> parser = new SnapshotParser<User>() {
            @Override
            public User parseSnapshot(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(User.class);
            }
        };

        Query messagesRef = globalData.getmUsersDBR().orderByChild(StaticValue.BLOCKADE + "/"
                + globalData.getmUser().getUserID()).equalTo(false);

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(messagesRef, parser)
                        .build();

        //mLinearLayoutManager.setStackFromEnd(true);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFriendsAdapter = new FirebaseRecyclerAdapter<User, FriendsViewerHolder>(options) {
            @Override
            public FriendsViewerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new FriendsViewerHolder(inflater.inflate(R.layout.item_friend_list, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final FriendsViewerHolder viewHolder,
                                            int position,
                                            final User friend) {
                if (friend != null) {
                    StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                    StaticValue.setViewVisibility(noitemText, View.INVISIBLE);
                    StaticValue.setTextViewText(viewHolder.friendSignView, friend.getSign());
                    StaticValue.setTextViewText(viewHolder.friendStatusView, STATUS[friend.getOnline() ? 1 : 0]);
                    StaticValue.setAccountImage(viewHolder.friendImageView, friend.getPhotoUrl(), getActivity());
                    StaticValue.setTextViewText(viewHolder.friendNameView,
                            globalData.getmUser().getFriendsName(friend.getUserID(), friend.getUsername()));
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final DatabaseReference fchatdbr = globalData.getmChatRoomDBR();
                            final String[] key = new String[1];
                            key[0] = "";
                            fchatdbr.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(globalData.getmUser().getUserID() + friend.getUserID())) {
                                        key[0] = globalData.getmUser().getUserID() + friend.getUserID();
                                    } else if (dataSnapshot.hasChild(friend.getUserID() + globalData.getmUser().getUserID())) {
                                        key[0] = friend.getUserID() + globalData.getmUser().getUserID();
                                    }
                                    if (Strings.isEmptyOrWhitespace(key[0])) {
                                        transToChat(generateChat(friend));
                                    } else {
                                        fchatdbr.child(key[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                transToChat(dataSnapshot.getValue(ChatRoom.class));
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        };

//        mFriendsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendCount = mFriendsAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // mUser is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mFriendsRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });

        mFriendsRecyclerView.setAdapter(mFriendsAdapter);
    }

    private void InitGroupsRecyclerView() {
        // New child entries
        SnapshotParser<ChatRoom> parser = new SnapshotParser<ChatRoom>() {
            @Override
            public ChatRoom parseSnapshot(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(ChatRoom.class);
            }
        };

        Query messagesRef = globalData.getmGroupDBR().orderByChild(StaticValue.USERID + "/"
                + globalData.getmUser().getUserID()).equalTo(true);

        FirebaseRecyclerOptions<ChatRoom> options =
                new FirebaseRecyclerOptions.Builder<ChatRoom>()
                        .setQuery(messagesRef, parser)
                        .build();

        //mLinearLayoutManager.setStackFromEnd(true);
        mGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mGroupsAdapter = new FirebaseRecyclerAdapter<ChatRoom, FriendsViewerHolder>(options) {
            @Override
            public FriendsViewerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new FriendsViewerHolder(inflater.inflate(R.layout.item_friend_list, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final FriendsViewerHolder viewHolder,
                                            int position,
                                            final ChatRoom group) {
                if (group != null) {
                    StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                    StaticValue.setViewVisibility(noitemText, View.INVISIBLE);
                    StaticValue.setViewVisibility(viewHolder.friendSignView, TextView.GONE);
                    StaticValue.setViewVisibility(viewHolder.friendStatusView, TextView.GONE);
                    StaticValue.setAccountImage(viewHolder.friendImageView, group.getPhotoUrl(), getActivity());
                    StaticValue.setTextViewText(viewHolder.friendNameView, group.getChatroomName());
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            globalData.setmChatroom(group);
                            EditGroupFragment mEditGroupFragment = new EditGroupFragment();
                            mEditGroupFragment.setGlobalData(globalData);
                            changeFragment(mEditGroupFragment);
                        }
                    });
                }
            }
        };

//        mFriendsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendCount = mFriendsAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // mUser is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mFriendsRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });

        mGroupsRecyclerView.setAdapter(mGroupsAdapter);
    }

    private void FriendsInit() {
        //getActivity().setContentView(R.layout.chat_list);

        Log.e("User", globalData.getmUser().toString());

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "CLICK");
                AddFriendFragment mAddFriendFragment = new AddFriendFragment();
                mAddFriendFragment.setGlobalData(globalData);
                changeFragment(mAddFriendFragment);
            }
        });

        if (StaticValue.isNullorEmptyMap(globalData.getmUser().getFriends()) && StaticValue.isNullorEmptyMap(globalData.getmUser().getFriends())) {
            StaticValue.setViewVisibility(noitemText, View.VISIBLE);
            StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
        }

        if (StaticValue.isNullorEmptyMap(globalData.getmUser().getGroups())) {
            StaticValue.setViewVisibility(mGroupsLayout, LinearLayout.GONE);
        } else {
            StaticValue.setViewVisibility(mGroupsLayout, LinearLayout.VISIBLE);
            InitGroupsRecyclerView();
        }

        if (StaticValue.isNullorEmptyMap(globalData.getmUser().getFriends())) {
            StaticValue.setViewVisibility(mFriendsLayout, LinearLayout.GONE);
        } else {
            StaticValue.setViewVisibility(mFriendsLayout, LinearLayout.VISIBLE);
            InitFriendsRecyclerView();
        }
    }

    private void changeFragment(Fragment fragment) {
        if (mFriendsAdapter != null) mFriendsAdapter.stopListening();
        if (mGroupsAdapter != null) mGroupsAdapter.stopListening();
        Activity thisAct = getActivity();
        StaticValue.setViewVisibility(thisAct.findViewById(R.id.mainLayout), View.INVISIBLE);
        StaticValue.setViewVisibility(thisAct.findViewById(R.id.fragmentlayout), View.VISIBLE);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentlayout, fragment)
                .commit();
    }

    private void transToChat(ChatRoom chat) {
        if (chat == null) {
            Log.wtf(TAG, "transTochat is null");
            return;
        }
        globalData.setmChatroom(chat);
        ChatFragment mChatFragment = new ChatFragment();
        mChatFragment.setGlobalData(globalData);
        changeFragment(mChatFragment);
    }

}
