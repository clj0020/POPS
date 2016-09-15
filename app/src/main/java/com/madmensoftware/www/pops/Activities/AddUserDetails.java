package com.madmensoftware.www.pops.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ErrorDialogFragment;
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
import com.madmensoftware.www.pops.Fragments.NeighborCreditCardFormFragment;
import com.madmensoftware.www.pops.Fragments.ParentImportantInfoFragment;
import com.madmensoftware.www.pops.Fragments.SignUpFirstPageFragment;
import com.madmensoftware.www.pops.Fragments.SignUpSecondPageFragment;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import org.parceler.Parcels;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Random;

public class AddUserDetails extends AppCompatActivity implements AddDetailsPopperFragment.SignUpPopperCallbacks,
        AddDetailsNeighborFragment.SignUpNeighborCallbacks, AddDetailsParentFragment.SignUpParentCallbacks,
        ParentImportantInfoFragment.ParentImportantInfoCallbacks, NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks {

    private static final String TAG = "AddUserDetails:";
    private static final String TEST_KEY = "pk_test_9SdGQF1ZibEEnbJ3vYmBaAFj";
    private static final String PUBLISHABLE_KEY = "";

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

        final DatabaseReference organizationRef = mDatabase.child("organizations").child(organizationCode + "");
        mDatabase.child("parent-codes").child(parentCode + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String parentUid = dataSnapshot.getValue().toString();
                    mDatabase.child("users").child(parentUid).child("childUid").setValue(auth.getCurrentUser().getUid());
                    popper.setParentUid(parentUid);
                    Log.i("UserDetails", "ParentUid" + parentUid);

                    mDatabase.child("users").child(parentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User parent = dataSnapshot.getValue(User.class);
                            popper.setSafeWord(parent.getSafeWord());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    organizationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                popper.setOrganizationName(dataSnapshot.child("name").getValue().toString());

                                Log.i(TAG + "Org: ", dataSnapshot.child("name").getValue().toString());

                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                mDatabase.child("users").child(firebaseUser.getUid()).setValue(popper);

                                TinyDB tinyDB = new TinyDB(getApplicationContext());
                                tinyDB.putObject("User", popper);


                                Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(AddUserDetails.this, "Organization Code not found.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    Toast.makeText(AddUserDetails.this, "Parent Code not found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        TinyDB tinyDB = new TinyDB(this);
        tinyDB.putObject("User", neighbor);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        if (fragment == null) {
            fragment = NeighborCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = NeighborCreditCardFormFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onCreditCardFormSubmit(String cardNumber, String expirationMonth, String expirationYear, String ccv, String postalCode) {
        int expMonth = Integer.parseInt(expirationMonth);
        int expYear = Integer.parseInt(expirationYear);

        Card card = new Card(
                cardNumber,
                expMonth,
                expYear,
                ccv
        );


        boolean validation = card.validateCard();
        if (validation) {
            new Stripe().createToken(
                    card,
                    TEST_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            TinyDB tinyDB = new TinyDB(getApplicationContext());
                            User neighbor = (User) tinyDB.getObject("User", User.class);

                            neighbor.setStripeToken(token.toString());

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            mDatabase.child("users").child(firebaseUser.getUid()).setValue(neighbor);

                            Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
                            startActivity(intent);
                        }
                        public void onError(Exception error) {
                        }
                    });
        }
        else if (!card.validateNumber()) {
            Toast.makeText(this, "The card number that you entered is invalid", Toast.LENGTH_LONG).show();
        }
        else if (!card.validateExpiryDate()) {
            Toast.makeText(this, "The expiration date that you entered is invalid", Toast.LENGTH_LONG).show();
        }
        else if (!card.validateCVC()) {
            Toast.makeText(this, "The CVC code that you entered is invalid", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "The card details that you entered is invalid", Toast.LENGTH_LONG).show();
        }
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

        String safeWord = generateRandomSafeWord();
        parent.setSafeWord(safeWord);

        parent.setType("Parent");

        Log.i("UserDetails", "ParentAccessCode" + parent.getAccessCode());
        Log.i("UserDetails", "ParentSafeWord" + parent.getSafeWord());


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(parent);

        mDatabase.child("parent-codes").child(parentCode + "").setValue(firebaseUser.getUid());
        mDatabase.child("safe-words").child(safeWord).setValue(firebaseUser.getUid());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_details_fragment_container);

        if (fragment == null) {
            fragment = ParentImportantInfoFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
        else {
            fragment = ParentImportantInfoFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.user_details_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onImportantInfoClose() {
        Intent intent = new Intent(AddUserDetails.this, MainActivity.class);
        startActivity(intent);
    }

    public int generateUniqueCode() {
        final int randomPIN = (int)(Math.random()*9000)+1000;

        Query parentCodeQuery = mDatabase.child("parent-codes").equalTo(randomPIN);
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

    public String generateRandomSafeWord() {
        String[] safeWords = {"FISH", "CHIP", "TACO"};

        Random rand = new Random();
        int randomNum = rand.nextInt((2 - 0) + 1) + 0;
        return safeWords[randomNum];
    }
}
