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

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.Calendar;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;

public class SearchResultsView extends MessageListView {
	private static final Logger _log = Logger.getLogger(SearchResultsView.class.getName());
	private static final long serialVersionUID = -7746213268158959726L;

	public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException  {

		DataTable data = new DataTable();
		try {
			String completedBy = request.getParameter("completedBy");
			String salesOrderId = request.getParameter("salesOrderId");
			String searchAllDates = request.getParameter("searchAllDates");
			data.addColumns(getColumnList(false));

			DAO dao = new DAO();
			com.googlecode.objectify.Query<DDMessage> result = null;
			result = dao.ofy().query(DDMessage.class);

			if (salesOrderId != null && salesOrderId.length() > 0) {
				result = result.filter("salesOrderId", salesOrderId);
			}

			if (completedBy != null && !completedBy.equals("allOperators")) {
				result = result.filter("completedBy", completedBy);
			}

			if (searchAllDates == null || searchAllDates.equals("false")) {
				String year = request.getParameter("year");
				String day = request.getParameter("day");
				String month = request.getParameter("month");
				
				Calendar beginToday = Calendar.getInstance();
				beginToday.set(Calendar.YEAR, Integer.valueOf(year));
				beginToday.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
				beginToday.set(Calendar.MONTH, Integer.valueOf(month));
				beginToday.set(Calendar.HOUR_OF_DAY, 3);
				beginToday.set(Calendar.MINUTE, 0);
				beginToday.set(Calendar.SECOND, 0);
				beginToday.set(Calendar.MILLISECOND, 0);

				Calendar endToday = Calendar.getInstance();
				endToday.set(Calendar.YEAR, Integer.valueOf(year));
				endToday.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day)+1);
				endToday.set(Calendar.MONTH, Integer.valueOf(month));
				endToday.set(Calendar.HOUR_OF_DAY, 2);
				endToday.set(Calendar.MINUTE, 59);
				endToday.set(Calendar.SECOND, 59);
				endToday.set(Calendar.MILLISECOND, 999);

				_log.warning("begin today: " + beginToday.toString());
				_log.warning("end today: " + endToday.toString());

				result = result.filter("createdDate >", beginToday.getTime());
				result = result.filter("createdDate <=", endToday.getTime());
			}


			_log.warning ("Result Query: " + result.toString());
			List<DDMessage> purchaseOrders = result.list();

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
