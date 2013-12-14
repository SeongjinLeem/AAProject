package com.example.aaproject.project;

import com.example.aaproject.R;
import com.example.aaproject.util.GeoInfoTrans;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class MemberLocationActivity extends FragmentActivity {
	private GoogleMap mMap;
	private LatLng currentLoc;
	private MarkerOptions mMarkerOptions;
	private String addr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member_location);
		Intent intent = getIntent();
		String geoPoint = intent.getExtras().getString("geoPoint");
		String field = intent.getExtras().getString("field");
		double lat = Double.parseDouble(geoPoint.substring(0, geoPoint.indexOf(",")));
		double lng = Double.parseDouble(geoPoint.substring(geoPoint.indexOf(",")+1));

		currentLoc = new LatLng(lat, lng);

		mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

		mMarkerOptions = new MarkerOptions();

		if(field.equals("Server Programmer")){
			mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon01));
		}else if(field.equals("Client Programmer")){
			mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon02));
		}else if(field.equals("Android Programmer")){
			mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon03));
		}else if(field.equals("Web Programmer")){
			mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon04));
		}else if(field.equals("UI Designer")){
			mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon05));
		}
		mMarkerOptions.position(currentLoc);
		addr = GeoInfoTrans.searchLocation(getBaseContext(), currentLoc);
		mMap.clear();
		mMap.addMarker(mMarkerOptions.title("address").snippet(addr));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.member_location, menu);
		return true;
	}

}
