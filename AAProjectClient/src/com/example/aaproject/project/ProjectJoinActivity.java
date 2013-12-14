package com.example.aaproject.project;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
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
import com.example.aaproject.util.ProgressControl;
import com.example.aaproject.util.TaskCallback;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectJoinActivity extends Activity implements TaskCallback{
	private static final String url = "http://test20103377.appspot.com/join";
	private EditText mProfileView;
	private JoinThread mJoinThread = null;
	private String mMyEmail;
	private String mProjectID;
	private ProgressControl mProgressControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_join);
		mProfileView = (EditText) findViewById(R.id.profile_message);
		
		mProgressControl = new ProgressControl(getBaseContext(), findViewById(R.id.join_form), findViewById(R.id.join_status), (TextView) findViewById(R.id.join_status_message));

		mMyEmail = getIntent().getExtras().getString("email");
		mProjectID = getIntent().getExtras().getString("projectID");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_join, menu);
		return true;
	}

	public void done() {
		Intent intent = new Intent(getBaseContext(), ProjectMemberFragment.class);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	public void OnClickSubmitButtonMethod(View v) {
		boolean cancel = false;
		EditText focusView = null;
		String errmsg = null;

		if (TextUtils.isEmpty(mProfileView.getText().toString())) {
			errmsg = "message is empty";
			focusView = mProfileView;
			cancel = true;
		} 
		
		if (cancel) {
			focusView.setError(errmsg);
			Toast.makeText(getBaseContext(), errmsg, Toast.LENGTH_SHORT).show();
			focusView.requestFocus();
		} else {
			mProgressControl.setMessageById(R.string.submit_progress);
			mProgressControl.showProgress(true);
			mJoinThread = (JoinThread) new JoinThread(this);
			mJoinThread.execute((Void) null);
		}
	}

	private class JoinThread extends AsyncTask<Void, Void, String> {

		private Exception exception;
		private TaskCallback mCallback;

		public JoinThread(TaskCallback callback) {
		    mCallback = callback;
		}

		@Override
		protected String doInBackground(Void... arg0) {

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			try {
				CookieSyncManager.createInstance(getBaseContext());
				CookieManager cookieManager = CookieManager.getInstance();
				String keyValue = cookieManager.getCookie("http://test20103377.appspot.com/");
				if(keyValue!=null){
					String [] cookieArray = keyValue.split("; ");
					for(int i=0;i<cookieArray.length;i++){
						String [] cookie = cookieArray[i].split("=");
						if(cookie[0].equals("JSESSIONID")){
							httpclient.getCookieStore().addCookie(new BasicClientCookie(cookie[0], cookie[1]));
							CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
							List<Cookie> cookies  = httpclient.getCookieStore().getCookies();
							List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
							httppost.setHeader((Header) cookieHeader.get(0));
						}
					}
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "join"));
				nameValuePairs.add(new BasicNameValuePair("email", mMyEmail));
				nameValuePairs.add(new BasicNameValuePair("projectID", mProjectID));
				nameValuePairs.add(new BasicNameValuePair("message", URLEncoder.encode(mProfileView.getText().toString(), "UTF-8")));
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
			mJoinThread = null;
			if(result.contains("Submit Success")){
				mCallback.done();
			}
			else{
				mProgressControl.showProgress(false);
				try {
					Toast.makeText(getBaseContext(), URLDecoder.decode(result, "UTF-8"), Toast.LENGTH_SHORT).show();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		protected void onCancelled() {
			mJoinThread = null;
			mProgressControl.showProgress(false);
		}
	}
}
