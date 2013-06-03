package com.letsrave;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


import java.text.ParseException;
import java.text.SimpleDateFormat;

@SuppressWarnings("serial")
public class PartyCreateServlet extends HttpServlet {
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
		String throwerID = req.getParameter("throwerID");
		String expiresStr = req.getParameter("expires");

		Date expires;
		try {
			expires = new SimpleDateFormat("yyyy-MM-d", Locale.ENGLISH).parse(expiresStr);
		} catch (ParseException e1) {
			resp.getWriter().println("ERR invalid date format");
			return;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Key eventKey = KeyFactory.createKey("Event", eventID);

		Entity party;
		try {
			party = datastore.get(eventKey);
			resp.getWriter().println("ERR party is already created");
		} catch (EntityNotFoundException e) {
			//create party and assign thrower
			party = new Entity(eventKey);
			party.setProperty("expires", expires);
			datastore.put(party);
			Entity thrower = new Entity("Thrower", throwerID, eventKey);
			thrower.setProperty("userID", throwerID);
			datastore.put(thrower);
			resp.getWriter().println("OK");
		}


	}
}
