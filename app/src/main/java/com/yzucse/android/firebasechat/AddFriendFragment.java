package com.yzucse.android.firebasechat;

import android.app.Fragment;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendFragment extends Fragment {
    private final String TAG = "AddGroupFragment";
    private ProgressBar mProgressBar;
    private TextView mNoitemText;
    private GlobalData globalData;
    private Button mAdd_btn;
    private LinearLayout mUserLayout;
    private CircleImageView mUserImageView;
    private TextView mUserNameText;
    private EditText mSearchEdittext;
    private Button mSearchBtn;
    private Button mClearBtn;

    public AddFriendFragment() {
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
        View view = inflater.inflate(R.layout.add_friend_fragment, container, false);
        StaticValue.setViewVisibility(getActivity().findViewById(R.id.fragmentlayout), View.VISIBLE);
        mProgressBar = view.findViewById(R.id.progressBar);
        mNoitemText = view.findViewById(R.id.noItem);
        mAdd_btn = view.findViewById(R.id.add_btn);
        mUserLayout = view.findViewById(R.id.add_friend_layout);
        mUserImageView = view.findViewById(R.id.userImageView);
        mUserNameText = view.findViewById(R.id.userNameView);
        mSearchEdittext = view.findViewById(R.id.friendSearchEditText);
        mSearchBtn = view.findViewById(R.id.searchFriendBtn);
        mClearBtn = view.findViewById(R.id.clearAll);

        Init();

        return view;
    }

    private void transToFriend() {
        getActivity().onBackPressed();
    }

    private void Init() {

        StaticValue.setViewVisibility(mSearchBtn, Button.GONE);
        StaticValue.setViewVisibility(mProgressBar, ProgressBar.GONE);
        StaticValue.setViewVisibility(mNoitemText, TextView.GONE);
        StaticValue.setViewVisibility(mUserLayout, LinearLayout.GONE);

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (StaticValue.isNullorWhitespace(s)) {
                    StaticValue.setViewVisibility(mNoitemText, View.INVISIBLE);
                    StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                    StaticValue.setViewVisibility(mSearchBtn, Button.GONE);
                    StaticValue.setViewVisibility(mUserLayout, LinearLayout.GONE);
                    StaticValue.setViewVisibility(mClearBtn, Button.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StaticValue.isNullorWhitespace(s)) {
                    StaticValue.setViewVisibility(mNoitemText, View.INVISIBLE);
                    StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                    StaticValue.setViewVisibility(mSearchBtn, Button.GONE);
                    StaticValue.setViewVisibility(mUserLayout, LinearLayout.GONE);
                    StaticValue.setViewVisibility(mClearBtn, Button.GONE);
                } else {
                    StaticValue.setViewVisibility(mSearchBtn, Button.VISIBLE);
                    StaticValue.setViewVisibility(mClearBtn, Button.VISIBLE);
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

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticValue.setViewVisibility(mProgressBar, ProgressBar.VISIBLE);
                StaticValue.setViewVisibility(mNoitemText, TextView.GONE);
                StaticValue.setViewVisibility(mUserLayout, LinearLayout.GONE);
                Query query = globalData.getmUsersDBR().orderByChild(StaticValue.EMAIL).equalTo(mSearchEdittext.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StaticValue.setViewVisibility(mProgressBar, ProgressBar.INVISIBLE);
                        if (dataSnapshot.exists()) {
                            final User user[] = new User[1];
                            for (DataSnapshot userds : dataSnapshot.getChildren())
                                user[0] = userds.getValue(User.class);
                            if (!globalData.getmUser().hasFriend(user[0].getUserID())) {
                                if (globalData.getmUser().getUserID().equals(user[0].getUserID())) {
                                    Log.e("Found User", user[0].getUserID());
                                    StaticValue.setTextViewText(mNoitemText, getString(R.string.yourself));
                                    StaticValue.setViewVisibility(mNoitemText, TextView.VISIBLE);
                                } else {
                                    StaticValue.setViewVisibility(mNoitemText, TextView.GONE);
                                    StaticValue.setViewVisibility(mUserLayout, LinearLayout.VISIBLE);
                                    StaticValue.setTextViewText(mUserNameText, user[0].getUsername());
                                    StaticValue.setAccountImage(mUserImageView, user[0].getPhotoUrl(), getActivity());
                                    mAdd_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user[0].addFriend(globalData.getmUser().getUserID(), globalData.getmUser().getUsername());
                                            globalData.getmUser().addFriend(user[0].getUserID(), user[0].getUsername());
                                            globalData.getmUser().addBlockade(user[0].getUserID());
                                            globalData.getmUsersDBR().child(user[0].getUserID()).child(StaticValue.FRIEND).setValue(user[0].getFriends());
                                            user[0].addBlockade(globalData.getmUser().getUserID());
                                            globalData.getmUsersDBR().child(user[0].getUserID()).child(StaticValue.BLOCKADE).setValue(user[0].getBlockade());
                                            globalData.getmUsersDBR().child(globalData.getmUser().getUserID()).child(StaticValue.FRIEND).setValue(globalData.getmUser().getFriends());
                                            globalData.getmUsersDBR().child(globalData.getmUser().getUserID()).child(StaticValue.BLOCKADE).setValue(globalData.getmUser().getBlockade());
                                            transToFriend();
                                        }
                                    });
                                }
                            } else {
                                StaticValue.setTextViewText(mNoitemText, getString(R.string.already_friends));
                                StaticValue.setViewVisibility(mNoitemText, TextView.VISIBLE);
                            }
                        } else {
                            StaticValue.setTextViewText(mNoitemText, getString(R.string.no_found));
                            StaticValue.setViewVisibility(mNoitemText, TextView.VISIBLE);
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