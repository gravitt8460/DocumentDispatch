package com.josiejune.documentdispatch.servlets.inter;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;


public class ToggleOperatorOnline  extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7133358560884201822L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
			String opId = request.getParameter("opId");
			
			DAO dao = new DAO();
		    Operator operator = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(opId)));
		    operator.toggleOnline();
		    operator.save();
		  
		    dao.writeEvent ("Changed Online Status: " + operator.getOpName() + ".  New Status=" + operator.isOnline(),
		    		"Operator", 
		    		operator.getId());

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
