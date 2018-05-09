package com.yzucse.android.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

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

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignUpButton = (Button) findViewById(R.id.signup_button_send);
        mReturnButton = (Button) findViewById(R.id.signup_button_return);

        usernameEdit = (EditText) findViewById(R.id.signup_username_edit);
        accountEdit = (EditText) findViewById(R.id.signup_account_edit);
        passwordEdit = (EditText) findViewById(R.id.signup_password_edit);
        chkpasswordEdit = (EditText) findViewById(R.id.signup_chkpassword_edit);

        usernameLayout = (TextInputLayout) findViewById(R.id.signup_username_layout);
        accoutLayout = (TextInputLayout) findViewById(R.id.signup_account_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.signup_password_layout);
        chkpasswordLayout = (TextInputLayout) findViewById(R.id.signup_chkpassword_layout);
        usernameLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        accoutLayout.setErrorEnabled(true);

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
                if (TextUtils.isEmpty(username)) {
                    usernameLayout.setError(getString(R.string.plz_input_username));
                    access = false;
                }
                if (TextUtils.isEmpty(account)) {
                    accoutLayout.setError(getString(R.string.plz_input_accout));
                    access = false;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError(getString(R.string.plz_input_pw));
                    access = false;
                }
                if (TextUtils.isEmpty(chkpassword)) {
                    chkpasswordLayout.setError(getString(R.string.plz_input_chkpw));
                    access = false;
                }
                if (!access) return;
                usernameLayout.setError("");
                accoutLayout.setError("");
                passwordLayout.setError("");
                if (!chkpassword.equals("") && !password.equals(chkpassword)) {
                    chkpasswordLayout.setError(getString(R.string.plz_input_chkpw_again));
                    return;
                }
                chkpasswordLayout.setError("");
                signup();
                break;
            case R.id.signup_button_return:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
                break;
            default:
                break;

        }
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
                            Toast.makeText(SignUpActivity.this, "註冊成功，現在可以用新帳號登入", Toast.LENGTH_SHORT).show();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                            mFirebaseAuth.signOut();
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
