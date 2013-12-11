package com.example.myProject.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Email;

@PersistenceCapable(identityType = IdentityType.APPLICATION)

public class Project {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String title;
    
    @Persistent
    private Email email;

	@Persistent
    private String contents; 

    @Persistent
    BlobKey blobKey;
    
    @Persistent
    Date date;
    
    
    public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public BlobKey getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(BlobKey blobKey) {
		this.blobKey = blobKey;
	}
    public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}
	

    public Project(String title, Email email, BlobKey blobKey, String contents) {
    	this.date = new Date();
        this.title = title; 
        this.email = email;
        this.blobKey = blobKey;
        this.contents = contents;
    }
}
