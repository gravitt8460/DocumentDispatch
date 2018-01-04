package com.josiejune.documentdispatch.servlets.views;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class DescriptivePerformance  extends DataSourceServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798465911458879136L;
	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request) {
		// Create a data table.
		DataTable data = new DataTable();

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		//cd.add(new ColumnDescription("poid", ValueType.TEXT, "Purchase Order"));
		cd.add(new ColumnDescription("operator", ValueType.TEXT, "Operator"));
		cd.add(new ColumnDescription("orderCount", ValueType.NUMBER, "Order Count"));
		cd.add(new ColumnDescription("avgCompletionTime", ValueType.TEXT, "Mean Completion Time"));
		cd.add(new ColumnDescription("stdDev", ValueType.TEXT, "Std Deviation"));
		cd.add(new ColumnDescription("skewness", ValueType.NUMBER, "Skewness"));
		cd.add(new ColumnDescription("kurtosis", ValueType.NUMBER, "Kurtosis"));
		data.addColumns(cd);

		try {

			DAO dao = new DAO();
			List<Operator> results = dao.getSortedOperators();
			for (int i=0; i< results.size(); i++) {

				Operator op = results.get(i);
				if (! (op.getRole().equals(OperatorHandler.PROCESS_PO) ||
						op.getRole().equals(OperatorHandler.OLD_PROCESS_PO))) {
					continue;
				}

				TableRow row = new TableRow();
				row.addCell(new TextValue(op.getOpName()));

//				List<DDMessage> completedPOs = dao.getCompletedPOs(op.getOpName());
//				if (completedPOs.size() != 0) {
//					DescriptiveStatistics stats = new DescriptiveStatistics();
//
//					// Add the data from the array
//					for( int j=0; j< completedPOs.size(); j++) {
//						PurchaseOrder po = completedPOs.get(j);
//						stats.addValue(po.getCompletedDate().getTime()-po.getCreatedDate().getTime());
//					}
//
//					String meanDuration = DurationFormatUtils.formatDurationWords((long) stats.getMean(), true, true);
//					String stdDev = DurationFormatUtils.formatDurationWords((long) stats.getStandardDeviation(), true, true);
//			//		String variance = DurationFormatUtils.formatDurationWords((long) stats.getVariance(), true, true);
//					row.addCell(new NumberValue(completedPOs.size()));
//					row.addCell(new TextValue(meanDuration));
//					row.addCell(new TextValue(stdDev));
//					row.addCell(new NumberValue(stats.getSkewness()));
//					row.addCell(new NumberValue(stats.getKurtosis()));
//
//					data.addRow(row);
//				}
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
