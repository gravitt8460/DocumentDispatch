package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.esxx.js.protocol.GAEConnectionManager;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.apphosting.api.DeadlineExceededException;
import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Document;

public class GetOCRResults extends FailSafeTask {

	private static final long serialVersionUID = 1361015271642305691L;
//	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(GetOCRResults.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse resp)
	throws ServletException {

		DAO dao = new DAO();
		Document doc = null;
		String docId = "";
		String jobId = "";
		
		try {
			docId = request.getParameter ("docId");
			jobId = request.getParameter ("jobId");

			doc = dao.ofy().get(new Key<Document>(Document.class, Long.valueOf(docId)));
			if (doc.getParent().getStatus().equals(Status.ROUTED) ||
					doc.getParent().getStatus().equals(Status.COMPLETED)) {
				return;
			}
			
			if (isTaskAttemptExpired(request)) {
				doc.setStatus(Status.LIMBO);
				doc.save();
				Queue queue = QueueFactory.getQueue("process-notify");
				queue.add(withUrl("/notify")
						.param("commandStr", "ROUTE")
						.param("notes", "Parsing Attempted for 1 Hour - " + doc.getParent().getId())
						.param("opId", String.valueOf(OperatorHandler.getAdminOperator().getId()))
						.method(POST));
				return;
			}

			HttpParams params = new BasicHttpParams();
			HttpClient httpclient = new DefaultHttpClient(new GAEConnectionManager(), params);
			String format = URLEncoder.encode("text/plain", "UTF-8");
			HttpPost retrievePost = 
				new HttpPost("https://www.ocrterminal.com/api/retrieve.cgi?version=1.0" +
						"&username=bdrenv&" +
						"password=peppermint1&" +
						"job=" + jobId + "&" +
						"format=" + format);

			HttpProtocolParamBean paramsBean1 = new HttpProtocolParamBean(params);
			paramsBean1.setUserAgent("APIClient:bdrenv");
			retrievePost.setParams(params);

			_log.warning("Checking for completion:"  + retrievePost.getRequestLine());

			HttpResponse response = httpclient.execute(retrievePost);
			HttpEntity entity = response.getEntity();
			InputStream retrieveInput = entity.getContent();
			String responseString = IOUtils.toString(retrieveInput);
			String copyOfResponseStr = new String(responseString);

			Pattern p =  Pattern.compile("OCRTerminal/1.0 Processing", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(responseString);
			if (m.find()) {
				dao.writeEvent ("OCR service is still processing.  Retrying in 20 seconds.", 
						"Document", 
						doc.getId());
				
				Queue queue = QueueFactory.getQueue("ocr-queue");
				queue.add(withUrl("/getocrresults")
						.param("docId", docId)
						.param("jobId", jobId)
						.method(POST)
						.countdownMillis(20000));
			}
			else {
			//	long id = Long.valueOf(docId);
			//	Document doc = dao.ofy().get(new Key<Document>(Document.class, id));
				dao.writeEvent ("Setting document contents to results from OCR service.  Results size: " + copyOfResponseStr.length(), 
						"Document", 
						doc.getId());
				
				doc.setOCRContents(copyOfResponseStr);
				doc.save();
			}
		} catch (DeadlineExceededException ex) {
			
			dao.writeEvent ("Deadline exceeding while retrieving OCR results.  Retrying in 20 seconds.", 
					"Document", 
					doc.getId());
			
			Queue queue = QueueFactory.getQueue("ocr-queue");
			queue.add(withUrl("/getocrresults")
					.param("docId", docId)
					.param("jobId", jobId)
					.param("origParseTime", String.valueOf(getOrigTaskTime(request)))
					.method(POST)
					.countdownMillis(20000));
			ExceptionHandler.handleException(ex);

		} catch (IOException io) {
			
			dao.writeEvent ("IO exception while retrieving OCR results.  Retrying in 20 seconds.", 
					"Document", 
					doc.getId());
			
			Queue queue = QueueFactory.getQueue("ocr-queue");
			queue.add(withUrl("/getocrresults")
					.param("docId", docId)
					.param("jobId", jobId)
					.param("origParseTime", String.valueOf(getOrigTaskTime(request)))
					.method(POST)
					.countdownMillis(20000));
			ExceptionHandler.handleException(io);
		}
		finally {
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}
}