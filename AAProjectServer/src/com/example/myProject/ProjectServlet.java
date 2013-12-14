package com.example.myProject;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.Member;
import com.example.myProject.data.Project;
import com.example.myProject.data.Recruit;
import com.example.myProject.data.Sponsor;
import com.example.myProject.data.User;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
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

public class ProjectServlet extends HttpServlet {
	public Date LastUpdated = new Date();
	String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	String regId = "";

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
			}else if(action.equals("join")){
				projectJoin(request, out);
			}else if(action.equals("recruit") || action.equals("recommend")){
				projectRecruit(request, out);
			}else if(action.equals("recruitlist")){
				projectRecruitList(request, out);
			}else if(action.equals("recruitreply")){
				projectRecruitReply(request, out);
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
			Project project = new Project(title, email, blobKey, contents, goal);
			Project myProject = pm.makePersistent(project);
			Member member = new Member(myProject.getId(), myProject.getEmail());
			pm.makePersistent(member);
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
		query = query + " order by date desc";
		
		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			@SuppressWarnings("unchecked")
			List<Project> projectList = (List<Project>) pm.newQuery(query).execute();
			query = "select from " + Recruit.class.getName() + " where toEmail == '" + session.getAttribute("loginEmail") + "'";
			@SuppressWarnings("unchecked")
			List<Recruit> joinList =  (List<Recruit>) pm.newQuery(query).execute();
			pm.close();
			synchronized(session) {
				session.setAttribute("LastAccessed", new Date());
			}
			boolean flag = false;
			for(int i=0;i<joinList.size();i++){
				if(joinList.get(i).getStatus().equals("waiting")){
					flag = true;
					break;
				}
			}
			String xmlStr = null;
			if(flag){
				xmlStr = "<projectList flag='true'>";
			}else{
				xmlStr = "<projectList>";
			}
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
				String userQuery = "select from " + User.class.getName() + " where :p1.contains(email)";
				userList = (List<User>)pm.newQuery(userQuery).execute(memberList);
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

	@SuppressWarnings("unchecked")
	public void projectJoin(HttpServletRequest request, PrintWriter out){
		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Project project = pm.getObjectById(Project.class, Long.parseLong(request.getParameter("projectID")));
			Long projectID = Long.parseLong(request.getParameter("projectID"));
			Email email = new Email(request.getParameter("email"));
			String message = URLDecoder.decode(request.getParameter("message"), "UTF-8");
			String query = "select from " + Recruit.class.getName() + " where fromEmail == '" + email.getEmail() + "'" +
					"&& projectID == " + projectID + " && toEmail == '" + project.getEmail().getEmail() +"'";

			List<Recruit> recruitList =  (List<Recruit>) pm.newQuery(query).execute();
			if(recruitList != null && recruitList.size()>0){
				out.print(URLEncoder.encode("해당 프로젝트에 이미 참여 요청을 한 상태입니다.", "UTF-8"));
				return;
			}

			query = "select from " + Recruit.class.getName() + " where toEmail == '" + email.getEmail() + "'" +
					"&& projectID == " + projectID + "&& fromEmail == '" + project.getEmail().getEmail() +"'";
			recruitList = (List<Recruit>) pm.newQuery(query).execute();
			if(recruitList != null && recruitList.size()>0){
				out.print(URLEncoder.encode("해당 프로젝트의 생성자로부터 참여 요청이 이미 와 있습니다.", "UTF-8"));
				return;
			}
			query = "select from " + User.class.getName() + " where email == '" + project.getEmail().getEmail() + "'";
			List<User> users = (List<User>) pm.newQuery(query).execute();
			User user = users.get(0);		
			Recruit recruit = new Recruit(email, user.getEmail(), projectID, message);
			pm.makePersistent(recruit);

			if(user.isLoggedin()){
				String regId = user.getRegId();
				sendGCM(regId, URLEncoder.encode("당신의 프로젝트에 참여하고자 하는 사용자가 존재합니다.", "UTF-8"));
			}
			out.print("Submit Success");

		} catch(Exception e){
			out.print(e.toString());
			out.print("Submit fail.");
		}
	}
	private class MyPair{
		int id;
		float value;
		public int getId() {
			return id;
		}
		public float getValue() {
			return value;
		}
		public MyPair(int id, float value) {
			this.id = id;
			this.value = value;
		}

	}

	@SuppressWarnings("unchecked")
	public void projectRecruit(HttpServletRequest request, PrintWriter out){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String action = request.getParameter("action");
		if(action.equals("recommend")){
			try{
			String query = "select from " + User.class.getName() + " where email == '" + request.getParameter("email") + "'";
			List<User> users = (List<User>) pm.newQuery(query).execute();
			User user = users.get(0);	
			
			Map<String, String> map = new HashMap<String, String>();
			String memberQuery = "select email from " + Member.class.getName() + " where projectID == " + request.getParameter("projectID") + "";
			List<Email> memberList = (List<Email>) pm.newQuery(memberQuery).execute();
			for(int i=0;i<memberList.size();i++)
				map.put(memberList.get(i).getEmail(), memberList.get(i).getEmail());
			String recruitQuery = "select toEmail from " + Recruit.class.getName() + " where fromEmail == '" + request.getParameter("email") + "' && projectID == " + request.getParameter("projectID") + "";
			List<Email> recruitList = (List<Email>) pm.newQuery(recruitQuery).execute();
			for(int i=0;i<recruitList.size();i++)
				map.put(recruitList.get(i).getEmail(), recruitList.get(i).getEmail());
			String joinQuery = "select fromEmail from " + Recruit.class.getName() + " where toEmail == '" + request.getParameter("email") + "' && projectID == " + request.getParameter("projectID") + "";
			List<Email> joinList = (List<Email>) pm.newQuery(joinQuery).execute();
			for(int i=0;i<joinList.size();i++)
				map.put(joinList.get(i).getEmail(), joinList.get(i).getEmail());
			
			Query q = pm.newQuery("select from " + User.class.getName());
			List<User> tempList = (List<User>)q.execute(memberList);
			List<User> userList = new ArrayList<User>();
			for(int i=0;i<tempList.size();i++){
				if(map.get(tempList.get(i).getEmail().getEmail()) == null)
					userList.add(tempList.get(i));
			}
			List<MyPair> pairList = new ArrayList<MyPair>();
			float myLat = user.getLocation().getLatitude();
			float myLng = user.getLocation().getLongitude();
			for(int i=0;i<userList.size();i++){
				float lat = userList.get(i).getLocation().getLatitude();
				float lng = userList.get(i).getLocation().getLongitude();
				float value = (myLat-lat)*(myLat-lat) + (myLng-lng)*(myLng-lng);
				pairList.add(new MyPair(i, value));
			}
			Collections.sort(pairList, new Comparator<MyPair>() { 
				@Override 
				public int compare(MyPair obj1, MyPair obj2) { 
					return Float.compare(obj1.getValue(), obj2.getValue()); 
				} 
			});
			int max = (userList.size()>10)?10:userList.size();
			String xmlStr = "<recommendList>";
			xmlStr = xmlStr + "<myLocation>";
			xmlStr = xmlStr + "<location>" + myLat + "," + myLng + "</location>";
			xmlStr = xmlStr + "</myLocation>";
			for(int i=0;i<max;i++){
				User u = userList.get(pairList.get(i).getId());
				xmlStr = xmlStr + "<member>";
				xmlStr = xmlStr + "<email>" + u.getEmail().getEmail() + "</email>";
				xmlStr = xmlStr + "<field>" + u.getField() + "</field>";
				xmlStr = xmlStr + "<location>" + u.getLocation().getLatitude() + "," + u.getLocation().getLongitude() + "</location>";
				xmlStr = xmlStr + "</member>";
			}
			xmlStr = xmlStr + "</recommendList>";
			out.print(xmlStr);
			}catch(Exception e){
				out.print(e.toString());
			}
		}else{
			try{
				Project project = pm.getObjectById(Project.class, Long.parseLong(request.getParameter("projectID")));
				Long projectID = Long.parseLong(request.getParameter("projectID"));
				String str = request.getParameter("recruit");
				String[] emailArr = str.split(" ");
				for(int i=0;i<emailArr.length;i++){		
					String query = "select from " + Recruit.class.getName() + " where fromEmail == '" + project.getEmail().getEmail() + "'" +
							"&& projectID == " + projectID + "&& toEmail == '" + emailArr[i] +"'";

					List<Recruit> recruitList =  (List<Recruit>) pm.newQuery(query).execute();
					if(recruitList != null && recruitList.size()>0){
						out.print(URLEncoder.encode("선택 목록 중 일부 사용자에게 이미 참여 요청을 한 상태입니다.", "UTF-8"));
						return;
					}

					query = "select from " + Recruit.class.getName() + " where toEmail == '" + project.getEmail().getEmail() + "'" +
							"&& projectID == " + projectID + "&& fromEmail == '" +  emailArr[i] +"'";
					recruitList = (List<Recruit>) pm.newQuery(query).execute();
					if(recruitList != null && recruitList.size()>0){
						out.print(URLEncoder.encode("선택 목록 중 일부 사용자로부터 참여 요청이 이미 와 있습니다.", "UTF-8"));
						return;
					}
				}

				String message = URLDecoder.decode(request.getParameter("message"), "UTF-8");
				for(int i=0;i<emailArr.length;i++){
					String query = "select from " + User.class.getName() + " where email == '" + emailArr[i] + "'";
					List<User> users = (List<User>) pm.newQuery(query).execute();
					User user = users.get(0);		
					Recruit recruit = new Recruit(new Email(request.getParameter("email")), user.getEmail(), projectID, message);
					pm.makePersistent(recruit);
					if(user.isLoggedin()){
						String regId = user.getRegId();
						sendGCM(regId, URLEncoder.encode("당신을 프로젝트에 초대하고자 하는 사용자가 존재합니다.", "UTF-8"));
					}
				}
				out.print("Submit Success");

			} catch(Exception e){
				out.print(e.toString());
				out.print("Submit fail.");
			}
		}
	}



	public void sendGCM(String regId, String str) {
		Sender sender = new Sender(myApiKey);
		String registrationId = regId;
		Message message = new Message.Builder()
		.collapseKey("collapseKey"+System.currentTimeMillis())
		.timeToLive(3)
		.delayWhileIdle(true)
		.addData("message", str)
		.build();

		Result result;
		try {
			result = sender.send(message, registrationId, 5);

			if (result.getMessageId() != null) {
				String canonicalRegId = result.getCanonicalRegistrationId();
				System.out.println("canonicalRegId : " + canonicalRegId);
				if (canonicalRegId != null) {
					// same device has more than on registration ID: update database
					System.out.println("same device has more than on registration ID: update database");
				}
			} else {
				String error = result.getErrorCodeName();
				System.out.println("[ERROR]"+error);
				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
					// application has been removed from device - unregister
					// database
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void projectRecruitList(HttpServletRequest request, PrintWriter out){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{	
			String query = "select from " + Recruit.class.getName() + " where fromEmail == '" + request.getParameter("email") + "'";
			List<Recruit> recruitList =  (List<Recruit>) pm.newQuery(query).execute();
			query = "select from " + Recruit.class.getName() + " where toEmail == '" + request.getParameter("email") + "' && status == 'waiting'";
			List<Recruit> joinList =  (List<Recruit>) pm.newQuery(query).execute();

			String xmlStr = "<recruitList>";
			if(recruitList != null && recruitList.size() > 0){
				for(int i=0;i<recruitList.size();i++){
					Recruit r = recruitList.get(i);
					xmlStr = xmlStr + "<recruit>";
					xmlStr = xmlStr + "<from>" + r.getFromEmail().getEmail() + "</from>";
					xmlStr = xmlStr + "<to>" + r.getToEmail().getEmail() + "</to>";
					xmlStr = xmlStr + "<projectID>" + r.getProjectID() + "</projectID>";
					Project project = pm.getObjectById(Project.class, r.getProjectID());
					xmlStr = xmlStr + "<projectName>" + URLEncoder.encode(project.getTitle(), "UTF-8") + "</projectName>";
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
					xmlStr = xmlStr + "<date>" + format.format(r.getDate()) + "</date>";
					xmlStr = xmlStr + "<state>" + r.getStatus() + "</state>";
					xmlStr = xmlStr + "<message>" + URLEncoder.encode(r.getMessage(), "UTF-8") + "</message>";
					xmlStr = xmlStr + "</recruit>";
					if(r.getStatus().equals("accepted") || r.getStatus().equals("denied")){
						pm.deletePersistent(r);
					}
				}
			}
			if(joinList != null && joinList.size() > 0){
				for(int i=0;i<joinList.size();i++){
					Recruit r = joinList.get(i);
					xmlStr = xmlStr + "<recruit>";
					xmlStr = xmlStr + "<from>" + r.getFromEmail().getEmail() + "</from>";
					xmlStr = xmlStr + "<to>" + r.getToEmail().getEmail() + "</to>";
					xmlStr = xmlStr + "<projectID>" + r.getProjectID() + "</projectID>";
					Project project = pm.getObjectById(Project.class, r.getProjectID());
					xmlStr = xmlStr + "<projectName>" + URLEncoder.encode(project.getTitle(), "UTF-8") + "</projectName>";
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); 
					xmlStr = xmlStr + "<date>" + format.format(r.getDate()) + "</date>";
					xmlStr = xmlStr + "<state>" + r.getStatus() + "</state>";
					xmlStr = xmlStr + "<message>" + URLEncoder.encode(r.getMessage(), "UTF-8") + "</message>";
					xmlStr = xmlStr + "</recruit>";
				}
			}
			xmlStr = xmlStr + "</recruitList>";
			out.print(xmlStr);

		} catch(Exception e){
			out.print(e.toString());
			out.print("list fail.");
		}
	}

	@SuppressWarnings("unchecked")
	public void projectRecruitReply(HttpServletRequest request, PrintWriter out){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			String query = "select from " + Recruit.class.getName() + " where fromEmail == '" + request.getParameter("fromEmail") + "'" +
					"&& projectID == " + request.getParameter("projectID") + " && toEmail == '" + request.getParameter("toEmail") +"'";
			List<Recruit> recruit =  (List<Recruit>) pm.newQuery(query).execute();

			Recruit r = recruit.get(0);
			r.setStatus(request.getParameter("state"));
			if(request.getParameter("state").equals("accepted")){
				Project project = pm.getObjectById(Project.class, Long.parseLong(request.getParameter("projectID")));
				Member m = null;
				if(project.getEmail().getEmail().equals(request.getParameter("fromEmail"))){
					m = new Member(Long.parseLong(request.getParameter("projectID")), new Email(request.getParameter("toEmail")));
				}else{
					m = new Member(Long.parseLong(request.getParameter("projectID")), new Email(request.getParameter("fromEmail")));
				}
				pm.makePersistent(m);
				pm.makePersistent(r);
			}

			out.print("Success");

		} catch(Exception e){
			out.print(e.toString());
			out.print("Fail");
		}
	}
}

