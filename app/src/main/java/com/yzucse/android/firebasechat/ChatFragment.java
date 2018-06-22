package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment {
    private SharedPreferences mSharedPreferences;
    private ImageButton mSendButton;
    private ProgressBar mProgressBar;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private String toDayStr;
    private GlobalData globalData;
    private String TAG = "ChatFragment";

    public ChatFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_chat, container, false);

        // Initialize View
        StaticValue.setViewVisibility(getActivity().findViewById(R.id.fragmentlayout), View.VISIBLE);
        mProgressBar = view.findViewById(R.id.progressBar);
        mMessageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        mMessageEditText = view.findViewById(R.id.messageEditText);
        mSendButton = view.findViewById(R.id.sendButton);
        mAddMessageImageView = view.findViewById(R.id.addMessageImageView);
        toDayStr = getString(R.string.today);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        ChatInit();

        return view;
    }

    private void setOtherMSG(FriendlyMessage friendlyMessage, final MessageViewHolder viewHolder, final Activity thisAct) {
        StaticValue.setViewVisibility(viewHolder.userMSGLayout, View.GONE);
        StaticValue.setViewVisibility(viewHolder.otherMSGLayout, View.VISIBLE);
        long t = friendlyMessage.getTimestamp();
        if (t > 0) {
//                    if (!StaticValue.getTimeByFormat(t, dateFormat)
//                            .equals(lastT[0]) &&
//                            !StaticValue.getTimeByFormat(t, dateFormat)
//                                    .equals(StaticValue.getTimeByFormat(System.currentTimeMillis(), dateFormat))) {
//                        lastT[0] = StaticValue.getTimeByFormat(t, dateFormat);
//                        StaticValue.setTextViewText(viewHolder.messagerDateTextView, lastT[0]);
//                    } else if (StaticValue.getTimeByFormat(t, dateFormat)
//                            .equals(StaticValue.getTimeByFormat(System.currentTimeMillis(), dateFormat))
//                            && !StaticValue.getTimeByFormat(t, dateFormat)
//                            .equals(lastT[0]) || lastT[0].equals("1")) {
//                        StaticValue.setTextViewText(viewHolder.messagerDateTextView, toDayStr);
//                        lastT[0] = StaticValue.getTimeByFormat(t, dateFormat);
//                    }
//                    else {
            StaticValue.setViewVisibility(viewHolder.messagerDateTextView, TextView.GONE);
//                    }
//                    lastT[0] = StaticValue.getTimeByFormat(t, dateFormat);
            StaticValue.setTextViewText(viewHolder.messengerTimestampView, StaticValue
                    .getTimeByFormat(t, globalData.getTIMEFORMAT()));
        }
        if (!Strings.isEmptyOrWhitespace(friendlyMessage.getText())) {
            StaticValue.setTextViewText(viewHolder.messageTextView, friendlyMessage.getText());
            StaticValue.setViewVisibility(viewHolder.friendChatBubbleLayout, TextView.VISIBLE);
            StaticValue.setViewVisibility(viewHolder.friendMSGImageCardView, ImageView.GONE);
        } else {
            String imageUrl = friendlyMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(viewHolder.messageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.messageImageView);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(viewHolder.messageImageView.getContext())
                        .load(friendlyMessage.getImageUrl())
                        .into(viewHolder.messageImageView);
            }
            StaticValue.setViewVisibility(viewHolder.friendMSGImageCardView, ImageView.VISIBLE);
            StaticValue.setViewVisibility(viewHolder.friendChatBubbleLayout, TextView.GONE);
        }

        StaticValue.setTextViewText(viewHolder.messengerTextView, globalData.getmUser().getFriendsName(friendlyMessage.getSenderID(), friendlyMessage.getSenderName()));
        globalData.getmUsersDBR().child(friendlyMessage.getSenderID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User sender = dataSnapshot.getValue(User.class);
                        StaticValue.setAccountImage(viewHolder.messengerImageView, sender.getPhotoUrl(), thisAct);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setUserMSG(FriendlyMessage friendlyMessage, final MessageViewHolder viewHolder) {
        StaticValue.setViewVisibility(viewHolder.otherMSGLayout, View.GONE);
        StaticValue.setViewVisibility(viewHolder.userMSGLayout, View.VISIBLE);
        long t = friendlyMessage.getTimestamp();
        if (t > 0) {
//                    if (!StaticValue.getTimeByFormat(t, dateFormat)
//                            .equals(lastT[0]) &&
//                            !StaticValue.getTimeByFormat(t, dateFormat)
//                                    .equals(StaticValue.getTimeByFormat(System.currentTimeMillis(), dateFormat))) {
//                        lastT[0] = StaticValue.getTimeByFormat(t, dateFormat);
//                        StaticValue.setTextViewText(viewHolder.messagerDateTextView, lastT[0]);
//                    } else if (StaticValue.getTimeByFormat(t, dateFormat)
//                            .equals(StaticValue.getTimeByFormat(System.currentTimeMillis(), dateFormat))
//                            && !StaticValue.getTimeByFormat(t, dateFormat)
//                            .equals(lastT[0]) || lastT[0].equals("1")) {
//                        StaticValue.setTextViewText(viewHolder.messagerDateTextView, toDayStr);
//                        lastT[0] = StaticValue.getTimeByFormat(t, dateFormat);
//                    }
//                    else {
            StaticValue.setViewVisibility(viewHolder.messagerDateTextView, TextView.GONE);
//                    }
//                    lastT[0] = StaticValue.getTimeByFormat(t, dateFormat);
            StaticValue.setTextViewText(viewHolder.userMessengerTimestampView, StaticValue
                    .getTimeByFormat(t, globalData.getTIMEFORMAT()));
        }
        if (!Strings.isEmptyOrWhitespace(friendlyMessage.getText())) {
            StaticValue.setTextViewText(viewHolder.userMessageTextView, friendlyMessage.getText());
            StaticValue.setViewVisibility(viewHolder.userChatBubbleLayout, TextView.VISIBLE);
            StaticValue.setViewVisibility(viewHolder.userMSGImageCardView, ImageView.GONE);
        } else {
            String imageUrl = friendlyMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(viewHolder.userMessageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.userMessageImageView);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(viewHolder.userMessageImageView.getContext())
                        .load(friendlyMessage.getImageUrl())
                        .into(viewHolder.userMessageImageView);
            }
            StaticValue.setViewVisibility(viewHolder.userMSGImageCardView, ImageView.VISIBLE);
            StaticValue.setViewVisibility(viewHolder.userChatBubbleLayout, TextView.GONE);
        }
    }

    private void ChatInit() {
        final Activity thisAct = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(thisAct);
        // New child entries
        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(FriendlyMessage.class);
            }
        };

        DatabaseReference messagesRef = globalData.getmChatRoomDBR().child(globalData.getmChatroom()
                .getChatroomID()).child(StaticValue.MESSAGES);

        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, parser)
                        .build();

        final String dateFormat = "yyyy.MM.dd";
        final String lastT[] = new String[1];
        lastT[0] = "1";

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            FriendlyMessage friendlyMessage) {
                StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                if (globalData.getmUser().getUserID().equals(friendlyMessage.getSenderID()))
                    setUserMSG(friendlyMessage, viewHolder);
                else setOtherMSG(friendlyMessage, viewHolder, thisAct);
            }
        };
        mFirebaseAdapter.startListening();

        if (mFirebaseAdapter.getItemCount() == 0)
            StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // mUser is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.smoothScrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(StaticValue.FRIENDLY_MSG_LENGTH, StaticValue.DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSendButton.setEnabled(!StaticValue.isNullorWhitespace(charSequence));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSendButton.setEnabled(!StaticValue.isNullorWhitespace(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final DatabaseReference chatroomref = globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID());
        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MSG = mMessageEditText.getText().toString();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(MSG,
                        globalData.getmUser(),
                        null /* no image */,
                        null /* no sticker */);
                String key = chatroomref.child(StaticValue.MESSAGES).push().getKey();
                chatroomref.child(StaticValue.MESSAGES).child(key).setValue(friendlyMessage);
                mMessageEditText.setText("");

                Map<String, Object> taskMap = new HashMap<>();
                //taskMap.put("lastMsg", key); maybe later
                taskMap.put(StaticValue.LASTMSG, key);
                chatroomref.updateChildren(taskMap);
                taskMap.clear();
                //check the Can message
                if (globalData.getmChatroom().getChatroomName().equals("chat"))
                    checkcan();
            }
        });

        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, StaticValue.REQUEST_IMAGE);
            }
        });

        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
    }

    public void checkcan() {
        String chatroomid = globalData.getmChatroom().getChatroomID();
        String friendname = globalData.getmUser().chatroomfindfriend(chatroomid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(friendname)//get user infomation
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                if (user.getCanMessage() != null) {
                                    if (user.isOpenCan()) {
                                        final DatabaseReference chatroomref = globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID());
                                        FriendlyMessage friendlyMessage = new
                                                FriendlyMessage(user.getCanMessage(),
                                                user,
                                                null /* no image */,
                                                null /* no sticker */);
                                        String key = chatroomref.child(StaticValue.MESSAGES).push().getKey();
                                        chatroomref.child(StaticValue.MESSAGES).child(key).setValue(friendlyMessage);
                                        Map<String, Object> taskMap = new HashMap<>();
                                        //taskMap.put("lastMsg", key); maybe later
                                        taskMap.put(StaticValue.LASTMSG, key);
                                        chatroomref.updateChildren(taskMap);
                                        taskMap.clear();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if mUser is signed in.
        // TODO: Add code to check if mUser is signed in.
    }

    @Override
    public void onPause() {
        if (mFirebaseAdapter != null) mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseAdapter != null) mFirebaseAdapter.startListening();
        /*View view = getView();
        if (view != null)
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        // handle back button's click listener
                        mFirebaseAdapter.stopListening();
                        getActivity().setContentView(R.layout.activity_main);
                        return true;
                    }
                    return false;
                }
            });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

                    FriendlyMessage tempMessage = new FriendlyMessage(null, globalData.getmUser(), StaticValue.LOADING_IMAGE_URL,
                            null);
                    globalData.getmChatRoomDBR()
                            .child(globalData.getmChatroom().getChatroomID()).child(StaticValue.MESSAGES).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
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

                                        putImageInStorage(storageReference, uri, key);
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

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(getActivity(),
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            FriendlyMessage friendlyMessage =
                                    new FriendlyMessage(null, globalData.getmUser(),
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString(), null);
                            globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID())
                                    .child(StaticValue.MESSAGES).child(key)
                                    .setValue(friendlyMessage);

                            globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID()).child(StaticValue.LASTMSG).setValue(key);
                            Log.e("Save Img", friendlyMessage.toString());
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
}
