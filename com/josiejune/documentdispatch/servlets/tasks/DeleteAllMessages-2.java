package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Document;
import com.josiejune.documentdispatch.models.DocumentGroup;

public class DeleteAllMessages extends HttpServlet {

	private static final long serialVersionUID = -8823322548293982679L;

	@SuppressWarnings({ "rawtypes" })
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	
		try {
			
			Class[] clazzes = { 
					DDMessage.class, DocumentGroup.class, Document.class};
			
			for (Class clazz : clazzes) {
				
				Queue queue = QueueFactory.getQueue("trashcan");
				queue.add(withUrl("/deletealloftype")
						.param("kind", clazz.getName())
						.method(POST));
			    			
				//dao.ofy().delete(dao.ofy().query(clazz).list());
			}
		
			
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}
	}

}
