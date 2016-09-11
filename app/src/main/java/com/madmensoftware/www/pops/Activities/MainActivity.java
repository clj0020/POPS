package com.madmensoftware.www.pops.Activities;

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
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser mFirebaseUser;
    private String uid;
    private String type;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Bundle b = this.getIntent().getExtras();
//        if (b != null) {
//            user = Parcels.unwrap(b.getParcelable("User"));
//
//            type = user.getType();
//
//            switch (type) {
//                case "Popper":
//                    Intent popperIntent = new Intent(MainActivity.this, PopperActivity.class);
//                    Bundle popperBundle = new Bundle();
//                    popperBundle.putParcelable("User", Parcels.wrap(user));
//                    popperIntent.putExtras(popperBundle);
//                    startActivity(popperIntent);
//                    break;
//                case "Parent":
//                    break;
//                case "Neighbor":
//                    Intent neighborIntent = new Intent(MainActivity.this, NeighborActivity.class);
//                    Bundle neighborBundle = new Bundle();
//                    neighborBundle.putParcelable("User", Parcels.wrap(user));
//                    neighborIntent.putExtras(neighborBundle);
//                    startActivity(neighborIntent);
//                    break;
//                default:
//                    break;
//            }
//
//        }

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //auth.signOut();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    mFirebaseUser = firebaseUser;
                    uid = mFirebaseUser.getUid();

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("users/" + uid);


                    // Read from the database
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            if(dataSnapshot.exists()) {
                                User mUser = dataSnapshot.getValue(User.class);
                                type = mUser.getType();

                                switch (type) {
                                    case "Popper":
                                        Intent popperIntent = new Intent(MainActivity.this, PopperActivity.class);
                                        Bundle popperBundle = new Bundle();
                                        popperBundle.putParcelable("User", Parcels.wrap(mUser));
                                        popperIntent.putExtras(popperBundle);
                                        startActivity(popperIntent);
                                        break;
                                    case "Parent":
                                        break;
                                    case "Neighbor":
                                        Intent neighborIntent = new Intent(MainActivity.this, NeighborActivity.class);
                                        Bundle neighborBundle = new Bundle();
                                        neighborBundle.putParcelable("User", Parcels.wrap(mUser));
                                        neighborIntent.putExtras(neighborBundle);
                                        startActivity(neighborIntent);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            else {
                                auth.signOut();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {


                        }
                    });
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}