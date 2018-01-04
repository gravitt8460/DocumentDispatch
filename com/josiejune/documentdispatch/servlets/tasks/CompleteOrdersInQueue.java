package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Operator;

public class CompleteOrdersInQueue extends HttpServlet {

	private static final long serialVersionUID = 1361015271642305691L;

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(CompleteOrdersInQueue.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse resp)
	throws ServletException, IOException {

		DAO dao = new DAO();

		try {
			
			Operator operator = dao.getOperatorByName(request.getParameter("opName"));
			
			List<DDMessage> purchaseOrders = operator.getMessages();
			for (int i=0; i< purchaseOrders.size(); i++) {
				DDMessage po = purchaseOrders.get(i);
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(withUrl("/completepo")
						.param("msgId", String.valueOf(po.getId()))
						.param("salesOrderId", "NA")
						.method(POST));
			}
		}

		catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}
}