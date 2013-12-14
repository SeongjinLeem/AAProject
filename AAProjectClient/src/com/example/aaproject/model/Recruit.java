package com.example.aaproject.model;

import java.util.Date;

public class Recruit {
	
    private Long id;

    private String from;

    private String to;
	
	private String status;
	
	private String message;
	
    Date date;
	
    private Long projectID;
    private String projectName;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
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

	public Recruit(String from, String to, Long projectID, String projectName, Date date, String status, String message) {
		this.from = from;
		this.to = to;
		this.projectID = projectID;
		this.projectName = projectName;
		this. message = message;
		this.status = status;
		this.date = date;
	}

   
}