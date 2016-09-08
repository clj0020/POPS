package com.madmensoftware.www.pops.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.madmensoftware.www.pops.Fragments.PopperCheckInFragment;
import com.madmensoftware.www.pops.Fragments.PopperDashboardFragment;
import com.madmensoftware.www.pops.Fragments.PopperJobsFragment;
import com.madmensoftware.www.pops.Fragments.PopperMapFragment;
import com.madmensoftware.www.pops.Fragments.PopperNotificationsFragment;
import com.madmensoftware.www.pops.Helpers.NonSwipeableViewPager;
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

//    private RelativeLayout mDashButton;
//    private RelativeLayout mMapButton;
//    private RelativeLayout mJobsButton;
//    private RelativeLayout mNotificationsButton;
//    private RelativeLayout mCheckInButton;

    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;

    private int[] tabIcons = {
            R.mipmap.ic_dashboard,
            R.mipmap.ic_jobs,
            R.mipmap.ic_map,
            R.mipmap.ic_notifications,
            R.mipmap.ic_check_in
    };

    public FirebaseUser mUser;
    public String uid;

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
        setContentView(R.layout.activity_popper);

        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        mDashButton = (RelativeLayout) findViewById(R.id.dash_button);
//        mMapButton = (RelativeLayout) findViewById(R.id.map_button);
//        mJobsButton = (RelativeLayout) findViewById(R.id.jobs_button);
//        mNotificationsButton = (RelativeLayout) findViewById(R.id.notifications_button);
//        mCheckInButton = (RelativeLayout) findViewById(R.id.check_in_button);


        //get firebase auth instance
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
                    mUser = user;
                    uid = mUser.getUid();

                    setupViewPager(viewPager, uid);
                    tabLayout.setupWithViewPager(viewPager);
                    setupTabIcons();
//                    FragmentManager fm = getSupportFragmentManager();
//                    Fragment mapFragment = fm.findFragmentById(R.id.popper_fragment_container);

//                    if (mapFragment == null) {
//                        mapFragment = PopperMapFragment.newInstance(uid);
//                        fm.beginTransaction()
//                                .add(R.id.popper_fragment_container, mapFragment)
//                                .commit();
//                    }
//
//                    mDashButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Fragment dashFragment = PopperDashboardFragment.newInstance(uid);
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                            transaction.replace(R.id.popper_fragment_container, dashFragment);
//                            transaction.commit();
//
//                            mDashButton.setBackgroundColor(Color.parseColor("#4d4d4d"));
//                            mMapButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mJobsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mNotificationsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mCheckInButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//
//                        }
//                    });
//
//                    mMapButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Fragment mapFragment = PopperMapFragment.newInstance(uid);
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                            transaction.replace(R.id.popper_fragment_container, mapFragment);
//                            transaction.commit();
//
//                            mMapButton.setBackgroundColor(Color.parseColor("#4d4d4d"));
//                            mDashButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mJobsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mNotificationsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mCheckInButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                        }
//                    });
//
//                    mJobsButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Fragment dashFragment = PopperJobsFragment.newInstance(uid);
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                            transaction.replace(R.id.popper_fragment_container, dashFragment);
//                            transaction.commit();
//
//                            mJobsButton.setBackgroundColor(Color.parseColor("#4d4d4d"));
//                            mMapButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mDashButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mNotificationsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mCheckInButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                        }
//                    });
//
//                    mNotificationsButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Fragment dashFragment = PopperNotificationsFragment.newInstance(uid);
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                            transaction.replace(R.id.popper_fragment_container, dashFragment);
//                            transaction.commit();
//
//                            mNotificationsButton.setBackgroundColor(Color.parseColor("#4d4d4d"));
//                            mMapButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mJobsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mDashButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mCheckInButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                        }
//                    });
//
//                    mCheckInButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Fragment dashFragment = PopperCheckInFragment.newInstance(uid);
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                            transaction.replace(R.id.popper_fragment_container, dashFragment);
//                            transaction.commit();
//
//                            mCheckInButton.setBackgroundColor(Color.parseColor("#4d4d4d"));
//                            mMapButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mJobsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mNotificationsButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                            mDashButton.setBackgroundColor(Color.parseColor("#a9a9a9"));
//                        }
//                    });
//

                }
            }
        };

    }


    @Override
    public void onSignOutButton() {
        Log.i("Sign Out Interface", "Sign Out Interface");
        auth.signOut();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupViewPager(ViewPager viewPager, String uid) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PopperDashboardFragment.newInstance(uid), "Dash");
        adapter.addFragment(PopperJobsFragment.newInstance(uid), "Jobs");
        adapter.addFragment(PopperMapFragment.newInstance(uid), "Map");
        adapter.addFragment(PopperNotificationsFragment.newInstance(uid), "Notifications");
        adapter.addFragment(PopperCheckInFragment.newInstance(uid), "Check In");

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