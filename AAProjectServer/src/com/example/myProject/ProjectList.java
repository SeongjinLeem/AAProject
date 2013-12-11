package com.example.myProject;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.PersistenceManager;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.Project;
import com.example.myProject.data.User;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;


public class ProjectList extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		String action = request.getParameter("action");
		if(action !=null){
			String query = null;
			if(action.equals("list")){
				query = "select from " + Project.class.getName();
			}
			else if(action.equals("mylist")){
				query = "select from " + Project.class.getName() + " where email == '" + request.getParameter("email") + "'";
			}
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try{
				@SuppressWarnings("unchecked")
				List<Project> projectList = (List<Project>) pm.newQuery(query).execute();
				pm.close();
				Collections.sort(projectList, new Comparator<Project>() {
			        public int compare(Project o1, Project o2) {
			            return o2.getDate().compareTo(o1.getDate());
			        }
			    });
				out.print("<projectList>");
				for(int i=0;i<projectList.size();i++){
					Project p = projectList.get(i);
					out.print("<project>");
					out.print("<id>" + p.getId() + "</id>");
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
					out.print("<date>" + format.format(p.getDate()) + "</date>");
					out.print("<title>" + URLEncoder.encode(p.getTitle(), "UTF-8") + "</title>");
					out.print("<email>" + p.getEmail().getEmail() + "</email>");
					out.print("<contents>" + URLEncoder.encode(p.getContents(), "UTF-8") + "</contents>");
					ImagesService imagesService = ImagesServiceFactory.getImagesService();
		            ServingUrlOptions suo = ServingUrlOptions.Builder.withBlobKey(p.getBlobKey());
		            String image_url = imagesService.getServingUrl(suo);
		            out.print("<imageUrl>" + image_url + "</imageUrl>");
		            out.print("</project>");
				}
				out.print("</projectList>");
			} finally{
				
			}
		}
		

	}
}
