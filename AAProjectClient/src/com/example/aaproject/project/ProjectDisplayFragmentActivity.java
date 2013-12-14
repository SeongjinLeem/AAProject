package com.example.aaproject.project;

import java.io.StringReader;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.example.aaproject.R;
import com.example.aaproject.R.id;
import com.example.aaproject.R.layout;
import com.example.aaproject.R.menu;
import com.example.aaproject.R.string;
import com.example.aaproject.main.MyProjectListFragment;
import com.example.aaproject.main.ProjectListFragment;
import com.example.aaproject.main.MainFragmentActivity.ListLoadingTask;
import com.example.aaproject.model.Member;
import com.example.aaproject.model.Project;
import com.example.aaproject.model.Sponsor;
import com.example.aaproject.util.PageLoadFragment;
import com.example.aaproject.util.ProgressControl;
import com.example.aaproject.util.TaskCallback;
import com.google.android.gms.maps.model.LatLng;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

public class ProjectDisplayFragmentActivity extends FragmentActivity implements TaskCallback{
	static final String url = "http://test20103377.appspot.com/projectdisplay";
	private ProjectLoadingTask mProjectLoadingTask = null;
	public Project mProject = null;
	public List<Member> mMemberList = null;
	public List<Sponsor> mSponsorList = null;
	public String mProjectID = null;
	public String mMyEmail = null;
	public String mJSESSIONID = null;
	public boolean mCreateFlag = true;
	private ProgressControl mProgressControl;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_display);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mProgressControl = new ProgressControl(getBaseContext(), mViewPager, findViewById(R.id.project_status), (TextView) findViewById(R.id.project_status_message));
		mProgressControl.setMessageById(R.string.project_progress);

		Bundle extras = getIntent().getExtras();
		mProjectID = extras.getString("projectID");
		mMyEmail = extras.getString("email");
		mJSESSIONID = extras.getString("JSESSIONID");

		projectInfoLoad();
	}

	public void projectInfoLoad(){
		mProgressControl.showProgress(true);

		mProjectLoadingTask = new ProjectLoadingTask(this);
		mProjectLoadingTask.execute();
	}

	@Override
	public void done() {
		mProgressControl.showProgress(false);
		if(mCreateFlag==false){
			List<Fragment> list = getSupportFragmentManager().getFragments();
			for(int i=0;i<list.size();i++){
			((PageLoadFragment)list.get(i)).pageLoad();
			}

		}else{
			mCreateFlag = false;
			mProjectLoadingTask = null;
			// Create the adapter that will return a fragment for each of the three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

			// Set up the ViewPager with the sections adapter.

			mViewPager.setAdapter(mSectionsPagerAdapter);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_display, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override

		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch(position) {
			case 0:
				return new ProjectIntroductionFragment();
			case 1:
				return new ProjectMemberFragment();
			case 2:
				return new ProjectSponsorFragment();
			}
			return null;
		}


		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.project_introduce);
			case 1:
				return getString(R.string.project_member);
			case 2:
				return getString(R.string.project_sponsor);
			}
			return null;
		}
	}
	public class ProjectLoadingTask extends AsyncTask<String, Void, Boolean> {
		private TaskCallback mCallback;
		public ProjectLoadingTask(TaskCallback callback) {
			mCallback = callback;
		}
		@Override
		protected Boolean doInBackground(String... params) {
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
				nameValuePairs.add(new BasicNameValuePair("action", "project"));
				nameValuePairs.add(new BasicNameValuePair("projectID", mProjectID));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseBody = EntityUtils.toString(response.getEntity());
				if(responseBody != null && !responseBody.equals("Project searching fail.")){
					InputSource is = new InputSource(new StringReader(responseBody));
					try{
						Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);    
						XPath xpath = XPathFactory.newInstance().newXPath();
						NodeList project = (NodeList)xpath.evaluate("//project", document, XPathConstants.NODESET);
						{
							Node node = project.item(0).getFirstChild();
							Long id = Long.parseLong(node.getTextContent());
							node = node.getNextSibling();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);   
							Date date = format.parse(node.getTextContent());
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
							node = node.getNextSibling();
							int goal = Integer.parseInt(node.getTextContent());
							node = node.getNextSibling();
							int donation = Integer.parseInt(node.getTextContent());
							mProject = new Project(id, date, title, email, contents, bitmap, goal, donation);
						}
						mMemberList = new ArrayList<Member>();
						NodeList members = (NodeList)xpath.evaluate("//member", document, XPathConstants.NODESET);
						for( int idx=0; idx<members.getLength(); idx++ ){
							Node node = members.item(idx).getFirstChild();
							String email = node.getTextContent();
							node = node.getNextSibling();
							String field = node.getTextContent();
							node = node.getNextSibling();
							String geoPoint = node.getTextContent();
							double lat = Double.parseDouble(geoPoint.substring(0, geoPoint.indexOf(",")));
							double lng = Double.parseDouble(geoPoint.substring(geoPoint.indexOf(",")+1));
							LatLng location = new LatLng(lat, lng);
							mMemberList.add(new Member(email, field, location));
						}
						mSponsorList = new ArrayList<Sponsor>();
						NodeList sponsors = (NodeList)xpath.evaluate("//sponsor", document, XPathConstants.NODESET);
						for( int idx=0; idx<sponsors.getLength(); idx++ ){
							Node node = sponsors.item(idx).getFirstChild();
							String email = node.getTextContent();
							node = node.getNextSibling();
							int donation = Integer.parseInt(node.getTextContent());
							node = node.getNextSibling();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);   
							Date date = format.parse(node.getTextContent());
							mSponsorList.add(new Sponsor(email, donation, date));
						}

					}catch(Exception e){
						Log.w("list error", e.toString());
					}
				}

				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean result) {
			mCallback.done();
		}
	}
}
