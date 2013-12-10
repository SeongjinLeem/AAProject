package com.example.aaproject.main;

import android.graphics.Bitmap;

public class Project {
	private Long id;
    private String title;
    private String email;
    private String contents;
    private Bitmap imgBitmap;
    
    public Project(Long id, String title, String email, String contents,
    		Bitmap imgBitmap) {
		this.id = id;
		this.title = title;
		this.email = email;
		this.contents = contents;
		this.imgBitmap = imgBitmap;
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
	
    
    
}

