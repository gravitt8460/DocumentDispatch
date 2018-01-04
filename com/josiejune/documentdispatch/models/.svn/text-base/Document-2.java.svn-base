package com.josiejune.documentdispatch.models;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.pdfbox.io.RandomAccessBuffer;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Cached;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.handlers.Status;
import com.josiejune.documentdispatch.search.SearchJanitor;
@Cached
public class Document extends DDEntity {
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(Document.class.getName());


	private String filename = null;
	private String contentType = null;
	private String textContents;	
	private int filesize;
	private String transferEncoding;
	private String charSet;
	private String status = Status.NEW;
	private byte[] data ;
	//private boolean includeInWorkCount = true;
	private Set<String> fts = new HashSet<String>();
	private int fileNum;

	private Key<DocumentGroup> parent;

	@Transient private DAO dao = new DAO();

	public Document() {super();}

	private Document (String filename, String contentType, String transferEncoding, String charSet, byte[] data) throws IOException {
		this ();
		save();
		setFilename (filename);
		setContentType (contentType);
		setTransferEncoding(transferEncoding);
		setCharSet(charSet);
		setData (data);
	}

	public int getFileNum() {
		return fileNum;
	}
	
	private void setFileNum() {
		fileNum = getParent().getDocuments().size() + 1;
	}

	public static Document factory (DocumentGroup dg, String filename, String contentType, String transferEncoding, String charSet,
			byte[] data) throws IOException, InterruptedException  {

		Document doc = new Document (filename, contentType, transferEncoding, charSet, data);
		doc.setParent(dg);
		doc.save();
		doc.setFileNum ();
		doc.save();
		return doc;
	}
	
	public void parse () {
		
		setStatus(Status.PROCESSING);
		
		DocumentParser dp = new DocumentParser();
		
		try {
			setTextContents(dp.getFileContents(getFilename(), getInputStream()));
		}
		catch (Exception e) {
			ExceptionHandler.handleException(e);
			setStatus (Status.LIMBO);
		}
		
		if (getContentType().startsWith("text")) {
			setStatus(Status.READY);
			return;
		}
		
		else if (getTextContents() == null || getTextContents().trim().equals("")) {
			setStatus(Status.PROCESSING);
			
			Queue queue = QueueFactory.getQueue("ocr-queue");
	        queue.add(withUrl("/submitocr")
	        		.param("docId", String.valueOf(getId()))
	        		.method(POST));
	        return;
		}
		setStatus(Status.READY);
	}

	public void setParent (DocumentGroup dg) {
		if (dg == null) {
			return;
		}
		dg.save();
			
		this.parent = ObjectifyService.factory().getKey(dg);
	}

	public DocumentGroup getParent () {
		return dao.ofy().get(parent);
	}

	public String getTextContents() {
		return textContents;
	}

	public void setOCRContents (String textContents) {
		setTextContents (textContents);
		setStatus (Status.READY);
		
	}

	public void setTextContents(String textContents) {
		if (textContents == null) {
			textContents = new String("");
		}
		this.textContents = textContents;
		contentForSearch();
	}
	
    public void contentForSearch() {
        SearchJanitor.updateFTSStuffForDocument(this);
    }

	public void setFts(Set<String> fts) {
		this.fts = fts;
	}

	public Set<String> getFts() {
		return fts;
	}

	public String getFilename() {
		return filename;
	}

	public int getFilesize() {
		return filesize;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getData ()  {
		return data;
	}

	public void setData (byte[] data){
//		HttpParams params = new BasicHttpParams();
//		HttpClient httpClient = new DefaultHttpClient(new GAEConnectionManager(), params);
//		
//		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
//		String uploadUrl = blobstoreService.createUploadwithUrl("/");
//		HttpPost post = new HttpPost(uploadUrl);
//
//		HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
//		//paramsBean.setUserAgent("APIClient:bdrenv");
//		post.setParams(params);
//		
//
//		MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		mpEntity.addPart("upload", new InputStreamKnownSizeBody(new ByteArrayInputStream(body), body.length, contentType, filename));
//
//		post.setEntity(mpEntity);
//		HttpResponse response = httpClient.execute(post);
//		HttpEntity entity = response.getEntity();
//		String responseString = EntityUtils.toString(entity);
//		

	//	this.data = Base64.encodeBase64(data);
		
//		if (getTransferEncoding().equals("quoted-printable")) {
//			QuotedPrintableCodec codec = null;
//			if (getCharSet() == null) {
//				codec = new QuotedPrintableCodec ();
//			}
//			else {
//				codec = new QuotedPrintableCodec (getCharSet());
//			}
//			try {
//				this.data = codec.decode (data);
//			} catch (DecoderException e) {
//				// TODO Auto-generated catch block
//				ExceptionHandler.handleException(e);
//			}
//		}
//		else {
		this.data = data;
		//}
		this.filesize = data.length;
	}
	
	public InputStream getInputStream () {
		return new ByteArrayInputStream(getData());
	}

	public int getFileSize() {
		return data.length;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		save();
		if (this.parent != null) {
			getParent().childUpdated();
		}	
	}

//	public boolean isIncludeInWorkCount() {
//		return includeInWorkCount;
//	}

//	public void setIncludeInWorkCount(boolean includeInWorkCount) {
//		this.includeInWorkCount = includeInWorkCount;
//	}

	public String getTransferEncoding() {
		return transferEncoding;
	}

	public void setTransferEncoding(String transferEncoding) {
		this.transferEncoding = transferEncoding;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	class DocumentParser {

		public DocumentParser() {super();}
		
		public String getFileContents ( String filename, InputStream resource) throws IOException, InterruptedException {
			if (filename == null || resource == null) {
				return null;
			}

			String ext = filename.substring(filename.lastIndexOf('.')+1, filename.length());
			if (ext == null) {
				return "";
			}
			else if (ext.equalsIgnoreCase("pdf")) {
				return getPDFContents (resource);
			}
			else if (ext.equalsIgnoreCase("xls") ||
					ext.equalsIgnoreCase("doc") ||
					ext.equalsIgnoreCase("docx") ||
					ext.equalsIgnoreCase("xlsx") ||
					ext.equalsIgnoreCase("msg") ||
					ext.equalsIgnoreCase("ppt") ||
					ext.equalsIgnoreCase("pptx")) {
				return getMSFileContents (resource) ;
			}
			else if (ext.equalsIgnoreCase("txt") ||
					ext.equalsIgnoreCase("htm") ||
					ext.equalsIgnoreCase("html")) {
				StringWriter writer = new StringWriter();
				IOUtils.copy(resource, writer);
				return writer.toString();
			}
			return "";
		}

		private String getPDFContents (InputStream resource) throws IOException {
			RandomAccessBuffer tempMemBuffer = new RandomAccessBuffer();
			PDDocument doc = PDDocument.load(resource, tempMemBuffer);
			PDFTextStripper sa = new PDFTextStripper();
			String contents =  sa.getText(doc);
			return contents;
//			PdfReader reader = new PdfReader(resource);
//			StringBuffer sb = new StringBuffer("");
//			for (int i=0; i< reader.getNumberOfPages(); i++) {
//				sb.append(PdfTextExtractor.getTextFromPage(reader, i));
//			}
//			return sb.toString();
		}

		private String getMSFileContents (InputStream resource) throws IOException {
			StringBuffer sb = new StringBuffer ("");
			POIFSFileSystem fileSystem = new POIFSFileSystem(resource);
			// Firstly, get an extractor for the Workbook
			POIOLE2TextExtractor oleTextExtractor =  ExtractorFactory.createExtractor(fileSystem);
			// Then a List of extractors for any embedded Excel, Word, PowerPoint
			// or Visio objects embedded into it.
			POITextExtractor[] embeddedExtractors =
				ExtractorFactory.getEmbededDocsTextExtractors(oleTextExtractor);
			for (POITextExtractor textExtractor : embeddedExtractors) {
				// If the embedded object was an Excel spreadsheet.
				if (textExtractor instanceof ExcelExtractor) {
					ExcelExtractor excelExtractor = (ExcelExtractor) textExtractor;
					sb.append(excelExtractor.getText());
				}
				// A Word Document
				else if (textExtractor instanceof WordExtractor) {
					WordExtractor wordExtractor = (WordExtractor) textExtractor;
					String[] paragraphText = wordExtractor.getParagraphText();
					for (String paragraph : paragraphText) {
						sb.append(paragraph);
					}
					// Display the document's header and footer text
					sb.append(wordExtractor.getFooterText());
					sb.append(wordExtractor.getHeaderText());
				}
				// PowerPoint Presentation.
				else if (textExtractor instanceof PowerPointExtractor) {
					PowerPointExtractor powerPointExtractor =
						(PowerPointExtractor) textExtractor;
					sb.append(powerPointExtractor.getText());
					sb.append(powerPointExtractor.getNotes());
				}
				// Visio Drawing
				else if (textExtractor instanceof VisioTextExtractor) {
					VisioTextExtractor visioTextExtractor = 
						(VisioTextExtractor) textExtractor;
					sb.append(visioTextExtractor.getText());
				}
			}
			sb.append(oleTextExtractor.getText());
			return sb.toString();
		}
	}
}
