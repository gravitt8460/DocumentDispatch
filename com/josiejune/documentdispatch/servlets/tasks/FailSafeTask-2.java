package com.josiejune.documentdispatch.servlets.tasks;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class FailSafeTask extends HttpServlet {

	private static final long serialVersionUID = 1361015271642305691L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(FailSafeTask.class.getName());

	public boolean isTaskAttemptExpired (HttpServletRequest request) {
	
		Date currentTime = new Date();
		long rightNow = currentTime.getTime();
		if (rightNow - getOrigTaskTime(request) > 3600000) {
			return true;
		}
		return false;
	}
	
	public long getOrigTaskTime(HttpServletRequest request) {
		
		long origParseTime = 0;
		Date currentTime = new Date();
		String origParseTimeStr = request.getParameter("origParseTime");
		long rightNow = currentTime.getTime();
		if (origParseTimeStr == null) {
			origParseTime = rightNow;
		}
		else {
			origParseTime = Long.valueOf(origParseTimeStr);
		}
		return origParseTime;
	}
}