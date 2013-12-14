package com.example.aaproject.project;


import com.example.aaproject.R;
import com.example.aaproject.main.MainFragmentActivity;
import com.example.aaproject.model.MemberAdapter;
import com.example.aaproject.util.PageLoadFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

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

	private boolean mReturningWithResult = false;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROJECT_RECRUIT_ACTIVITY:
			if(resultCode == Activity.RESULT_OK)
				mReturningWithResult = true;
			break;
		case PROJECT_JOIN_ACTIVITY:
			mReturningWithResult = true;
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mReturningWithResult) {
			((ProjectDisplayFragmentActivity)getActivity()).projectInfoLoad();
		}
		mReturningWithResult = false;
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
							intent.putExtra("email", ((ProjectDisplayFragmentActivity)mContext).mMyEmail);
							intent.putExtra("projectID", ((ProjectDisplayFragmentActivity)mContext).mProjectID);
							startActivityForResult(intent, PROJECT_RECRUIT_ACTIVITY);
						}
					});
		}else{
			mRecruitButton.setVisibility(View.GONE);
			boolean memberFlag = false;
			for(int i=0;i<((ProjectDisplayFragmentActivity)mContext).mMemberList.size();i++){
				if(((ProjectDisplayFragmentActivity)mContext).mMyEmail.equals(((ProjectDisplayFragmentActivity)mContext).mMemberList.get(i).getEmail())){
					memberFlag = true;
					break;
				}
			}
			if(memberFlag){
				mJoinButton.setVisibility(View.GONE);
			}else{
				mJoinButton.setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent(mContext, ProjectJoinActivity.class);
								intent.putExtra("email", ((ProjectDisplayFragmentActivity)mContext).mMyEmail);
								intent.putExtra("projectID", ((ProjectDisplayFragmentActivity)mContext).mProjectID);
								startActivityForResult(intent, PROJECT_JOIN_ACTIVITY);
							}
						});
			}
		}

		mMemberListView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) { 
				Intent intent = new Intent(mContext, MemberLocationActivity.class);
				intent.putExtra("geoPoint", ((TextView) v.findViewById(R.id.location)).getText().toString());
				intent.putExtra("field", ((TextView) v.findViewById(R.id.field)).getText().toString());
				startActivity(intent);
			}
		});
	}
}
