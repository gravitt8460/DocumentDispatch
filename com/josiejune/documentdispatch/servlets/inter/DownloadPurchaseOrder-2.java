package com.josiejune.documentdispatch.servlets.inter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;

import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.DDMessage;
import com.josiejune.documentdispatch.models.Document;

public class DownloadPurchaseOrder extends HttpServlet {

	private static final long serialVersionUID = -7133358560884201822L;

	//@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(DownloadPurchaseOrder.class.getName());
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
			//String opId = request.getParameter("opId");
			String msgId = request.getParameter("msgId");
			String fileNum = request.getParameter("fileNum");

			DAO dao = new DAO();
			DDMessage po = dao.getMessage(Long.valueOf(msgId));

			if (po != null) {
				List<Document> files = po.getDocuments();
				Document g = files.get(Integer.valueOf(fileNum));

				// To encode the correct UTF-8 downloaded filename
				String fileName = g.getFilename();	    	
				fileName = encodeFileName(fileName, request.getHeader("User-Agent"));

				// Display in the browser or pop up the save-as dialog according to the file type
				response.setHeader("Content-Disposition", "inline;filename=" + fileName);
				// Force the browser to download the file (i.e. pop up the save-as dialog)
				//response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

				// Set the ContentLength
				response.setContentLength(g.getFileSize());
				_log.warning ("response: " + response);
				//response.setCharacterEncoding(g.getTransferEncoding());
			
				// Set the ContentType as a binary file
				//resp.setContentType("application/octet-stream");
				// Set the ContentType from the datastore
//				String mimeType = g.getContentType();
//				String charSet = g.getC
				String contentType = g.getContentType();
				if (contentType == null) {
					response.setContentType("application/octet-stream");
				} else {
					response.setContentType(g.getContentType());
				}

				dao.writeEvent("File Downloaded", "Document", g.getId());
				
				OutputStream o = response.getOutputStream();
				IOUtils.copy(g.getInputStream(), o);
				o.close();
			}
			else {
				// No entities in Google datastore
				PrintWriter out = response.getWriter();
				out.print("ID \""+msgId+"\" not exists.");
				out.close();
			}
		}
		catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}
	}

	/**
	 * @param fileName 	The UTF-8 filename.
	 * @param userAgent The User-Agent in HTTP request headers.
	 * 
	 * @return The encoded filename corresponding to different browsers.
	 * 
	 * @throws IOException
	 */
	private String encodeFileName(String fileName, String userAgent) throws IOException {
		if (null != userAgent && -1 != userAgent.indexOf("MSIE")) {
			fileName = fileName.replace(" ", "_");
			// UTF-8 URL encoding only works in IE
			return URLEncoder.encode(fileName, "UTF-8");
			//return "%E7%B4%AB%E8%91%89%E6%A7%AD.jpg"
		} else if (null != userAgent && -1 != userAgent.indexOf("Mozilla")) {
			// Base64 encoding works in Firefox
			return "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
			//return "=?UTF-8?B?57Sr6JGJ5qetLmpwZw==?="
		} else {
			return fileName;
		}
	}
}
