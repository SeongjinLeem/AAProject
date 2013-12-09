package com.example.myProject.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;

@PersistenceCapable(identityType = IdentityType.APPLICATION)

public class User {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Persistent
    private Email email;

    @Persistent
    private String password;

    @Persistent
    private String name;

    @Persistent
    private int age;
    
    @Persistent
    private String gender;

    @Persistent
    private String field;
    
    @Persistent
    private GeoPt location;
    
    public User(Email email, String password, String name, int age,
			String gender, String field, GeoPt location) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.field = field;
		this.location = location;
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


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public GeoPt getLocation() {
		return location;
	}


	public void setLocation(GeoPt location) {
		this.location = location;
	}


	
    

    

}
