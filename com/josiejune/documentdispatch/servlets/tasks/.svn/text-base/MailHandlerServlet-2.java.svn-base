package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DDMessage;

public class MailHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1361015271642305691L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(MailHandlerServlet.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
		
			DDMessage inboundMessage = new DDMessage();
			inboundMessage.setStatus(DDMessage.NEW);
			inboundMessage.parse (request.getInputStream());
			inboundMessage.save();
			
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/summarize").method(POST).countdownMillis(10000));
			//inboundMessage.childUpdated();
			
		} catch (Exception ex) {
			ExceptionHandler.handleException (ex);
		} finally {
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}