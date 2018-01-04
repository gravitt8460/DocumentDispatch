package com.josiejune.documentdispatch.servlets.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class ResetAllOperatorStats extends HttpServlet {
	
	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(ResetAllOperatorStats.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
			DAO dao = new DAO();
			List<Operator> results = dao.getAllOperators();
			for (Operator op : results) {
				op.setCompletedOrdersToday(0);
				op.setLastRoutedPOTimestamp(new Date(0));
				op.setLastRoutedUnmanagedOrderTimestamp(new Date(0));
				op.setUnmanagedOrdersToday(0);
				op.setManagedOrdersToday(0);
				dao.ofy().put(op);
			}
		}
		catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}
		String URL = "/ProcessPOOperatorList.jsp";
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher(URL);
		rd.forward(request,response);
	}

}
