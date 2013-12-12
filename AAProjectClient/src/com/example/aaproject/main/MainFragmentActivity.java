package com.example.aaproject.main;

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
import com.example.aaproject.project.Project;
import com.example.aaproject.util.TaskCallback;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;



public class MainFragmentActivity extends FragmentActivity implements
ActionBar.TabListener, TaskCallback {
	static final String url = "http://test20103377.appspot.com/projectlist";
	public List<Project> mProjectList = null;
	public List<Project> mMyProjectList = null;
	private ListLoadingTask mListLoadingTask = null;
	private View mListStatusView;
	private TextView mListStatusMessageView;
	public String mMyEmail = null;
	public String mJSESSIONID = null;
	private boolean mCreateFlag = true;

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
		setContentView(R.layout.activity_main);

		mViewPager = (ViewPager) findViewById(R.id.pager);

		CookieSyncManager.createInstance(getBaseContext());
		CookieManager cookieManager = CookieManager.getInstance();
		String keyValue = cookieManager.getCookie(url);
		if(keyValue!=null){
			String [] cookieArray = keyValue.split("; ");
			for(int i=0;i<cookieArray.length;i++){
				String [] cookie = cookieArray[i].split("=");
				if(cookie[0].equals("JSESSIONID")){
					mJSESSIONID = cookie[1];
				}else if(cookie[0].equals("email")){
					mMyEmail = cookie[1];
				}
			}
		}
		mListStatusView = findViewById(R.id.main_status);
		mListStatusMessageView = (TextView) findViewById(R.id.main_status_message);
		mListStatusMessageView.setText(R.string.main_progress);

		projectListLoad();

	}

	public void projectListLoad(){
		showProgress(true);

		mListLoadingTask = new ListLoadingTask(this);
		mListLoadingTask.execute();
	}

	@Override
	public void done() {
		mListLoadingTask = null;
		if(mCreateFlag==false){
			List<Fragment> list = getSupportFragmentManager().getFragments();
			((ProjectListFragment)list.get(0)).listLoad();
			((MyProjectListFragment)list.get(1)).listLoad();

		}else{
			mCreateFlag = false;
			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Create the adapter that will return a fragment for each of the three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

			// Set up the ViewPager with the sections adapter.
			mViewPager.setAdapter(mSectionsPagerAdapter);

			// When swiping between different sections, select the corresponding
			// tab. We can also use ActionBar.Tab#select() to do this if we have
			// a reference to the Tab.
			mViewPager
			.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
				}
			});

			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				// Create a tab with text corresponding to the page title defined by
				// the adapter. Also specify this Activity object, which implements
				// the TabListener interface, as the callback (listener) for when
				// this tab is selected.
				actionBar.addTab(actionBar.newTab()
						.setText(mSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(this));
			}
		}
		showProgress(false);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
				return new ProjectListFragment();
			case 1:
				return new MyProjectListFragment();
			}
			return null;
		}


		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	public class ListLoadingTask extends AsyncTask<String, Void, Boolean> {
		private TaskCallback mCallback;
		public ListLoadingTask(TaskCallback callback) {
			mCallback = callback;
		}
		@Override
		protected Boolean doInBackground(String... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			try {
				httpclient.getCookieStore().addCookie(new BasicClientCookie("JSESSIONID", mJSESSIONID));
				CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
				List<Cookie> cookies  = httpclient.getCookieStore().getCookies();
				List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
				httppost.setHeader((Header) cookieHeader.get(0));
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "list"));
				if(mCreateFlag)
					nameValuePairs.add(new BasicNameValuePair("start", "true"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseBody = EntityUtils.toString(response.getEntity());
				if(responseBody != null){
					if(responseBody.equals("no data updated"))
						return true;
					mProjectList = new ArrayList<Project>();
					mMyProjectList = new ArrayList<Project>();
					InputSource is = new InputSource(new StringReader(responseBody));
					try{
						Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);    
						XPath xpath = XPathFactory.newInstance().newXPath();
						NodeList projects = (NodeList)xpath.evaluate("/projectList/project", document, XPathConstants.NODESET);
						for( int idx=0; idx<projects.getLength(); idx++ ){
							Node node = projects.item(idx).getFirstChild();
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
							mProjectList.add(new Project(id, date, title, email, contents, bitmap, goal, donation));
							if(email.equals(mMyEmail))
								mMyProjectList.add(new Project(id, date, title, email, contents, bitmap, goal, donation));
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

			mViewPager.setVisibility(View.VISIBLE);
			mViewPager.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mViewPager.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mListStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
