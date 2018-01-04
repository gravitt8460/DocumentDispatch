package com.josiejune.documentdispatch.handlers;

import java.util.ArrayList;
import java.util.List;

import com.josiejune.documentdispatch.models.Operator;

public class SearchResults {
	
	private Operator operator;
	private List<String> keywords;
	private boolean managedOrder = false;
	
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	
	public void addKeyword (String keyword) {
		if (keywords == null) {
			keywords = new ArrayList<String>();
		}
		keywords.add(keyword);
	}
	
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	public boolean isManagedOrder() {
		return managedOrder;
	}
	public void setManagedOrder(boolean managedOrder) {
		this.managedOrder = managedOrder;
	}
	

}
