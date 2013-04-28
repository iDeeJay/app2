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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.Date;

public class ChannelCreateServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ChannelCreateServlet.class.getName());
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	
    	ChannelService channelService = ChannelServiceFactory.getChannelService();
    	
    	String isThrower = req.getParameter("thrower");
    	String eventID = req.getParameter("event");
    	String fbID = req.getParameter("fbid");
    	log.info(isThrower);
    	log.info(eventID);
    	log.info(fbID);
    	
    	String token = channelService.createChannel(fbID);
    	log.info(token);
    	Key eventKey = KeyFactory.createKey("Event", eventID);
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	
    	if(isThrower.equals("True") ){
    		//setup for partythrower
    		Entity thrower = new Entity("Thrower", eventKey);
    		thrower.setProperty("channel_key", fbID);
    		//add expiration date
    		Date date = new Date();
    	    thrower.setProperty("expires", date);
    	    datastore.put(thrower);
    	}else{
    		//setup for partygoer
    		Entity goer = new Entity("Goer", eventKey);
    		goer.setProperty("channel_key", fbID);
    		datastore.put(goer);
    	}
    	
    	
    	
        resp.setContentType("text/plain");
        resp.getWriter().print(token);

    }
}