package com.madmensoftware.www.pops.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.madmensoftware.www.pops.Adapters.PopperCurrentJobsAdapter;
import com.madmensoftware.www.pops.Adapters.SimpleSectionedRecyclerViewAdapter;
import com.madmensoftware.www.pops.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopperCurrentJobsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PopperCurrentJobsAdapter mAdapter;

    public PopperCurrentJobsFragment() {
        // Required empty public constructor
    }

    public static PopperCurrentJobsFragment newInstance() {
        PopperCurrentJobsFragment fragment = new PopperCurrentJobsFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popper_current_jobs_list, container, false);

//        //Your RecyclerView
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.popper_current_jobs_recycler_view);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//
//
//        //Your RecyclerView.Adapter
//        mAdapter = new PopperCurrentJobsAdapter(this, sCheeseStrings);
//
//
//        //This is the code to provide a sectioned list
//        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
//                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
//
//        //Sections
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Section 1"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(5,"Section 2"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(12,"Section 3"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(14,"Section 4"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(20,"Section 5"));
//
//        //Add your adapter to the sectionAdapter
//        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
//        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
//                SimpleSectionedRecyclerViewAdapter(getActivity(), R.layout.recycler_view_header, R.id.recycler_view_header_text, mAdapter);
//        mSectionedAdapter.setSections(sections.toArray(dummy));
//
//        //Apply this adapter to the RecyclerView
//        mRecyclerView.setAdapter(mSectionedAdapter);


        return view;
    }

}

