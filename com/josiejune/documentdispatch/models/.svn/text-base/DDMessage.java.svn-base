package com.josiejune.documentdispatch.models;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.message.SimpleContentHandler;
import org.apache.james.mime4j.dom.field.Field;
import org.apache.james.mime4j.parser.MimeStreamParser;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.Status;

@Cached
public class DDMessage extends DocumentGroup {

	//@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(DDMessage.class.getName());
	@Transient private DAO dao = new DAO();
	@Transient private DDMessageContentHandler contentHandler;

	private String from;
	private Date routedForResolutionDate;
	private Date resolvedDate;
	private Date completedDate;
	private String completedBy;
	private String salesOrderId;
	private String keyword = "";
	private Key<Operator> parent;
	private boolean managed;
	private String notes="";
	private Set<String> tags = new HashSet<String>();
	private int workCount=1;
	private String emailMsgId;
	private String msgSubject;

	public DDMessage() {
		super();
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		if (this.from == null) {
			return "";
		}
		String fullAddress = from.toString();
		int indexOfLessThan = fullAddress.indexOf("<");
		int indexOfGreaterThan = fullAddress.indexOf(">");
		return fullAddress.substring(indexOfLessThan+1, indexOfGreaterThan);
	}

	public boolean isReady () {
		for (Document doc : getDocuments()) {
			if (! doc.getStatus().equals(Status.READY)) {
				return false;
			}
		}
		return true;
	}

	public Set<String> getTokens () {
		Set<String> tokens = new HashSet<String>();
		for (Document doc : getDocuments()) {
			tokens.addAll(doc.getFts());
		}
		return tokens;
	}

	public void setParent(Key<Operator> parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Operator getParent () {
		if (this.parent == null) {
			setParent (dao.getNullOperator().getKey());
			save();
		}
		return dao.ofy().get(this.parent);
	}

	public Date getRoutedForResolutionDate() {
		return routedForResolutionDate;
	}

	public void setRoutedForResolutionDate(Date routedForResolutionDate) {
		this.routedForResolutionDate = routedForResolutionDate;
	}

	public Date getResolvedDate() {
		return resolvedDate;
	}

	public void setResolvedDate(Date resolvedDate) {
		this.resolvedDate = resolvedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getCompletedBy() {
		return completedBy;
	}

	public String getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(String salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}

	public int getWorkCount() {
		return workCount;
	}

	public void setWorkCount(int workCount) {
		this.workCount = workCount;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void parse(InputStream inputStream) throws MimeException, IOException {
		setStatus (Status.PROCESSING);
		MimeStreamParser parser = new MimeStreamParser();
		parser.setContentDecoding(true);
		contentHandler = new DDMessageContentHandler();
		parser.setContentHandler(contentHandler);
		parser.parse(new BufferedInputStream(inputStream));

		for (Document doc : getDocuments()) {
			doc.parse();
		}
	}

	public void childUpdated() {

		if (isReady()) {
			// all documents are ready, process message
			setStatus (Status.PROCESSING);
			Queue queue = QueueFactory.getQueue("process-message");
			queue.add(withUrl("/processmessage")
					.param("msgId", String.valueOf(this.getId()))
					.method(POST));
			return;
		}

		else {
			for (Document doc : getDocuments()) {
				if (doc.getStatus().startsWith(Status.LIMBO)) {
					setStatus (Status.LIMBO);
				}
			}
		}
	}


	public Set<String> getTags () {
		return tags;
	}

	public void addTag (String tag) {
		tags.add(tag);
	}

	public boolean isManaged() {
		return managed;
	}

	public void setManaged(boolean managed) {
		this.managed = managed;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getEmailMsgId() {
		return emailMsgId;
	}

	public void setEmailMsgId(String emailMsgId) {
		this.emailMsgId = emailMsgId;
	}

	public String getMsgSubject() {
		return msgSubject;
	}

	public void setMsgSubject(String msgSubject) {
		this.msgSubject = msgSubject;
	}

	class DDMessageContentHandler extends SimpleContentHandler {

		StringBuffer sbMetadata = new StringBuffer("");
		String subject = "";
		//StringBuffer bodyContent = new StringBuffer("");

		@Override
		public void body(BodyDescriptor bd, InputStream is)
		throws IOException {

			if (bd.getContentTypeParameters().containsKey("name")) {
				try {

					String filename = bd.getContentTypeParameters().get("name");
					String mimeType = null;
					if (filename.endsWith(".pdf")) {
						mimeType = "application/pdf";
					}
					else {
						mimeType = bd.getMimeType();
					}
					addDocument(filename, 
							mimeType, bd.getTransferEncoding(),
							bd.getCharset(),
							IOUtils.toByteArray(is));
				} catch (InterruptedException e) {
					ExceptionHandler.handleException(e);
				}
			}
			else {
				String lineSep = System.getProperty("line.separator");
				if (bd.getMimeType().equals("text/html")) {
					lineSep = "<br/>";
				}
				sbMetadata.append("*************   New Body Decoded   ***************" + lineSep);
				sbMetadata.append("MIME Type: " + bd.getMimeType() + lineSep);
				sbMetadata.append("Media Type: " + bd.getMediaType() + lineSep);
				sbMetadata.append("Sub Type: " + bd.getSubType() + lineSep);
				sbMetadata.append("Transfer Encoding: " + bd.getTransferEncoding() + lineSep);
				sbMetadata.append("Content Length: " + bd.getContentLength() + lineSep);
				sbMetadata.append("Charset: " + bd.getCharset() + lineSep);

				Map<String, String> contentTypeHeaders = bd.getContentTypeParameters();
				Iterator<String> iter = contentTypeHeaders.keySet().iterator();
				sbMetadata.append("Content Type Parameters:"+ lineSep);
				while (iter.hasNext()) {
					String key = iter.next();
					sbMetadata.append(key + ": " + contentTypeHeaders.get(key) + lineSep);
				}

				// assume it's not an attachment and can be interpreted as text
				//	subject = bd.
				String filename = null ;
				if (bd.getMimeType().equals("text/plain")) {
					filename = "message" + getDocuments().size() + ".txt";
				}
				else if (bd.getMimeType().equals("text/html")) {
					filename = "message" + getDocuments().size() + ".html";
				}
				else {
					filename = "file";
				}
				
				try {
					//is = MimeUtility.decode(is, "base64");
					addDocument(filename, bd.getMimeType(), bd.getCharset(), bd.getTransferEncoding(), IOUtils.toByteArray(is));
				} catch (InterruptedException e) {
					ExceptionHandler.handleException(e);
				} 
			}
		}

		@Override
		public void headers(Header header) {

			List<Field> fields = header.getFields();
			sbMetadata.append("Message Headers:" + System.getProperty("line.separator"));

			for (int i=0; i< fields.size(); i++) {
				Field field = fields.get(i);
				sbMetadata.append(field.getName());
				_log.warning ("Header Found: " + field.getName() + "; " + field.getBody());
				sbMetadata.append(":" + field.getBody() + System.getProperty("line.separator"));
				if (field.getName().equals("From")) {
					setFrom(field.getBody());
				}
				else if (field.getName().equals("Message-Id")) {
					setEmailMsgId(field.getBody());
				}
				else if (field.getName().equals("Subject")) {
					setMsgSubject(field.getBody());
				}

			}
		}

		public void endMessage() {
			try {
				addDocument("MessageMetadata.txt", "text/plain", "", "utf-8", sbMetadata.toString().getBytes());
			} catch (IOException e) {
				ExceptionHandler.handleException(e);
			} catch (InterruptedException e) {
				ExceptionHandler.handleException(e);
			}
		}

	}
}


