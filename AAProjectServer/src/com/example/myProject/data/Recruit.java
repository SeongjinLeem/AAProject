package com.example.myProject.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Email;

@PersistenceCapable(identityType = IdentityType.APPLICATION)

public class Recruit {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Email fromEmail;

	@Persistent
    private Email toEmail;
	
	@Persistent
	private String status;
	
	@Persistent
    Date date;
	
	@Persistent
    String message;
	
	@Persistent
    private Long projectID;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Email getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(Email fromEmail) {
		this.fromEmail = fromEmail;
	}

	public Email getToEmail() {
		return toEmail;
	}

	public void setToEmail(Email toEmail) {
		this.toEmail = toEmail;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getProjectID() {
		return projectID;
	}

	public void setProjectID(Long projectID) {
		this.projectID = projectID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Recruit(Email fromEmail, Email toEmail, Long projectID, String message) {
		super();
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.projectID = projectID;
		this.message = message;
		this.status = "waiting";
		this.date = new Date();
	}

   
}
