package com.madmensoftware.www.pops.Adapters;

/**
 * Created by carsonjones on 9/8/16.
 */
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import com.madmensoftware.www.pops.Models.Job;
    import com.madmensoftware.www.pops.R;

    import java.util.List;

    public class JobRecyclerViewAdapter extends RecyclerView.Adapter<JobRecyclerViewAdapter.JobViewHolder> {

        private List<Job> jobsList;

        public class JobViewHolder extends RecyclerView.ViewHolder {
            public TextView title, budget, description, posterName;

            public JobViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.job_title);
                budget = (TextView) view.findViewById(R.id.job_budget);
                description = (TextView) view.findViewById(R.id.job_description);
                posterName = (TextView) view.findViewById(R.id.job_poster_name);
            }
        }


        public JobRecyclerViewAdapter(List<Job> jobsList) {
            this.jobsList = jobsList;
        }

        @Override
        public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.job_list_row, parent, false);

            return new JobViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(JobViewHolder holder, int position) {
            Job job = jobsList.get(position);
            holder.title.setText(job.getTitle());
            holder.budget.setText(job.getBudget() + "");
            holder.description.setText(job.getDescription());
            holder.posterName.setText(job.getPosterName());
        }

        @Override
        public int getItemCount() {
            return jobsList.size();
        }
    }

