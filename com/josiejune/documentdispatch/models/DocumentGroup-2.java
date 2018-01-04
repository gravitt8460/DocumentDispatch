package com.josiejune.documentdispatch.models;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.util.IOUtils;

import com.googlecode.objectify.annotation.Cached;
@Cached
public class DocumentGroup extends DDEntity {

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(DocumentGroup.class.getName());
	private String status = NEW;
	public static final String NEW = "NEW";
	
	public DocumentGroup () {
		super();
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status.equals(getStatus())) {
			return;
		}
		
		dao.writeEvent("Changing status from " + getStatus() + " to " + status + ".", "Message", getId());
		this.status = status;
		save();
	}
	
	public List<Document> getDocuments () {
		return dao.getDocuments(this);
	}
	
	public String getAllTextContents() {
		StringBuffer sb = new StringBuffer("");
		for (Document doc : getDocuments()) {
			sb.append(doc.getTextContents());
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}
	
	public void addDocument(String inFileName, String contentType, String transferEncoding, String charSet, byte[] data) throws IOException, InterruptedException {
		Document doc = Document.factory(this, inFileName, contentType, transferEncoding, charSet, data);
		doc.save();
	}

	public void addDocument(String inFileName, String contentType, String transferEncoding, String charSet, InputStream fileStream) throws IOException, InterruptedException {
		addDocument (inFileName, contentType, transferEncoding, charSet, IOUtils.toByteArray(fileStream));
	}

	public List<String> getFilenames() {
		List<String> filenames = new ArrayList<String>();
		for (Document doc : getDocuments()) {
			filenames.add(doc.getFilename());
		}
		return filenames;
	}

	public void childUpdated() {
	}
}