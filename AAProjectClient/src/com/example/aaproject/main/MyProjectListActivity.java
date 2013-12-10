package com.example.aaproject.main;

import java.io.StringReader;
import java.net.URL;
import java.net.URLDecoder;
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
import com.example.aaproject.login.LoginActivity;
import com.example.aaproject.util.TaskCallback;
import com.example.aaproject.util.TaskCallbackWithResume;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
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
public class MyProjectListActivity extends Fragment implements TaskCallbackWithResume{
	private static final int PROJECT_CREATE_ACTIVITY = 3;
	Context mContext;
	private GridView mGridView;  
	private LogoutThread mLogoutThread;
	private View mListStatusView;
	private TextView mListStatusMessageView;
	private MyListLoadingTask mMyListLoadingTask = null;
	static final String url = "http://test20103377.appspot.com/projectlist";
	private List<Project> projectList = null;
	private String task = null;


	public MyProjectListActivity(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_my_project_list, null);
		mGridView = (GridView) view.findViewById(R.id.gridView);
		mListStatusView = view.findViewById(R.id.list_status);
		mListStatusMessageView = (TextView) view.findViewById(R.id.list_status_message);
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

		mListStatusMessageView.setText(R.string.list_progress);
		showProgress(true);

		mMyListLoadingTask = new MyListLoadingTask(this);
		mMyListLoadingTask.execute((Void) null);
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
		}
		
	}
	@Override
	public void onResume() {
		super.onResume();
		if (mReturningWithResult) {
			mListStatusMessageView.setText(R.string.list_progress);
			showProgress(true);

			mMyListLoadingTask = new MyListLoadingTask(this);
			mMyListLoadingTask.execute((Void) null);
			List<Fragment> list = getFragmentManager().getFragments();	
			ProjectListActivity getFrament = (ProjectListActivity)list.get(0);
			getFrament.listReload();
		}
		mReturningWithResult = false;
	}

	@Override
	public void onPostResume() {
		
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
				task = "Logout";
				mCallback.done();
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

			mListStatusView.setVisibility(View.VISIBLE);
			mListStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mListStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mGridView.setVisibility(View.VISIBLE);
			mGridView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mGridView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mListStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mGridView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class MyListLoadingTask extends AsyncTask<Void, Void, String> {
		private TaskCallback mCallback;
		public MyListLoadingTask(TaskCallback callback) {
			mCallback = callback;
		}
		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			String myEmail = null;
			try {
				CookieSyncManager.createInstance(mContext);
				CookieManager cookieManager = CookieManager.getInstance();
				String keyValue = cookieManager.getCookie(url);
				if(keyValue!=null){
					String [] cookieArray = keyValue.split("; ");
					for(int i=0;i<cookieArray.length;i++){
						String [] cookie = cookieArray[i].split("=");
						if(cookie[0].equals("email")){
							myEmail = cookie[1];
							break;
						}
					}
				}
				if(myEmail!=null){
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("action", "mylist"));
					nameValuePairs.add(new BasicNameValuePair("email", myEmail));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					String responseBody = EntityUtils.toString(response.getEntity());
					if(responseBody != null){
						projectList = new ArrayList<Project>();
						InputSource is = new InputSource(new StringReader(responseBody));
						try{
							Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);    
							XPath xpath = XPathFactory.newInstance().newXPath();
							NodeList projects = (NodeList)xpath.evaluate("/projectList/project", document, XPathConstants.NODESET);
							for( int idx=0; idx<projects.getLength(); idx++ ){
								Node node = projects.item(idx).getFirstChild();
								Long id = Long.parseLong(node.getTextContent());
								node = node.getNextSibling();
								String title = URLDecoder.decode(node.getTextContent(), "UTF-8");
								node = node.getNextSibling();
								String email = node.getTextContent();
								node = node.getNextSibling();
								String contents = URLDecoder.decode(node.getTextContent(), "UTF-8");
								node = node.getNextSibling();
								String urlString = node.getTextContent();
								URL imgUrl = new URL(urlString);
								Bitmap bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
								projectList.add(new Project(id, title, email, contents, bitmap));
							}
						}catch(Exception e){
						}
					}
					return responseBody;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			return null;
		}

		@Override
		protected void onPostExecute(final String result) {
			task = "List";
			mCallback.done();
		}

		@Override
		protected void onCancelled() {
			mMyListLoadingTask = null;
			showProgress(false);
		}
	}

	@Override
	public void done() {
		if(task.equals("List")){
			ProjectAdapter adapter = new ProjectAdapter(mContext, projectList);
			mGridView.setAdapter(adapter);  
			adapter.notifyDataSetChanged();

			mGridView.setOnItemClickListener(new OnItemClickListener() {  
				public void onItemClick(AdapterView<?> parent, View v,  
						int position, long id) {  
					Toast.makeText(mContext,  ((TextView) v.findViewById(R.id.title)).getText(), Toast.LENGTH_SHORT).show();  
				}  
			});

			mMyListLoadingTask = null;
			showProgress(false);
		}
		else if(task.equals("Logout")){
			Intent intent = new Intent(mContext, LoginActivity.class);
			getActivity().startActivity(intent);
			getActivity().finish();
		}
	}

}