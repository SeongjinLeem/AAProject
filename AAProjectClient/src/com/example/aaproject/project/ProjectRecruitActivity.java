package com.example.aaproject.project;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectRecruitActivity extends FragmentActivity implements TaskCallback{
	private static final int PROJECT_SEND_RECRUIT_ACTIVITY = 8;
	static final String url = "http://test20103377.appspot.com/recruit";
	private MapLoadingTask mMapLoadingTask = null;
	private GoogleMap mMap;
	private ListView mRecommendListView;
	private List<Member> mRecommendList = null;
	private String mMyEmail = null;
	private String mProjectID = null;
	private LatLng mMyLocation = null;
	private ProgressControl mProgressControl = null;
	private int mCheckedCount = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_recruit);
		mProgressControl = new ProgressControl(getBaseContext(), findViewById(R.id.recruit_form), findViewById(R.id.recruit_status), (TextView)findViewById(R.id.recruit_status_message));
		mRecommendListView = (ListView)  findViewById(R.id.recommend_list);
		mMyEmail = getIntent().getExtras().getString("email");
		mProjectID = getIntent().getExtras().getString("projectID");
		mProgressControl.showProgress(true);

		mMapLoadingTask = new MapLoadingTask(this);
		mMapLoadingTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_recruit, menu);
		return true;
	}

	public void done() {

		mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyLocation, 17));
		for(int i=0;i<mRecommendList.size();i++){
			Member m = mRecommendList.get(i);
			MarkerOptions markerOptions = new MarkerOptions();
			Bitmap markerIcon = markerIconCreate(getBaseContext(), m.getField(), i);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerIcon));
			markerOptions.position(m.getLocation());
			String addr = GeoInfoTrans.searchLocation(getBaseContext(), m.getLocation());
			mMap.addMarker(markerOptions.title(m.getField()).snippet(addr));
		}
		RecommendMemberAdapter adapter = new RecommendMemberAdapter(getBaseContext(), mRecommendList);
		mRecommendListView.setAdapter(adapter);
		mRecommendListView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) { 

				RecommendMemberAdapter adapter = (RecommendMemberAdapter) parent
						.getAdapter();
				Member m = (Member) adapter.getItem(position);
				mCheckedCount += (m.isChecked())?-1:1;
				m.setChecked(!m.isChecked());
				((CheckedTextView)v.findViewById(R.id.profile)).setChecked(m.isChecked());
				adapter.notifyDataSetChanged();

			}  
		});
		mProgressControl.showProgress(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROJECT_SEND_RECRUIT_ACTIVITY:
			if(resultCode == Activity.RESULT_OK){
				Intent intent = new Intent(getBaseContext(), ProjectMemberFragment.class);
				this.setResult(RESULT_OK, intent);
				this.finish();
			}
			break;
		}
	}

	public class MapLoadingTask extends AsyncTask<String, Void, Boolean> {
		private TaskCallback mCallback;
		public MapLoadingTask(TaskCallback callback) {
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
				nameValuePairs.add(new BasicNameValuePair("action", "recommend"));
				nameValuePairs.add(new BasicNameValuePair("email", mMyEmail));
				nameValuePairs.add(new BasicNameValuePair("projectID", mProjectID));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseBody = EntityUtils.toString(response.getEntity());
				if(responseBody != null){
					mRecommendList = new ArrayList<Member>();
					InputSource is = new InputSource(new StringReader(responseBody));
					try{
						Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);    
						XPath xpath = XPathFactory.newInstance().newXPath();
						NodeList myLoc = (NodeList)xpath.evaluate("/recommendList/myLocation", document, XPathConstants.NODESET);
						String geoPoint = myLoc.item(0).getFirstChild().getTextContent();
						double lat = Double.parseDouble(geoPoint.substring(0, geoPoint.indexOf(",")));
						double lng = Double.parseDouble(geoPoint.substring(geoPoint.indexOf(",")+1));
						mMyLocation = new LatLng(lat, lng);
						NodeList memberList = (NodeList)xpath.evaluate("/recommendList/member", document, XPathConstants.NODESET);
						for( int idx=0; idx<memberList.getLength(); idx++ ){
							Node node = memberList.item(idx).getFirstChild();
							String email = node.getTextContent();
							node = node.getNextSibling();
							String field = node.getTextContent();
							node = node.getNextSibling();
							geoPoint = node.getTextContent();
							lat = Double.parseDouble(geoPoint.substring(0, geoPoint.indexOf(",")));
							lng = Double.parseDouble(geoPoint.substring(geoPoint.indexOf(",")+1));
							mRecommendList.add(new Member(email, field, new LatLng(lat, lng)));
						}
					}catch(Exception e){
						Log.w("recommend list error", e.toString());
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

	public void OnClickSubmitButtonMethod(View v) {
		boolean cancel = false;
		String errmsg = null;

		if (mCheckedCount == 0) {
			errmsg = "최소한 한 명의 사용자는 초대해야 합니다.";
			cancel = true;
		} 
		if (cancel) {
			Toast.makeText(getBaseContext(), errmsg, Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(getApplicationContext(), ProjectSendRecruitActivity.class);
			intent.putExtra("projectID", mProjectID);
			intent.putExtra("email", mMyEmail);
			String message = "";
			for(int i=0;i<mRecommendList.size();i++){
				Member m = mRecommendList.get(i);
				if(m.isChecked())
					message += m.getEmail() + " ";
			}
			message.trim();
			intent.putExtra("recruit", message);
			startActivityForResult(intent, PROJECT_SEND_RECRUIT_ACTIVITY);
		}
	}


	public Bitmap markerIconCreate(Context context, String field, int index){
		int baseMarkerID = R.drawable.map_icon01;
		if(field.equals("Server Programmer")){
			baseMarkerID = R.drawable.map_icon01;
		}else if(field.equals("Client Programmer")){
			baseMarkerID = R.drawable.map_icon02;
		}else if(field.equals("Android Programmer")){
			baseMarkerID = R.drawable.map_icon03;
		}else if(field.equals("Web Programmer")){
			baseMarkerID = R.drawable.map_icon04;
		}else if(field.equals("UI Designer")){
			baseMarkerID = R.drawable.map_icon05;
		}
		Bitmap baseMarker = BitmapFactory.decodeResource(context.getResources(),
				baseMarkerID);
		int overlayIndexID = R.drawable.a;
		switch(index){
		case 0: overlayIndexID = R.drawable.a;break;
		case 1: overlayIndexID = R.drawable.b;break;
		case 2: overlayIndexID = R.drawable.c;break;
		case 3: overlayIndexID = R.drawable.d;break;
		case 4: overlayIndexID = R.drawable.e;break;
		case 5: overlayIndexID = R.drawable.f;break;
		case 6: overlayIndexID = R.drawable.g;break;
		case 7: overlayIndexID = R.drawable.h;break;
		case 8: overlayIndexID = R.drawable.i;break;
		case 9: overlayIndexID = R.drawable.j;break;
		}
		Bitmap overlayIndex = BitmapFactory.decodeResource(context.getResources(),
				overlayIndexID);
		Bitmap bmpBase = Bitmap.createBitmap(baseMarker.getWidth(), baseMarker.getHeight(), baseMarker.getConfig());
		Canvas canvas = new Canvas(bmpBase);
		canvas.drawBitmap(baseMarker, 0, 0, null);
		canvas.drawBitmap(overlayIndex, 0, 0, null);

		return bmpBase;
	}

}
