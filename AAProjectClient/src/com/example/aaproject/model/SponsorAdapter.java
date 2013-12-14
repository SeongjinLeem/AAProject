package com.example.aaproject.model;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


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



public class SponsorAdapter extends BaseAdapter {  
	private Context context;  
	private List<Sponsor> sponsors;  



	public SponsorAdapter(Context context, List<Sponsor> sponsors) {  
		this.context = context;  
		this.sponsors = sponsors;  
	}  

	class ViewHolder {
		public TextView email;
		public TextView donation;
		public TextView date;
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
			rowView = inflater.inflate(R.layout.sponsor, null); 
			holder = new ViewHolder();
			holder.email = (TextView) rowView.findViewById(R.id.email);
			holder.donation = (TextView) rowView.findViewById(R.id.donation);
			holder.date = (TextView) rowView.findViewById(R.id.date);
			rowView.setTag(holder);	
		} 
		else 
		{
			holder = (ViewHolder) rowView.getTag();
		}
		holder.email.setText(sponsors.get(position).getEmail());
		holder.donation.setText(String.format("%,d ¿ø", sponsors.get(position).getDonation()));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
		holder.date.setText(format.format(sponsors.get(position).getDate()));
		return rowView;
	} 


	@Override 

	public int getCount() {  
		return sponsors.size();  
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
