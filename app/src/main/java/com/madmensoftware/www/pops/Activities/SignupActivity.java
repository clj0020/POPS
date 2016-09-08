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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmensoftware.www.pops.Fragments.SignUpNeighborFragment;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.madmensoftware.www.pops.Fragments.SignUpFirstPageFragment;
import com.madmensoftware.www.pops.Fragments.SignUpPopperFragment;
import com.madmensoftware.www.pops.Fragments.SignUpSecondPageFragment;

import java.util.Date;

public class SignupActivity extends SingleFragmentActivity implements SignUpFirstPageFragment.SignUpFirstPageCallbacks, SignUpSecondPageFragment.SignUpSecondPageCallbacks, SignUpPopperFragment.SignUpPopperCallbacks, SignUpNeighborFragment.SignUpNeighborCallbacks {
    public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;


    private void writeNewUser(User mUser) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(mUser);
    }

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
    public void onPopperNextButton(String userId, String name, int age, int zip_code, String transportation, int radius, double goal, Date goalDate) {
        User user = new User();

        user.setName(name);
        user.setAge(age);
        user.setZipCode(zip_code);
        user.setTransportationType(transportation);
        user.setRadius(radius);
        user.setGoal(goal);
        user.setGoalDate(goalDate);
        user.setEarned(0);
        user.setType("Popper");

        writeNewUser(user);

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNeighborSubmit(String name, String address, int zip_code, int organizationCode) {
        User user = new User();

        user.setName(name);
        user.setAddress(address);
        user.setZipCode(zip_code);
        user.setOrganizationCode(organizationCode);
        user.setType("Neighbor");

        writeNewUser(user);

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPopperBackButton() {

    }

    @Override
    public void onNextButton(String email, String password, String type) {

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
                                    FragmentManager fm = getSupportFragmentManager();
                                    Fragment fragment = fm.findFragmentById(R.id.fragment_container);

                                    switch (mType) {
                                        case "Popper":
                                            FirebaseUser firebasePopper = FirebaseAuth.getInstance().getCurrentUser();
                                            if (firebasePopper != null) {
                                                String email = firebasePopper.getEmail();
                                                String uid = firebasePopper.getUid();
                                                User user = new User();
                                                user.setEmail(email);
                                                user.setType("Popper");
                                                user.setUid(uid);


                                                fragment = SignUpPopperFragment.newInstance(user.getUid());
                                                fm.beginTransaction()
                                                        .replace(R.id.fragment_container, fragment)
                                                        .commit();
                                            }

                                            break;
                                        case "Parent":

                                            break;
                                        case "Neighbor":
                                            FirebaseUser firebaseNeighbor = FirebaseAuth.getInstance().getCurrentUser();
                                            if (firebaseNeighbor != null) {
                                                String email = firebaseNeighbor.getEmail();
                                                String uid = firebaseNeighbor.getUid();
                                                User user = new User();
                                                user.setEmail(email);
                                                user.setType("Neighbor");
                                                user.setUid(uid);

                                                fragment = SignUpNeighborFragment.newInstance(user.getUid());
                                                fm.beginTransaction()
                                                        .replace(R.id.fragment_container, fragment)
                                                        .commit();
                                            }

                                            break;
                                    }
                                }
                            }
                        });

        }

    }
}