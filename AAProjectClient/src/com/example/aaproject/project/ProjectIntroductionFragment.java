package com.example.aaproject.project;

import com.example.aaproject.R;
import com.example.aaproject.main.MainFragmentActivity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProjectIntroductionFragment extends PageLoadFragment {
	private static final int PROJECT_SPONSOR_ACTIVITY = 7;
	Context mContext;
	private ImageView mProjectImageView;
	private ProgressBar progressbarView;
	private TextView mGoalRateView;
	private TextView mDonationView;
	private TextView mSponsorView;
	private TextView mContentsView;
	private boolean mReturningWithResult = false;
	public ProjectIntroductionFragment() {
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_project_introduction, null);
		mProjectImageView = (ImageView) view.findViewById(R.id.project_image);
		progressbarView = (ProgressBar) view.findViewById(R.id.progressBar);
		mGoalRateView = (TextView) view.findViewById(R.id.goal_rate);
		mDonationView = (TextView) view.findViewById(R.id.donation);
		mSponsorView = (TextView) view.findViewById(R.id.sponsor);
		mContentsView = (TextView) view.findViewById(R.id.contents);
		
		((Button) view.findViewById(R.id.sponsor_button)).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(mContext, ProjectSponsorActivity.class);
						intent.putExtra("projectID", ((ProjectDisplayFragmentActivity)mContext).mProjectID);
						intent.putExtra("email", ((ProjectDisplayFragmentActivity)mContext).mMyEmail);
						startActivityForResult(intent, PROJECT_SPONSOR_ACTIVITY);
					}
				});
		
		pageLoad();
		return view;
	}
	
	public void pageLoad(){
		mProjectImageView.setLayoutParams(new LinearLayout.LayoutParams(-1, 600));
		mProjectImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mProjectImageView.setImageBitmap(((ProjectDisplayFragmentActivity)mContext).mProject.getImgBitmap());
		int goal = ((ProjectDisplayFragmentActivity)mContext).mProject.getGoal();
		int donation = ((ProjectDisplayFragmentActivity)mContext).mProject.getDonation();
		int rate = Math.round(donation/(float)goal*100);
		progressbarView.setMax(100);
		progressbarView.setProgress((rate<100)?rate:100);
		mGoalRateView.setText(String.format("목표 %,d 원 중 %d %% 모임", goal, rate));
		mDonationView.setText(String.format("%,d 원", donation));
		mSponsorView.setText(String.format("%,d 명", ((ProjectDisplayFragmentActivity)mContext).mSponsorList.size()));
		mContentsView.setText(((ProjectDisplayFragmentActivity)mContext).mProject.getContents());
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROJECT_SPONSOR_ACTIVITY:
			if(resultCode == Activity.RESULT_OK)
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
}

