package com.josiejune.documentdispatch.servlets.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;

public class ThroughputByHourSource  extends DataSourceServlet {

	/**
	 * 
	 */

	private static final long serialVersionUID = 3798465911458879136L;
	@Override
	public DataTable generateDataTable(Query query1, HttpServletRequest request) {
		// Create a data table.
		DataTable data = new DataTable();
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("hour", ValueType.TEXT, "Hour"));
		cd.add(new ColumnDescription("incoming", ValueType.NUMBER, "New Orders"));
		cd.add(new ColumnDescription("completed", ValueType.NUMBER, "Completed"));
		data.addColumns(cd);

		try {
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			// Get a handle on the datastore itself
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			//PersistenceManager pm = pmfInstance.getPersistenceManager();
			com.google.appengine.api.datastore.Query newQuery = new com.google.appengine.api.datastore.Query ("Event");
			newQuery.addFilter("eventTimestamp", 
					com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN, 
					today.getTime());

			ArrayList<Integer> newPOList = new ArrayList<Integer>();
			ArrayList<Integer> completedPOList = new ArrayList<Integer>();
			for (int i=0; i< 23; i++) {
				newPOList.add(i, 0);
				completedPOList.add(i, 0);
			}

			for (Entity eventEntity : datastore.prepare(newQuery).asIterable()) {
				Date date = (Date) eventEntity.getProperty("eventTimestamp");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
		//		int hour = calendar.get(Calendar.HOUR_OF_DAY);
//				if (eventEntity.getProperty("eventLevel").equals(Event.NEWPO)) {
//					newPOList.set(hour, newPOList.get(hour)+1);
//				}
//				else if (eventEntity.getProperty("eventLevel").equals(Event.POCOMPLETE)) {
//					completedPOList.set(hour, completedPOList.get(hour)+1);
//				}
			}

			for (int i=8; i<18; i++) {
				TableRow row = new TableRow();
				row.addCell(new TextValue(String.valueOf(i-3 + ":00")));
				row.addCell(new NumberValue(newPOList.get(i)));
				row.addCell(new NumberValue(completedPOList.get(i)));
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
	@Override
	protected boolean isRestrictedAccessMode() {
		return false;
	}
}
