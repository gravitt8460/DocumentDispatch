package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Operator;


public class CompletePurchaseOrder extends HttpServlet {

	private static final long serialVersionUID = -7133358560884201822L;

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(CompletePurchaseOrder.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		DAO dao = new DAO();
		DDMessage po = null;
		try {
			String msgId = request.getParameter("msgId");
			String salesOrderId = request.getParameter("salesOrderId");

			po = dao.getMessage(Long.valueOf(msgId));
			Operator fromOp = po.getParent();
			fromOp.setCompletedOrdersToday(fromOp.getCompletedOrdersToday()+po.getWorkCount());
			fromOp.save();

			dao.movePurchaseOrder(false, po, OperatorHandler.getArchiveOperator());

			po.setCompletedBy(fromOp.getOpName());
			po.setCompletedDate(new Date());
			po.setSalesOrderId(salesOrderId);
			po.setStatus(Status.COMPLETED);
			po.save();
	
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/summarize").method(POST).countdownMillis(10000));
			
			dao.writeEvent ("Completed message from queue: " + fromOp.getOpName() + ". Sales Order Number: " + salesOrderId, 
					"Message", 
					po.getId());
			
			String URL = "/ProcessPOOperatorList.jsp";
			RequestDispatcher rd;
			rd = getServletContext().getRequestDispatcher(URL);
			rd.forward(request,response);
		}
		catch (Exception ex ) {
			dao.writeEvent ("Exception attempting to complete message. " + ex.getMessage(),
					"Message", 
					po.getId());
			ExceptionHandler.handleException(ex);
		}

	}
}
