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


public class Login extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		if(action.equals("LoginCheck")){
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			if(session.getAttribute("loginEmail") != null){
				out.println("already loggedin");
				out.println(session.getAttribute("loginEmail"));
			}else{
				out.println("login required");
			}
		}else if(action.equals("Login")){
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try{
				String query = "select from " + User.class.getName() + " where email == '" + request.getParameter("email") + "'";
				@SuppressWarnings("unchecked")
				List<User> users = (List<User>) pm.newQuery(query).execute();
				if(!users.isEmpty() && request.getParameter("password").equals(users.get(0).getPassword())){
					synchronized(session) {
						session.setAttribute("loginEmail", request.getParameter("email"));
					}
					if(request.getParameter("regID")!=null){
						synchronized(session) {
							session.setAttribute("regID", request.getParameter("regID"));
						}
						response.setContentType("text/plain");
						PrintWriter out = response.getWriter();
						out.println("Login Success");
					}else{
						response.sendRedirect("/projectlist");
					}
				}
				else{
					if(request.getParameter("regID")!=null){
						response.setContentType("text/plain");
						PrintWriter out = response.getWriter();
						out.println("Login Fail");
					}else{
						response.sendRedirect("/login_fail.html");
					}
				}
			} finally{
				pm.close();
			}
		}
	}
}
