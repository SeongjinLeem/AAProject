package com.example.aaproject.main;

import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import com.example.aaproject.model.Recruit;
import com.example.aaproject.util.ProgressControl;
import com.example.aaproject.util.TaskCallback;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class RecruitStateActivity extends Activity implements TaskCallback{
	static final String url = "http://test20103377.appspot.com/recruitlist";
	private RecruitListTask mRecruitListTask = null;
	private ProgressControl mProgressControl = null;
	private ExpandableListView mRecruitListView;
	private ExpandableListView mJoinListView;
	private List<Recruit> mRecruitList = null;
	private List<Recruit> mJoinList = null;
	private Recruit mSelectedRecruit = null;
	private String mMyEmail = null;
	public TaskCallback task = this;
	public RecruitTask mRecruitTask = null;
	public boolean startFlag = true;
	public String mAccept = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recruit_state);

		mMyEmail = getIntent().getExtras().getString("email");
		mRecruitListView = (ExpandableListView) findViewById(R.id.recruit_request);
		mJoinListView = (ExpandableListView) findViewById(R.id.join_request);
		mProgressControl = new ProgressControl(getBaseContext(), findViewById(R.id.main_form), findViewById(R.id.loading_status), (TextView) findViewById(R.id.loading_status_message));
		mProgressControl.setMessageById(R.string.loading_progress);
		mProgressControl.showProgress(true);
		mRecruitListTask = new RecruitListTask(this);
		mRecruitListTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recruit_state, menu);
		return true;
	}

	@Override
	public void done() {
		if(startFlag){
			startFlag = false;
			mRecruitListTask = null;
			RecruitAdapter recruitAdapter= new RecruitAdapter(getBaseContext(), mRecruitList, mMyEmail);
			mRecruitListView.setAdapter(recruitAdapter);

			RecruitAdapter joinAdapter= new RecruitAdapter(getBaseContext(), mJoinList, mMyEmail);
			mJoinListView.setAdapter(joinAdapter);

			mProgressControl.showProgress(false);
		}else{
			
		}
	}


	public class RecruitListTask extends AsyncTask<String, Void, Boolean> {
		private TaskCallback mCallback;
		public RecruitListTask(TaskCallback callback) {
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
				nameValuePairs.add(new BasicNameValuePair("action", "recruitlist"));
				nameValuePairs.add(new BasicNameValuePair("email", mMyEmail));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseBody = EntityUtils.toString(response.getEntity());

				if(responseBody != null){
					mRecruitList = new ArrayList<Recruit>();
					mJoinList = new ArrayList<Recruit>();
					InputSource is = new InputSource(new StringReader(responseBody));
					try{
						Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);    
						XPath xpath = XPathFactory.newInstance().newXPath();
						NodeList recruits = (NodeList)xpath.evaluate("/recruitList/recruit", document, XPathConstants.NODESET);
						for( int idx=0; idx<recruits.getLength(); idx++ ){
							Node node = recruits.item(idx).getFirstChild();
							String from = node.getTextContent();
							node = node.getNextSibling();
							String to = node.getTextContent();
							node = node.getNextSibling();
							Long projectID = Long.parseLong(node.getTextContent());
							node = node.getNextSibling();
							String projectName = URLDecoder.decode(node.getTextContent(), "UTF-8");
							node = node.getNextSibling();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);   
							Date date = format.parse(node.getTextContent());
							node = node.getNextSibling();
							String state = node.getTextContent();
							node = node.getNextSibling();
							String message = URLDecoder.decode(node.getTextContent(), "UTF-8");
							if(from.equals(mMyEmail))
								mRecruitList.add(new Recruit(from, to, projectID, projectName, date, state, message));
							else
								mJoinList.add(new Recruit(from, to, projectID, projectName, date, state, message));
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

	public class RecruitTask extends AsyncTask<Void, Void, String> {
		private TaskCallback mCallback;
		public RecruitTask(TaskCallback callback) {
			mCallback = callback;
		}
		@Override
		protected String doInBackground(Void... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://test20103377.appspot.com/recruitreply");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "recruitreply"));
				nameValuePairs.add(new BasicNameValuePair("toEmail", mMyEmail));
				nameValuePairs.add(new BasicNameValuePair("fromEmail", mSelectedRecruit.getFrom()));
				nameValuePairs.add(new BasicNameValuePair("projectID", mSelectedRecruit.getProjectID().toString()));
				nameValuePairs.add(new BasicNameValuePair("state", mAccept));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				return EntityUtils.toString(response.getEntity());

			} catch (Exception e) {
				// TODO Auto-generated catch block   
			}

			return null;
		}

		@Override
		protected void onPostExecute(final String result) {
			mRecruitTask = null;
			//mProgressControl.showProgress(false);
			if(result.equals("Success")){
				mCallback.done();
			}
		}
	}


	public class RecruitAdapter extends BaseExpandableListAdapter {  
		private Context context;  
		private List<Recruit> recruits; 
		private String mMyEmail;

		public RecruitAdapter(Context context, List<Recruit> recruits, String email) {  
			this.context = context;  
			this.recruits = recruits; 
			this.mMyEmail = email;
		}  

		class ViewHolder {
			public TextView from;
			public TextView project;
			public TextView state;
			public TextView message;
			public Button permitButton;
			public Button rejectButton;
			public TextView date;
		}

		@Override
		public Object getChild(int arg0, int arg1) {
			return recruits.get(arg0).getMessage();
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			if (rowView == null) 
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
				rowView = inflater.inflate(R.layout.recruit_request, null); 
				holder = new ViewHolder();
				holder.message = (TextView) rowView.findViewById(R.id.invite_message);
				holder.permitButton = (Button) rowView.findViewById(R.id.permit_button);
				holder.rejectButton = (Button) rowView.findViewById(R.id.reject_button);
				rowView.setTag(holder);	
			} 
			else 
			{
				holder = (ViewHolder) rowView.getTag();
			}
			holder.message.setText(recruits.get(groupPosition).getMessage());
			if(recruits.get(groupPosition).getFrom().equals(mMyEmail)){
				holder.permitButton.setVisibility(View.GONE);
				holder.rejectButton.setVisibility(View.GONE);
			}else{
				holder.permitButton.setTag((groupPosition)*10);
				holder.rejectButton.setTag((groupPosition)*10 + 1);
			
				OnClickListener cl = new OnClickListener() {  
					@Override  
					public void onClick(View v) {  
						int tag = Integer.parseInt(v.getTag().toString());
						int pos = tag/10;
						int flag = tag%10;
						mSelectedRecruit = mJoinList.get(pos);
						mJoinList.remove(pos);
						RecruitAdapter joinAdapter= new RecruitAdapter(getBaseContext(), mJoinList, mMyEmail);
						mJoinListView.setAdapter(joinAdapter);		
						((RecruitAdapter)mJoinListView.getExpandableListAdapter()).notifyDataSetChanged();
						mAccept = (flag==0)?"accepted":"denied";
						mRecruitTask = new RecruitTask(task);
						mRecruitTask.execute();
					}  
				};  
				
				holder.permitButton.setOnClickListener(cl);
				holder.rejectButton.setOnClickListener(cl);
				
			}
			return rowView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return 1;
		}

		@Override
		public Object getGroup(int arg0) {
			return recruits.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return recruits.size();
		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			if (rowView == null) 
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
				rowView = inflater.inflate(R.layout.recruit_state, null); 
				holder = new ViewHolder();
				holder.project = (TextView) rowView.findViewById(R.id.project);
				holder.from = (TextView) rowView.findViewById(R.id.invite_email);
				holder.state = (TextView) rowView.findViewById(R.id.invite_state);
				rowView.setTag(holder);	
			} 
			else 
			{
				holder = (ViewHolder) rowView.getTag();
			}
			holder.project.setText(recruits.get(groupPosition).getProjectName());
			if(recruits.get(groupPosition).getFrom().equals(mMyEmail))
				holder.from.setText(recruits.get(groupPosition).getTo());
			else
				holder.from.setText(recruits.get(groupPosition).getFrom());
			holder.state.setText(recruits.get(groupPosition).getStatus());
			if(recruits.get(groupPosition).getStatus().equals("accepted"))
				holder.state.setTextColor(Color.rgb(50, 205, 50));
			else if(recruits.get(groupPosition).getStatus().equals("denied"))
				holder.state.setTextColor(Color.rgb(178, 34, 34));
			return rowView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}  
	} 

}
