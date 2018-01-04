package com.josiejune.documentdispatch.servlets.views;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class GetProcessPOOperatorList  extends DataSourceServlet {

	private static final long serialVersionUID = 3798465911458879136L;
	
	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request) {
		// Create a data table.
		DataTable data = new DataTable();
	
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("online", ValueType.TEXT, "Online"));
		cd.add(new ColumnDescription("opName", ValueType.TEXT, "Name"));
		cd.add(new ColumnDescription("lastHitKeyword", ValueType.TEXT, "Last Keyword"));	
		cd.add(new ColumnDescription("queue", ValueType.NUMBER, "Orders in Queue"));
		//cd.add(new ColumnDescription("numOrdersToday", ValueType.TEXT, "Routed"));
		//cd.add(new ColumnDescription("managedOrdersToday", ValueType.TEXT, "Managed"));
		//cd.add(new ColumnDescription("unmanagedOrdersToday", ValueType.TEXT, "Basket"));
		cd.add(new ColumnDescription("dwellTimeBetweenOperators", ValueType.NUMBER, "Dwell Time (mins)"));
		
		cd.add(new ColumnDescription("lastRoutedPOTimestamp", ValueType.DATETIME, "Last"));
		cd.add(new ColumnDescription("showPurchaseOrders", ValueType.TEXT, "Queue"));
		cd.add(new ColumnDescription("manageKeywords", ValueType.TEXT, "Keywords"));
		cd.add(new ColumnDescription("edit", ValueType.TEXT, "Edit"));
		data.addColumns(cd);

		try {
			DAO dao = new DAO();
			List<Operator> results = dao.getAllOperators();
			for (int i=0; i< results.size(); i++) {
				
				Operator op = results.get(i);
				if (! (op.getRole().equals(OperatorHandler.PROCESS_PO) ||
						op.getRole().equals(OperatorHandler.OLD_PROCESS_PO) ||
						op.getRole().equals(OperatorHandler.ADMIN))) {
					continue;
				}
				TableRow row = new TableRow();
				StringBuffer sb = new StringBuffer("");
				sb.append("<a href=/toggleonline?");
				sb.append("opId=" + op.getKey().getId());
				sb.append("><img src=images/");
				if (op.isOnline()) {
					sb.append("Green.png");
				}
				else {
					sb.append("Red.png");
				}
				sb.append(" width=25 height=25 /></a>");
				row.addCell(new TextValue(sb.toString()));
			
				row.addCell(new TextValue(op.getOpName()));
				if (op.getLastHitKeyword() == null) {
					row.addCell(new TextValue(""));
				}
				else {
					row.addCell(new TextValue(op.getLastHitKeyword()));
				}
				row.addCell(new NumberValue(op.getMessages().size()));
			//	row.addCell(new TextValue(String.valueOf(op.getManagedOrdersToday() + op.getUnmanagedOrdersToday())));		
			//	row.addCell(new TextValue(String.valueOf(op.getManagedOrdersToday())));
			//	row.addCell(new TextValue(String.valueOf(op.getUnmanagedOrdersToday())));
				row.addCell(new NumberValue(op.getDwellTimeBetweenOrders()));
				
				GregorianCalendar gcCreated = (GregorianCalendar) 
				Calendar.getInstance(new ULocale("en_US@@calendar=gregorian"));

				java.util.Calendar calCreated = java.util.Calendar.getInstance();
				calCreated.setTime(op.getLastRoutedPOTimestamp());

				
				gcCreated.set(Calendar.YEAR, calCreated.get(java.util.Calendar.YEAR));
				gcCreated.set(Calendar.DAY_OF_MONTH, calCreated.get(java.util.Calendar.DAY_OF_MONTH));
				gcCreated.set(Calendar.HOUR_OF_DAY, calCreated.get(java.util.Calendar.HOUR_OF_DAY));
				gcCreated.set(Calendar.MINUTE, calCreated.get(java.util.Calendar.MINUTE));
				gcCreated.set(Calendar.SECOND, calCreated.get(java.util.Calendar.SECOND));
				gcCreated.set(Calendar.MILLISECOND, calCreated.get(java.util.Calendar.MILLISECOND));

				gcCreated.setTime(op.getLastRoutedPOTimestamp());

				int hourCreated = calCreated.get(java.util.Calendar.HOUR_OF_DAY);

				DateTimeValue dtvCreated = new DateTimeValue(
						calCreated.get(java.util.Calendar.YEAR),
						calCreated.get(java.util.Calendar.MONTH),
						calCreated.get(java.util.Calendar.DAY_OF_MONTH),
						hourCreated,
						calCreated.get(java.util.Calendar.MINUTE),
						calCreated.get(java.util.Calendar.SECOND),
						calCreated.get(java.util.Calendar.MILLISECOND));

				dtvCreated.getObjectToFormat().setTimeZone(TimeZone.getTimeZone("America/New_York"));

				row.addCell(dtvCreated);

				sb = new StringBuffer("");
				sb.append("<a href=msglists/GetPurchaseOrdersInQueue.jsp?");
				sb.append("opId=" + op.getKey().getId());
				sb.append("><img width=25 height=25 src=\"images/queue.png\"/></a>");	
				row.addCell(new TextValue(sb.toString()));

				sb = new StringBuffer("");
				sb.append("<a href=/GetKeywordsForOperator.jsp?");
				sb.append("opId=" + op.getKey().getId());
				sb.append("><img width=25 height=25 src=\"images/keyword.png\"/></a>");	
				row.addCell(new TextValue(sb.toString()));

				sb = new StringBuffer("");
				sb.append("<a href=/EditOperator.jsp?");
				sb.append("opId=" + op.getKey().getId());
				sb.append(">Edit</a>");	
				row.addCell(new TextValue(sb.toString()));
	
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
