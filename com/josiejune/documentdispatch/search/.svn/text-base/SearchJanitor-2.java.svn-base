package com.josiejune.documentdispatch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.googlecode.objectify.Query;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Document;

public class SearchJanitor {

	private static final Logger log = Logger.getLogger(SearchJanitor.class.getName());

	public static final int MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH = 5;

	public static final int MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX = 5000;

	public static List<Document> searchDocuments (String queryString) {

		DAO dao = new DAO();

		Query<Document> query = dao.ofy().query(Document.class);

		Set<String> queryTokens = SearchJanitorUtils.getTokensForIndexingOrQuery(queryString, MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);

		List<String> parametersForSearch = new ArrayList<String>(queryTokens);

		for (String token : parametersForSearch) {
			query.filter("fts", token);
		}

		List<Document> result = null;

		try {
			result = new ArrayList<Document>();
			for (Document doc : query) {
				StringBuffer sb = new StringBuffer("");
				for (String s : doc.getFts()) {
					sb.append(s + " ");
				}
				//log.warning ("Adding document with following fts: " + sb.toString());
				result.add(doc);
			}

		} catch (DatastoreTimeoutException e) {
			log.severe(e.getMessage());
			log.severe("datastore timeout at: " + queryString);
		} catch (DatastoreNeedIndexException e) {
			log.severe(e.getMessage());
			log.severe("datastore need index exception at: " + queryString);
		}

		return result;

	}

	public static void updateFTSStuffForDocument(Document document) {

		StringBuffer sb = new StringBuffer();

		sb.append(document.getTextContents());
	//	log.warning ("sb contents: " + sb.toString());
		
		Set<String> new_ftsTokens = SearchJanitorUtils.getTokensForIndexingOrQuery(sb.toString(), MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);

		Set<String> ftsTokens = document.getFts();

		ftsTokens.clear();

		for (String token : new_ftsTokens) {
		//	log.warning ("adding token to document: " + document.getId() + ": " + token);
			ftsTokens.add(token);

		}
	}

}
