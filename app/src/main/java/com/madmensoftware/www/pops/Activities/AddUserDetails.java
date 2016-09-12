package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.AddDetailsNeighborFragment;
import com.madmensoftware.www.pops.Fragments.AddDetailsParentFragment;
import com.madmensoftware.www.pops.Fragments.AddDetailsPopperFragment;
import com.madmensoftware.www.pops.Fragments.SignUpFirstPageFragment;
import com.madmensoftware.www.pops.Fragments.SignUpSecondPageFragment;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

public class AddUserDetails extends AppCompatActivity implements AddDetailsPopperFragment.SignUpPopperCallbacks,
        AddDetailsNeighborFragment.SignUpNeighborCallbacks, AddDetailsParentFragment.SignUpParentCallbacks {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    startActivity(new Intent(AddUserDetails.this, LoginActivity.class));
                    finish();
                }
                else {

                }
            }
        };


        mDatabase = FirebaseDatabase.getInstance().getReference();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        TinyDB tinyDb = new TinyDB(this);
        String type = tinyDb.getString("userType");

        switch (type) {
            case "Popper":
                if (fragment == null) {
                    fragment = AddDetailsPopperFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
            case "Parent":
                if (fragment == null) {
                    fragment = AddDetailsParentFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
            case "Neighbor":
                if (fragment == null) {
                    fragment = AddDetailsNeighborFragment.newInstance();
                    fm.beginTransaction()
                            .add(R.id.user_details_fragment_container, fragment)
                            .commit();
                }
                break;
        }
    }


    @Override
    public void onPopperSubmit(String name, int age, int zip_code, String transportation, int radius, double goal, long goalDateLong, int parentCode, int organizationCode) {
        FirebaseUser firebasePopper = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebasePopper.getEmail();

        final User popper = new User();

        Query parentCodeQuery = FirebaseDatabase.getInstance().getReference().child("parent-codes").equalTo(parentCode);
        parentCodeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("AddUserDetails", dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query organizationCodeQuery = FirebaseDatabase.getInstance().getReference().child("organization-codes").equalTo(organizationCode);
        organizationCodeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("AddUserDetails", dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            popper.setName(name);
            popper.setEmail(email);
            popper.setAge(age);
            popper.setZipCode(zip_code);
            popper.setTransportationType(transportation);
            popper.setRadius(radius);
            popper.setGoal(goal);
            popper.setGoalDate(goalDateLong);
            popper.setEarned(0);
            popper.setType("Popper");

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child("users").child(firebaseUser.getUid()).setValue(popper);

            Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
            startActivity(intent);



    }

    @Override
    public void onNeighborSubmit(String name, String address, int zip_code, int organizationCode) {
        FirebaseUser firebaseNeighbor = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseNeighbor.getEmail();

        User neighbor = new User();

        neighbor.setName(name);
        neighbor.setEmail(email);
        neighbor.setAddress(address);
        neighbor.setZipCode(zip_code);
        neighbor.setOrganizationCode(organizationCode);
        neighbor.setType("Neighbor");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(neighbor);


        Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onParentSubmit(String name, int phone) {
        FirebaseUser firebaseParent = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseParent.getEmail();

        User parent = new User();

        parent.setName(name);
        parent.setPhone(phone);
        parent.setEmail(email);


        int parentCode = generateUniqueCode();
        parent.setAccessCode(parentCode);


        parent.setType("Parent");


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(parent);

        mDatabase.child("parent-codes").child(firebaseUser.getUid()).setValue(parentCode);


        Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
        startActivity(intent);
    }

    public int generateUniqueCode() {
        final int randomPIN = (int)(Math.random()*9000)+1000;

        Query parentCodeQuery = mDatabase.child("parent-codes").equalTo(randomPIN);
        // My top posts by number of stars
        parentCodeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        if (postSnapshot.getValue() != null) {
                            generateUniqueCode();
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return randomPIN;
    }

}
