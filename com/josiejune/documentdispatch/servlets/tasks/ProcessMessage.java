package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.handlers.SearchResults;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;

public class ProcessMessage extends HttpServlet {

	DAO dao = new DAO();

	private static final long serialVersionUID = 1361015271642305691L;

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(ProcessMessage.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse resp)
	throws ServletException, IOException {

		DAO dao = new DAO();
		DDMessage msg = null;

		try {
			String msgId = request.getParameter ("msgId");
			long id = Long.valueOf(msgId);
			msg = dao.getMessage(id);
			if (dao.isMessageDuplicate(msg.getEmailMsgId())) {
				dao.writeEvent ("DUPLICATE: Duplicate email header for Message-Id of " + msg.getEmailMsgId() +
						" appears to be a duplicate.", "Message", msg.getId());
				
				msg.setManaged(true);
				msg.setStatus(Status.LIMBO);
				Queue queue = QueueFactory.getQueue("process-message");
				queue.add(withUrl("/routepo")
						.param("msgId", String.valueOf(msg.getId()))
						.param("opId", String.valueOf(OperatorHandler.getInboundOperator().getId()))
						.param("toOpId", String.valueOf(OperatorHandler.getAdminOperator().getId()))
						.param("managed", Status.YES)
						.param("notes", "Duplicate email header detected.")
						.method(POST));
				return;
			}
			
			if (msg.getStatus().equals(Status.ROUTED) ||
					msg.getStatus().equals(Status.COMPLETED)) {
				return;
			}
			
			msg.setStatus(Status.PROCESSING);
			msg.save();

			dao.writeEvent ("Beginning to process new message.", "Message", msg.getId());

			if (msg.isReady()) {
				routeNewOrder(msg);
			}
		}

		catch (Exception e) {
			if (msg != null) {
				msg.setManaged(true);
				msg.setStatus(Status.LIMBO);
				Queue queue = QueueFactory.getQueue("process-message");
				queue.add(withUrl("/routepo")
						.param("msgId", String.valueOf(msg.getId()))
						.param("opId", String.valueOf(OperatorHandler.getInboundOperator().getId()))
						.param("toOpId", String.valueOf(OperatorHandler.getAdminOperator().getId()))
						.param("managed", Status.YES)
						.param("notes", "Error Occured While Processing")
						.method(POST));
			}
			ExceptionHandler.handleException(e);
		} finally {
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}

	private void routeNewOrder (DDMessage po) throws IOException {

		SearchResults sr = OperatorHandler.findOperator(po);
		if (sr.getOperator() == null) {
			po.setStatus(Status.LIMBO);
			po.setManaged(true);
			po.save();
			
			dao.writeEvent ("Could not route message.  No operators available.", "Message", po.getId());

			
			Queue queue = QueueFactory.getQueue("process-message");
			queue.add(withUrl("/routepo")
					.param("msgId", String.valueOf(po.getId()))
					.param("opId", String.valueOf(OperatorHandler.getInboundOperator().getId()))
					.param("toOpId", String.valueOf(OperatorHandler.getAdminOperator().getId()))
					.param("managed", Status.YES)
					.param("notes", "No Operator Found - Perhaps Out of Capacity (?)")
					.method(POST));
			return;
		}

		String notes = "";
		String managed = "";
		if (sr.isManagedOrder()) {

			List<String> keywordMatch = sr.getKeywords();
			StringBuffer sb = new StringBuffer("");
			for (int i=0; i< keywordMatch.size(); i++) {
				if (i == keywordMatch.size()-1) {
					sb.append (keywordMatch.get(i) );
					po.setKeyword(keywordMatch.get(i));
				}
				else {
					sb.append (keywordMatch.get(i) + ", ");
				}
			}
			notes = "keyword match=" + sb.toString();
			dao.writeEvent ("Keyword found for message: " + sb.toString(), "Message", po.getId());
			po.setManaged(true);
			sr.getOperator().setLastHitKeyword(sr.getKeywords().get(sr.getKeywords().size()-1));
			sr.getOperator().save();
			po.setKeyword(sb.toString());
			po.save();
			managed = new String(Status.YES);
		}
		else {
			dao.writeEvent ("No keyword found for message.", "Message", po.getId());
			po.setManaged(false);
			notes = "unmanaged order";
			managed = new String (Status.NO);
		}

		Queue queue = QueueFactory.getQueue("process-message");
		queue.add(withUrl("/routepo")
				.param("msgId", String.valueOf(po.getId()))
				.param("opId", String.valueOf(OperatorHandler.getInboundOperator().getId()))
				.param("toOpId", String.valueOf(sr.getOperator().getId()))
				.param("managed", managed)
				.param("notes", notes)
				.method(POST));
	}
}