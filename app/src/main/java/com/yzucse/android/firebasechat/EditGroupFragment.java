package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditGroupFragment extends Fragment {
    private final String TAG = "FriendsFragment";
    private final EditText roomNameView[] = new EditText[1];
    private final CircleImageView roomImageView[] = new CircleImageView[1];
    private final Button editButton[] = new Button[1];
    private final Dialog d[] = new Dialog[1];
    public FirebaseRecyclerAdapter<User, MembersViewerHolder>
            mMembersAdapter;
    private Button clearButton;
    private RecyclerView mMembersRecyclerView;
    private CircleImageView mGroupImageView;
    private TextView mGroupName;
    private CircleImageView mAddMemberBtn;
    private LinearLayout mGroupLayout;
    private ProgressBar mProgressBar;
    private GlobalData globalData;
    private LayoutInflater factory;
    private View prompt;
    private LinearLayout layout;
    private String prevImage;

    public EditGroupFragment() {
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
        //if (mMembersAdapter != null) mMembersAdapter.startListening();
    }

    @Override
    public void onPause() {
        //if (mMembersAdapter != null) mMembersAdapter.stopListening();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_group_fragment, container, false);

        mProgressBar = view.findViewById(R.id.listprogressBar);
        mMembersRecyclerView = view.findViewById(R.id.membersRecyclerView);
        mAddMemberBtn = view.findViewById(R.id.add_button);
        mGroupImageView = view.findViewById(R.id.roomImageView);
        mGroupName = view.findViewById(R.id.roomNameView);
        mGroupLayout = view.findViewById(R.id.groupLayout);

        factory = getActivity().getLayoutInflater();
        prompt = factory.inflate(R.layout.new_group_dialog_layout, null);
        layout = prompt.findViewById(R.id.add_group_layout);
        clearButton = layout.findViewById(R.id.clearAll);
        roomNameView[0] = layout.findViewById(R.id.roomNameView);
        roomImageView[0] = layout.findViewById(R.id.roomImageView);
        editButton[0] = layout.findViewById(R.id.create_btn);
        d[0] = new Dialog(getActivity());

        MembersInit();
        if (mMembersAdapter != null) mMembersAdapter.startListening();

        return view;
    }

    private void InitMembersRecyclerView() {
        // New child entries
        SnapshotParser<User> parser = new SnapshotParser<User>() {
            @Override
            public User parseSnapshot(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(User.class);
            }
        };

        d[0].setContentView(layout);
        d[0].setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                globalData.getmChatroom().setPhotoUrl(prevImage);
                globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID()).child(StaticValue.PHOTO).setValue(globalData.getmChatroom().getPhotoUrl());
                StaticValue.setAccountImage(mGroupImageView, prevImage, getActivity());
                mMembersAdapter.startListening();
            }
        });

        prevImage = globalData.getmChatroom().getPhotoUrl();
        StaticValue.setAccountImage(mGroupImageView, globalData.getmChatroom().getPhotoUrl(), getActivity());
        StaticValue.setTextViewText(mGroupName, globalData.getmChatroom().getChatroomName());

        mGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d[0].show();

                StaticValue.setAccountImage(roomImageView[0], globalData.getmChatroom().getPhotoUrl(), getActivity());
                roomImageView[0]
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                startActivityForResult(intent, StaticValue.REQUEST_IMAGE);
                            }
                        });

                StaticValue.setTextViewText(roomNameView[0], globalData.getmChatroom().getChatroomName());
                roomNameView[0].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (StaticValue.isNullorWhitespace(s)) {
                            StaticValue.setViewVisibility(clearButton, Button.GONE);
                            editButton[0].setEnabled(false);
                        } else {
                            StaticValue.setViewVisibility(clearButton, Button.VISIBLE);
                            editButton[0].setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                StaticValue.setButtonText(editButton[0], getString(R.string.edit));
                editButton[0].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String roomID = globalData.getmChatroom().getChatroomID();
                        String roomName = roomNameView[0].getText().toString();
                        globalData.getmChatroom().setChatroomName(roomName);
                        globalData.getmChatRoomDBR().child(roomID).setValue(globalData.getmChatroom());
                        globalData.getmGroupDBR().child(roomID).setValue(globalData.getmChatroom());
                        d[0].dismiss();
                        StaticValue.setTextViewText(mGroupName, roomName);
                        StaticValue.setAccountImage(mGroupImageView, globalData.getmChatroom().getPhotoUrl(), getActivity());
                    }
                });
            }
        });

        Query messagesRef = globalData.getmUsersDBR().orderByChild(StaticValue.GROUPS + "/"
                + globalData.getmChatroom().getChatroomID()).equalTo(true);

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(messagesRef, parser)
                        .build();

        //mLinearLayoutManager.setStackFromEnd(true);
        mMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMembersAdapter = new FirebaseRecyclerAdapter<User, MembersViewerHolder>(options) {
            @Override
            public MembersViewerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MembersViewerHolder(inflater.inflate(R.layout.item_friend_list_button, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MembersViewerHolder viewHolder,
                                            int position,
                                            final User friend) {
                if (friend.getUserID().equals(globalData.getmUser().getUserID())) {
                    StaticValue.setViewVisibility(viewHolder.itemLayout, View.GONE);
                    StaticValue.setViewVisibility(viewHolder.itemView, View.GONE);
                    return;
                }
                StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                StaticValue.setAccountImage(viewHolder.friendImageView, friend.getPhotoUrl(), getActivity());
                StaticValue.setTextViewText(viewHolder.friendNameView,
                        globalData.getmUser().getFriendsName(friend.getUserID(), friend.getUsername()));
                Log.e("HH", friend.toString());
                viewHolder.kickOutButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(String.format(getString(R.string.kick_msg), viewHolder.friendNameView.getText()))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // FIRE ZE MISSILES!
                                        globalData.getmGroupDBR().child(globalData.getmChatroom().getChatroomID()).child(StaticValue.USERID).child(friend.getUserID()).removeValue();
                                        globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID()).child(StaticValue.USERID).child(friend.getUserID()).removeValue();
                                        globalData.getmChatroom().eraseMember(friend.getUserID());
                                        globalData.getmUsersDBR().child(friend.getUserID()).child(StaticValue.CHATROOM).child(globalData.getmChatroom().getChatroomID()).removeValue();
                                        globalData.getmUsersDBR().child(friend.getUserID()).child(StaticValue.GROUPS).child(globalData.getmChatroom().getChatroomID()).removeValue();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                }).show();
                    }
                });
            }
        };

//        mMembersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendCount = mMembersAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // mUser is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mMembersRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });

        mMembersRecyclerView.setAdapter(mMembersAdapter);

        mAddMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transToChat();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == StaticValue.REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    globalData.getmChatRoomDBR()
                            .child(globalData.getmChatroom().getChatroomID()).child(StaticValue.PHOTO)
                            .setValue(globalData.getmChatroom().getPhotoUrl(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(globalData.getmUser().getUserID())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri) {
        storageReference.putFile(uri).addOnCompleteListener(getActivity(),
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String photoURL = task.getResult().getMetadata().getDownloadUrl()
                                    .toString();
                            globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID())
                                    .child(StaticValue.PHOTO)
                                    .setValue(photoURL);
                            globalData.getmChatroom().setPhotoUrl(photoURL);
                            StaticValue.setAccountImage(roomImageView[0], photoURL, getActivity());
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    private void MembersInit() {
        //getActivity().setContentView(R.layout.chat_list);

        Log.e("User", globalData.getmUser().toString());

        InitMembersRecyclerView();
    }

    private void transToChat() {
        mMembersAdapter.stopListening();
        Activity thisAct = getActivity();
        StaticValue.setViewVisibility(thisAct.findViewById(R.id.mainLayout), View.INVISIBLE);
        StaticValue.setViewVisibility(thisAct.findViewById(R.id.fragmentlayout), View.VISIBLE);
        EditGroupAddMemberFragment mEditGroupAddMemberFragment = new EditGroupAddMemberFragment();
        mEditGroupAddMemberFragment.setGlobalData(globalData);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        //transaction.add(this, "EditGroupFragment");
        transaction.replace(R.id.fragmentlayout, mEditGroupAddMemberFragment)
                .commit();
    }

}
