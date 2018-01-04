package com.josiejune.documentdispatch.models;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Cached;

@Cached
public abstract class DDEntity {

	@Id Long id;
	private Date createdDate;
	private Date modifiedTimestamp;
	
	@Transient DAO dao = new DAO();
	
	public DDEntity () {
		this.createdDate = new Date();
		this.modifiedTimestamp = new Date();
	}
	
	public Long getId() {
		if (id == null) {
			save();
		}
		return id;
	}
	
	public void save () {
		this.modifiedTimestamp = new Date();
		dao.ofy().put(this);
	}
	
	@SuppressWarnings("rawtypes")
	public Key getKey () {
		if (id == null) {
			save();
		}
		return ObjectifyService.factory().getKey(this);
	}
	
	public void setModifiedTimestamp(Date modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}
	
	public Date getModifiedTimestamp() {
		return modifiedTimestamp;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	
}
