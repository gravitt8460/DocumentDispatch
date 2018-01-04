package com.josiejune.documentdispatch.servlets.inter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class CreateOperator extends HttpServlet {

	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(CreateOperator.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		String opName = request.getParameter("opName");
		String role = OperatorHandler.PROCESS_PO;
		String email = request.getParameter("email");
		String googleAcct = request.getParameter("googleAcct");
		
		String sms = request.getParameter("sms");
		if (sms == null) {
			sms = "000000000";
		}
		String dwellTimeBetweenOrders = request.getParameter("dwellTimeBetweenOrders");
	
		if (opName != null && email != null) {
			Operator operator = new Operator(opName, role, email, googleAcct,
					sms, Integer.valueOf(dwellTimeBetweenOrders));
			DAO dao = new DAO();
			dao.writeEvent("New Operator Created: " + operator.getOpName(), 
					"Operator",
					operator.getId());
			
			operator.save();
		}
		String URL = "/ProcessPOOperatorList.jsp";
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher(URL);
		rd.forward(request,response);
	}

}
