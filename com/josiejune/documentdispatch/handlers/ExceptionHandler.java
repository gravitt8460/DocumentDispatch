package com.josiejune.documentdispatch.handlers;

import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.appengine.api.utils.SystemProperty;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Document;
import com.josiejune.documentdispatch.models.StatusCapture;

public class ExceptionHandler {

	private static final Logger _log = Logger.getLogger(ExceptionHandler.class.getName());

	public static void handleException (Exception ex) {
		StringBuffer sb = new StringBuffer("");
		sb.append("Exception has occured in Document Dispatch: " + SystemProperty.applicationId.get() + System.getProperty("line.separator"));
		sb.append(ex.getClass().getSimpleName() + System.getProperty("line.separator") );
		sb.append(ex.getMessage() + System.getProperty("line.separator") );
		for (StackTraceElement element : ex.getStackTrace()) {
			sb.append(element.getClassName() + "; " + element.getMethodName() + "; " + 
					String.valueOf(element.getLineNumber()) + System.getProperty("line.separator"));
		}

		sendMail("bill.goodwin@burtsbees.com", 
				"Exception Occured in " + SystemProperty.applicationId.get() + ": " + ex.getClass().getSimpleName(), 
				sb.toString());

		_log.warning("Exception Message: " + sb.toString());
		
		DAO dao = new DAO();
		dao.writeEvent ("Exception Occurred: " + sb.toString(), "N/A", 0);

	}

	public static void handleException (Exception ex, StatusCapture sc) {
		StringBuffer sb = new StringBuffer("");
		//sb.append(sc.getLog().toString()+ System.getProperty("line.separator"));
		sb.append("Exception has occured in Document Dispatch" + System.getProperty("line.separator"));
		sb.append(ex.getClass().getSimpleName() + System.getProperty("line.separator") );
		sb.append(ex.getMessage() + System.getProperty("line.separator") );
		for (StackTraceElement element : ex.getStackTrace()) {
			sb.append(element.getClassName() + "; " + element.getMethodName() + "; " + 
					String.valueOf(element.getLineNumber()) + System.getProperty("line.separator"));
		}

		sendMail("bill.goodwin@burtsbees.com", 
				"Exception Occured in " + SystemProperty.applicationId.get() + ": " + ex.getClass().getSimpleName(), 
				sb.toString());

		_log.warning("Exception Message: " + sb.toString());
	}


	public static void sendMail (String to, String subject, String body) {

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("bodr.env1.w@gmail.com", "Document Dispatch"));
			message.setReplyTo(new Address[] {new InternetAddress("po@" + SystemProperty.applicationId.get() + ".appspotmail.com", "Document Dispatch")});
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to, to));
			message.setSubject(subject);
			message.setText(body);

			Multipart mp = new MimeMultipart();
			String plainText = new String (body);    

			MimeBodyPart plainBody = new MimeBodyPart();
			plainBody.setContent(plainText, "text/plain");
			mp.addBodyPart(plainBody);

			message.setContent(mp);
			message.saveChanges();
			Transport.send(message);
		}
		catch (Exception ex) {
			_log.warning ("Unable to send email.");
			_log.warning ("Ex: " + ex.getMessage());
			//ExceptionHandler.handleException(ex);
		}
	}

	public static void sendMailWithAttachments (String to, String subject, String body, List<Document> attachments) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("bodr.env1.w@gmail.com", "Document Dispatch"));
			message.setReplyTo(new Address[] {new InternetAddress("po@" + SystemProperty.applicationId.get() + ".appspotmail.com", "Document Dispatch")});
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to, to));
			message.setSubject(subject);
			message.setText(body);
			//message.addHeader("POID", "12345");

			Multipart mp = new MimeMultipart();


			MimeBodyPart plainBody = new MimeBodyPart();

			for (int i=0; i< attachments.size(); i++) {

				Document att = attachments.get(i);
				String filename = "";
				// To encode the correct UTF-8 downloaded filename    		
				filename = URLEncoder.encode(att.getFilename(), "UTF-8");

				MimeBodyPart attachmentBP = new MimeBodyPart();
				attachmentBP.setFileName(filename);
				attachmentBP.setDisposition(Part.ATTACHMENT);
				DataSource src = null;
				if (att.getData().length > 900000) {
					String linkToFile = "http://" + SystemProperty.applicationId.get() + 
					".appspot.com/download?poId=" + parsePoId(subject) + "&fileNum=" + i;

					body = body + System.getProperty("line.separator") + System.getProperty("line.separator") +
					"NOTE: An attachment named " + filename + " was too large to send in email.  You can download it " +
					"by clicking the link below (you may need to copy and paste to your browser)." + 
					System.getProperty ("line.separator") + linkToFile;
				}
				else {

					DataHandler handler = new DataHandler (src);
					attachmentBP.setDataHandler(handler); 
					mp.addBodyPart(attachmentBP);
				}
			}

			String plainText = new String (body);   
			plainBody.setContent(plainText, "text/plain");
			mp.addBodyPart(plainBody);

			message.setContent(mp);
			message.saveChanges();
			Transport.send(message);
		}
		catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}
	}

	private static String parsePoId (String messageBody) {

		Pattern p =  Pattern.compile("POID=\\d*\\D", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(messageBody);
		boolean result = m.find();
		if(result) {
			String matchLine = m.group();
			return matchLine.substring(5, matchLine.length()-1);
		}
		return "";
	}
}
