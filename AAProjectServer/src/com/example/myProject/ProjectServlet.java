package com.example.myProject;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.Member;
import com.example.myProject.data.Project;
import com.example.myProject.data.Sponsor;
import com.example.myProject.data.User;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.apphosting.datastore.DatastoreV4.GqlQuery;

public class ProjectServlet extends HttpServlet {
	public Date LastUpdated = new Date();

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		// Get the image representation
		String action = request.getParameter("action");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		if(action != null){
			if(action.equals("ReqUrl")){
				out.print(blobstoreService.createUploadUrl("/upload"));
			}else if(action.equals("ProjectCreate")){
				projectCreate(request, out);
			}else if(action.equals("list")){
				projectList(request, out);
			}else if(action.equals("project")){
				projectDisplay(request, out);
			}else if(action.equals("donation")){
				projectDonation(request, out);
			}
		}

	}

	public void projectCreate(HttpServletRequest request, PrintWriter out){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			String title = null;
			String contents = null;
			Long goal = (long) 0;
			Email email = null;
			BlobKey blobKey = null;

			title = URLDecoder.decode(request.getParameter("title"), "UTF-8");
			goal = Long.parseLong(request.getParameter("goal"));
			contents = URLDecoder.decode(request.getParameter("contents"), "UTF-8");
			email = new Email(request.getParameter("email"));
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
			List<BlobKey> keys = blobs.get("image"); 
			if (keys != null && keys.size() > 0) {
				blobKey = keys.get(0);
			}
			out.println(title + " " + email + " " + blobKey + " " + contents);
			Project myProject = new Project(title, email, blobKey, contents, goal);
			pm.makePersistent(myProject);
			out.print("Create Success");
			LastUpdated = new Date();
		}catch(Exception e){
			out.print("Create Fail");
		}finally {
			pm.close();
		}
	}

	public void projectList(HttpServletRequest request, PrintWriter out){

		HttpSession session = request.getSession();
		String startFlag = request.getParameter("start");
		Date LastAccessed = (Date) session.getAttribute("LastAccessed");
		if(startFlag == null && LastAccessed != null && LastUpdated.compareTo(LastAccessed)<0){
			out.print("no data updated");
			return;
		}

		String query = "select from " + Project.class.getName();
		if(request.getParameter("email") != null){
			query = query + " where email == '" + request.getParameter("email") + "'";
		}
		query = query + " order by date desc";

		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			@SuppressWarnings("unchecked")
			List<Project> projectList = (List<Project>) pm.newQuery(query).execute();
			pm.close();
			synchronized(session) {
				session.setAttribute("LastAccessed", new Date());
			}
			String xmlStr = null;
			xmlStr = "<projectList>";
			for(int i=0;i<projectList.size();i++){
				Project p = projectList.get(i);
				xmlStr = xmlStr + "<project>";
				xmlStr = xmlStr + "<id>" + p.getId() + "</id>";
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
				xmlStr = xmlStr + "<date>" + format.format(p.getDate()) + "</date>";
				xmlStr = xmlStr + "<title>" + URLEncoder.encode(p.getTitle(), "UTF-8") + "</title>";
				xmlStr = xmlStr + "<email>" + p.getEmail().getEmail() + "</email>";
				xmlStr = xmlStr + "<contents>" + URLEncoder.encode(p.getContents(), "UTF-8") + "</contents>";
				ImagesService imagesService = ImagesServiceFactory.getImagesService();
				ServingUrlOptions suo = ServingUrlOptions.Builder.withBlobKey(p.getBlobKey());
				String image_url = imagesService.getServingUrl(suo);
				xmlStr = xmlStr + "<imageUrl>" + image_url + "</imageUrl>";
				xmlStr = xmlStr + "<goal>" + p.getGoal() + "</goal>";
				xmlStr = xmlStr + "<donation>" + p.getDonation() + "</donation>";
				xmlStr = xmlStr + "</project>";
			}
			xmlStr = xmlStr + "</projectList>";
			out.print(xmlStr);

		} catch(Exception e){
			out.print("Project searching fail.");
		}
	}

	@SuppressWarnings("unchecked")
	public void projectDisplay(HttpServletRequest request, PrintWriter out){
		HttpSession session = request.getSession();
		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Entity project = datastoreService.get(KeyFactory.createKey("Project", Long.parseLong(request.getParameter("projectID"))));

			String memberQuery = "select email from " + Member.class.getName() + " where projectID == " + request.getParameter("projectID") + "";
			List<Email> memberList = (List<Email>) pm.newQuery(memberQuery).execute();
			List<User> userList = null;
			if(memberList != null && memberList.size() > 0){
				javax.jdo.Query q = pm.newQuery(
						"select from User where :p1.contains(email)");
				userList = (List<User>)q.execute(memberList);
			}
			String SponsorQuery = "select from " + Sponsor.class.getName() + " where projectID == " + request.getParameter("projectID") + " order by date desc";
			List<Sponsor> sponsorList = (List<Sponsor>) pm.newQuery(SponsorQuery).execute();
			pm.close();
			synchronized(session) {
				session.setAttribute("LastAccessedProject", request.getParameter("projectID"));
				session.setAttribute("ProjectLastAccessed", new Date());
			}
			String xmlStr = null;
			xmlStr = "<projectInfo>";

			{
				xmlStr = xmlStr + "<project>";
				xmlStr = xmlStr + "<id>" + request.getParameter("projectID") + "</id>";
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
				xmlStr = xmlStr + "<date>" + format.format((Date)project.getProperty("date")) + "</date>";
				xmlStr = xmlStr + "<title>" + URLEncoder.encode(project.getProperty("title").toString(), "UTF-8") + "</title>";
				xmlStr = xmlStr + "<email>" + ((Email)project.getProperty("email")).getEmail() + "</email>";
				xmlStr = xmlStr + "<contents>" + URLEncoder.encode(project.getProperty("contents").toString(), "UTF-8") + "</contents>";
				ImagesService imagesService = ImagesServiceFactory.getImagesService();
				ServingUrlOptions suo = ServingUrlOptions.Builder.withBlobKey((BlobKey)project.getProperty("blobKey"));
				String image_url = imagesService.getServingUrl(suo);
				xmlStr = xmlStr + "<imageUrl>" + image_url + "</imageUrl>";
				xmlStr = xmlStr + "<goal>" + project.getProperty("goal") + "</goal>";
				xmlStr = xmlStr + "<donation>" + project.getProperty("donation") + "</donation>";
				xmlStr = xmlStr + "</project>";

			}
			if(userList != null && userList.size() > 0){
				xmlStr = xmlStr + "<members>";
				for(int i=0;i<userList.size();i++){
					User u = userList.get(i);
					xmlStr = xmlStr + "<member>";
					xmlStr = xmlStr + "<email>" + u.getEmail().getEmail() + "</email>";
					xmlStr = xmlStr + "<field>" + u.getField() + "</field>";
					xmlStr = xmlStr + "<location>" + u.getLocation().getLatitude() + "," + u.getLocation().getLongitude() + "</location>";
					xmlStr = xmlStr + "</member>";
				}
				xmlStr = xmlStr + "</members>";
			}
			if(sponsorList != null && sponsorList.size() > 0){
				xmlStr = xmlStr + "<sponsors>";
				for(int i=0;i<sponsorList.size();i++){
					Sponsor s = sponsorList.get(i);
					xmlStr = xmlStr + "<sponsor>";
					xmlStr = xmlStr + "<email>" + s.getEmail().getEmail() + "</email>";
					xmlStr = xmlStr + "<donation>" + s.getDonation() + "</donation>";
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
					xmlStr = xmlStr + "<date>" + format.format(s.getDate()) + "</date>";
					xmlStr = xmlStr + "</sponsor>";
				}
				xmlStr = xmlStr + "</sponsors>";
			}
			xmlStr = xmlStr + "</projectInfo>";
			out.print(xmlStr);


		} catch(Exception e){
			out.print(e.toString());
			out.print("Project searching fail.");
		}
	}

	public void projectDonation(HttpServletRequest request, PrintWriter out){
		try{
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Project project = pm.getObjectById(Project.class, Long.parseLong(request.getParameter("projectID")));
			Long projectID = Long.parseLong(request.getParameter("projectID"));
			Email email = new Email(request.getParameter("email"));
			Long donation = Long.parseLong(request.getParameter("donation"));
			Sponsor sponsor = new Sponsor(projectID, email, donation);
			project.setDonation(project.getDonation() + donation);
			pm.makePersistent(project);
			pm.makePersistent(sponsor);
			out.print("Submit Success");

		} catch(Exception e){
			out.print(e.toString());
			out.print("Submit fail.");
		}
	}
}

