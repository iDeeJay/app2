package com.letsrave;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class My {
	public static String getParam(HttpServletRequest req, HttpServletResponse resp, String paramName) throws IOException{
		String str = req.getParameter(paramName);
		if(str==null || str.length()==0){
			resp.getWriter().println("ERR empty "+paramName);
			return null;
		}else return str;
	}
}
