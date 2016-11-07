package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

public class TypePickerActivity extends AppCompatActivity implements View.OnClickListener, PickRadiusDialog.PickRadiusDialogCallbacks, EnterNameDialog.EnterNameDialogCallbacks {

    @BindView(R.id.popperBtn) Button mPopperBtn;
    @BindView(R.id.parentBtn) Button mParentBtn;
    @BindView(R.id.neighborBtn) Button mNeighborBtn;

    private DatabaseReference mDatabase;
    private boolean FacebookIsProvider;
    private boolean GoogleIsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_picker);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mPopperBtn.setOnClickListener(this);
        mParentBtn.setOnClickListener(this);
        mNeighborBtn.setOnClickListener(this);

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
}
