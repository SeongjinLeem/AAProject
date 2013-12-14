package com.example.aaproject.model;

import com.google.android.gms.maps.model.LatLng;

public class Member {
    private String email;
    private String field;
    private LatLng location;
    private boolean Checked;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public void setChecked(boolean b){
		Checked = b;
	}
	public boolean isChecked(){
		return Checked;
	}
	public Member(String email, String field, LatLng location) {
		super();
		this.email = email;
		this.field = field;
		this.location = location;
		this.Checked = false;
	}
    
    
}

