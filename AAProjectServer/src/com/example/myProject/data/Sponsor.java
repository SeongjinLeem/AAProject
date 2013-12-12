package com.example.myProject.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Email;

@PersistenceCapable(identityType = IdentityType.APPLICATION)

public class Sponsor {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private Long projectID;
	
	@Persistent
	private Email email;

	@Persistent
	private Long donation; 

	@Persistent
	Date date;

	
	public Sponsor(Long projectID, Email email, Long donation) {
		super();
		this.projectID = projectID;
		this.email = email;
		this.donation = donation;
		this.date = new Date();
	}
	public Long getProjectID() {
		return projectID;
	}
	public void setProjectID(Long projectID) {
		this.projectID = projectID;
	}
	public Long getDonation() {
		return donation;
	}
	public void setDonation(Long donation) {
		this.donation = donation;
	}

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
	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}

}
