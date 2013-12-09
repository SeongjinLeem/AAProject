package com.example.myProject;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.*;
import javax.servlet.http.*;

import com.example.myProject.data.Project;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Email;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class ProjectUpload extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); 
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		// Get the image representation

		PersistenceManager pm = PMF.get().getPersistenceManager();
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try{
			String title = null;
			String contents = null;
			Email email = null;
			BlobKey blobKey = null;

			title = URLDecoder.decode(request.getParameter("title"), "UTF-8");
			contents = URLDecoder.decode(request.getParameter("contents"), "UTF-8");
			email = new Email(request.getParameter("email"));
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
			List<BlobKey> keys = blobs.get("image"); 
			if (keys != null && keys.size() > 0) {
				blobKey = keys.get(0);
			}
			out.println(title + " " + email + " " + blobKey + " " + contents);
			Project myImage = new Project(title, email, blobKey, contents);
			pm.makePersistent(myImage);
			out.println("Create Success");

		}catch(Exception e){
			out.println("Create Fail");
		}finally {
			pm.close();
		}
	}
}

