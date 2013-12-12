package com.example.aaproject.main;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.example.aaproject.R;
import com.example.aaproject.util.TaskCallback;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectCreateActivity extends Activity implements TaskCallback {
	private static final String url = "http://test20103377.appspot.com/projectcreate";
	private static final int REQ_CODE_PICK_PICTURE = 0;
	private EditText mTitleView;
	private EditText mContentsView;
	private EditText mGoalView;
	private ImageView mProjectImgView;
	private String mImageName = null;
	private View mCreateFormView;
	private View mCreateStatusView;
	private TextView mCreateStatusMessageView;

	private SubmitThread mSubmitThread = null;
	final Handler mHandler = new Handler();

	public void done() {
		mSubmitThread = null;
		Intent intent = new Intent(getBaseContext(), MyProjectListFragment.class);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_create);
		mTitleView = (EditText) findViewById(R.id.title);
		mProjectImgView = (ImageView)findViewById(R.id.project_image);
		mContentsView = (EditText) findViewById(R.id.contents);
		mGoalView = (EditText) findViewById(R.id.goal);
		mProjectImgView.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent i = new Intent(Intent.ACTION_PICK);
						i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
						i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
						startActivityForResult(i, REQ_CODE_PICK_PICTURE);
					}
				});
		
		mCreateFormView = findViewById(R.id.create_form);
		mCreateStatusView = findViewById(R.id.create_status);
		mCreateStatusMessageView = (TextView) findViewById(R.id.create_status_message);
	}

	public void OnClickSubmitButtonMethod(View v) {
		boolean cancel = false;
		String errmsg = null;

		if (TextUtils.isEmpty(mTitleView.getText().toString())) {
			errmsg = "Title is empty";
			cancel = true;
		}else if (TextUtils.isEmpty(mImageName)) {
			errmsg = "ImageFile is not selected";
			cancel = true;
		}else if (TextUtils.isEmpty(mContentsView.getText().toString())) {
			errmsg = "Contents is empty";
			cancel = true;
		} 

		if (cancel) {
			Toast.makeText(getBaseContext(), errmsg, Toast.LENGTH_SHORT).show();
			
		} else {
			mCreateStatusMessageView.setText(R.string.create_progress);
			showProgress(true);
			mSubmitThread = (SubmitThread) new SubmitThread(this);
			mSubmitThread.execute((Void) null);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQ_CODE_PICK_PICTURE) {
			if (resultCode == Activity.RESULT_OK) { 
				mProjectImgView.setImageURI(intent.getData()); // 사진 선택한 사진URI로 연결하기 
				Uri selPhotoUri = intent.getData();
				try{ 
					Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null,null,null,null);
					c.moveToNext();
					mImageName = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
				}catch(Exception e){}
			}
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

			mCreateStatusView.setVisibility(View.VISIBLE);
			mCreateStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mCreateStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mCreateFormView.setVisibility(View.VISIBLE);
			mCreateFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mCreateFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mCreateStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mCreateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			String reqUrl = null;
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "ReqUrl"));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpClient.execute(httpPost);
				reqUrl = EntityUtils.toString(response.getEntity());				
			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			
			httpClient = new DefaultHttpClient();
			localContext = new BasicHttpContext();
			httpPost = new HttpPost(reqUrl);
			String email = null;
			try {
				CookieSyncManager.createInstance(getBaseContext());
				CookieManager cookieManager = CookieManager.getInstance();
				String keyValue = cookieManager.getCookie("http://test20103377.appspot.com/");
				if(keyValue!=null){
					String [] cookieArray = keyValue.split("; ");
					for(int i=0;i<cookieArray.length;i++){
						String [] cookie = cookieArray[i].split("=");
						if(cookie[0].equals("email")){
							email = cookie[1];
							break;
						}
					}
				}
				
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("action", new StringBody("ProjectCreate"));
				entity.addPart("title", new StringBody(URLEncoder.encode(mTitleView.getText().toString(), "UTF-8")));
				entity.addPart("email", new StringBody(email));
				entity.addPart("image", new FileBody(new File (mImageName)));
				entity.addPart("goal", new StringBody(mGoalView.getText().toString()));
				entity.addPart("contents", new StringBody(URLEncoder.encode(mContentsView.getText().toString(), "UTF-8")));
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				return EntityUtils.toString(response.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			mSubmitThread = null;
			showProgress(false);
			if(result.contains("Create Success")){
				mCallback.done();
			}
		}
		@Override
		protected void onCancelled() {
			mSubmitThread = null;
			showProgress(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
