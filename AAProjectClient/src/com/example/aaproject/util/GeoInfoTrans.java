package com.example.aaproject.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GeoInfoTrans {
	public static String searchLocation(Context context, LatLng latlng){
		Geocoder coder = new Geocoder(context);
		try{
			List<Address> addrList = coder.getFromLocation(latlng.latitude, latlng.longitude, 1);
			StringBuilder strBlder = new StringBuilder();
			if(addrList.size()>0){
				Address addr = addrList.get(0);
				for(int i=0;i<=addr.getMaxAddressLineIndex();i++)
					strBlder.append(addr.getAddressLine(i));
				return strBlder.toString();
			}
		}catch(IOException e)
		{
			Log.w("GeoInfoTrans", e.toString());
		}
		return null;
	}
	
	public static LatLng searchGeoPoint(Context context, String location){
		String searchingName = location;
		Geocoder coder = new Geocoder(context);
		try{
			List<Address> addrList = coder.getFromLocationName(searchingName, 1);
			double lat = 0f;
			double lng = 0f;
			if(addrList.size()>0){
				Address loc = addrList.get(0);
				lat = loc.getLatitude();
				lng = loc.getLongitude();
				return new LatLng(lat, lng) ;
			}
		}catch(IOException e)
		{
			Log.w("GeoInfoTrans", e.toString());
		}
		return null;
	}
}
