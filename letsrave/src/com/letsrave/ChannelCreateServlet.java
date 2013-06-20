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
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/plain");

		ChannelService channelService = ChannelServiceFactory.getChannelService();

		String throwerStr = My.getParam(req, resp,"thrower");
		String eventID = My.getParam(req, resp, "event");
		String userID =  My.getParam(req, resp, "user");
		if(eventID==null || userID==null || throwerStr==null) return;
		log.info(eventID);
		log.info(userID);

		String userType;
		if(throwerStr.compareTo("true")==0){
			userType = "Thrower";
		}else if(throwerStr.compareTo("false")==0){
			userType = "Goer";
		}else{
			resp.getWriter().print("ERR thrower must be either 'true' or 'false'");
			return;
		}

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
				resp.getWriter().print("ERR no thrower, wrong order first partyCreate should be called which would create a thrower");
				return;
			}
		}

		String channel_key = userID+"#"+eventID+userType.substring(0,2);
		String token = channelService.createChannel(channel_key);
		log.info(token);

		user.setProperty("channel_key", channel_key);
		datastore.put(user);

		resp.getWriter().print(token);

	}
}