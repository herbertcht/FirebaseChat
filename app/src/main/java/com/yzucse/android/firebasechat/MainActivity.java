/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yzucse.android.firebasechat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser mFirebaseUser;
    public ProgressBar mProgressBar;
    private GlobalData globalData;
    private ChatRoomFragment mChatRoomFragment;
    private FriendsFragment mFriendsFragment;
    private SettingFragment mSettingFragment;
    private GoogleApiClient mGoogleApiClient;
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
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.chatlayout).setVisibility(View.INVISIBLE);

        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mProgressBar = findViewById(R.id.FragmentProgressBar);
        globalData = new GlobalData();
        globalData.setmFirebaseDatabaseReference(FirebaseDatabase.getInstance().getReference());
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
        if (item >= 0) Main_status[item] = true;
    }

    private void changeToChatRoomFragment() {
        if (Main_status[1]) return;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                waitForUser();
                mChatRoomFragment = new ChatRoomFragment();
                mChatRoomFragment.setGlobalData(globalData);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.myFrameLayout, mChatRoomFragment)
                        .commit();
                setStatus(1);
            }
        }).start();
        if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void changeToFriendFragment() {
        if (Main_status[0]) return;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                waitForUser();
                mFriendsFragment = new FriendsFragment();
                mFriendsFragment.setGlobalData(globalData);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.myFrameLayout, mFriendsFragment)
                        .commit();
                setStatus(0);
            }
        }).start();
        if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void initFragment() {
        changeToFriendFragment();
        //changeToChatRoomFragment();
    }

    private void init() {
        if(isOffline) return;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        globalData.setTIMEFORMAT(getString(R.string.timeFormat));
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            if (globalData.getmUsersDBR() != null) {
                final DatabaseReference usrdbr = globalData.getmUsersDBR().child(mFirebaseUser.getUid());
                if (usrdbr != null) {
                    usrdbr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class); // it might be null, so can't just assign it
                            if (user != null && globalData != null) {
                                globalData.setmUser(user);
                                Log.e("globalData.mUser", globalData.getmUser().toString());
                                globalData.setUserStatus(true);
                                if (complete())
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
            if (complete()) mProgressBar.setVisibility(ProgressBar.INVISIBLE);

            if (mFirebaseUser.getPhotoUrl() != null) {
                globalData.setmPhotoUrl(mFirebaseUser.getPhotoUrl().toString());
            }
        }
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
                Log.e("RE", "on");
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
                Log.e("DE", "off");
                isOffline =true;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(StaticValue.TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, getString(R.string.Google_server_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(StaticValue.TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == StaticValue.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(StaticValue.TAG, "Uri: " + uri.toString());

                    FriendlyMessage tempMessage = new FriendlyMessage(null, globalData.getmUser(), globalData.getmPhotoUrl(),
                            StaticValue.LOADING_IMAGE_URL);
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
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(StaticValue.TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            FriendlyMessage friendlyMessage =
                                    new FriendlyMessage(null, globalData.getmUser(),
                                            globalData.getmPhotoUrl(),
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString());
                            globalData.getmChatRoomDBR().child(globalData.getmChatroom().getChatroomID())
                                    .child(StaticValue.MESSAGES).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w(StaticValue.TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (findViewById(R.id.chatlayout) != null) {
            if (findViewById(R.id.chatlayout).getVisibility() == View.VISIBLE) {
                findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.chatlayout).setVisibility(View.INVISIBLE);
                setStatus(-1);
                changeToChatRoomFragment();
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

    }
}
