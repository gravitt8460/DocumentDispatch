package com.josiejune.documentdispatch.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

import com.josiejune.documentdispatch.handlers.ExceptionHandler;

public class SearchJanitorUtils {
	
	
//	private static final Logger log = Logger.getLogger(SearchJanitorUtils.class.getName());
	
	/** From StopAnalyzer Lucene 2.9.1 */
	public final static String[] stopWords = new String[]{
	  	    "a", "an", "and", "are", "as", "at", "be", "but", "by",
		    "for", "if", "in", "into", "is", "it",
		    "no", "not", "of", "on", "or", "such",
		    "that", "the", "their", "then", "there", "these",
		    "they", "this", "to", "was", "will", "with"
		  };
	
	/**
	 * Uses english stemming (snowball + lucene) + stopwords for getting the words.
	 * 
	 * @param index
	 * @return
	 */
	public static Set<String> getTokensForIndexingOrQuery(
			String index_raw,
			int maximumNumberOfTokensToReturn) {
		
		String indexCleanedOfHTMLTags = index_raw.replaceAll("\\<.*?>"," ");
		
		
		Set<String> returnSet = new HashSet<String>();
		
		Analyzer analyzer =  new SnowballAnalyzer(
				org.apache.lucene.util.Version.LUCENE_30,
				"English",
				stopWords);		
	      
		TokenStream tokenStream = analyzer.tokenStream(
				"content", 
				new StringReader(indexCleanedOfHTMLTags));
		
		try {
			while (tokenStream.incrementToken() 
				&& (returnSet.size() < maximumNumberOfTokensToReturn)) {
				TermAttribute a = tokenStream.getAttribute(TermAttribute.class);
				returnSet.add(a.term());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.handleException(e);
		}
	
		return returnSet;
		
		
	}
	
	
	
	
}
