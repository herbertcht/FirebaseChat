package com.yzucse.android.firebasechat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSignUpButton;
    private Button mReturnButton;
    private EditText usernameEdit;
    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText chkpasswordEdit;
    private TextInputLayout usernameLayout;
    private TextInputLayout accoutLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout chkpasswordLayout;
    private Dialog d;

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
        StaticValue.setTextViewText((TextView) layout.findViewById(R.id.loggingText), getString(R.string.logining_in));
        d = new Dialog(this);
        d.setContentView(layout);

        mSignUpButton.setOnClickListener(this);
        mReturnButton.setOnClickListener(this);
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

    private void backToSignIn(){
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }

    private void signup() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        final String username = ((EditText) findViewById(R.id.signup_username_edit)).getText().toString();
        String account = ((EditText) findViewById(R.id.signup_account_edit)).getText().toString();
        String password = ((EditText) findViewById(R.id.signup_password_edit)).getText().toString();

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
