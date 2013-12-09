package com.example.myProject;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;




/** Servlet that prints out the param1, param2, and param3
 *  request parameters. Does not filter out HTML tags.
 *  <p>
 *  From <a href="http://courses.coreservlets.com/Course-Materials/">the
 *  coreservlets.com tutorials on servlets, JSP, Struts, JSF, Ajax, GWT, and Java</a>.
 */

public class LoginForm extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";

	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Web Services are not supported.";
		String docType =
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
						"Transitional//EN\">\n";
		out.println(docType +
				"<HTML>\n" +
				"<HEAD><TITLE>" + title + "</TITLE></HEAD>\n" +
				"<BODY BGCOLOR=\"#FDF5E6\">\n" +
				"<H1 ALIGN=\"CENTER\">" + title + "</H1>\n" +
				"<FORM ACTION=\"/login\" METHOD=\"GET\">\n" +
				"<TABLE>" +
				"<TR><TD>Email:</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"email\"></TD></TR>" +
				"<TR><TD>Password:</TD><TD><INPUT TYPE=\"PASSWORD\" NAME=\"password\"></TD></TR>" +
				"<TR><TD COLSPAN=\"2\"><CENTER><INPUT TYPE=\"SUBMIT\" VALUE=\"Login\" /></CENTER></TD></TR>" +
				"</TABLE></FORM></BODY></HTML>");
	}

}
