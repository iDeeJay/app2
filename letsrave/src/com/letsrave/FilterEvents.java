package com.letsrave;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class FilterEvents extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/plain");

		String events = My.getParam(req, resp, "events");
		if(events==null) return;
		
		String[] eventArr = events.split(",");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity party;
		for(String eventID : eventArr) {
			if(eventID!=null && eventID.length()>0){
				try {
					party = datastore.get(KeyFactory.createKey("Event", eventID));
					resp.getWriter().print(eventID + ",");
				} catch (EntityNotFoundException e) {}
			}
		}
	}

}
