package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.NeighborJobsFragment;
import com.madmensoftware.www.pops.Fragments.NeighborNotificationsFragment;
import com.madmensoftware.www.pops.Helpers.NonSwipeableViewPager;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by carsonjones on 8/30/16.
 */
public class ParentActivity extends AppCompatActivity {

    public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    @BindView(R.id.parent_tabs) TabLayout tabLayout;
    @BindView(R.id.parent_viewpager) NonSwipeableViewPager viewPager;
    @BindView(R.id.parent_toolbar) Toolbar myToolbar;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    public FirebaseUser mFirebaseUser;
    private CallbackManager mCallbackManager;
    private User user;
    public String uid;
    private int[] tabIcons = {
            R.mipmap.ic_jobs,
            R.mipmap.ic_notifications
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_parent);
        ButterKnife.bind(this);
        mCallbackManager = CallbackManager.Factory.create();

        setSupportActionBar(myToolbar);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            user = Parcels.unwrap(b.getParcelable("User"));
        }

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Log.i("Auth", "User is null");
                    startActivity(new Intent(ParentActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    mFirebaseUser = firebaseUser;
                    uid = mFirebaseUser.getUid();

                    setupViewPager(viewPager, uid);
                    tabLayout.setupWithViewPager(viewPager);
                    setupTabIcons();

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("users/" + uid);

                    // Read from the database
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                User mUser = dataSnapshot.getValue(User.class);
                                user = mUser;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parent_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.parent_action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.parent_action_log_out:
                auth.signOut();
                LoginManager.getInstance().logOut();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager, String uid) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(NeighborJobsFragment.newInstance(uid), "Jobs");
        adapter.addFragment(NeighborNotificationsFragment.newInstance(uid), "Notifications");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}