package com.josiejune.documentdispatch.servlets.inter;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;

public class UpdateMessageNotes extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(UpdateMessageNotes.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		String msgId = request.getParameter("msgId");
		
		String msgNotes = request.getParameter("notes");
		int workCount = Integer.valueOf(request.getParameter("workCount"));
		String managed = request.getParameter("managed");
	
		DAO dao = new DAO();
		DDMessage msg = dao.ofy().get(new Key<DDMessage>(DDMessage.class, Long.valueOf(msgId)));
		if (managed != null && managed.equals("true")) {
			msg.setManaged(true);
		}
		else {
			msg.setManaged(false);
		}
		msg.setWorkCount(workCount);
		msg.setNotes(msgNotes);
		msg.save();
		
		dao.writeEvent("Message Updated to work count of "+ workCount + " and notes of " + msgNotes,
				"DDMessage",
				msg.getId());
		
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(withUrl("/summarize").method(POST).countdownMillis(10000));
		
		String URL = "/ProcessPOOperatorList.jsp";
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher(URL);
		rd.forward(request,response);
	}

}
