package com.example.aaproject.model;

import java.util.List;

import com.example.aaproject.R;
import com.example.aaproject.util.GeoInfoTrans;

import android.content.Context;  

import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  

import android.widget.TextView;  


public class RecommendMemberAdapter extends BaseAdapter {  
	private Context context;  
	private List<Member> members;  



	public RecommendMemberAdapter(Context context, List<Member> members) {  
		this.context = context;  
		this.members = members;  
	}  

	class ViewHolder {
		public TextView profile;
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
			rowView = inflater.inflate(R.layout.recommend_member, null); 
			holder = new ViewHolder();
			holder.profile = (TextView) rowView.findViewById(R.id.profile);
			rowView.setTag(holder);	
		} 
		else 
		{
			holder = (ViewHolder) rowView.getTag();
		}
		int index = 'A' + position;
		String profile = Character.toString((char)index) + "\n" + 
				members.get(position).getEmail() + "\n" + 
				members.get(position).getField() + "\n" +  
				GeoInfoTrans.searchLocation(context, members.get(position).getLocation());
		holder.profile.setText(profile);
		return rowView;
	} 


	@Override 

	public int getCount() {  
		return members.size();  
	}  

	@Override 
	public Object getItem(int position) {  
		return members.get(position);  
	}  

	@Override 
	public long getItemId(int position) {  
		return 0;  
	}  
} 
