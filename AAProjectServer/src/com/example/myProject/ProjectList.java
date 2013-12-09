package com.example.myProject;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.Project;
import com.example.myProject.data.User;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


public class ProjectList extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			String query = "select from " + Project.class.getName();
			@SuppressWarnings("unchecked")
			List<Project> projectList = (List<Project>) pm.newQuery(query).execute();
			Iterator<Project> iter = projectList.iterator();
		} finally{
			pm.close();
		}

	}
}
