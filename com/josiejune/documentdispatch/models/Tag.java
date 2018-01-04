package com.josiejune.documentdispatch.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
@Cached
public class Tag extends DDEntity  {

	private  String tag;
	private Key<DDMessage> parent;
	
	public Tag () {super();}
	
	public Tag (Key<DDMessage> msg, String tag) {
		setParent(msg);
		setTag(tag);
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Key<DDMessage> getParent() {
		return parent;
	}
	public void setParent(Key<DDMessage> parent) {
		this.parent = parent;
	}
}
