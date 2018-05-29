package com.yzucse.android.firebasechat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final private String TAG = "SignUpActivity";
    private Button mSignUpButton;
    private Button mReturnButton;
    private CircleImageView userImage;
    private EditText usernameEdit;
    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText chkpasswordEdit;
    private TextInputLayout usernameLayout;
    private TextInputLayout accoutLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout chkpasswordLayout;
    private Dialog d;
    private String photoURL;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignUpButton = findViewById(R.id.signup_button_send);
        mReturnButton = findViewById(R.id.signup_button_return);

        usernameEdit = findViewById(R.id.signup_username_edit);
        accountEdit = findViewById(R.id.signup_account_edit);
        passwordEdit = findViewById(R.id.signup_password_edit);
        chkpasswordEdit = findViewById(R.id.signup_chkpassword_edit);
        userImage = findViewById(R.id.userImageView);

        usernameLayout = findViewById(R.id.signup_username_layout);
        accoutLayout = findViewById(R.id.signup_account_layout);
        passwordLayout = findViewById(R.id.signup_password_layout);
        chkpasswordLayout = findViewById(R.id.signup_chkpassword_layout);
        usernameLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        accoutLayout.setErrorEnabled(true);

        final LayoutInflater factory = getLayoutInflater();
        View prompt = factory.inflate(R.layout.logging_layout, null);
        LinearLayout layout = prompt.findViewById(R.id.logging);
        StaticValue.setTextViewText((TextView) layout.findViewById(R.id.loggingText), getString(R.string.registering));
        d = new Dialog(this);
        d.setCanceledOnTouchOutside(false);
        d.setContentView(layout);

        mSignUpButton.setOnClickListener(this);
        mReturnButton.setOnClickListener(this);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, StaticValue.REQUEST_IMAGE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button_send:
                String username = usernameEdit.getText().toString();
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String chkpassword = chkpasswordEdit.getText().toString();
                boolean access = true;
                if (StaticValue.isNullorWhitespace(username)) {
                    usernameLayout.setError(getString(R.string.plz_input_username));
                    access = false;
                } else usernameLayout.setError("");
                if (StaticValue.isNullorWhitespace(account)) {
                    accoutLayout.setError(getString(R.string.plz_input_accout));
                    access = false;
                } else accoutLayout.setError("");
                if (StaticValue.isNullorWhitespace(password)) {
                    passwordLayout.setError(getString(R.string.plz_input_pw));
                    access = false;
                } else passwordLayout.setError("");
                if (StaticValue.isNullorWhitespace(chkpassword)) {
                    chkpasswordLayout.setError(getString(R.string.plz_input_chkpw));
                    access = false;
                } else chkpasswordLayout.setError("");
                if (!access) return;
                usernameLayout.setError("");
                accoutLayout.setError("");
                passwordLayout.setError("");
                if (!chkpassword.equals("") && !password.equals(chkpassword)) {
                    chkpasswordLayout.setError(getString(R.string.plz_input_chkpw_again));
                    return;
                }
                chkpasswordLayout.setError("");
                d.show();
                signup();
                break;
            case R.id.signup_button_return:
                backToSignIn();
                break;
            default:
                break;

        }
    }

    private void backToSignIn() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }

    private void signup() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        final String username = usernameEdit.getText().toString();
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        mFirebaseAuth.createUserWithEmailAndPassword(account, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            mFirebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            String uid = mFirebaseAuth.getCurrentUser().getUid();
                            User saveUser = new User(uid, firebaseUser.getDisplayName());
                            saveUser.setPhotoUrl(photoURL);
                            if (Strings.isEmptyOrWhitespace(saveUser.getUsername()))
                                saveUser.setUsername(username);
                            saveUser.setEmail(firebaseUser.getEmail());
                            if (firebaseUser.getPhotoUrl() != null)
                                saveUser.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                            FirebaseDatabase.getInstance().getReference().child(StaticValue.Users).child(uid).setValue(saveUser);
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                            mFirebaseAuth.signOut();
                            d.dismiss();
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            d.dismiss();
                        }
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
                    mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                FirebaseDatabase.getInstance().getReference().child(StaticValue.ANONYMOUS).push()
                                        .setValue(StaticValue.LOADING_IMAGE_URL, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError,
                                                                   DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    String key = databaseReference.getKey();
                                                    StorageReference storageReference =
                                                            FirebaseStorage.getInstance()
                                                                    .getReference(mAuth.getCurrentUser().getUid())
                                                                    .child(key)
                                                                    .child(uri.getLastPathSegment());

                                                    putImageInStorage(storageReference, uri);
                                                } else {
                                                    Log.w(TAG, "Unable to write message to database.",
                                                            databaseError.toException());
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });


                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri) {
        final Activity thisAct = this;
        storageReference.putFile(uri).addOnCompleteListener(thisAct,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            photoURL = task.getResult().getMetadata().getDownloadUrl()
                                    .toString();

                            StaticValue.setAccountImage(userImage, photoURL, thisAct);
                            mAuth.getCurrentUser().delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User account deleted.");
                                            }
                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference().child(StaticValue.ANONYMOUS).removeValue();
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        backToSignIn();
        super.onBackPressed();
    }
}
