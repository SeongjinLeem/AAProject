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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.example.aaproject.R;
import com.example.aaproject.util.TaskCallback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("ValidFragment")
public class ProjectListActivity extends Fragment implements TaskCallback{
	Context mContext;
	private GridView mGridView;  
	private View mListStatusView;
	private TextView mListStatusMessageView;
	private ListLoadingTask mListLoadingTask = null;
	static final String url = "http://test20103377.appspot.com/projectlist";
	private List<Project> projectList = null;

	public ProjectListActivity(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_project_list, null);

		mGridView = (GridView) view.findViewById(R.id.gridView);
		mListStatusView = view.findViewById(R.id.list_status);
		mListStatusMessageView = (TextView) view.findViewById(R.id.list_status_message);
		mListStatusMessageView.setText(R.string.list_progress);
		showProgress(true);

		mListLoadingTask = new ListLoadingTask(this);
		mListLoadingTask.execute((Void) null);

		return view;
	}
	public void listReload(){
		mListStatusMessageView.setText(R.string.list_progress);
		showProgress(true);

		mListLoadingTask = new ListLoadingTask(this);
		mListLoadingTask.execute((Void) null);
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

	public class ListLoadingTask extends AsyncTask<Void, Void, Boolean> {
		private TaskCallback mCallback;
		public ListLoadingTask(TaskCallback callback) {
			mCallback = callback;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "list"));
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

		@Override
		protected void onCancelled() {
			mListLoadingTask = null;
			showProgress(false);
		}
	}

	@Override
	public void done() {
		ProjectAdapter adapter = new ProjectAdapter(mContext, projectList);
		mGridView.setAdapter(adapter);  
		adapter.notifyDataSetChanged(); 
		mGridView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v,  
					int position, long id) {  
				Toast.makeText(mContext,  ((TextView) v.findViewById(R.id.title)).getText(), Toast.LENGTH_SHORT).show();  
			}  
		});
		mListLoadingTask = null;
		showProgress(false);
	}
	public void onPostResume() {
	}
}

