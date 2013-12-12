package com.example.aaproject.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.example.aaproject.R;
import com.example.aaproject.register.LocationActivity;
import com.example.aaproject.util.TaskCallback;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
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
	private View mSubmitStatusView;
	private TextView mSubmitStatusMessageView;

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
		mSubmitStatusView = findViewById(R.id.submit_status);
		mSubmitStatusMessageView = (TextView) findViewById(R.id.submit_status_message);

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
			mSubmitStatusMessageView.setText(R.string.sponsor_progress);
			showProgress(true);
			mSubmitThread = (SubmitThread) new SubmitThread(this);
			mSubmitThread.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mSubmitStatusView.setVisibility(View.VISIBLE);
			mSubmitStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mSubmitStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mSponsorGroup.setVisibility(View.VISIBLE);
			mSponsorGroup.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mSponsorGroup.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mSubmitStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSponsorGroup.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}


	private class SubmitThread extends AsyncTask<Void, Void, String> {
		private TaskCallback mCallback;

		public SubmitThread(TaskCallback callback) {
			mCallback = callback;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			try {
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
			showProgress(false);
			if(result.contains("Submit Success")){
				mCallback.done();
			}
		}
		@Override
		protected void onCancelled() {
			mSubmitThread = null;
			showProgress(false);
		}
	}


}
