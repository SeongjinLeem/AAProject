package com.example.aaproject.model;

import java.util.Date;

public class Sponsor {
	
    private String email;
    private int donation;
    private Date date;
    public Sponsor(String email, int donation, Date date) {
		super();
		this.email = email;
		this.donation = donation;
		this.date = date;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getDonation() {
		return donation;
	}
	public void setDonation(int donation) {
		this.donation = donation;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
    
}

