package com.example.aaproject.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.example.aaproject.R;

import android.content.Context;  
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageView;  

import android.widget.TextView;  



public class ProjectAdapter extends BaseAdapter {  
	private Context context;  
	private List<Project> projects;  



	public ProjectAdapter(Context context, List<Project> projects) {  
		this.context = context;  
		this.projects = projects;  
	}  

	class ViewHolder {
		public ImageView image;
		public TextView  title;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ViewHolder will buffer the assess to the individual fields of the row
		// layout

		ViewHolder holder;
		// Recycle existing view if passed as parameter
		// This will save memory and time on Android
		// This only works if the base layout for all classes are the same
		View rowView = convertView;
		if (rowView == null) 
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			rowView = inflater.inflate(R.layout.project, null); 
			holder = new ViewHolder();
			holder.image = (ImageView) rowView.findViewById(R.id.image);
			holder.title = (TextView) rowView.findViewById(R.id.title);
			rowView.setTag(holder);
			
		} 
		else 
		{
			holder = (ViewHolder) rowView.getTag();
		}

		holder.image.setImageBitmap(projects.get(position).getImgBitmap());
		holder.title.setText(projects.get(position).getTitle());

		return rowView;
	} 


	@Override 

	public int getCount() {  
		return projects.size();  
	}  

	@Override 
	public Object getItem(int position) {  
		return null;  
	}  

	@Override 
	public long getItemId(int position) {  
		return 0;  
	}  
} 
