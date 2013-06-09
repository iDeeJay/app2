package com.letsrave;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class UserPartiesServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		String userID =  My.getParam(req,resp,"user");
		String throwerStr =  My.getParam(req,resp,"thrower");
		if(userID==null || throwerStr==null) return;
		
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
		
		Filter filter = new FilterPredicate("userID", Query.FilterOperator.EQUAL, userID);
		Query query = new Query(userType).setFilter(filter);
		List<Entity> goerAtParties = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(Constants.goerAtMaxParties));
		
		for(Entity goerAtParty : goerAtParties){
			String eventID = goerAtParty.getParent().getName();
			resp.getWriter().print(eventID+",");
		}
		
	}
}
