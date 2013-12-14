package com.example.myProject;

import java.io.*;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.User;


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
				session.invalidate();
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
					User user = users.get(0);
					user.setLoggedin(true);
					user.setRegId(request.getParameter("regId"));
					pm.makePersistent(user);
					response.setContentType("text/plain");
					PrintWriter out = response.getWriter();
					out.println("Login Success");
				}
				else{
					response.setContentType("text/plain");
					PrintWriter out = response.getWriter();
					out.println("Login Fail");
					session.invalidate();
				}
			} finally{
				pm.close();
			}
		}
	}
}
