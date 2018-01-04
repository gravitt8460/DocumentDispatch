package com.josiejune.documentdispatch.servlets.views;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Document;
import com.josiejune.documentdispatch.models.Operator;

public abstract class MessageListView extends DataSourceServlet {

	private static final long serialVersionUID = 1L;

	public ArrayList<ColumnDescription> getColumnList (boolean inQueue) {
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("id", ValueType.TEXT, "ID"));
		cd.add(new ColumnDescription("view", ValueType.TEXT, "View"));
		if (!inQueue) {
			cd.add(new ColumnDescription("owner", ValueType.TEXT, "Owner"));
		}
		cd.add(new ColumnDescription("keyword", ValueType.TEXT, "Keyword"));
		cd.add(new ColumnDescription("files", ValueType.TEXT, "Files"));
		cd.add(new ColumnDescription("createdDate", ValueType.DATETIME, "Created"));	
		cd.add(new ColumnDescription("modifiedDate", ValueType.DATETIME, "Modified"));
		if (!inQueue) {
			cd.add(new ColumnDescription("status", ValueType.TEXT, "Status"));
		}
		cd.add(new ColumnDescription("reroute", ValueType.TEXT, "Route"));
		cd.add(new ColumnDescription("tag", ValueType.TEXT, "Tag"));
		return cd;
	}
	
	public TableRow getTableRow (DDMessage po, boolean inQueue) throws UnsupportedEncodingException {
		TableRow row = new TableRow();
		row.addCell(new TextValue(String.valueOf(po.getId())));
		row.addCell(new TextValue("<a href=/ViewMsg.jsp?msgId=" 
				+ URLEncoder.encode(String.valueOf(po.getId()), "UTF-8")
				+ "><img width=25 height=25 src=\"../images/binoculars-icon.gif\"/></a>"));

		if (!inQueue) {
			Operator op = po.getParent();
			if (op != null) {
				if (po.getStatus().equals(Status.COMPLETED)) {
					row.addCell(new TextValue(po.getCompletedBy()));
				}
				else {
					row.addCell(new TextValue(op.getOpName()));
				}
			}
			else {
				row.addCell(new TextValue("No Operator"));
			}
		}

		
		row.addCell(new TextValue(po.getKeyword()));
		
		StringBuffer sb = new StringBuffer("");
		List<Document> docs = po.getDocuments();
		for (Document d : docs) {
			if (d.getFilename().endsWith("pdf") ||
					d.getFilename().endsWith("xls") ||
					d.getFilename().endsWith("doc")) {
				
				sb.append(d.getFilename() + "<br/>");
			}
		}
		
		if (sb.length() == 0) {
			sb.append(po.getMsgSubject() + "<br/>");
		}
		row.addCell(new TextValue(sb.toString()));
		row.addCell(FormatUtil.getDateTimeValue(po.getCreatedDate()));
		row.addCell(FormatUtil.getDateTimeValue(po.getModifiedTimestamp()));
		
		if (!inQueue) {
			row.addCell(new TextValue(po.getStatus()));
		}

		if (po.getStatus().equals(Status.COMPLETED)) {
			row.addCell(new TextValue("N/A"));
		}
		else {
			sb = new StringBuffer("");
			sb.append("<a href=/ReroutePO.jsp?");
			sb.append("opId=" + po.getParent().getId());
			sb.append("&msgId=");
			sb.append(URLEncoder.encode(String.valueOf(po.getKey().getId()), "UTF-8"));
			sb.append("><img width=25 height=25 src=\"../images/route.png\"/></a>");	
			row.addCell(new TextValue(sb.toString()));
		}

		sb = new StringBuffer("");
		sb.append("<a href=/GetTagsForMessage.jsp?");
		sb.append("msgId=");
		sb.append(URLEncoder.encode(String.valueOf(po.getKey().getId()), "UTF-8"));
		sb.append("><img width=25 height=25 src=\"../images/tag.jpg\"/></a>");	
		row.addCell(new TextValue(sb.toString()));
		return row;
	}
	
	@Override
	public DataTable generateDataTable(Query arg0, HttpServletRequest arg1)
			throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
