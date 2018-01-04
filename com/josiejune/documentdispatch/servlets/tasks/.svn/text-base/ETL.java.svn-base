package com.josiejune.documentdispatch.servlets.tasks;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DailyStatus;

public class ETL extends HttpServlet {
	
	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(ETL.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		try {
			DAO dao = new DAO();
			DailyStatus ds = dao.getDailyStatus();
			ds.setTotalManagedReceived(dao.getNumberReceivedMessagesToday(true));
			ds.setTotalUnmanagedReceived(dao.getNumberReceivedMessagesToday(false));
			ds.setTotalManagedCompleted(dao.getNumberCompletedMessagesToday(true));
			ds.setTotalUnmanagedCompleted(dao.getNumberCompletedMessagesToday(false));
			ds.save();
			
		}
		catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}
	}

}
