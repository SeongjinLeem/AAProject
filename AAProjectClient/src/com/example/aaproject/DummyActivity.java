package com.example.aaproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.example.aaproject.register.RegisterActivity;
/*
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
 */
import com.google.android.gcm.GCMRegistrar;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DummyActivity extends Activity {
	static final String url = "http://test20103377.appspot.com/login";
	static final String SENDER_ID = "789115720179";
	private static final int CONNECT_TIMEOUT = 3000;
	private static final int READ_TIMEOUT = 10000;

	private String regId = "";
	private String id = "";
	private String password = "";
	private LoginThread mLoginThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dummy);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.v("test", "Already registered");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dummy, menu);
		return true;
	}

	public void onClickLoginButtonMethod(View v) {

		try {
			id = ((TextView) findViewById(R.id.editText_ID)).getText()
					.toString();
			password = ((TextView) findViewById(R.id.editText_Password))
					.getText().toString();
			if (id.trim().equals("") || password.trim().equals(""))
				return;
			//Intent intent = new Intent(this, SecondActivity.class);
			//startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG)
			.show();
		}

		mLoginThread = (LoginThread) new LoginThread().execute((Void) null);


	}

	public void onClickRegisterButtonMethod(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	private class LoginThread extends AsyncTask<Void, Void, Void> {

		private Exception exception;

		@Override
		protected Void doInBackground(Void... arg0) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "baseinfo"));
				nameValuePairs.add(new BasicNameValuePair("regID", regId));
				nameValuePairs.add(new BasicNameValuePair("name", id));
				nameValuePairs.add(new BasicNameValuePair("password", password));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseBody = EntityUtils.toString(response.getEntity());

			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			return null;
		}
	}
}
