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
import com.example.aaproject.R.id;
import com.example.aaproject.R.layout;
import com.example.aaproject.login.LoginActivity;
import com.example.aaproject.util.TaskCallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class MyProjectListActivity extends Fragment implements TaskCallback{
	private static final int PROJECT_CREATE_ACTIVITY = 3;
	Context mContext;
	GridView gridView;  
	LogoutThread mLogoutThread;
     static final String[] MOBILE_OS = new String[] { "1", "2" };  


	public MyProjectListActivity(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_my_project_list, null);
		GridView gridView = (GridView) view.findViewById(R.id.gridView);   
		gridView.setAdapter(new ProjectAdapter(mContext, MOBILE_OS));  
		gridView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) {  
				Toast.makeText(mContext,  ((TextView) v.findViewById(R.id.title)).getText(), Toast.LENGTH_SHORT).show();  
			}  
		});  
		view.findViewById(R.id.project_create_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(mContext, ProjectCreateActivity.class);
						startActivityForResult(intent, PROJECT_CREATE_ACTIVITY);
					}
				});
		
		final TaskCallback callback = this;
		
		view.findViewById(R.id.logout_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mLogoutThread = (LogoutThread) new LogoutThread(callback).execute((Void) null);
					}
				});

		return view;
	}
	
	private class LogoutThread extends AsyncTask<Void, Void, String> {

		private Exception exception;
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
				// TODO Auto-generated catch block   
			}
			return null;
		}

		protected void onPostExecute(String result) {
			if(result.contains("Logout Success")){
				mCallback.done();
			}
		}
	}

	@Override
	public void done() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();
	}

}