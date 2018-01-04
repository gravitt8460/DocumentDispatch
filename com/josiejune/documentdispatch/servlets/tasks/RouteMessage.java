package com.josiejune.documentdispatch.servlets.tasks;

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
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Operator;


public class RouteMessage extends HttpServlet {

	private static final long serialVersionUID = -7133358560884201822L;

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(RouteMessage.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
			String toOpId = request.getParameter("toOpId");
			String msgId = request.getParameter("msgId");
			String opId = request.getParameter("opId");
			String notes = request.getParameter("notes");
			String managed = request.getParameter("managed");
			boolean isManaged = false;
			if (managed.equals(Status.YES)) {
				isManaged = true;
			}

			DAO dao = new DAO();
			DDMessage po = dao.getMessage(Long.valueOf(msgId));

			Operator fromOp = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(opId)));
			Operator operator = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(toOpId)));

			dao.movePurchaseOrder(isManaged, po, operator);
			operator.save();
			dao.writeEvent ("Route from " + fromOp.getOpName() + " to " + 
					operator.getOpName() + ".  Is managed?: " + isManaged, 
					"Message", 
					po.getId());

			if (notes == null) {
				notes = "No Notes";
			}
			Queue queue = QueueFactory.getQueue("process-notify");
			queue.add(withUrl("/notify")
					.param("msgId", String.valueOf(po.getId()))
					.param("commandStr", "ROUTE")
					.param("notes", notes)
					.param("opId", toOpId)
					.method(POST));
			po.setStatus(Status.ROUTED);

			if (!(fromOp.getOpName().equals(OperatorHandler.INBOUND_OPERATOR))) {
				String messageToPriorOp = new String("Order has been re-routed from your queue.");
				//Queue queue = QueueFactory.getQueue("process-notify");
				queue.add(withUrl("/notify")
						.param("msgId", String.valueOf(po.getId()))
						.param("commandStr", "UN-ROUTE")
						.param("notes", messageToPriorOp)
						.param("opId", String.valueOf(fromOp.getId()))
						.method(POST));
			}
			
			Queue queue2 = QueueFactory.getDefaultQueue();
			queue2.add(withUrl("/summarize").method(POST).countdownMillis(10000));

			String URL = "/ProcessPOOperatorList.jsp";
			RequestDispatcher rd;
			rd = getServletContext().getRequestDispatcher(URL);
			rd.forward(request,response);
		}
		catch (Exception ex ) {
			ExceptionHandler.handleException(ex);
		}

	}
}
