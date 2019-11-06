package com.example.drugconsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {


    @BindView(R.id.gotoEmailButton) Button verifying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        verifying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ResetPasswordActivity.this,"Check your email ",Toast.LENGTH_LONG).show();            }
        });
    }
}
