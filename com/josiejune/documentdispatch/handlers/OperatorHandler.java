package com.josiejune.documentdispatch.handlers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;
import com.josiejune.documentdispatch.models.DDMessage;

public class OperatorHandler {

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(OperatorHandler.class.getName());

	public static final String RESOLVE_PO = "Resolution";
	public static final String PROCESS_PO = "Process_PO";
	public static final String OLD_PROCESS_PO = "Process PO";
	public static final String ARCHIVE_PO = "Archival";
	public static final String ADMIN = "Administrator";

	public static final String[] ROLE_TYPES = {RESOLVE_PO, PROCESS_PO, ARCHIVE_PO, ADMIN};

	public static final String INBOUND_OPERATOR = "INBOUND_OPERATOR";
	public static final String ADMIN_OPERATOR = "ADMIN_OPERATOR";
	public static final String ARCHIVE_OPERATOR = "ARCHIVE_OPERATOR";


	public static SearchResults findOperator (DDMessage po) {

		DAO dao = new DAO();
		boolean nextUnmanagedOperatorDueFound = false;
		SearchResults sr = new SearchResults();

		for (Operator operator : dao.getSortedOperators()) {
			if (!nextUnmanagedOperatorDueFound) {
				long currentDateTime = Calendar.getInstance().getTimeInMillis();
				Date lastRouted = operator.getLastRoutedUnmanagedOrderTimestamp();
				long numMinutesLapsedSinceLastRoute = (currentDateTime - lastRouted.getTime())/(1000*60);
				if (numMinutesLapsedSinceLastRoute > operator.getDwellTimeBetweenOrders() && operator.isOnline()) {
					dao.writeEvent ("If keyword is not matched, operator would be: " + operator.getOpName(), "Message", po.getId());
					sr.setOperator(operator);
					nextUnmanagedOperatorDueFound = true;
				}
			}


			List<String> keywordList = operator.getKeywords();
			for (int j=0; j< keywordList.size(); j++) {

				// Create a pattern to match comments
				Pattern p =  Pattern.compile("\\b" + keywordList.get(j) + "\\b", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(po.getAllTextContents());
				boolean result = m.find();
				if(result) {
					//		_log.warning ("hit keyword: " + keywordList.get(j));
					dao.writeEvent ("Found keyword: " + keywordList.get(j), "Message", po.getId());

					sr.setManagedOrder(true);
					sr.addKeyword(keywordList.get(j));
					sr.setOperator(operator);
					return sr;
				}
			}
		}
		dao.writeEvent ("Is managed message: " + sr.isManagedOrder(), "Message", po.getId());
		return sr;
	}

	public static Operator getInboundOperator() {
		return getOperatorByName(INBOUND_OPERATOR);
	}

	public static Operator getArchiveOperator() {
		return getOperatorByName(ARCHIVE_OPERATOR);
	}

	public static Operator getAdminOperator() {
		return getOperatorByName(ADMIN_OPERATOR);
	}

	public static Operator getOperatorByName(String opNameVar) {
		DAO dao = new DAO();
		return dao.getOperatorByName(opNameVar);
	}

	public static List<String>getAllOperatorNames() {
		DAO dao = new DAO();
		return dao.getAllOperatorNames();
	}

	public static Map<Long, String> getAllOpIDsAndNames() {
		DAO dao = new DAO();
		return dao.getAllOpIDsAndNames();
	}

	public static Operator getOperatorByEmail (String email) {
		DAO dao = new DAO();
		return dao.getOperatorByEmail(email);
	}

}
