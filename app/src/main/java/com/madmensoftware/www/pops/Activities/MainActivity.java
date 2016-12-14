package com.madmensoftware.www.pops.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.PopperDashboardFragment;
import com.madmensoftware.www.pops.Fragments.PopperMapFragment;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.madmensoftware.www.pops.Services.PollService;
import com.orhanobut.logger.Logger;
import com.squareup.haha.perflib.Main;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser mFirebaseUser;
    private String uid;
    private String type;
    private User user;

    private DatabaseReference mUserDatabaseReference;
    private ValueEventListener mUserValueEventListener;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        Logger.i(auth.toString());
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    startActivity(new Intent(MainActivity.this, LandingPageActivity.class));
                    Logger.i("User not authorized");
                    finish();
                }
                else {
                    mFirebaseUser = firebaseUser;
                    uid = mFirebaseUser.getUid();
                    Logger.i("User is authorized, checking for a matching entry in database.");

                    Logger.i(firebaseUser.getUid());
                }
            }
        };

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);

        ValueEventListener userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.getValue() != null) {
                    User mUser = dataSnapshot.getValue(User.class);
                    type = mUser.getType();

                    Logger.i("User entry found, finding user type.");

                    switch (type) {
                        case "Popper":
                            Logger.i("User type is popper.");
                            Intent popperIntent = new Intent(MainActivity.this, PopperActivity.class);
                            Bundle popperBundle = new Bundle();
                            TinyDB tinyDB = new TinyDB(getApplicationContext());
                            tinyDB.putObject("User", mUser);
                            popperBundle.putParcelable("User", Parcels.wrap(mUser));
                            popperIntent.putExtras(popperBundle);
                            startActivity(popperIntent);
                            break;
                        case "Parent":
                            Logger.i("User type is parent.");
                            Intent parentIntent = new Intent(MainActivity.this, ParentActivity.class);
                            Bundle parentBundle = new Bundle();
                            TinyDB tinyDB2 = new TinyDB(getApplicationContext());
                            tinyDB2.putObject("User", mUser);
                            parentBundle.putParcelable("User", Parcels.wrap(mUser));
                            parentIntent.putExtras(parentBundle);
                            startActivity(parentIntent);
                            break;
                        case "Neighbor":
                            Logger.i("User type is neighbor.");
                            Intent neighborIntent = new Intent(MainActivity.this, NeighborActivity.class);
                            Bundle neighborBundle = new Bundle();
                            TinyDB tinyDB3 = new TinyDB(getApplicationContext());
                            tinyDB3.putObject("User", mUser);
                            neighborBundle.putParcelable("User", Parcels.wrap(mUser));
                            neighborIntent.putExtras(neighborBundle);
                            startActivity(neighborIntent);
                            break;
                        default:
                            break;
                    }
                }
                else {
                    Intent intent = new Intent(MainActivity.this, TypePickerActivity.class);
                    Logger.i("User entry not found in database, send to Type Picker.");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUserDatabaseReference.addValueEventListener(userValueEventListener);

        mUserValueEventListener = userValueEventListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mUserValueEventListener != null) {
            mUserDatabaseReference.removeEventListener(mUserValueEventListener);
        }
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}