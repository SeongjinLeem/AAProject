package com.example.aaproject.project;

import com.example.aaproject.R;
import com.example.aaproject.util.PageLoad;
import com.example.aaproject.util.PageLoadFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectMemberFragment extends PageLoadFragment {
	private static final int PROJECT_RECRUIT_ACTIVITY = 5;
	private static final int PROJECT_JOIN_ACTIVITY = 6;
	Context mContext;
	private ListView mMemberListView;
	private Button mRecruitButton;
	private Button mJoinButton;
	public ProjectMemberFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_project_member, null);
		mMemberListView = (ListView) view.findViewById(R.id.memberlistView);
		
		mRecruitButton = (Button)view.findViewById(R.id.recruit_button);
		mJoinButton = (Button)view.findViewById(R.id.join_button);
		
		pageLoad();
		
		return view;
	}
	
	public void pageLoad(){
		MemberAdapter adapter = new MemberAdapter(mContext, ((ProjectDisplayFragmentActivity)mContext).mMemberList);
		mMemberListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		if(((ProjectDisplayFragmentActivity)mContext).mProject.getEmail().equals(((ProjectDisplayFragmentActivity)mContext).mMyEmail)){
			mJoinButton.setVisibility(View.GONE);
			mRecruitButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(mContext, ProjectRecruitActivity.class);
							startActivityForResult(intent, PROJECT_RECRUIT_ACTIVITY);
						}
					});
		}else{
			mRecruitButton.setVisibility(View.GONE);
			mJoinButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(mContext, ProjectJoinActivity.class);
							startActivityForResult(intent, PROJECT_JOIN_ACTIVITY);
						}
					});
		}

		mMemberListView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) { 
				//Intent intent = new Intent(mContext, ProjectDisplayActivity.class);
				//intent.putExtra("projectID", ((TextView) v.findViewById(R.id.project_id)).getText().toString());
				//startActivityForResult(intent, PROJECT_DISPLAY_ACTIVITY);
			}
		});
	}
}
