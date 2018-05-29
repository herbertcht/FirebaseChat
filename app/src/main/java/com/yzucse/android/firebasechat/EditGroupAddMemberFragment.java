package com.yzucse.android.firebasechat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditGroupAddMemberFragment extends Fragment {
    private final String TAG = "EditGroupAddMemberFragment";
    private ListView mFriendsListView;
    private ProgressBar mProgressBar;
    private TextView mNoitemText;
    private GlobalData globalData;
    private Button mCreate_btn;
    private EditText mSearchEdittext;
    private Button mClearBtn;
    private String STATUS[];
    private ArrayList<IdAndName> adapter;
    private ArrayList<String> checkLst;

    public EditGroupAddMemberFragment() {
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
        mClearBtn = view.findViewById(R.id.clearAll);

        adapter = new ArrayList<>();
        checkLst = new ArrayList<>();
        STATUS = new String[]{getString(R.string.offline), getString(R.string.online)};
        FriendsInit();

        return view;
    }

    final private void setAllFriends() {
        for (Map.Entry<String, String> item : globalData.getmUser().getFriends().entrySet()) {
            if (!globalData.getmChatroom().hasMember(item.getKey()))
                adapter.add(new IdAndName(item.getKey(), item.getValue()));
        }

        mFriendsListView.setAdapter(new FriendsAdapter(getActivity(), adapter));
    }

    public void FriendsInit() {
        //getActivity().setContentView(R.layout.chat_list);

        StaticValue.setButtonText(mCreate_btn, getString(R.string.add_to_group));
        mCreate_btn.setEnabled(false);

        if (StaticValue.isNullorEmptyMap(globalData.getmUser().getFriends()) || globalData.AllFriendsInChatRoom()) {
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
                    StaticValue.setViewVisibility(mClearBtn, Button.GONE);
                } else {
                    StaticValue.setViewVisibility(mProgressBar, ProgressBar.VISIBLE);
                    StaticValue.setViewVisibility(mClearBtn, Button.VISIBLE);
                    IdAndName addValue;
                    for (String name : globalData.getmUser().getAllFriendsNames()) {
                        if (name.contains(s)) {
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

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdittext.setText("");
            }
        });

        mCreate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String id : checkLst)
                    globalData.getmChatroom().addMember(id);
                DatabaseReference groupdbr = globalData.getmGroupDBR().child(globalData.getmChatroom().getChatroomID());
                groupdbr.child(StaticValue.USERID).setValue(globalData.getmChatroom().getUserID());

                for (String id : globalData.getmChatroom().getUserID().keySet()) {
                    globalData.getmUsersDBR().child(id).child(StaticValue.CHATROOM)
                            .child(globalData.getmChatroom().getChatroomID())
                            .setValue(globalData.getmChatroom().getChatroomName());
                    globalData.getmUsersDBR().child(id).child(StaticValue.GROUPS)
                            .child(globalData.getmChatroom().getChatroomID())
                            .setValue(true);
                }

                transToEdit();
            }
        });

        setAllFriends();
    }

    private void transToEdit() {
        EditGroupFragment mEditGroupFragment = new EditGroupFragment();
        mEditGroupFragment.setGlobalData(globalData);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentlayout, mEditGroupFragment)
                .commit();
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
