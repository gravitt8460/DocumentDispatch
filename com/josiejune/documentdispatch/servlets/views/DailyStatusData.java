package com.josiejune.documentdispatch.servlets.views;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DailyStatus;

public class DailyStatusData  extends DataSourceServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798465911458879136L;
	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request) {
		// Create a data table.
		DataTable data = new DataTable();

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("type", ValueType.TEXT, "Type"));
		cd.add(new ColumnDescription("rec", ValueType.NUMBER, "Received"));
		cd.add(new ColumnDescription("com", ValueType.NUMBER, "Completed"));
	
		data.addColumns(cd);

		try {

			DAO dao = new DAO();
			DailyStatus ds = dao.getDailyStatus();

			

			TableRow managedRow = new TableRow();
			managedRow.addCell(new TextValue("Managed"));
			managedRow.addCell(new NumberValue(ds.getTotalManagedReceived()));
			managedRow.addCell(new NumberValue(ds.getTotalManagedCompleted()));
			data.addRow(managedRow);
			
			TableRow unmanagedRow = new TableRow();
			unmanagedRow.addCell(new TextValue("Unmanaged"));
			unmanagedRow.addCell(new NumberValue(ds.getTotalUnmanagedReceived()));
			unmanagedRow.addCell(new NumberValue(ds.getTotalUnmanagedCompleted()));
			data.addRow(unmanagedRow);


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
