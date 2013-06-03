package com.letsrave;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ChannelCreateServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ChannelCreateServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/plain");

		ChannelService channelService = ChannelServiceFactory.getChannelService();

		Boolean thrower = req.getParameter("thrower").compareTo("true")==0;
		String eventID = req.getParameter("event");
		String userID = req.getParameter("user");
		log.info(eventID);
		log.info(userID);

		String userType;
		if(thrower){
			userType = "Thrower";
		}else{
			userType = "Goer";
		}
		log.info(userType);

		String channel_key = userID+"#"+eventID;
		String token = channelService.createChannel(channel_key);
		log.info(token);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Key eventKey = KeyFactory.createKey("Event", eventID);

		Key userFullKey = new KeyFactory.Builder(eventKey).addChild(userType, userID).getKey();

		Entity user;
		try {
			user = datastore.get(userFullKey);
		} catch (EntityNotFoundException e) {
			if(userType.compareTo("Goer")==0){
				user = new Entity(userFullKey);
				user.setProperty("userID", userID);
			}else {
				resp.getWriter().print("no thrower, wrong order first partyCreate should be called which would create a thrower");
				return;
			}
		}

		user.setProperty("channel_key", channel_key);
		datastore.put(user);

		resp.getWriter().print(token);

	}
}