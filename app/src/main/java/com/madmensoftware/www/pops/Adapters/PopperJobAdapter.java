package com.madmensoftware.www.pops.Adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Models.Job;

/**
 * Created by carsonjones on 9/10/16.
 */
public class PopperJobAdapter extends FirebaseRecyclerAdapter<Job, PopperJobViewHolder> {

    private Context context;

    public PopperJobAdapter(Class<Job> modelClass, int modelLayout, Class<PopperJobViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(PopperJobViewHolder viewHolder, Job model, int position) {
        viewHolder.jobTitle.setText(model.getTitle());
        viewHolder.jobPosterName.setText(model.getPosterName());
        viewHolder.jobDescription.setText(model.getDescription());
        viewHolder.jobBudget.setText(model.getBudget() + "");
    }
}