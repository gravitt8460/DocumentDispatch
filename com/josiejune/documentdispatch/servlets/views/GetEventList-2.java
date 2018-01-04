package com.josiejune.documentdispatch.servlets.views;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Document;
import com.josiejune.documentdispatch.models.Event;

public class GetEventList  extends DataSourceServlet {

	private static final long serialVersionUID = -6722305053044540440L;

	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request) {
		// Create a data table.
		DataTable data = new DataTable();

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("eventTimestamp", ValueType.DATETIME, "Event Timestamp"));
		cd.add(new ColumnDescription("eventText", ValueType.TEXT, "Event Text"));
		cd.add(new ColumnDescription("itemType", ValueType.TEXT, "Item Type"));
		cd.add(new ColumnDescription("itemId", ValueType.TEXT, "Item ID"));

		data.addColumns(cd);

		try {
			DAO dao = new DAO();

			String itemType = request.getParameter("itemType");
			String itemId = request.getParameter("itemId");
			if (itemType != null && itemId != null) {
				if (itemType.equals("Message")) {
					List<Event> messageEvents = dao.getEventsForMessage(Long.valueOf(itemId));
					for (Event messageEvent : messageEvents) {
						TableRow row = new TableRow();
						row.addCell(FormatUtil.getDateTimeValue(messageEvent.getEventTimestamp()));
						row.addCell(new TextValue(messageEvent.getEventText()));
						row.addCell(new TextValue(messageEvent.getItemType()));
						row.addCell(new TextValue(String.valueOf(messageEvent.getItemId())));
						data.addRow(row);
					}

					List<Document> docs = dao.getMessage(Long.valueOf(itemId)).getDocuments();
					for (Document d : docs) {
						List<Event> docEvents = dao.getEventsForDocument(d.getId());
						for (Event docevent : docEvents) {
							TableRow docrow = new TableRow();
							docrow.addCell(FormatUtil.getDateTimeValue(docevent.getEventTimestamp()));
							docrow.addCell(new TextValue(docevent.getEventText()));
							docrow.addCell(new TextValue(docevent.getItemType()));
							docrow.addCell(new TextValue(itemId + "." + d.getFileNum()));
							data.addRow(docrow);
						}
					}
				}
			}
			else {
				List<Event> results = dao.ofy().query(Event.class).list();
				for (Event event : results) {
					TableRow row = new TableRow();
					row.addCell(FormatUtil.getDateTimeValue(event.getEventTimestamp()));
					row.addCell(new TextValue(event.getEventText()));
					row.addCell(new TextValue(event.getItemType()));
					row.addCell(new TextValue(String.valueOf(event.getItemId())));
					data.addRow(row);
				}
			}

		} catch (TypeMismatchException e) {
			ExceptionHandler.handleException(e);
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
