package com.example.drugconsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.iUserNameL)
    EditText usernameOl;
    @BindView(R.id.iPasswordL)
    EditText passwordOl;
    @BindView(R.id.iEmailL)
    EditText emailOl;
    @BindView(R.id.loginButton)
    Button login;
    @BindView(R.id.swap)
    TextView signUp;
    @BindView(R.id.forget)
    TextView resetPassword;

    private FirebaseAuth authorized;

    private FirebaseAuth.AuthStateListener authorizedListener;

    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        authorized = FirebaseAuth.getInstance();
        createAuthProgressDialog();


        authorizedListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        resetPassword.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v == login) {
            loginWithPassword();
        }

        if (v == signUp) {
            Intent intent1 = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent1);
            finish();
        }

        if (v == resetPassword) {
            Intent intent2 = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent2);
            finish();

        }
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        authorized.addAuthStateListener(authorizedListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authorizedListener != null) {
            authorized.removeAuthStateListener(authorizedListener);
        }
    }

    private void loginWithPassword() {
        String username = usernameOl.getText().toString().trim();
        String password = passwordOl.getText().toString().trim();
        String email = emailOl.getText().toString().trim();

        if (username.equals("")) {
            usernameOl.setError("Please enter your username");
            return;
        }
        if (password.equals("")) {
            passwordOl.setError("Password cannot be blank");
            return;
        }
        if (email.equals("")) {
            emailOl.setError("Please enter your email");
            return;
        }
        mAuthProgressDialog.show();
        authorized.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mAuthProgressDialog.dismiss();
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
                            startActivity(intent);
                        }
                    }
                });


    }


}

