package com.example.aaproject.main;


import java.util.ArrayList;
import java.util.List;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.aaproject.R;
import com.example.aaproject.login.LoginActivity;
import com.example.aaproject.model.ProjectAdapter;
import com.example.aaproject.project.ProjectDisplayFragmentActivity;
import com.example.aaproject.util.TaskCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.GridView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


@SuppressLint("ValidFragment")
public class MyProjectListFragment extends Fragment implements TaskCallback{
	private static final int PROJECT_CREATE_ACTIVITY = 3;
	private static final int PROJECT_DISPLAY_ACTIVITY = 4;
	Context mContext;
	private GridView mGridView;  
	private LogoutThread mLogoutThread;

	public MyProjectListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_project_list, null);
		mGridView = (GridView) view.findViewById(R.id.gridView);
		listLoad();
		
		view.findViewById(R.id.project_create_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(mContext, ProjectCreateActivity.class);
						startActivityForResult(intent, PROJECT_CREATE_ACTIVITY);
					}
				});
		
		view.findViewById(R.id.recruit_state_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(mContext, RecruitStateActivity.class);
						intent.putExtra("email", ((MainFragmentActivity)mContext).mMyEmail);
						startActivity(intent);
					}
				});

		final TaskCallback callback = this;
		view.findViewById(R.id.logout_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mLogoutThread = (LogoutThread) new LogoutThread(callback);
						mLogoutThread.execute((Void) null);
					}
				});
		return view;
	}

	private boolean mReturningWithResult = false;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROJECT_CREATE_ACTIVITY:
			if(resultCode == Activity.RESULT_OK)
				mReturningWithResult = true;
			break;
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

	private class LogoutThread extends AsyncTask<Void, Void, String> {
		private TaskCallback mCallback;

		public LogoutThread(TaskCallback callback) {
			mCallback = callback;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://test20103377.appspot.com/logout");
			try {
				CookieSyncManager.createInstance(mContext);
				CookieManager cookieManager = CookieManager.getInstance();
				String keyValue = cookieManager.getCookie("http://test20103377.appspot.com/");
				if(keyValue!=null){
					String [] cookie = cookieManager.getCookie("http://test20103377.appspot.com/").split("=");
					httpclient.getCookieStore().addCookie(new BasicClientCookie(cookie[0], cookie[1]));
					CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
					List<Cookie> cookies  = httpclient.getCookieStore().getCookies();
					List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
					httppost.setHeader((Header) cookieHeader.get(0));
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "Logout"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				return EntityUtils.toString(response.getEntity());

			} catch (Exception e) {
				Log.w("logout err", e.toString()) ;
			}
			return null;
		}

		protected void onPostExecute(String result) {
			if(result.contains("Logout Success")){
				mCallback.done();
			}
		}
	}
	
	public void listLoad(){
		ProjectAdapter adapter = new ProjectAdapter(mContext, ((MainFragmentActivity)mContext).mMyProjectList);
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

	@Override
	public void done() {
		mLogoutThread = null;
		Intent intent = new Intent(mContext, LoginActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();

	}

}