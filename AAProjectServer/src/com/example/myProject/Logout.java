package com.example.myProject;

import java.io.*;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.User;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


public class Logout extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		if(action.equals("Logout")){
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			if(session.getAttribute("loginEmail") != null){
				synchronized(session) {
					session.invalidate();
				}
				out.println("Logout Success");
			}else{
				out.println("Logout Failed");
			}
		}
	}
}