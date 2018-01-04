package com.josiejune.documentdispatch.servlets.views;

// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Document;
import com.josiejune.documentdispatch.models.Operator;
import com.josiejune.documentdispatch.search.SearchJanitor;

public class SearchMessages extends DataSourceServlet {
	//private static final Logger _log = Logger.getLogger(SearchMessages.class.getName());

	private static final long serialVersionUID = -7746213268158959726L;
	private static final Logger _log = Logger.getLogger(SearchMessages.class.getName());
	
	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {

		String lookin = request.getParameter("lookin");
		_log.warning("lookin: " + lookin);
		if (lookin == null) {
			lookin = "doccontents";
		}
		
		try {
			if (lookin.equals("ordernum")) {
				return searchForOrder(query, request);
			}
			else if (lookin.equals("msgtags")) {
				return searchTags (query, request);
			}
			else {
				return searchContents(query, request);
			}
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.handleException(e);
			return null;
		}
	}

	private DataTable searchContents(Query query, HttpServletRequest request) {

		// Create a data table.
		DataTable data = new DataTable();

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("id", ValueType.TEXT, "Document ID"));
		cd.add(new ColumnDescription("view", ValueType.TEXT, "View"));
		cd.add(new ColumnDescription("filename", ValueType.TEXT, "Filename"));
		cd.add(new ColumnDescription("modifiedDate", ValueType.DATETIME, "Modified"));
		cd.add(new ColumnDescription("createdDate", ValueType.DATETIME, "Created"));

		data.addColumns(cd);

		try {
			List<Document> documents = SearchJanitor.searchDocuments(request.getParameter("searchStr"));

			for (int i=0; i< documents.size(); i++) {
				Document doc = documents.get(i);

				TableRow row = new TableRow();
				row.addCell(new TextValue(String.valueOf(doc.getId())));
				row.addCell(new TextValue("<a href=/ViewDoc.jsp?docId=" 
						+ URLEncoder.encode(String.valueOf(doc.getId()), "UTF-8")
						+ ">View</a>"));		

				row.addCell(new TextValue(doc.getFilename()));
				row.addCell(FormatUtil.getDateTimeValue(doc.getModifiedTimestamp()));
				row.addCell(FormatUtil.getDateTimeValue(doc.getCreatedDate()));	
				data.addRow(row);
			}
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

		return data;

	}

	private DataTable searchTags(Query query, HttpServletRequest request) throws UnsupportedEncodingException, TypeMismatchException {
		DataTable data = new DataTable();

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("id", ValueType.TEXT, "ID"));
		cd.add(new ColumnDescription("view", ValueType.TEXT, "View"));
		cd.add(new ColumnDescription("owner", ValueType.TEXT, "Owner"));
		cd.add(new ColumnDescription("keyword", ValueType.TEXT, "Keyword"));
		cd.add(new ColumnDescription("modifiedDate", ValueType.DATETIME, "Modified"));
		cd.add(new ColumnDescription("createdDate", ValueType.DATETIME, "Created"));

		cd.add(new ColumnDescription("status", ValueType.TEXT, "Status"));
		cd.add(new ColumnDescription("reroute", ValueType.TEXT, "Route"));
		cd.add(new ColumnDescription("tag", ValueType.TEXT, "Tag"));

		data.addColumns(cd);

		DAO dao = new DAO();
		List<DDMessage> purchaseOrders = dao.getMessagesForTag(request.getParameter("searchStr"));
	
		for (int i=0; i< purchaseOrders.size(); i++) {
			DDMessage po = purchaseOrders.get(i);

			TableRow row = new TableRow();
			row.addCell(new TextValue(String.valueOf(po.getId())));
			row.addCell(new TextValue("<a href=/ViewMsg.jsp?msgId=" 
					+ URLEncoder.encode(String.valueOf(po.getId()), "UTF-8")
					+ ">View</a>"));

			Operator op = po.getParent();
			if (op != null) {
				row.addCell(new TextValue(op.getOpName()));
			}
			else {
				row.addCell(new TextValue("No Operator"));
			}

			StringBuffer sb = new StringBuffer("");
			row.addCell(new TextValue(po.getKeyword()));

			row.addCell(FormatUtil.getDateTimeValue(po.getModifiedTimestamp()));
			row.addCell(FormatUtil.getDateTimeValue(po.getCreatedDate()));
			row.addCell(new TextValue(po.getStatus()));

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

			data.addRow(row);
		}

		return data;
	}

	private DataTable searchForOrder(Query query, HttpServletRequest request) throws UnsupportedEncodingException, TypeMismatchException {
		DataTable data = new DataTable();

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("id", ValueType.TEXT, "ID"));
		cd.add(new ColumnDescription("view", ValueType.TEXT, "View"));
		cd.add(new ColumnDescription("owner", ValueType.TEXT, "Owner"));
		cd.add(new ColumnDescription("keyword", ValueType.TEXT, "Keyword"));
		cd.add(new ColumnDescription("modifiedDate", ValueType.DATETIME, "Modified"));
		cd.add(new ColumnDescription("createdDate", ValueType.DATETIME, "Created"));
		cd.add(new ColumnDescription("status", ValueType.TEXT, "Status"));
		cd.add(new ColumnDescription("reroute", ValueType.TEXT, "Route"));
		cd.add(new ColumnDescription("tag", ValueType.TEXT, "Tag"));

		data.addColumns(cd);

		DAO dao = new DAO();
		List<DDMessage> purchaseOrders = dao.getMessageForOrderNum(request.getParameter("searchStr"));
	
		for (int i=0; i< purchaseOrders.size(); i++) {
			DDMessage po = purchaseOrders.get(i);

			TableRow row = new TableRow();
			row.addCell(new TextValue(String.valueOf(po.getId())));
			row.addCell(new TextValue("<a href=/ViewMsg.jsp?msgId=" 
					+ URLEncoder.encode(String.valueOf(po.getId()), "UTF-8")
					+ ">View</a>"));

			Operator op = po.getParent();
			if (op != null) {
				row.addCell(new TextValue(op.getOpName()));
			}
			else {
				row.addCell(new TextValue("No Operator"));
			}

			StringBuffer sb = new StringBuffer("");
			row.addCell(new TextValue(po.getKeyword()));

			row.addCell(FormatUtil.getDateTimeValue(po.getModifiedTimestamp()));
			row.addCell(FormatUtil.getDateTimeValue(po.getCreatedDate()));
			row.addCell(new TextValue(po.getStatus()));

			if (!po.getStatus().equals(Status.COMPLETED)) {
				sb = new StringBuffer("");
				sb.append("<a href=/ReroutePO.jsp?");
				sb.append("opId=" + po.getParent().getId());
				sb.append("&msgId=");
				sb.append(URLEncoder.encode(String.valueOf(po.getKey().getId()), "UTF-8"));
				sb.append("><img width=25 height=25 src=\"../images/route.png\"/></a>");	
				row.addCell(new TextValue(sb.toString()));
			}
			else {
				row.addCell("N/A");
			}

			sb = new StringBuffer("");
			sb.append("<a href=/GetTagsForMessage.jsp?");
			sb.append("msgId=");
			sb.append(URLEncoder.encode(String.valueOf(po.getKey().getId()), "UTF-8"));
			sb.append("><img width=25 height=25 src=\"../images/tag.jpg\"/></a>");	
			row.addCell(new TextValue(sb.toString()));

			data.addRow(row);
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
