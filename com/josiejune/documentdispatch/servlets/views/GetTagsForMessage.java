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
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Tag;

public class GetTagsForMessage extends DataSourceServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7746213268158959726L;
	
	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {
		// Create a data table.
		DataTable data = new DataTable();
	
		String msgId = request.getParameter("msgId");
		DAO dao = new DAO();
		
		DDMessage message = dao.ofy().get(new Key<DDMessage>(DDMessage.class, Long.valueOf(msgId)));
		List<Tag> tags = dao.getTagsForMessage(message);
	
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();

		cd.add(new ColumnDescription("tag", ValueType.TEXT, "Tag"));
		cd.add(new ColumnDescription("delete", ValueType.TEXT, "Delete"));

		data.addColumns(cd);

		try {

			for (int i=0; i< tags.size(); i++) {
				Tag tag = tags.get(i);
				TableRow row = new TableRow();
				row.addCell(new TextValue(tag.getTag()));
				
				StringBuffer sb = new StringBuffer("");
				sb.append("<a href=/removetag?");
				sb.append("tagId=" + tag.getId());
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
