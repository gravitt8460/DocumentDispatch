package com.josiejune.documentdispatch.servlets.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.TimeOfDayValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Operator;

public class GetPOProcessingCycleTime extends MessageListView {
	private static final Logger _log = Logger.getLogger(GetPOProcessingCycleTime.class.getName());
	private static final long serialVersionUID = -7746213268158959726L;

	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {

		// Create a data table.
		DataTable data = new DataTable();
		try {
			ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
			cd.add(new ColumnDescription("operator", ValueType.TEXT, "Operator"));
			cd.add(new ColumnDescription("completionTime", ValueType.TIMEOFDAY, "Processing Time"));
			data.addColumns(cd);
			
			DAO dao = new DAO();
			
			List<Operator> operators = dao.getSortedOperators();
			for (Operator operator : operators) {
				
				com.googlecode.objectify.Query<DDMessage> result = null;
				result = dao.ofy().query(DDMessage.class)
					.filter("status", "COMPLETED")
					.filter("completedBy", operator.getOpName())
					.order("createdDate");

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

				result = result.filter("createdDate >", beginToday.getTime());
				result = result.filter("createdDate <=", endToday.getTime());

				_log.warning ("Result Query: " + result.toString());
				List<DDMessage> purchaseOrders = result.list();
				if (purchaseOrders.size() == 0) {
					continue;
				}
				float averageProcessingTime = 0;
				float sumProcessingTime = 0;
				for (DDMessage po : purchaseOrders) {
					Date createdDate = po.getCreatedDate();
					Date completedDate = po.getCompletedDate();
					sumProcessingTime = sumProcessingTime + completedDate.getTime() - createdDate.getTime();
				}
				averageProcessingTime = sumProcessingTime / (purchaseOrders.size()+1);

				long time = (long) averageProcessingTime / 1000;  
				int seconds = (int)(time % 60);  
				int minutes = (int)(time % 3600) / 60;  
				int hours = (int) time / 3600;  

				TimeOfDayValue tdv = new TimeOfDayValue(hours, minutes, seconds);
				
				TableRow row = new TableRow();
				row.addCell(new TextValue(operator.getOpName()));
				row.addCell(tdv);
				data.addRow(row);
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
