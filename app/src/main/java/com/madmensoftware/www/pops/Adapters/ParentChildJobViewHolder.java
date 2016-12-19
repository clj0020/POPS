package com.madmensoftware.www.pops.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.madmensoftware.www.pops.R;

/**
 * Created by carson on 12/18/2016.
 */

public class ParentChildJobViewHolder extends RecyclerView.ViewHolder {
    public TextView jobTitle;
    public TextView jobDescription;
    public TextView jobBudget;
    public TextView jobPosterName;

    View mView;
    Context mContext;

    public ParentChildJobViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        jobTitle = (TextView) itemView.findViewById(R.id.job_list_title);
        jobDescription = (TextView) itemView.findViewById(R.id.job_list_description);
        jobBudget = (TextView) itemView.findViewById(R.id.job_list_budget);
        jobPosterName = (TextView) itemView.findViewById(R.id.job_list_poster_name);
        //itemView.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View view) {
//        final ArrayList<Job> jobs = new ArrayList<>();
//        Query jobQuery = FirebaseDatabase.getInstance().getReference().child("jobs").orderByChild("posterUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        jobQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Job job = snapshot.getValue(Job.class);
//                    jobs.add(job);
//                    Log.i("NeighborJobViewHolder", " job added to jobs. UID is " +job.getUid());
//                }
//
//                int itemPosition = getLayoutPosition();
//
//                Log.i("NeighborJobViewHolder", " jobsSize is " + jobs.size());
//                Log.i("NeighborJobViewHolder", " itemPosition is " + itemPosition);
//
//
//                if (itemPosition != -1) {
//                    try {
//                        Intent intent = new Intent(mContext, JobDetailActivity.class);
//                        intent.putExtra("position", itemPosition + "");
//                        // intent.putExtra("job", Parcels.wrap(jobs.get(itemPosition)));
//                        intent.putExtra("job", jobs.get(itemPosition).getUid());
//                        mContext.startActivity(intent);
//                    } catch (ArrayIndexOutOfBoundsException e) {
//                        Logger.e(e.getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}