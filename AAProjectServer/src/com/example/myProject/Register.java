package com.example.myProject;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.User;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;

public class Register extends HttpServlet {

	String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	String regId = "";

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action != null){
			if(action.equals("Register")){
				Email email = new Email(request.getParameter("email"));
				String password = request.getParameter("password");
				String name = URLDecoder.decode(request.getParameter("name"), "UTF-8");
				int age = Integer.parseInt(request.getParameter("age"));
				String gender = request.getParameter("gender");
				String field = request.getParameter("field");
				String geoPoint = request.getParameter("location");
				float lat = Float.parseFloat(geoPoint.substring(0, geoPoint.indexOf(",")));
				float lng = Float.parseFloat(geoPoint.substring(geoPoint.indexOf(",")+1));
				GeoPt location = new GeoPt(lat, lng);
				User user = new User(email, password, name, age, gender, field, location);
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					String query = "select from " + User.class.getName() + " where email == '" + request.getParameter("email") + "'";
					@SuppressWarnings("unchecked")
					List<User> users = (List<User>) pm.newQuery(query).execute();
					response.setContentType("text/plain");
					PrintWriter out = response.getWriter();
					if(!users.isEmpty()){
						out.println("Duplicate Email");
					}
					else{
						pm.makePersistent(user);
						out.println("Register Success");
					}
				} finally {
					pm.close();
				}
			}
			else if(action.equals("EmailCheck")){
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try{
					String query = "select from " + User.class.getName() + " where email == '" + request.getParameter("email") + "'";
					@SuppressWarnings("unchecked")
					List<User> users = (List<User>) pm.newQuery(query).execute();
					response.setContentType("text/plain");
					PrintWriter out = response.getWriter();
					if(!users.isEmpty()){
						out.println("Duplicate Email");
					}
					else{
						out.println("Email Address is available");
					}
				} finally{
					pm.close();
				}
				
			}
		}

	}
}

