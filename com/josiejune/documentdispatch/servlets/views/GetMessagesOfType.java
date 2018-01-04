package com.josiejune.documentdispatch.servlets.views;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;

public class GetMessagesOfType extends MessageListView {
	//private static final Logger _log = Logger.getLogger(GetMessagesOfType.class.getName());
	private static final long serialVersionUID = -7746213268158959726L;

	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {
	
		// Create a data table.
		DataTable data = new DataTable();
		try {
			String status = request.getParameter("status");
			data.addColumns(getColumnList(false));
			DAO dao = new DAO();
			
			//com.googlecode.objectify.Query<DDMessage> result = null;
			List<DDMessage> purchaseOrders = dao.ofy().query(DDMessage.class)
				.filter("status", status)
				.order("createdDate")
				.list();
	
			for (int i=0; i< purchaseOrders.size(); i++) {
				DDMessage po = purchaseOrders.get(i);
				data.addRow(getTableRow(po, false));
			}
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}
		return data;
	}

	/**
	 * NOTE: By default, this function returns true, which means that cross
	 * domain requests are rejected.
	 * This check is disabled here so examples can be used directly from the
	 * address bar of the browser. Bear in mind that this exposes your
	 * data source to xsrf attacks.
	 * If the only use of the data source url is from your application,
	 * that runs on the same domain, it is better to remain in restricted mode.
	 */
	protected boolean isRestrictedAccessMode() {
		return false;
	}


}
