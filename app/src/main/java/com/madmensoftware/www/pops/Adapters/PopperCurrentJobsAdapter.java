package com.madmensoftware.www.pops.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.madmensoftware.www.pops.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by carsonjones on 9/13/16.
 */
public class PopperCurrentJobsAdapter extends RecyclerView.Adapter<PopperCurrentJobsAdapter.SimpleViewHolder> {

        private final Context mContext;
        private List<String> mData;

        public void add(String s,int position) {
            position = position == -1 ? getItemCount()  : position;
            mData.add(position,s);
            notifyItemInserted(position);
        }

        public void remove(int position){
            if (position < getItemCount()  ) {
                mData.remove(position);
                notifyItemRemoved(position);
            }
        }


        public static class SimpleViewHolder extends RecyclerView.ViewHolder {
            public final TextView title;

            public SimpleViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.job_list_title);
            }
        }

        public PopperCurrentJobsAdapter(Context context, String[] data) {
            mContext = context;
            if (data != null)
                mData = new ArrayList<String>(Arrays.asList(data));
            else mData = new ArrayList<String>();
        }

        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.job_list_row, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, final int position) {
            holder.title.setText(mData.get(position));
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext,"Position ="+position,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

