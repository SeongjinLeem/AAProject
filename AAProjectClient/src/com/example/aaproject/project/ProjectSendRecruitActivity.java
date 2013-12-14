package com.example.aaproject.project;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.example.aaproject.R;
import com.example.aaproject.main.MainFragmentActivity;
import com.example.aaproject.model.Member;
import com.example.aaproject.model.RecommendMemberAdapter;
import com.example.aaproject.util.GeoInfoTrans;
import com.example.aaproject.util.ProgressControl;
import com.example.aaproject.util.TaskCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectSendRecruitActivity extends FragmentActivity implements TaskCallback{
	static final String url = "http://test20103377.appspot.com/recruit";
	private RecruitThread mRecruitThread = null;
	private String mMyEmail = null;
	private String mProjectID = null;
	private String mRecruit = null;
	private String mMessage = null;
	private ProgressControl mProgressControl = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_send_recruit);
		mProgressControl = new ProgressControl(getBaseContext(), findViewById(R.id.submit_form), findViewById(R.id.submit_status), (TextView)findViewById(R.id.submit_status_message));
		mMyEmail = getIntent().getExtras().getString("email");
		mProjectID = getIntent().getExtras().getString("projectID");
		mRecruit = getIntent().getExtras().getString("recruit");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_recruit, menu);
		return true;
	}

	public void done() {
			Intent intent = new Intent(getBaseContext(), ProjectRecruitActivity.class);
			this.setResult(RESULT_OK, intent);
			this.finish();
	}
	
	public void OnClickSubmitButtonMethod(View v) {
		boolean cancel = false;
		String errmsg = null;
		mMessage = ((EditText)findViewById(R.id.profile_message)).getText().toString();
		if (TextUtils.isEmpty(mMessage)) {
			errmsg = "초대 메세지를 입력해 주세요.";
			cancel = true;
		} 
		if (cancel) {
			Toast.makeText(getBaseContext(), errmsg, Toast.LENGTH_SHORT).show();
		} else {
			mProgressControl.setMessageById(R.string.submit_progress);
			mProgressControl.showProgress(true);
			mRecruitThread = new RecruitThread(this);
			mRecruitThread.execute((Void) null);
		}
	}

	private class RecruitThread extends AsyncTask<Void, Void, String> {

		private Exception exception;
		private TaskCallback mCallback;

		public RecruitThread(TaskCallback callback) {
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
				nameValuePairs.add(new BasicNameValuePair("action", "recruit"));
				nameValuePairs.add(new BasicNameValuePair("email", mMyEmail));
				nameValuePairs.add(new BasicNameValuePair("projectID", mProjectID));
				nameValuePairs.add(new BasicNameValuePair("recruit", mRecruit));
				nameValuePairs.add(new BasicNameValuePair("message", URLEncoder.encode(mMessage, "UTF-8")));
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
			mRecruitThread = null;
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
			mRecruitThread = null;
			mProgressControl.showProgress(false);
		}
	}

}
