package com.example.aaproject.project;

import java.util.Date;

import android.graphics.Bitmap;

public class Project {
	private Long id;
	private Date date;
    private String title;
    private String email;
    private String contents;
    private Bitmap imgBitmap;
    private int goal;
    private int donation;
    
    public Project(Long id, Date date, String title, String email, String contents,
    		Bitmap imgBitmap, int goal, int donation) {
		this.id = id;
		this.setDate(date);
		this.title = title;
		this.email = email;
		this.contents = contents;
		this.imgBitmap = imgBitmap;
		this.goal = goal;
		this.donation = donation;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public Bitmap getImgBitmap() {
		return imgBitmap;
	}
	public void setImgBitmap(Bitmap imgBitmap) {
		this.imgBitmap = imgBitmap;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getGoal() {
		return goal;
	}
	public void setGoal(int goal) {
		this.goal = goal;
	}
	public int getDonation() {
		return donation;
	}
	public void setDonation(int donation) {
		this.donation = donation;
	}
	
    
    
}

