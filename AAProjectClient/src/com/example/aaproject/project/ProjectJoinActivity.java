package com.example.aaproject.project;

import com.example.aaproject.R;
import com.example.aaproject.R.layout;
import com.example.aaproject.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ProjectJoinActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_join);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_join, menu);
		return true;
	}

}
