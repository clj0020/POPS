package com.madmensoftware.www.pops.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Activities.JobDetailActivity;
import com.madmensoftware.www.pops.Activities.MainActivity;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by carsonjones on 9/15/16.
 */

public class PopperJobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView jobTitle;
    public TextView jobDescription;
    public TextView jobBudget;
    public TextView jobPosterName;

    View mView;
    Context mContext;

    public PopperJobViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        jobTitle = (TextView) itemView.findViewById(R.id.job_list_title);
        jobDescription = (TextView) itemView.findViewById(R.id.job_list_description);
        jobBudget = (TextView) itemView.findViewById(R.id.job_list_budget);
        jobPosterName = (TextView) itemView.findViewById(R.id.job_list_poster_name);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Job> jobs = new ArrayList<>();
        Query jobQuery = FirebaseDatabase.getInstance().getReference().child("jobs").orderByChild("popperUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        jobQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);
                    jobs.add(job);
                    Log.i("PopperJobViewHolder", " job added to jobs. UID is " +job.getUid());
                }

                int itemPosition = getLayoutPosition();

                Log.i("PopperJobViewHolder", " jobsSize is " + jobs.size());
                Log.i("PopperJobViewHolder", " itemPosition is " + itemPosition);

                try {
                    Intent intent = new Intent(mContext, JobDetailActivity.class);
                    intent.putExtra("position", itemPosition + "");
//                intent.putExtra("job", Parcels.wrap(jobs.get(itemPosition)));
                    intent.putExtra("job", jobs.get(itemPosition).getUid());
                    mContext.startActivity(intent);
                }
                catch(ArrayIndexOutOfBoundsException e) {
                    Logger.i("Exception at PopperJobViewHolder: " + e);
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
