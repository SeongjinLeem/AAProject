package com.example.aaproject.model;

import java.util.List;


import com.example.aaproject.R;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;  

import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageView;  
import android.widget.LinearLayout;

import android.widget.TextView;  



public class MemberAdapter extends BaseAdapter {  
	private Context context;  
	private List<Member> members;  



	public MemberAdapter(Context context, List<Member> members) {  
		this.context = context;  
		this.members = members;  
	}  

	class ViewHolder {
		public TextView email;
		public TextView field;
		public TextView location;
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
			rowView = inflater.inflate(R.layout.member, null); 
			holder = new ViewHolder();
			holder.email = (TextView) rowView.findViewById(R.id.email);
			holder.field = (TextView) rowView.findViewById(R.id.field);
			holder.location = (TextView) rowView.findViewById(R.id.location);
			rowView.setTag(holder);	
		} 
		else 
		{
			holder = (ViewHolder) rowView.getTag();
		}
		holder.email.setText(members.get(position).getEmail());
		holder.field.setText(members.get(position).getField());
		LatLng loc = members.get(position).getLocation();
		holder.location.setText(loc.latitude + "," + loc.longitude);
		return rowView;
	} 


	@Override 

	public int getCount() {  
		return members.size();  
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
