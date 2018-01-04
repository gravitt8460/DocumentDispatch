package com.josiejune.documentdispatch.models;

import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
@Cached
public class Event {

	@Id Long id;
	private Date eventTimestamp;
	private String eventText = "";
	private String itemType = "";
	private long itemId;

	public Event () {super();}
	
	public Event (String eventText, String itemType, long itemId) {
	
		this.eventText = eventText;
		this.eventTimestamp = new Date();
		this.itemType = itemType;
		this.itemId = itemId;
	}

	public long getItemId() {
		return itemId;
	}
	
	public Date getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(Date eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getEventText() {
		return eventText;
	}

	public void setEventText(String eventText) {
		this.eventText = eventText;
	}


	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
}
