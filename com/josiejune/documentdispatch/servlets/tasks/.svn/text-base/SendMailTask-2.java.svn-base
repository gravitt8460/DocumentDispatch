package com.josiejune.documentdispatch.servlets.tasks;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Key;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Document;
import com.josiejune.documentdispatch.models.Operator;
import com.josiejune.documentdispatch.servlets.views.FormatUtil;

public class SendMailTask extends HttpServlet {

	private static final long serialVersionUID = -7133358560884201822L;


	private static final Logger _log = Logger.getLogger(CompletePurchaseOrder.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
			Message message = buildMessage (request);
			Transport.send(message);
		}
		//		catch (OverQuotaException oqe) {
		//			Message message;
		//			try {
		//				message = buildMessage (false, request);
		//				Transport.send(message);	
		//			} catch (Exception e) {
		//				// TODO Auto-generated catch block
		//				ExceptionHandler.handleException(e);
		//				return;
		//			}
		//		}
		//		catch (RequestTooLargeException rtle) {
		//			Message message;
		//			try {
		//				message = buildMessage (false, request);
		//				Transport.send(message);	
		//			} catch (Exception e) {
		//				// TODO Auto-generated catch block
		//				ExceptionHandler.handleException(e);
		//				return;
		//			}
		//		}
		//		catch (ApiDeadlineExceededException adee) {
		//			Queue queue = QueueFactory.getQueue("process-notify");
		//			queue.add(withUrl("/notify")
		//					.param("msgId", request.getParameter("msgId"))
		//					.param("commandStr", "ROUTE")
		//					.param("notes", request.getParameter("notes"))
		//					.param("opId", request.getParameter("opId"))
		//					.param("attach", "no")
		//					.method(POST));
		//		}
		catch (Exception ex ) {
			ExceptionHandler.handleException(ex);
		}
	}



	private Message buildMessage (HttpServletRequest request) throws MessagingException, IOException, InterruptedException {

		String commandStr = request.getParameter("commandStr");
		String poId = request.getParameter("msgId");
		String opId = request.getParameter("opId");
		String notes = request.getParameter("notes");
		String attach = request.getParameter("attach");

		DAO dao = new DAO();
		Operator operator = dao.ofy().get(new Key<Operator>(Operator.class, Long.valueOf(opId)));
		boolean includeAttachments = operator.isIncludeAttOnEmail();
		if (attach != null && attach.equals("no")) {
			includeAttachments = false;
		}

		Date datestamp = new Date();

		StringBuffer sb = new StringBuffer("");
		sb.append ("Event: " + commandStr+ System.getProperty("line.separator"));
		sb.append ("Date of Event: " + FormatUtil.getDateTimeValue(datestamp).toString() + System.getProperty("line.separator"));
		sb.append ("Notes: " + notes + System.getProperty("line.separator") + System.getProperty("line.separator") );
		sb.append (System.getProperty("line.separator") + System.getProperty("line.separator"));
		sb.append ("Operator Statistics for " + operator.getOpName() + ": " + System.getProperty("line.separator"));
		sb.append ("Total Orders Routed Today: " + String.valueOf(operator.getManagedOrdersToday() + operator.getUnmanagedOrdersToday()) + 
				System.getProperty("line.separator"));
		sb.append ("Managed Orders: " + String.valueOf(operator.getManagedOrdersToday()) + 
				System.getProperty("line.separator"));
		sb.append ("Basket Orders: " + String.valueOf(operator.getUnmanagedOrdersToday()) + 
				System.getProperty("line.separator"));
		sb.append ("Basket Order Dwell Time: " + String.valueOf(operator.getDwellTimeBetweenOrders()) + 
				System.getProperty("line.separator"));
		sb.append ("Total Orders Completed Today: " + String.valueOf(operator.getCompletedOrdersToday()) + 
				System.getProperty("line.separator"));
		sb.append ("Last Hit Keyword: " + operator.getLastHitKeyword() + 
				System.getProperty("line.separator"));
		sb.append ("Current Queue Size: " + operator.getMessages().size()+ 
				System.getProperty("line.separator"));

		String to = operator.getEmailAddr();
		String subject = "";

		Multipart mp = new MimeMultipart();
		DDMessage po = null;
		if (poId != null) {
			po = dao.ofy().get(new Key<DDMessage>(DDMessage.class, Long.valueOf(poId)));
		}
		if (po != null) {
			sb.append ("MSGID=" + po.getId() + System.getProperty("line.separator"));
			for (String filename : po.getFilenames()) {
				sb.append("Filename: " + filename + System.getProperty("line.separator"));
			}
			subject = "Dispatch: " + commandStr + ": MSGID=" + poId;

			if (includeAttachments) {
				List<Document> attachments= po.getDocuments(); ;
				for (int i=0; i< attachments.size(); i++) {
					Document att = attachments.get(i);
					String filename = URLEncoder.encode(att.getFilename(), "UTF-8");
					MimeBodyPart attachmentBP = new MimeBodyPart();
					attachmentBP.setFileName(filename);
					attachmentBP.setDisposition(Part.ATTACHMENT);
					DataSource src =  new ByteArrayDataSource (att.getData(), att.getContentType()); 
					DataHandler handler = new DataHandler (src);
					attachmentBP.setDataHandler(handler); 
					mp.addBodyPart(attachmentBP);
				}
			}
			dao.writeEvent ("Sending notification to " + operator.getOpName() + ".", 
					"Message", 
					po.getId());
		}
		else {
			subject = "Dispatch: " + commandStr + ": Notes= " + notes;
			dao.writeEvent ("Sending notification to " + operator.getOpName() + ".", 
					"Message", 
					-1);
		}

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("bodr.env1.w@gmail.com", "Document Dispatch"));
		message.setReplyTo(new Address[] {new InternetAddress("po@" + SystemProperty.applicationId.get() + ".appspotmail.com", "Document Dispatch")});
		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(to, to));
		message.setSubject(subject);
		MimeBodyPart plainBody = new MimeBodyPart();
		plainBody.setContent(sb.toString(), "text/plain");
		plainBody.setFileName("plainbody.txt");
		_log.warning ("sb.toString(): " + sb.toString());
		mp.addBodyPart(plainBody);
		message.setText(sb.toString());
		message.setContent(mp);
		message.saveChanges();
		return message;
	}
}

