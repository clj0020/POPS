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
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Fragments.PopperCheckInFragment;
import com.madmensoftware.www.pops.Fragments.PopperDashboardFragment;
import com.madmensoftware.www.pops.Fragments.PopperJobsFragment;
import com.madmensoftware.www.pops.Fragments.PopperMapFragment;
import com.madmensoftware.www.pops.Fragments.PopperNotificationsFragment;
import com.madmensoftware.www.pops.Helpers.NonSwipeableViewPager;
import com.madmensoftware.www.pops.Helpers.TinyDB;
import com.madmensoftware.www.pops.Models.User;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by carsonjones on 8/30/16.
 */
public class PopperActivity extends AppCompatActivity implements PopperDashboardFragment.PopperDashCallbacks, PopperCheckInFragment.PopperCheckInCallbacks,
                PopperMapFragment.PopperMapFragmentCallbacks {
    public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private GeoFire geofire;
    private int[] tabIcons = {
            R.mipmap.ic_dashboard,
            R.mipmap.ic_jobs,
            R.mipmap.ic_map,
            R.mipmap.ic_notifications,
            R.mipmap.ic_check_in
    };

    private CallbackManager mCallbackManager;
    
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) NonSwipeableViewPager viewPager;

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        Logger.d("Popper: ", "PopperActivity started.");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

        Logger.d("Popper: PopperActivity stopped.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_popper);
        ButterKnife.bind(this);

        mCallbackManager = CallbackManager.Factory.create();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Logger.d("Auth", "User is null");
                    startActivity(new Intent(PopperActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        mDatabase.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User mUser = dataSnapshot.getValue(User.class);
                TinyDB tinyDb = new TinyDB(getApplicationContext());
                tinyDb.putObject("User", mUser);

            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

        Logger.d("Popper: ", "PopperActivity resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();

        Logger.d("Popper: ", "PopperActivity paused.");
    }

    @Override
    public void onSignOutButton() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onCheckIn(double latitude, double longitude) {
        String uid = auth.getCurrentUser().getUid();
        Date currentDate = Calendar.getInstance().getTime();
        long currentTime = currentDate.getTime();

        Logger.d(currentTime);

        String checkInId = mDatabase.child("users").child(uid).child("check-in").push().getKey();
        mDatabase.child("users").child(uid).child("check-in").child(checkInId).setValue(currentTime);

        geofire = new GeoFire(mDatabase.child("check_in_location"));
        geofire.setLocation(checkInId, new GeoLocation(latitude, longitude));

        Toast.makeText(this, "Successfully Checked In!", Toast.LENGTH_LONG).show();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PopperDashboardFragment.newInstance(), "Dash");
        adapter.addFragment(PopperJobsFragment.newInstance(), "Jobs");
        adapter.addFragment(PopperMapFragment.newInstance(), "Map");
        adapter.addFragment(PopperNotificationsFragment.newInstance(), "Notifications");
        adapter.addFragment(PopperCheckInFragment.newInstance(), "Check In");
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 2);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
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
            return null;
        }

    }

}