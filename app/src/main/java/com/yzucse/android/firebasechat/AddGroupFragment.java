package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupFragment extends Fragment {
    private final String TAG = "AddGroupFragment";
    private final EditText roomNameView[] = new EditText[1];
    private final CircleImageView roomImageView[] = new CircleImageView[1];
    private final Button createButton[] = new Button[1];
    private final Dialog d[] = new Dialog[1];
    private ListView mFriendsListView;
    private ProgressBar mProgressBar;
    private TextView mNoitemText;
    private GlobalData globalData;
    private Button mCreate_btn;
    private EditText mSearchEdittext;
    private Button mClesrBtn;
    private LayoutInflater factory;
    private View prompt;
    private LinearLayout layout;
    private String STATUS[];
    private ArrayList<IdAndName> adapter;
    private ArrayList<String> checkLst;

    public AddGroupFragment() {
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_group_fragment, container, false);

        mProgressBar = view.findViewById(R.id.progressBar);
        mFriendsListView = view.findViewById(R.id.friendsListView);
        mNoitemText = view.findViewById(R.id.noItem);
        mNoitemText.setVisibility(View.INVISIBLE);
        mCreate_btn = view.findViewById(R.id.create_group);
        mSearchEdittext = view.findViewById(R.id.friendSearchEditText);
        mClesrBtn = view.findViewById(R.id.clearAll);

        factory = getActivity().getLayoutInflater();
        prompt = factory.inflate(R.layout.new_group_dialog_layout, null);
        layout = prompt.findViewById(R.id.add_group_layout);
        roomNameView[0] = layout.findViewById(R.id.roomNameView);
        roomImageView[0] = layout.findViewById(R.id.roomImageView);
        createButton[0] = layout.findViewById(R.id.create_btn);
        d[0] = new Dialog(getActivity());

        adapter = new ArrayList<>();
        checkLst = new ArrayList<>();
        STATUS = new String[]{getString(R.string.offline), getString(R.string.online)};
        FriendsInit();

        return view;
    }

    final private void setAllFriends() {
        for (Map.Entry<String, String> item : globalData.getmUser().getFriends().entrySet()) {
            adapter.add(new IdAndName(item.getKey(), item.getValue()));
        }

        mFriendsListView.setAdapter(new FriendsAdapter(getActivity(), adapter));
    }

    public void FriendsInit() {
        //getActivity().setContentView(R.layout.chat_list);

        mCreate_btn.setEnabled(false);

        if (StaticValue.isNullorEmptyMap(globalData.getmUser().getFriends())) {
            mNoitemText.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFriendsListView.setAdapter(null);
                adapter.clear();
                StaticValue.setViewVisibility(mNoitemText, View.INVISIBLE);
                if (StaticValue.isNullorWhitespace(s)) {
                    setAllFriends();
                    StaticValue.setViewVisibility(mClesrBtn, Button.GONE);
                } else {
                    StaticValue.setViewVisibility(mProgressBar, ProgressBar.VISIBLE);
                    StaticValue.setViewVisibility(mClesrBtn, Button.VISIBLE);
                    IdAndName addValue;
                    for (String name : globalData.getmUser().getAllFriendsNames()) {
                        if (name.toLowerCase().contains(s.toString().toLowerCase())) {
                            for (String id : globalData.getmUser().getFriendsId(name)) {
                                addValue = new IdAndName(id, name);
                                if (!adapter.contains(addValue))
                                    adapter.add(addValue);
                            }
                        }
                    }
                    if (adapter.isEmpty()) {
                        StaticValue.setViewVisibility(mNoitemText, View.VISIBLE);
                        StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                    } else {
                        StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                        mFriendsListView.setAdapter(new FriendsAdapter(getActivity(), adapter));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final String key[] = new String[1];

        d[0].setContentView(layout);
        d[0].setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                globalData.getmChatRoomDBR().child(key[0]).removeValue();
                StaticValue.setAccountImage(roomImageView[0], "", getActivity());
            }
        });

        mClesrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdittext.setText("");
            }
        });

        mCreate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createButton[0].setEnabled(false);
                Map<String, Boolean> users = new HashMap<>();
                for (String id : checkLst)
                    users.put(id, true);
                users.put(globalData.getmUser().getUserID(), true);
                key[0] = globalData.getmChatRoomDBR().push().getKey();
                globalData.setmChatroom(new ChatRoom(users, key[0], roomNameView[0].getText().toString()));
                d[0].show();
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
                roomNameView[0].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (StaticValue.isNullorWhitespace(s)) createButton[0].setEnabled(false);
                        else createButton[0].setEnabled(true);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                createButton[0].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String roomName = roomNameView[0].getText().toString();
                        globalData.getmChatroom().setChatroomName(roomName);
                        globalData.getmChatRoomDBR().child(key[0]).setValue(globalData.getmChatroom());
                        globalData.getmGroupDBR().child(key[0]).setValue(globalData.getmChatroom());
                        globalData.getmUser().addChatroom(key[0], roomName);
                        globalData.getmUser().addGroup(key[0]);
                        for (String id : globalData.getmChatroom().getUserID().keySet()) {
                            globalData.getmUsersDBR().child(id).child(StaticValue.CHATROOM)
                                    .child(globalData.getmChatroom().getChatroomID())
                                    .setValue(globalData.getmChatroom().getChatroomName());
                            globalData.getmUsersDBR().child(id).child(StaticValue.GROUPS)
                                    .child(globalData.getmChatroom().getChatroomID())
                                    .setValue(true);
                        }
                        d[0].dismiss();
                        transToChat();
                    }
                });
            }
        });

        setAllFriends();
    }

    private void transToChat() {
        ChatFragment mChatFragment = new ChatFragment();
        mChatFragment.setGlobalData(globalData);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentlayout, mChatFragment)
                .commit();
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
                            .setValue(StaticValue.LOADING_IMAGE_URL, new DatabaseReference.CompletionListener() {
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

    public class FriendsAdapter extends ArrayAdapter<IdAndName> {

        public FriendsAdapter(Context context, ArrayList<IdAndName> users) {
            super(context, R.layout.item_friend_list_checkbox, users);
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
            // Get the data item for this position
            final IdAndName user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            final FriendsViewHolder viewHolder[] = new FriendsViewHolder[1]; // view lookup cache stored in tag
            if (itemView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                LayoutInflater inflater = LayoutInflater.from(getContext());
                itemView = inflater.inflate(R.layout.item_friend_list_checkbox, parent, false);
                viewHolder[0] = new FriendsViewHolder();
                viewHolder[0].friendNameView = itemView.findViewById(R.id.friendNameView);
                viewHolder[0].friendImageView = itemView.findViewById(R.id.friendImageView);
                viewHolder[0].friendSignView = itemView.findViewById(R.id.friendSignView);
                viewHolder[0].friendStatusView = itemView.findViewById(R.id.friendStatusView);
                viewHolder[0].friendCheckBoxView = itemView.findViewById(R.id.friendCheckBoxView);
                // Cache the viewHolder object inside the fresh view
                itemView.setTag(viewHolder[0]);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder[0] = (FriendsViewHolder) itemView.getTag();
            }
            final User user1[] = new User[1];
            globalData.getmUsersDBR().child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user1[0] = dataSnapshot.getValue(User.class);
                    if (user1[0] != null) {
                        // Populate the data from the data object via the viewHolder object
                        // into the template view.
                        StaticValue.setTextViewText(viewHolder[0].friendNameView, user.getName());
                        StaticValue.setAccountImage(viewHolder[0].friendImageView, user1[0].getPhotoUrl(), getActivity());
                        StaticValue.setTextViewText(viewHolder[0].friendSignView, user1[0].getSign());
                        StaticValue.setTextViewText(viewHolder[0].friendStatusView, STATUS[user1[0].getOnline() ? 1 : 0]);

                        if (checkLst.contains(user.getName()))
                            viewHolder[0].friendCheckBoxView.setChecked(true);

                        viewHolder[0].friendCheckBoxView
                                .setOnCheckedChangeListener
                                        (new CompoundButton.OnCheckedChangeListener() {
                                             @Override
                                             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                 if (isChecked && !checkLst.contains(user1[0].getUserID())) {
                                                     checkLst.add(user1[0].getUserID());
                                                     if (!mCreate_btn.isEnabled())
                                                         mCreate_btn.setEnabled(true);
                                                 } else if (!isChecked && checkLst.contains(user1[0].getUserID())) {
                                                     checkLst.remove(user1[0].getUserID());
                                                     if (checkLst.isEmpty() && mCreate_btn.isEnabled())
                                                         mCreate_btn.setEnabled(false);
                                                 }
                                             }
                                         }
                                        );
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Return the completed view to render on screen
            return itemView;
        }

        private class FriendsViewHolder {
            TextView friendSignView;
            TextView friendNameView;
            TextView friendStatusView;
            CircleImageView friendImageView;
            CheckBox friendCheckBoxView;
        }
    }
}
