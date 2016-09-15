package com.madmensoftware.www.pops.Adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.madmensoftware.www.pops.Models.Job;

/**
 * Created by carsonjones on 9/10/16.
 */
public class NeighborJobAdapter extends FirebaseRecyclerAdapter<Job, NeighborJobViewHolder> {

    private Context context;

    public NeighborJobAdapter(Class<Job> modelClass, int modelLayout, Class<NeighborJobViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(NeighborJobViewHolder viewHolder, Job model, int position) {
        viewHolder.jobTitle.setText(model.getTitle());
        viewHolder.jobPosterName.setText(model.getPosterName());
        viewHolder.jobDescription.setText(model.getDescription());

        if(model.getStatus().equals("active")) {

            viewHolder.jobBudget.setText(model.getBudget() + "");
        }
        else {
            viewHolder.jobBudget.setText(model.getStatus());

        }
    }
}