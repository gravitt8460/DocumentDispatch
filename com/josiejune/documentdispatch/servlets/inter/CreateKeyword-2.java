package com.josiejune.documentdispatch.servlets.inter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class CreateKeyword extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(CreateKeyword.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		DAO dao = new DAO();
		String opId = null;
		String keyword = null;
		Operator operator = null;
		try {

			opId = request.getParameter("opId");
			keyword = request.getParameter("keyword");

			
			operator = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(opId)));
			operator.addKeyword(keyword);
			operator.save();

			dao.writeEvent ("Added Keyword: " + keyword + " to list for " + operator.getOpName() + ".",
					"Operator",
					operator.getId());


		} catch (Exception ex) {
			dao.writeEvent ("Unable to add Keyword: " + keyword + " to list for " + operator.getOpName() + ".",
					"Operator",
					operator.getId());
			
			ExceptionHandler.handleException(ex);
		}

		String URL = "/ProcessPOOperatorList.jsp";
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher(URL);
		rd.forward(request,response);
	}

}
