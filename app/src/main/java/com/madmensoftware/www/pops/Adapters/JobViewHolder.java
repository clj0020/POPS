package com.madmensoftware.www.pops.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.Activities.JobDetailActivity;
import com.madmensoftware.www.pops.Models.Job;
import com.madmensoftware.www.pops.R;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by carsonjones on 9/10/16.
 */
public class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView jobTitle;
    public TextView jobDescription;
    public TextView jobBudget;
    public TextView jobPosterName;

    View mView;
    Context mContext;

    public JobViewHolder(View itemView) {
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("jobs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    jobs.add(snapshot.getValue(Job.class));
                }

                int itemPosition = getLayoutPosition();

                Intent intent = new Intent(mContext, JobDetailActivity.class);
                intent.putExtra("position", itemPosition + "");
                intent.putExtra("jobs", Parcels.wrap(jobs));
                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

