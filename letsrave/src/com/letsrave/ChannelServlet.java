package com.letsrave;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class ChannelServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ChannelCreateServlet.class.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("NOTHING");

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String content = req.getParameter("content");
		log.info(content);
		
		String eventID = req.getParameter("event");
		log.info(eventID);
		Key eventKey = KeyFactory.createKey("Event", eventID);
		
		String isThrower = req.getParameter("thrower");
		log.info(isThrower);
		
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query query;
		if(isThrower.equals("True")){
			//message from thrower to goers
			query = new Query("Goer", eventKey);
		}else{
			//message from goer to throwers
			query = new Query("Thrower", eventKey);
		}
		
		List<Entity> receivers = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
		
		for (Entity receiver: receivers){
			String channel_key = (String) receiver.getProperty("channel_key");
			log.info(channel_key);
			channelService.sendMessage(new ChannelMessage(channel_key,content));
		}

		resp.setContentType("text/plain");
		resp.getWriter().println("OK");

	}
}
