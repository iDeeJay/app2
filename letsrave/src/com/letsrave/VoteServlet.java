package com.letsrave;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/**
 * If thrower isn't connected, than the vote is stored on the server, 
 * and when thrower connects integrated into the playlist.
 */
public class VoteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
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
		resp.setContentType("text/plain");
		
		String eventID = req.getParameter("event");
		//log.info("Vote: event : "+eventID);
		String userID = req.getParameter("user");
		String song = req.getParameter("song");
		String up = req.getParameter("up");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key eventKey = KeyFactory.createKey("Event", eventID);
		
		Entity thrower;
		try {
			thrower = datastore.prepare(new Query("Thrower", eventKey)).asSingleEntity();
		} catch (TooManyResultsException e) {
			resp.getWriter().println("ERR A party can have only one thrower");
			return;
		}
		
		if(thrower == null){
			resp.getWriter().println("ERR No thrower for a party, party wans't created, ignore vote");
			return;
		}
		
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String channelKey = (String) thrower.getProperty("channelKey");
		log.info(channelKey);
		if(channelKey.length() > 0 ){
			try {
				channelService.sendMessage(new ChannelMessage(channelKey,
						"{\"user\":\""+userID+"\",\"song\":\""+song+"\",\"up\":"+up+"}"));
				resp.getWriter().println("OK");
				return;
			} catch (ChannelFailureException e) { log.info("send msg to "+thrower.getProperty("userID")+"failed"); }	
		}
		//if we are here it means that either channelKey == 0 or exception happend
		//store temporarily the vote
		Entity vote = new Entity("Vote", eventKey);
		vote.setProperty("userID", userID);
		vote.setProperty("song", song);
		vote.setProperty("up", (up.compareTo("true")==0) );
		
		datastore.put(vote);
		
		log.info("vote saved in datastore");
		resp.getWriter().println("OK");

	}

}
