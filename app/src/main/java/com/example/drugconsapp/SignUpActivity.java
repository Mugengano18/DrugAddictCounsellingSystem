package com.example.drugconsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG=SignUpActivity.class.getSimpleName();

    @BindView(R.id.iUserName)EditText usernameOrg;
    @BindView(R.id.iPassword)EditText passwordOrg;
    @BindView(R.id.iPassword1)EditText comfirmPasswordOrg;
    @BindView(R.id.iEmail)EditText emailOrg;
    @BindView(R.id.signupButton) Button signUpOrg;
    @BindView(R.id.swapL) TextView loginOrg;
    @BindView(R.id.check) CheckBox verify;

    private FirebaseAuth authorized;
    private FirebaseAuth.AuthStateListener authorizedThListener;
    private ProgressDialog authorizedProgressDialog;

    private String fUserName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        ButterKnife.bind(this);

        authorized=FirebaseAuth.getInstance();
        createAuthStateListener();
        createAuthProgressDialog();


        loginOrg.setOnClickListener(this);
        signUpOrg.setOnClickListener(this);

    }

    private void createAuthProgressDialog() {
        authorizedProgressDialog = new ProgressDialog(this);
        authorizedProgressDialog.setTitle("Loading...");
        authorizedProgressDialog.setMessage("Authenticating with Firebase...");
        authorizedProgressDialog.setCancelable(true);
    }


    @Override
    public void onClick(View v) {
        if(v==loginOrg){
            Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if(v==signUpOrg){
            if(verify.isChecked()){
                createNewAccount();
            }
            else{
                Animation clignote= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
                verify.startAnimation(clignote);
            }
        }
    }

    private void createNewAccount() {

        final String username=usernameOrg.getText().toString().trim();
        final String email = emailOrg.getText().toString().trim();
        String password = passwordOrg.getText().toString().trim();
        String confirmPassword = comfirmPasswordOrg.getText().toString().trim();
        fUserName=usernameOrg.getText().toString().trim();


        boolean validEmail=isValidEmail(email);
        boolean validUserName=isValidUserName(username);
        boolean validPassword=isValidPassword(password,confirmPassword);

        if(!validEmail||!validUserName||!validPassword)return;

        authorizedProgressDialog.show();

        authorized.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authorizedProgressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication successful");

                            createFirebaseUserProfile(task.getResult().getUser());

                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        authorized.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication successful");
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createAuthStateListener() {
        authorizedThListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

        };
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            emailOrg.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidUserName(String fUserName) {
        if (fUserName.equals("")) {
            usernameOrg.setError("Please enter your username");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            passwordOrg.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            passwordOrg.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {

        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(fUserName).build();

        user.updateProfile(addProfileName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "The displayed username has been set", Toast.LENGTH_LONG).show();

                        }
                    }

                });
    }
    @Override
    public void onStart() {
        super.onStart();
        authorized.addAuthStateListener(authorizedThListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authorizedThListener != null) {
            authorized.removeAuthStateListener(authorizedThListener);
        }
    }
}
