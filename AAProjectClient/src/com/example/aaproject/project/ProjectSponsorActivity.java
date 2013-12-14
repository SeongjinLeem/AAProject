package com.example.aaproject.project;

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
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectSponsorActivity extends Activity implements TaskCallback{
	static final String url = "http://test20103377.appspot.com/sponsor";
	private RadioGroup mSponsorGroup;
	private SubmitThread mSubmitThread = null;
	private Long donationValue;
	private ProgressControl mProgressControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_sponsor);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_sponsoring, menu);

		mSponsorGroup = (RadioGroup) findViewById(R.id.donation_select);
		mProgressControl = new ProgressControl(getBaseContext(), mSponsorGroup, findViewById(R.id.submit_status), (TextView) findViewById(R.id.submit_status_message));

		((EditText)findViewById(R.id.donation_edit)).setOnFocusChangeListener(new EditText.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View view, boolean bFocus) {
				if (bFocus == false) {
					if(!((EditText)view).getText().toString().equals("")){
						if(Long.parseLong(((EditText)view).getText().toString())<1000)
							((EditText)view).setError("최소 입력 금액은 1000원 입니다.");
						else
							((EditText)view).setError(null);
					}
				}
			}
		});

		return true;
	}

	public void done() {
		mSubmitThread = null;
		Intent intent = new Intent(getBaseContext(), ProjectIntroductionFragment.class);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	public void OnClickSubmitButtonMethod(View v) {
		boolean cancel = false;
		String errmsg = null;
		int radioButtonId = mSponsorGroup.getCheckedRadioButtonId();
		View radioButton = mSponsorGroup.findViewById(radioButtonId);
		int idx = mSponsorGroup.indexOfChild(radioButton);
		String donation = ((RadioButton)mSponsorGroup.getChildAt(idx)).getText().toString();
		if (donation.equals("")) {
			donation = ((EditText)findViewById(R.id.donation_edit)).getText().toString();
			if(donation.equals("")){
				((EditText)findViewById(R.id.donation_edit)).setError("금액을 입력해주세요.");
				cancel = true;
			}
		}
		donationValue  = Long.parseLong(donation);

		if (cancel) {
			Toast.makeText(getBaseContext(), errmsg, Toast.LENGTH_SHORT).show();
		} else {
			mProgressControl.setMessageById(R.string.sponsor_progress);
			mProgressControl.showProgress(true);
			mSubmitThread = (SubmitThread) new SubmitThread(this);
			mSubmitThread.execute((Void) null);
		}
	}

	private class SubmitThread extends AsyncTask<Void, Void, String> {
		private TaskCallback mCallback;

		public SubmitThread(TaskCallback callback) {
			mCallback = callback;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			try {
				CookieSyncManager.createInstance(getBaseContext());
				CookieManager cookieManager = CookieManager.getInstance();
				String keyValue = cookieManager.getCookie("http://test20103377.appspot.com/");
				if(keyValue!=null){
					String [] cookieArray = keyValue.split("; ");
					for(int i=0;i<cookieArray.length;i++){
						String [] cookie = cookieArray[i].split("=");
						if(cookie[0].equals("JSESSIONID")){
							httpClient.getCookieStore().addCookie(new BasicClientCookie(cookie[0], cookie[1]));
							CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
							List<Cookie> cookies  = httpClient.getCookieStore().getCookies();
							List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
							httpPost.setHeader((Header) cookieHeader.get(0));
						}
					}
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "donation"));
				nameValuePairs.add(new BasicNameValuePair("projectID", getIntent().getExtras().getString("projectID")));
				nameValuePairs.add(new BasicNameValuePair("email", getIntent().getExtras().getString("email")));
				nameValuePairs.add(new BasicNameValuePair("donation", donationValue.toString()));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpClient.execute(httpPost);
				return EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			return null;
		}

		protected void onPostExecute(String result) {
			mSubmitThread = null;
			if(result.contains("Submit Success")){
				mCallback.done();
			}
		}
		@Override
		protected void onCancelled() {
			mSubmitThread = null;
			mProgressControl.showProgress(false);
		}
	}


}
