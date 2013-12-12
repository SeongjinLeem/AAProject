package com.example.aaproject.main;


import com.example.aaproject.R;
import com.example.aaproject.project.ProjectDisplayFragmentActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("ValidFragment")
public class ProjectListFragment extends Fragment {
	private static final int PROJECT_DISPLAY_ACTIVITY = 4;
	Context mContext;
	private GridView mGridView;  

	public ProjectListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_project_list, null);

		mGridView = (GridView) view.findViewById(R.id.gridView);
		listLoad();

		return view;
	}
	
	private boolean mReturningWithResult = false;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROJECT_DISPLAY_ACTIVITY:
				mReturningWithResult = true;
			break;
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		if (mReturningWithResult) {
			((MainFragmentActivity)getActivity()).projectListLoad();
		}
		mReturningWithResult = false;
	}
	
	public void listLoad(){
		ProjectAdapter adapter = new ProjectAdapter(mContext, ((MainFragmentActivity)mContext).mProjectList);
		mGridView.setAdapter(adapter);  
		adapter.notifyDataSetChanged(); 
		mGridView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) {  
				Intent intent = new Intent(mContext, ProjectDisplayFragmentActivity.class);
				intent.putExtra("projectID", ((TextView) v.findViewById(R.id.project_id)).getText().toString());
				intent.putExtra("email",((MainFragmentActivity)getActivity()).mMyEmail);
				intent.putExtra("JSESSIONID",((MainFragmentActivity)getActivity()).mJSESSIONID);
				startActivityForResult(intent, PROJECT_DISPLAY_ACTIVITY);
			}  
		});
	}

}

