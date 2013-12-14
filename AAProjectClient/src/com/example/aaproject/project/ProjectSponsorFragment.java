package com.example.aaproject.project;


import com.example.aaproject.R;
import com.example.aaproject.model.SponsorAdapter;
import com.example.aaproject.util.PageLoad;
import com.example.aaproject.util.PageLoadFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;


public class ProjectSponsorFragment extends PageLoadFragment {
	Context mContext;
	private ListView mSponsorListView;
	public ProjectSponsorFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_project_sponsor, null);
		mSponsorListView = (ListView) view.findViewById(R.id.sponsorlistView);

		pageLoad();

		return view;
	}
	
	public void pageLoad(){
		SponsorAdapter adapter = new SponsorAdapter(mContext, ((ProjectDisplayFragmentActivity)mContext).mSponsorList);
		mSponsorListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
}
