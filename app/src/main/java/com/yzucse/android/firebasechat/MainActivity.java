package com.yzucse.android.firebasechat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    final private String TAG = "MainActivity";
    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser mFirebaseUser;
    public ProgressBar mProgressBar;
    private GlobalData globalData;
    private ChatRoomFragment mChatRoomFragment;
    private FriendsFragment mFriendsFragment;
    private SettingFragment mSettingFragment;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference usrdbr;
    private ValueEventListener mValueEventListener;
    private boolean Main_status[] = new boolean[3];
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isOffline = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chat:
                    changeToChatRoomFragment();
                    return true;
                case R.id.friends:
                    changeToFriendFragment();
                    return true;
                case R.id.setting:
                    changeToSettingFragment();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.fragmentlayout).setVisibility(View.INVISIBLE);
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mProgressBar = findViewById(R.id.FragmentProgressBar);
        globalData = new GlobalData();
        globalData.setmFirebaseDatabaseReference(FirebaseDatabase.getInstance().getReference());
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class); // it might be null, so can't just assign it
                if (user != null && globalData != null) {
                    globalData.setmUser(user);
                    Log.e("globalData.mUser", globalData.getmUser().toString());
                    globalData.setUserStatus(true);
                    if (complete()) {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        setUser();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        init();
        initFragment();
    }

    private boolean complete() {
        return globalData.getmUser() != null/* && globalData.mFriend != null && globalData.mUserChatroom != null*/;
    }

    private void waitForUser() {
        while (!complete()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setStatus(int item) {
        for (int i = 0; i < Main_status.length; ++i) Main_status[i] = false;
        if (item >= 0) {
            Main_status[item] = true;
            mFriendsFragment = null;
            mChatRoomFragment = null;
            mSettingFragment = null;
        }
    }

    private void chageFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.myFrameLayout, fragment)
                .commit();
    }

    private void changeToChatRoomFragment() {
        if (Main_status[1]) return;
        StaticValue.setViewVisibility(findViewById(R.id.userLayout), View.GONE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                waitForUser();
                setStatus(1);
                mChatRoomFragment = new ChatRoomFragment();
                mChatRoomFragment.setGlobalData(globalData);
                chageFragment(mChatRoomFragment);
            }
        }).start();
        if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void changeToSettingFragment() {
        if (Main_status[2]) return;
        StaticValue.setViewVisibility(findViewById(R.id.userLayout), View.GONE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                waitForUser();
                setStatus(2);
                mSettingFragment = new SettingFragment();
                mSettingFragment.setGlobalData(globalData);
                chageFragment(mSettingFragment);
            }
        }).start();
        if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void setUser() {
        StaticValue.setViewVisibility(findViewById(R.id.userLayout), View.VISIBLE);
        StaticValue.setAccountImage((CircleImageView) findViewById(R.id.userImageView), globalData.getmUser().getPhotoUrl(), this);
        StaticValue.setTextViewText((TextView) findViewById(R.id.userNameView), StaticValue.MaxLengthText(globalData.getmUser().getUsername()));
        StaticValue.setTextViewText((TextView) findViewById(R.id.userSignView), StaticValue.MaxLengthText(globalData.getmUser().getSign()));
    }

    private void changeToFriendFragment() {
        if (Main_status[0]) return;
        StaticValue.setViewVisibility(findViewById(R.id.userLayout), View.INVISIBLE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                waitForUser();
                setStatus(0);
                mFriendsFragment = new FriendsFragment();
                mFriendsFragment.setGlobalData(globalData);
                chageFragment(mFriendsFragment);
            }
        }).start();
        if (complete()) {
            setUser();
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    private void initFragment() {
        changeToFriendFragment();
        //changeToChatRoomFragment();
    }

    private void init() {
        if (isOffline) return;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        globalData.setTIMEFORMAT(getString(R.string.timeFormat));

        //unsubscribeFromAllTopic();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(mFirebaseUser.getUid());
            Log.e("FCM-debug", "I subscribeToTopic " + mFirebaseUser.getUid());
            if (globalData.getmUsersDBR() != null) {
                usrdbr = globalData.getmUsersDBR().child(mFirebaseUser.getUid());
                if (usrdbr != null) {
                    usrdbr.addValueEventListener(mValueEventListener);
                }

               /* final DatabaseReference frienddbr = usrdbr.child(StaticValue.FRIEND);
                if (frienddbr != null) {
                    frienddbr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<List<IDAndName>> genericTypeIndicator = new GenericTypeIndicator<List<IDAndName>>() {
                            };
                            List<IDAndName> friends = dataSnapshot.getValue(genericTypeIndicator);
                            globalData.setmFriend(friends);
                            Log.e("Friends", StaticValue.ListIDAndNametoString(globalData.mFriend));
                            if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                final DatabaseReference userchatroomdbr = usrdbr.child(StaticValue.CHATROOM);
                if (userchatroomdbr != null) {
                    userchatroomdbr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<List<IDAndName>> genericTypeIndicator = new GenericTypeIndicator<List<IDAndName>>() {
                            };
                            List<IDAndName> chatrooms = dataSnapshot.getValue(genericTypeIndicator);
                            globalData.setmUserChatroom(chatrooms);
                            if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            Log.e("UserChatroom", StaticValue.ListIDAndNametoString(globalData.mUserChatroom));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }*/

            }
            if (complete()) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                setUser();
            }

            if (mFirebaseUser.getPhotoUrl() != null) {
                globalData.setmPhotoUrl(mFirebaseUser.getPhotoUrl().toString());
            }
        }
    }

    private void unsubscribeFromAllTopic() {
        globalData.getmUsersDBR().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(d.getKey());
                        Log.e("FCM-debug", "REMOVE " + d.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if mUser is signed in.
        // TODO: Add code to check if mUser is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        if (globalData != null)
            if (globalData.getmUser() != null) {
                globalData.setUserStatus(true);
                isOffline = false;
            }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (globalData != null)
            if (globalData.getmUser() != null) {
                globalData.setUserStatus(false);
                isOffline = true;
                usrdbr.removeEventListener(mValueEventListener);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                globalData.setUserStatus(false);
                globalData = null;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case R.id.about_menu:
                AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.about)
                        .setMessage(R.string.about_message)
                        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, getString(R.string.Google_server_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().commit();
            return;
        }
        if (mFriendsFragment != null || mChatRoomFragment != null || mSettingFragment != null) {
            if (findViewById(R.id.fragmentlayout).getVisibility() == View.VISIBLE) {
                findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.fragmentlayout).setVisibility(View.INVISIBLE);
                setStatus(-1);
                if (mFriendsFragment != null) changeToFriendFragment();
                else if (mChatRoomFragment != null) changeToChatRoomFragment();
                else if(mSettingFragment!=null)changeToSettingFragment();
                return;
            }
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.closeMSG), Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void addGroup(View view) {
        findViewById(R.id.mainLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.fragmentlayout).setVisibility(View.VISIBLE);
        AddGroupFragment mAddGroupFragment = new AddGroupFragment();
        mAddGroupFragment.setGlobalData(globalData);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentlayout, mAddGroupFragment)
                .commit();
    }

}
