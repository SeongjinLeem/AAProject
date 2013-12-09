package com.example.aaproject.main;

import com.example.aaproject.R;
import com.example.aaproject.R.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


@SuppressLint("ValidFragment")
public class RecruitMemberActivity extends Fragment {
		Context mContext;

		public RecruitMemberActivity(Context context) {
			mContext = context;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, 
				ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.activity_recruit_member, null);
	    	return view;
		}

}

