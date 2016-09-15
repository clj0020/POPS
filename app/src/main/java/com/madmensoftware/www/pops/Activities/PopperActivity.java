package com.madmensoftware.www.pops.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carsonjones on 8/30/16.
 */
public class PopperActivity extends AppCompatActivity implements PopperDashboardFragment.PopperDashCallbacks {
    public final static String EXTRA_USER_ID = "com.madmensoftware.www.pops.userId";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    public FirebaseUser mFirebaseUser;


    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    public String mUid;

    private int[] tabIcons = {
            R.mipmap.ic_dashboard,
            R.mipmap.ic_jobs,
            R.mipmap.ic_map,
            R.mipmap.ic_notifications,
            R.mipmap.ic_check_in
    };

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        Log.i("Popper: ", "PopperActivity started.");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

        Log.i("Popper: ", "PopperActivity stopped.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popper);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);



        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.i("Auth", "User is null");
                    startActivity(new Intent(PopperActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    mFirebaseUser = user;
                    mUid = auth.getCurrentUser().getUid();
                    setupViewPager(viewPager, mUid);

                    tabLayout.setupWithViewPager(viewPager);
                    setupTabIcons();


                }
            }
        };

        mDatabase.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TinyDB tinyDb = new TinyDB(getApplicationContext());
                tinyDb.putObject("User", dataSnapshot.getValue(User.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("Popper: ", "PopperActivity resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i("Popper: ", "PopperActivity paused.");
    }

    @Override
    public void onSignOutButton() {
        auth.signOut();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupViewPager(ViewPager viewPager , String mUid) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PopperDashboardFragment.newInstance(), "Dash");
        adapter.addFragment(PopperJobsFragment.newInstance(), "Jobs");
        adapter.addFragment(PopperMapFragment.newInstance(), "Map");
        adapter.addFragment(PopperNotificationsFragment.newInstance(mUid), "Notifications");
        adapter.addFragment(PopperCheckInFragment.newInstance(), "Check In");
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
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
            return mFragmentTitleList.get(position);
        }

    }

}