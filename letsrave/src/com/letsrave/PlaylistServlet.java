package com.letsrave;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class PlaylistServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.setContentType("text/plain");
		
		String eventID = My.getParam(req, resp, "event");
		if(eventID==null) return;
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key eventKey = KeyFactory.createKey("Event", eventID);
		
		Entity party;
		try {
			party = datastore.get(eventKey);
			Text json = (Text) party.getProperty("json");
			String uri = (String) party.getProperty("playlistURI");
			String offset = (String) party.getProperty("offset");
			resp.getWriter().print("{\"uri\":\""+uri+"\", \"offset\":\"" +offset+ "\", \"data\":"+json.getValue()+"}");
		} catch (EntityNotFoundException e) {
			resp.getWriter().println("ERR party should be first created");
		}
	}
}
