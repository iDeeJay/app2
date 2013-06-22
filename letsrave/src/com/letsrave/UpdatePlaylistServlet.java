package com.letsrave;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Text;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class UpdatePlaylistServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(UpdatePlaylistServlet.class.getName());
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
		
		String eventID = My.getParam(req, resp, "event");
		String playlistJSON = req.getParameter("json");
		String uri = req.getParameter("uri");
		String offset = req.getParameter("offset");
		if(eventID==null || playlistJSON==null || uri==null || offset==null) return;
		
		
		//Update the playlist in datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key eventKey = KeyFactory.createKey("Event", eventID);
		
		Entity party;
		try {
			party = datastore.get(eventKey);
			party.setProperty("json", new Text(playlistJSON));
			party.setProperty("playlistURI", uri);
			party.setProperty("offset", offset);
			datastore.put(party);
		} catch (EntityNotFoundException e) {
			resp.getWriter().println("ERR party should be first created");
			return;
		}
		
		//Now send the playlist to partygoers
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		
		Query query = new Query("Goer", eventKey);
		
		List<Entity> goers = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(Constants.maxGoers));
		
		for (Entity goer: goers){
			String channel_key = (String) goer.getProperty("channel_key");
			if(channel_key!=null && channel_key.length() > 0)
				channelService.sendMessage(new ChannelMessage(channel_key,
						"{\"uri\":\""+uri+"\", \"offset\":\"" +offset+ "\", \"data\":"+playlistJSON+"}"));
		}

		resp.getWriter().println("OK");
		
	}
}
