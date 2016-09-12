package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.AddDetailsNeighborFragment;
import com.madmensoftware.www.pops.Fragments.AddDetailsParentFragment;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.madmensoftware.www.pops.Fragments.SignUpFirstPageFragment;
import com.madmensoftware.www.pops.Fragments.AddDetailsPopperFragment;
import com.madmensoftware.www.pops.Fragments.SignUpSecondPageFragment;

public class SignupActivity extends SingleFragmentActivity implements SignUpFirstPageFragment.SignUpFirstPageCallbacks,
        SignUpSecondPageFragment.SignUpSecondPageCallbacks {
    public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected Fragment createFragment() {
        return new SignUpFirstPageFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = SignUpFirstPageFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onTypeSelected(String type) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment != null) {
            fragment = SignUpSecondPageFragment.newInstance(type);
            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = SignUpSecondPageFragment.newInstance(type);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackButton() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment != null) {
            fragment = SignUpFirstPageFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = SignUpFirstPageFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onSubmitButton(String email, String password, String type) {
        final ProgressBar progressBar;
        final String mType = type;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
           // create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getApplicationContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    TinyDB tinydb = new TinyDB(getApplicationContext());
                                    tinydb.putString("userType", mType);

                                    Toast.makeText(getApplicationContext(), "Success!",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(SignupActivity.this, AddUserDetails.class);
                                    startActivity(intent);
                                }
                            }
                        });

        }

    }
}