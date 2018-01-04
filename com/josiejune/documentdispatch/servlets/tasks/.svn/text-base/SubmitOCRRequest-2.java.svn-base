package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.util.EntityUtils;
import org.esxx.js.protocol.GAEConnectionManager;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Document;

public class SubmitOCRRequest extends FailSafeTask {

	private static final long serialVersionUID = 1361015271642305691L;
	//@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(SubmitOCRRequest.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse resp)
	throws ServletException, IOException {

		
		String docId = null;
		Document doc = null;
		DAO dao = new DAO();
		
		try {
		
			docId = request.getParameter ("docId");
			doc = dao.ofy().get(new Key<Document>(Document.class, Long.valueOf(docId)));

			if (doc.getParent().getStatus().equals(Status.ROUTED) ||
					doc.getParent().getStatus().equals(Status.COMPLETED)) {
				return;
			}
				
			dao.writeEvent ("Submitting document to OCR service.", 
					"Document", 
					doc.getId());


			if (isTaskAttemptExpired(request)) {
				dao.writeEvent ("Attempts at OCR expired - attempted for 1 hour.", 
						"Document", 
						doc.getId());

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
			HttpClient httpClient = new DefaultHttpClient(new GAEConnectionManager(), params);
			HttpPost post = new HttpPost("https://www.ocrterminal.com/api/submit.cgi?version=1.0&username=bdrenv&password=peppermint1");

			HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
			paramsBean.setUserAgent("APIClient:bdrenv");
			post.setParams(params);

			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			byte[] body = null;
			String filename = null;
			String contentType = null;

			if (doc.getFileSize() > 1000000) {
				// need to convert file to jpeg before sending to OCR
				ImagesService imagesService = ImagesServiceFactory.getImagesService();
				Image oldImage=ImagesServiceFactory.makeImage(doc.getData());//Arrays.copyOf(doc.getData(), 1000000));
				Transform resize = ImagesServiceFactory.makeResize(450, 900);
				Image newImage = imagesService.applyTransform(resize, oldImage);
				filename = doc.getFilename().substring(0, doc.getFilename().indexOf(".")) + ".jpg";

				body = newImage.getImageData();
				contentType = "image/jpg";
			}
			else {
				// use regular file
				body = doc.getData();
				filename = doc.getFilename();
				contentType = doc.getContentType();
			}

			if (contentType.equals("image/tif")) {
				// ocrterminal requires 2 t's
				contentType = "image/tiff";
			}

			if (contentType.equals("application/pdf")) {
				contentType = "application/octet-stream";
			}
	
			//ContentBody contentBody = new InputStreamKnownSizeBody(new ByteArrayInputStream(body), body.length, contentType, filename);
			//Object transferEncoding;
		//	Object charSet;
			//contentBody.
			
		//	ByteArrayPartSource partSource = new ByteArrayPartSource(filename, body);
		//	PartBase partBase = new PartBase (filename, contentType, charSet, transferEncoding);
			mpEntity.addPart("upload", new InputStreamKnownSizeBody(new ByteArrayInputStream(body), body.length, contentType, filename));
			//mpEntity.setContentEncoding(doc.getTransferEncoding());

			post.setEntity(mpEntity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			String jobId = "";

			//sc.addMessage("response String: "+  responseString);

			Pattern p =  Pattern.compile("Job id:.*$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(responseString);
			boolean result = m.find();
			if(result) {
			
				String jobLine = m.group();
				jobId = jobLine.substring(8, jobLine.length());
				dao.writeEvent ("Submission to OCR service successful.  Job ID: " + jobId + 
						".  Attempting to retrieve results in 20 seconds.", 
						"Document", 
						doc.getId());
				
				Queue queue = QueueFactory.getQueue("ocr-queue");
				queue.add(withUrl("/getocrresults")
						.param("docId", docId)
						.param("jobId", jobId)
						.method(POST)
						.countdownMillis(20000));
				//			.param("sId", String.valueOf(sc.getId())));
			}
			else {
				doc.setStatus(Status.LIMBO);
				doc.save();
				
				dao.writeEvent ("Error submitting document to OCR service.  Response from service: " + responseString, 
						"Document", 
						doc.getId());

				
				_log.warning("OCR return string: " + responseString);
				_log.warning("doc content type: " + doc.getContentType());
				_log.warning("doc name: " + doc.getFilename());
				_log.warning("doc size: " + doc.getFileSize());
				
				Queue queue = QueueFactory.getQueue("process-notify");
				queue.add(withUrl("/notify")
						.param("commandStr", "ROUTE")
						.param("notes", "Error with OCR Submission: MSGID=" + doc.getParent().getId())
						.param("opId", String.valueOf(OperatorHandler.getAdminOperator().getId()))
						.method(POST));
				return;
			}
		} catch (IOException ex) {
			ExceptionHandler.handleException(ex);
			
			dao.writeEvent ("IOException submitting document to OCR service.  " + ex.getMessage() + ".  Retrying in 60 seconds.", 
					"Document", 
					doc.getId());
			
			Queue queue = QueueFactory.getQueue("ocr-queue");
			queue.add(withUrl("/submitocr")
					.param("docId", docId)
					.method(POST)
					.countdownMillis(60000)
					.param("origParseTime", String.valueOf(getOrigTaskTime(request))));
		}
		catch (Exception ex) {
			
			dao.writeEvent ("Exception submitting document to OCR service.  " + ex.getMessage() + ".  Setting document to ERROR.", 
					"Document", 
					doc.getId());
			
			doc.setStatus(Status.LIMBO);
			doc.save();
			Queue queue = QueueFactory.getQueue("process-notify");
			queue.add(withUrl("/notify")
					.param("commandStr", "ROUTE")
					.param("notes", "Error with OCR Submission")
					.param("opId", String.valueOf(OperatorHandler.getAdminOperator().getId()))
					.method(POST));
			ExceptionHandler.handleException (ex);
		} finally {
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}
}

class InputStreamKnownSizeBody extends InputStreamBody {
	private int lenght;

	public InputStreamKnownSizeBody(
			final InputStream in, final int lenght,
			final String mimeType, final String filename) {
		super(in, mimeType, filename);
		this.lenght = lenght;
	}

	@Override
	public long getContentLength() {
		return this.lenght;
	}
}