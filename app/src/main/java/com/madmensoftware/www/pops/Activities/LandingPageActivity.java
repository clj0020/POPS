package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingPageActivity extends AppCompatActivity {

    @BindView(R.id.login_button) Button loginButton;
    @BindView(R.id.signup_button) Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingPageActivity.this, LoginActivity.class));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingPageActivity.this, SignupActivity.class));
            }
        });
    }
}
