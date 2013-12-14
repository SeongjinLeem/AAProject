package com.example.aaproject.register;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.example.aaproject.R;
import com.example.aaproject.R.drawable;
import com.example.aaproject.R.id;
import com.example.aaproject.R.layout;
import com.example.aaproject.R.menu;
import com.example.aaproject.util.GeoInfoTrans;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

import android.widget.EditText;


public class LocationActivity extends FragmentActivity {
	private GoogleMap mMap;
	private LatLng currentLoc;
	private EditText mLocation;
	private Marker mMarker = null;
	private MarkerOptions mMarkerOptions;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		Intent intent = getIntent();
		String geoPoint = intent.getExtras().getString("geoPoint");
		String field = intent.getExtras().getString("field");
		if(geoPoint!=null){
			double lat = Double.parseDouble(geoPoint.substring(0, geoPoint.indexOf(",")));
			double lng = Double.parseDouble(geoPoint.substring(geoPoint.indexOf(",")+1));
			currentLoc = new LatLng(lat, lng);
		}else{
			currentLoc = new LatLng(37.611554, 126.997563);
		}
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
		mMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				String addr;
				if(mMarker!=null)
					mMarker.remove();
				
				mMarkerOptions.position(latLng);
				addr = GeoInfoTrans.searchLocation(getBaseContext(), latLng);
				currentLoc = latLng;
				mMap.clear();
				mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				mMarker = mMap.addMarker(mMarkerOptions.title("address").snippet(addr));
			}

		});

		mLocation = (EditText) findViewById(R.id.location);

		findViewById(R.id.set_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LatLng latlng;
						latlng = GeoInfoTrans.searchGeoPoint(getBaseContext(), mLocation.getText().toString());
						if(latlng != null){
							currentLoc = latlng;
							mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
	public void OnClickSelectButtonMethod(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		Bundle extra = new Bundle();
		extra.putString("latlng", currentLoc.latitude + "," + currentLoc.longitude);
		intent.putExtras(extra);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
