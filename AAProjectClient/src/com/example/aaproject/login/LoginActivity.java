package com.example.aaproject.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import com.example.aaproject.main.MainFragmentActivity;
import com.example.aaproject.register.RegisterActivity;
import com.example.aaproject.util.ProgressControl;
import com.example.aaproject.util.TaskCallback;
import com.google.android.gcm.GCMRegistrar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements TaskCallback {
	private static final int REGISTER_ACTIVITY = 1;
	static final String url = "http://test20103377.appspot.com/login";
	static final String SENDER_ID = "789115720179";

	private String mRegID = "";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private ProgressControl mProgressControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		mRegID = GCMRegistrar.getRegistrationId(this);
		if (mRegID.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.v("test", "Already registered");
		}
		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
		mProgressControl = new ProgressControl(getBaseContext(), findViewById(R.id.login_form), findViewById(R.id.login_status),(TextView) findViewById(R.id.login_status_message));

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
						startActivityForResult(intent, REGISTER_ACTIVITY);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mProgressControl.setMessageById(R.string.login_progress_signing_in);
			mProgressControl.showProgress(true);
			mAuthTask = new UserLoginTask(this);
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private TaskCallback mCallback;
		public UserLoginTask(TaskCallback callback) {
		    mCallback = callback;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
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
				nameValuePairs.add(new BasicNameValuePair("action", "Login"));
				nameValuePairs.add(new BasicNameValuePair("regId", mRegID));
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				nameValuePairs.add(new BasicNameValuePair("password", sha1(mPassword)));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseBody = EntityUtils.toString(response.getEntity());

				if(responseBody.contains("Login Success")){
					CookieSyncManager.createInstance(getBaseContext());
					cookieManager = CookieManager.getInstance();
					cookieManager.removeAllCookie();
					Header[] cookies = response.getHeaders("Set-Cookie");
					boolean cookieFlag = false;
					for(int i=0;i<cookies.length && cookieFlag==false;i++){
						String [] header = cookies[i].getValue().split("; ");
						for(int j=0;j<header.length && cookieFlag==false;j++){
							String [] cookie = header[j].split("=");
							if(cookie[0].equals("JSESSIONID")){
								cookieManager.setCookie("http://test20103377.appspot.com/", cookie[0] + "=" + cookie[1]);
								cookieFlag=true;
							}
						}
					}
					cookieManager.setCookie("http://test20103377.appspot.com/", "email" + "=" + mEmail);
					CookieSyncManager.getInstance().startSync();
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			mProgressControl.showProgress(false);

			if (success) {
				mCallback.done();
				
			} else {
				Toast.makeText(getBaseContext(), "Wrong Login Information", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			mProgressControl.showProgress(false);
		}
	}

	@Override
	public void done() {
		Intent intent = new Intent(getBaseContext(), MainFragmentActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);

		switch(requestCode){
		case REGISTER_ACTIVITY:
			break;
		}
	}
}
