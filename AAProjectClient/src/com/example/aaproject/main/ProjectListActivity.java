package com.example.aaproject.main;

import com.example.aaproject.R;
import com.example.aaproject.R.id;
import com.example.aaproject.R.layout;

import android.annotation.SuppressLint;
import android.content.Context;
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
public class ProjectListActivity extends Fragment {
	Context mContext;
	GridView gridView;  
     static final String[] MOBILE_OS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" };  


	public ProjectListActivity(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_project_list, null);
		GridView gridView = (GridView) view.findViewById(R.id.gridView);   
		gridView.setAdapter(new ProjectAdapter(mContext, MOBILE_OS));  
		gridView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) {  
				Toast.makeText(mContext,  ((TextView) v.findViewById(R.id.title)).getText(), Toast.LENGTH_SHORT).show();  
			}  
		});  


		return view;
	}

}

