package com.example.aaproject.register;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.aaproject.R;
import com.example.aaproject.R.array;
import com.example.aaproject.R.id;
import com.example.aaproject.R.layout;
import com.example.aaproject.R.menu;
import com.example.aaproject.login.LoginActivity;
import com.example.aaproject.util.TaskCallback;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements TaskCallback {
	private static final String url = "http://test20103377.appspot.com/register";
	private static final int LOCATION_ACTIVITY = 2;

	ArrayAdapter<CharSequence>  fieldSpin;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmView;
	private EditText mNameView;
	private EditText mAgeView;
	private RadioGroup mGenderGroup;
	private Spinner mFieldView;
	private EditText mLocationView;
	private View mRegisterFormView;
	private View mRegisterStatusView;
	private TextView mRegisterStatusMessageView;

	private RegisterThread mRegisterThread = null;
	private EmailCheckThread mEmailCheckThread = null;
	final Handler mHandler = new Handler();
	
	public void done() {
		Intent intent = new Intent(getBaseContext(), LoginActivity.class);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
		mNameView = (EditText) findViewById(R.id.name);
		mAgeView = (EditText) findViewById(R.id.age);
		mGenderGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mFieldView = (Spinner) findViewById(R.id.spinner1);
		mFieldView.setPrompt("select your field");
		fieldSpin = ArrayAdapter.createFromResource(this, R.array.field,    android.R.layout.simple_spinner_item);
		fieldSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mFieldView.setAdapter(fieldSpin);

		mRegisterFormView = findViewById(R.id.register_form);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);
		
		mLocationView = (EditText) findViewById(R.id.location);
		mLocationView.setKeyListener(null);
		mLocationView.setOnFocusChangeListener(new EditText.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View view, boolean bFocus) {
				if (bFocus == true) {
					Intent intent = new Intent(getBaseContext(), LocationActivity.class);
					Bundle extra = new Bundle();
					if(!mLocationView.getText().toString().equals("")){
						extra.putString("geoPoint", mLocationView.getText().toString());
					}
					extra.putString("field", mFieldView.getSelectedItem().toString());
					intent.putExtras(extra);
					startActivityForResult(intent, LOCATION_ACTIVITY);

				}
			}
		});

		mEmailView.setOnFocusChangeListener(new EditText.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View view, boolean bFocus) {
				if (bFocus == false) {
					if(!mEmailView.getText().toString().contains("@")){
						mEmailView.setError("invalid Email Address");
					}
					else{
						mEmailCheckThread = (EmailCheckThread) new EmailCheckThread().execute((Void) null);
					}
				}
			}
		});

		mPasswordConfirmView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().equals(mPasswordView.getText().toString())){
					mPasswordConfirmView.setError(null);
				}else{
					mPasswordConfirmView.setError("Password and its confirmation are different");
				}
			}
			@Override
			public void afterTextChanged(Editable arg0) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});

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

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mRegisterStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mRegisterFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);

		switch(requestCode){
		case LOCATION_ACTIVITY:
			if(resultCode == RESULT_OK){
				String latlng = intent.getExtras().getString("latlng");
				mLocationView.setText(latlng);
			}
		}
	}

	public void OnClickRegisterButtonMethod(View v) {
		boolean cancel = false;
		EditText focusView = null;
		String errmsg = null;

		if (TextUtils.isEmpty(mEmailView.getText().toString())) {
			errmsg = "Email Address is empty";
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmailView.getText().toString().contains("@")) {
			errmsg = "invalid Email Address";
			focusView = mEmailView;
			cancel = true;
		} 
		else if (TextUtils.isEmpty(mPasswordView.getText().toString())) {
			errmsg = "Password is empty";
			focusView = mPasswordView;
			cancel = true;
		} else if (mPasswordView.getText().toString().length() < 4) {
			errmsg = "Too short Password";
			focusView = mPasswordView;
			cancel = true;
		} 
		else if (TextUtils.isEmpty(mPasswordConfirmView.getText().toString())) {
			errmsg = "Password confirm is empty";
			focusView = mPasswordConfirmView;
			cancel = true;
		} else if (!mPasswordView.getText().toString().equals(mPasswordConfirmView.getText().toString())) {
			errmsg = "Password and its confirmation are different";
			focusView = mPasswordConfirmView;
			cancel = true;
		} 
		else if (TextUtils.isEmpty(mNameView.getText().toString())) {
			errmsg = "Name is empty";
			focusView = mNameView;
			cancel = true;
		} else if (TextUtils.isEmpty(mAgeView.getText().toString())) {
			errmsg = "Age is empty";
			focusView = mAgeView;
			cancel = true;
		} else if (TextUtils.isEmpty(mLocationView.getText().toString())) {
			errmsg = "Location is empty";
			focusView = mLocationView;
			cancel = true;
		}

		if (cancel) {
			focusView.setError(errmsg);
			Toast.makeText(getBaseContext(), errmsg, Toast.LENGTH_SHORT).show();
			focusView.requestFocus();
		} else {
			mRegisterStatusMessageView.setText(R.string.register_progress);
			showProgress(true);
			mRegisterThread = (RegisterThread) new RegisterThread(this).execute((Void) null);
		}
	}

	private class RegisterThread extends AsyncTask<Void, Void, String> {

		private Exception exception;
		private TaskCallback mCallback;

		public RegisterThread(TaskCallback callback) {
		    mCallback = callback;
		}

		@Override
		protected String doInBackground(Void... arg0) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "Register"));
				nameValuePairs.add(new BasicNameValuePair("email", mEmailView.getText().toString()));
				String HashedPassword = sha1(mPasswordView.getText().toString());
				nameValuePairs.add(new BasicNameValuePair("password", HashedPassword));
				nameValuePairs.add(new BasicNameValuePair("name", URLEncoder.encode(mNameView.getText().toString(), "UTF-8")));
				nameValuePairs.add(new BasicNameValuePair("age", mAgeView.getText().toString()));
				int radioButtonId = mGenderGroup.getCheckedRadioButtonId();
				View radioButton = mGenderGroup.findViewById(radioButtonId);
				int idx = mGenderGroup.indexOfChild(radioButton);
				String gender = ((RadioButton)mGenderGroup.getChildAt(idx)).getText().toString();
				nameValuePairs.add(new BasicNameValuePair("gender", gender));
				nameValuePairs.add(new BasicNameValuePair("field", mFieldView.getSelectedItem().toString()));
				nameValuePairs.add(new BasicNameValuePair("location", mLocationView.getText().toString()));
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
			mRegisterThread = null;
			showProgress(false);
			if(result.contains("Register Success")){
				mCallback.done();
			}else if(result.contains("Duplicate Email")){
				mHandler.post(new Runnable() {
					public void run() {
						mEmailView.setError("This email address is already used");
					}
				});
			}
		}
		
		protected void onCancelled() {
			mRegisterThread = null;
			showProgress(false);
		}
	}

	private class EmailCheckThread extends AsyncTask<Void, Void, String> {

		private Exception exception;
		@Override
		protected String doInBackground(Void... arg0) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "EmailCheck"));
				nameValuePairs.add(new BasicNameValuePair("email", mEmailView.getText().toString()));
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
			if(result.contains("Duplicate Email")){
				mHandler.post(new Runnable() {
				      public void run() {
				    	  mEmailView.setError("This email address is already used");
				      }
				});
			}else{
				mHandler.post(new Runnable() {
				      public void run() {
				    	  mEmailView.setError(null);
				    	  Toast.makeText(getBaseContext(), "This email address is available", Toast.LENGTH_SHORT).show();
				      }
				});
			}
		}
	}
	static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());         
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	} 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
