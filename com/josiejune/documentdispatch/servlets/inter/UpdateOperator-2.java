package com.josiejune.documentdispatch.servlets.inter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class UpdateOperator extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7330573754253267513L;
	private static final Logger _log = Logger.getLogger(UpdateOperator.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		String opId = request.getParameter("opId");
		
		String email = request.getParameter("email");
		//String googleAcct = request.getParameter("googleAcct");
		String sms = request.getParameter("sms");
		String includeAttachments = request.getParameter("includeAttachments");
		
		_log.warning("include attachments: " + includeAttachments);
		float dwellTime = Float.valueOf(request.getParameter("dwellTimeBetweenOrders"));
		
		DAO dao = new DAO();
		Operator operator = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(opId)));
		operator.setDwellTimeBetweenOrders(dwellTime);
		operator.setEmailAddr(email);
		//operator.setGoogleAcct(googleAcct);
		operator.setSmsNumber(sms);
		if (includeAttachments != null && includeAttachments.equals("true")) {
			operator.setIncludeAttOnEmail(true);
		}
		else {
			operator.setIncludeAttOnEmail(false);
		}
		operator.save();

		dao.writeEvent("Operator Updated: " + operator.getOpName(),
				"Operator",
				operator.getId());
		
		String URL = "/ProcessPOOperatorList.jsp";
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher(URL);
		rd.forward(request,response);
	}

}
