package com.josiejune.documentdispatch.servlets.views;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;

public class GetLatestMessages extends MessageListView {
	private static final Logger _log = Logger.getLogger(GetLatestMessages.class.getName());
	private static final long serialVersionUID = -7746213268158959726L;

	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {

		// Create a data table.
		DataTable data = new DataTable();
		try {

			data.addColumns(getColumnList(false));
			DAO dao = new DAO();
			List<DDMessage> purchaseOrders = null;

			com.googlecode.objectify.Query<DDMessage> result = null;
			result = dao.ofy().query(DDMessage.class).order("createdDate");

			Calendar beginToday = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
			beginToday.set(Calendar.HOUR_OF_DAY, 0);
			beginToday.set(Calendar.MINUTE, 0);
			beginToday.set(Calendar.SECOND, 0);
			beginToday.set(Calendar.MILLISECOND, 0);

			Calendar endToday = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
			endToday.set(Calendar.HOUR_OF_DAY, 23);
			endToday.set(Calendar.MINUTE, 59);
			endToday.set(Calendar.SECOND, 59);
			endToday.set(Calendar.MILLISECOND, 999);

			_log.warning("begin today: " + beginToday.toString());
			_log.warning("end today: " + endToday.toString());

			result = result.filter("createdDate >", beginToday.getTime());
			result = result.filter("createdDate <=", endToday.getTime());

			_log.warning ("Result Query: " + result.toString());
			purchaseOrders = result.list();

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
