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

import java.net.URLEncoder;
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
import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class GetKeywordsForOperator extends DataSourceServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7746213268158959726L;
	
	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {
		// Create a data table.
		DataTable data = new DataTable();
	
		String opId = request.getParameter("opId");
		DAO dao = new DAO();
		
		Operator operator = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(opId)));
		List<String> keywords = operator.getKeywords();
	
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();

		cd.add(new ColumnDescription("keyword", ValueType.TEXT, "keyword"));
		cd.add(new ColumnDescription("delete", ValueType.TEXT, "delete"));

		data.addColumns(cd);

		try {

			for (int i=0; i< keywords.size(); i++) {
				String keyword = keywords.get(i);
				TableRow row = new TableRow();
				row.addCell(new TextValue(keyword));
				
				StringBuffer sb = new StringBuffer("");
				sb.append("<a href=/removekeyword?");
				sb.append("opId=" + opId);
				sb.append("&keyword=");
				sb.append(URLEncoder.encode(keyword, "UTF-8"));
				sb.append("><img src=\"images/delete-icon.png\"/></a>");	
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
	protected boolean isRestrictedAccessMode() {
		return false;
	}
}
