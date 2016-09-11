package com.madmensoftware.www.pops.Activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.madmensoftware.www.pops.Adapters.JobDetailPagerAdapter;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class JobDetailActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private JobDetailPagerAdapter adapterViewPager;
    ArrayList<Job> mJobs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        mViewPager = (ViewPager) findViewById(R.id.jobDetailViewPager);

        mJobs = Parcels.unwrap(getIntent().getParcelableExtra("jobs"));

        int startingPosition = Integer.parseInt(getIntent().getStringExtra("position"));

        adapterViewPager = new JobDetailPagerAdapter(getSupportFragmentManager(), mJobs);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.setCurrentItem(startingPosition);

    }

}
