package com.letsrave;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class GetVotesServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.setContentType("text/plain");
		
		String eventID = My.getParam(req, resp, "event");
		if(eventID==null) return;
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key eventKey = KeyFactory.createKey("Event", eventID);
		
		Query query = new Query("Vote", eventKey);
		
		List<Entity> votes = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(Constants.maxVotes));
		
		resp.getWriter().print("[");
		for (Entity vote: votes){
			String user = (String) vote.getProperty("userID");
			String song = (String) vote.getProperty("song");
			Boolean up = (Boolean) vote.getProperty("up");
			
			resp.getWriter().print(" {\"userID\":\""+user+"\", \"song\":\""+song+"\", \"up\":"+up.toString() +"},");
		}
		resp.getWriter().print(" \"\" ]");
	}

}
