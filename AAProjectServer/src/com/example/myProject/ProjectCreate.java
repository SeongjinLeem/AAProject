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

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class ProjectCreate extends HttpServlet {

	//String myApiKey = "AIzaSyD3fJvWiw0yEkmt9TmvgPB_HJbvpLDpe3o";
	//String regId = "";
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); 
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action!=null && action.equals("ReqUrl")){
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.print(blobstoreService.createUploadUrl("/upload"));
		}

	}
}

