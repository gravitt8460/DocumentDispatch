package com.josiejune.documentdispatch.servlets.inter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;

public class CreateTag extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(CreateTag.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {

			String msgId = request.getParameter("msgId");
			String tag = request.getParameter("tag");

			DAO dao = new DAO();
			
			dao.tagMessage(Long.valueOf(msgId), tag);
			
			dao.writeEvent ("Tagged Message: " + tag, "Message", Long.valueOf(msgId));

		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

		String URL = "/msglists/LatestMessages.jsp";
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher(URL);
		rd.forward(request,response);
	}

}
