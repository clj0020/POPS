package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmensoftware.www.pops.Dialogs.EnterNameDialog;
import com.madmensoftware.www.pops.Dialogs.PickRadiusDialog;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;
import com.squareup.haha.perflib.Main;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypePickerActivity extends AppCompatActivity implements View.OnClickListener, PickRadiusDialog.PickRadiusDialogCallbacks, EnterNameDialog.EnterNameDialogCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.popperBtn) Button mPopperBtn;
    @BindView(R.id.parentBtn) Button mParentBtn;
    @BindView(R.id.neighborBtn) Button mNeighborBtn;
    @BindView(R.id.typePickerCancelButton) Button mCancelButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean FacebookIsProvider;
    private boolean GoogleIsProvider;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_type_picker);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mCallbackManager = CallbackManager.Factory.create();

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]


        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        mPopperBtn.setOnClickListener(this);
        mParentBtn.setOnClickListener(this);
        mNeighborBtn.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                Logger.i("User is signed in with Facebook");
                FacebookIsProvider = true;
            }
            else if (user.getProviderId().equals("google.com")) {
                Logger.i("User is signed in with Google");
                GoogleIsProvider = true;
            }
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Logger.i("onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Logger.i("onAuthStateChanged:signed_out");
                    startActivity(new Intent(TypePickerActivity.this, SignUpActivityUpdated.class));
                }
            }
        };
        // [END auth_state_listener]
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.popperBtn:
                TinyDB tinyDB = new TinyDB(getApplicationContext());
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final User popper = new User();
                popper.setType("Popper");
                popper.setUid(firebaseUser.getUid());

                for (UserInfo profile : firebaseUser.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();

                    // UID specific to the provider
                    String uid = profile.getUid();

                    String name = profile.getDisplayName();
                    String email = profile.getEmail();

                    popper.setName(name);
                    popper.setEmail(email);
//                    if (providerId.equals("facebook.com")) {
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                (AccessToken) tinyDB.getObject("FacebookToken", AccessToken.class),
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(
//                                            JSONObject object,
//                                            GraphResponse response) {
//                                        try {
//                                            String name = object.getString("name");
//                                            String email = object.getString("email");
//                                            int age = object.getJSONObject("age_range").getInt("min");
//
//                                            popper.setName(name);
//                                            popper.setEmail(email);
//                                            popper.setAge(age);
//
//                                            Logger.i(name);
//                                            Logger.i(email);
//                                            Logger.i(age + "");
//                                        }
//                                        catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,name,link,email,age_range");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//                    }
                };

                showRadiusDialog(popper);
                break;
            case R.id.parentBtn:
                TinyDB parentTinyDB = new TinyDB(getApplicationContext());
                FirebaseUser firebaseParent = FirebaseAuth.getInstance().getCurrentUser();
                final User parent = new User();
                parent.setType("Parent");
                parent.setUid(firebaseParent.getUid());

                for (UserInfo profile : firebaseParent.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();

                    // UID specific to the provider
                    String uid = profile.getUid();

                    String name = profile.getDisplayName();
                    String email = profile.getEmail();

                    parent.setName(name);
                    parent.setEmail(email);
//                    if (providerId.equals("facebook.com")) {
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                (AccessToken) tinyDB.getObject("FacebookToken", AccessToken.class),
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(
//                                            JSONObject object,
//                                            GraphResponse response) {
//                                        try {
//                                            String name = object.getString("name");
//                                            String email = object.getString("email");
//                                            int age = object.getJSONObject("age_range").getInt("min");
//
//                                            popper.setName(name);
//                                            popper.setEmail(email);
//                                            popper.setAge(age);
//
//                                            Logger.i(name);
//                                            Logger.i(email);
//                                            Logger.i(age + "");
//                                        }
//                                        catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,name,link,email,age_range");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//                    }
                };
                mDatabase.child("users").child(firebaseParent.getUid()).setValue(parent);

                if (FacebookIsProvider) {
                    startActivity(new Intent(TypePickerActivity.this, MainActivity.class));
                }
                else {
                    startActivity(new Intent(TypePickerActivity.this, MainActivity.class));
                }

                break;
            case R.id.neighborBtn:
                TinyDB neighborTinyDB = new TinyDB(getApplicationContext());
                FirebaseUser firebaseNeighbor = FirebaseAuth.getInstance().getCurrentUser();
                final User neighbor = new User();
                neighbor.setType("Neighbor");
                neighbor.setUid(firebaseNeighbor.getUid());

                for (UserInfo profile : firebaseNeighbor.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();

                    // UID specific to the provider
                    String uid = profile.getUid();

                    String name = profile.getDisplayName();
                    String email = profile.getEmail();

                    neighbor.setName(name);
                    neighbor.setEmail(email);
//                    if (providerId.equals("facebook.com")) {
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                (AccessToken) tinyDB.getObject("FacebookToken", AccessToken.class),
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(
//                                            JSONObject object,
//                                            GraphResponse response) {
//                                        try {
//                                            String name = object.getString("name");
//                                            String email = object.getString("email");
//                                            int age = object.getJSONObject("age_range").getInt("min");
//
//                                            popper.setName(name);
//                                            popper.setEmail(email);
//                                            popper.setAge(age);
//
//                                            Logger.i(name);
//                                            Logger.i(email);
//                                            Logger.i(age + "");
//                                        }
//                                        catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,name,link,email,age_range");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//                    }
                };
                mDatabase.child("users").child(firebaseNeighbor.getUid()).setValue(neighbor);

                if (FacebookIsProvider) {
                    startActivity(new Intent(TypePickerActivity.this, MainActivity.class));
                }
                else {
                    startActivity(new Intent(TypePickerActivity.this, MainActivity.class));
                }

                break;
            case R.id.typePickerCancelButton:
                signOut();
                startActivity(new Intent(TypePickerActivity.this, SignUpActivityUpdated.class));
                break;
            default:
                break;
        }

    }

    @Override
    public void onRadiusEntered(int radius, User popper) {
        if (radius == 0) {
            Toast.makeText(getApplicationContext(), "Pick a radius greater than zero!", Toast.LENGTH_LONG).show();
            showRadiusDialog(popper);
        }
        else {
            popper.setRadius(radius);

            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(popper);

            if (FacebookIsProvider) {
                startActivity(new Intent(TypePickerActivity.this, MainActivity.class));
            }
            else {
                startActivity(new Intent(TypePickerActivity.this, MainActivity.class));
            }

        }
    }

    @Override
    public void onNameEntered(String name, User user) {

    }

    private void showRadiusDialog(User popper) {
        FragmentManager fm = getSupportFragmentManager();
        PickRadiusDialog pickRadiusDialog = PickRadiusDialog.newInstance("Some Title", popper);
        pickRadiusDialog.show(fm, "fragment_edit_name");
    }

    private void showNameDialog(User user) {
        FragmentManager fm = getSupportFragmentManager();
        EnterNameDialog enterNameDialog = EnterNameDialog.newInstance("Some Title", user);
        enterNameDialog.show(fm, "fragment_edit_name");
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        signOut();
        startActivity(new Intent(TypePickerActivity.this, SignUpActivityUpdated.class));
        finish();
    }

    public void signOut() {

        if (GoogleIsProvider) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Logger.i("Signing User out of Google Account");
                            revokeGoogleAccess();
                        }
                    });
        }
        if (FacebookIsProvider) {
            LoginManager.getInstance().logOut();
        }

        mAuth.signOut();
    }

    private void revokeGoogleAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Logger.i("Revoked Google Access to this Account");
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Logger.d("GoogleApi onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
