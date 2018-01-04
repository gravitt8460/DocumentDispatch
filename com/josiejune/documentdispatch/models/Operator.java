package com.josiejune.documentdispatch.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Cached;
@Cached
public class Operator extends DDEntity {

	private String opName;
	private String role;
	private String emailAddr;
	private String smsNumber;
	private List<String> keywords;
	private String lastHitKeyword;
	private int basketCapacity;
	private Date lastRoutedPOTimestamp = new Date();
	private Date lastRoutedUnmanagedOrderTimestamp = new Date();
	private int managedOrdersToday;
	private int unmanagedOrdersToday;
	private int completedOrdersToday;
	private boolean includeAttOnEmail = false;
	private boolean online = false;
	private float dwellTimeBetweenOrders;
	private User user;

	public Operator () {super();}

	public Operator (String opName, String role, String emailAddr, String googleAcct, String smsNumber, float dwellTimeBetweenOrders) {
		this();
		this.opName = opName;
		this.role = role;
		this.emailAddr = emailAddr;
		this.smsNumber = smsNumber;
		this.lastHitKeyword = new String(" ");
		this.dwellTimeBetweenOrders = dwellTimeBetweenOrders;
		this.managedOrdersToday = 0;
		this.lastRoutedPOTimestamp = new Date();
		this.online = false;
		this.user = new User(googleAcct, "documentdispatch");
	}

	public void setGoogleAcct (String googleAcct) {
		this.user = new User(googleAcct, "documentdispatch");
	}
	
	public void bringOnline () {
		this.online = true;
	}

	public void takeOffline () {
		this.online = false;
	}

	public void toggleOnline() {
		this.online = !this.online;
	}

	public boolean isOnline() {
		return this.online;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
		setUser (new User(emailAddr, "documentdispatch"));
	}

	public String getSmsNumber() {
		return smsNumber;
	}

	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	public List<String> getKeywords() {
		if (keywords == null) {
			keywords = new ArrayList<String>();
		}
		return keywords;
	}

	public void addKeyword(String keyword) {
		if (this.keywords == null) {
			this.keywords = new ArrayList<String>();
		}
		this.keywords.add (keyword);
		save();
	}

	public void removeKeyword(String keyword) {
		if (this.keywords == null) {
			this.keywords = new ArrayList<String>();
			return;
		}
		this.keywords.remove (keyword);
	}


	public String getLastHitKeyword() {
		return lastHitKeyword;
	}

	public void setLastHitKeyword(String lastHitKeyword) {
		this.lastHitKeyword = lastHitKeyword;
	}

	public int getBasketCapacity() {
		return basketCapacity;
	}

	public void setBasketCapacity(int basketCapacity) {
		this.basketCapacity = basketCapacity;
	}

	public Date getLastRoutedPOTimestamp() {
		return lastRoutedPOTimestamp;
	}

	public void setLastRoutedPOTimestamp(Date lastRoutedPOTimestamp) {
		this.lastRoutedPOTimestamp = lastRoutedPOTimestamp;
	}

	public void routingUnmanagedOrder (int numOrders) {
		this.setUnmanagedOrdersToday(this.getUnmanagedOrdersToday() + numOrders);
		setLastRoutedUnmanagedOrderTimestamp(new Date());
		setLastRoutedPOTimestamp(new Date());
	}

	public void routingManagedOrder (int numOrders) {
		this.setManagedOrdersToday(this.getManagedOrdersToday() + numOrders);
		setLastRoutedPOTimestamp(new Date());
	}

	public int getManagedOrdersToday() {
		return managedOrdersToday;
	}

	public void setManagedOrdersToday(int managedOrdersToday) {
		this.managedOrdersToday = managedOrdersToday;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<DDMessage> getMessages() {
		DAO dao = new DAO();
		return dao.getMessages(this);
	}

	public int getUnmanagedOrdersToday() {
		return unmanagedOrdersToday;
	}

	public void setUnmanagedOrdersToday(int unmanagedOrdersToday) {
		this.unmanagedOrdersToday = unmanagedOrdersToday;
	}

	public void completedOrder () {
		this.completedOrdersToday++;
	}

	public int getCompletedOrdersToday() {
		return completedOrdersToday;
	}

	public void setCompletedOrdersToday(int completedOrdersToday) {
		this.completedOrdersToday = completedOrdersToday;
	}

	public float getDwellTimeBetweenOrders() {
		return dwellTimeBetweenOrders;
	}

	public void setDwellTimeBetweenOrders(float maxHourlyRate) {
		this.dwellTimeBetweenOrders = maxHourlyRate;
	}

	public Date getLastRoutedUnmanagedOrderTimestamp() {
		return lastRoutedUnmanagedOrderTimestamp;
	}

	public void setLastRoutedUnmanagedOrderTimestamp(
			Date lastRoutedUnmanagedOrderTimestamp) {
		this.lastRoutedUnmanagedOrderTimestamp = lastRoutedUnmanagedOrderTimestamp;
	}

	public boolean isIncludeAttOnEmail() {
		return includeAttOnEmail;
	}

	public void setIncludeAttOnEmail(boolean includeAttOnEmail) {
		this.includeAttOnEmail = includeAttOnEmail;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
