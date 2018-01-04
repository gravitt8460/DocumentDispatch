package com.josiejune.documentdispatch.models;

import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Cached;

@Cached
public class DailyStatus extends DDEntity {
	 	
	 	int totalManagedReceived;
	 	int totalUnmanagedReceived;
	 	int totalManagedCompleted;
	 	int totalUnmanagedCompleted;
	 	
	 	@Transient DAO dao = new DAO();
	 	
		public DailyStatus() {}
	    
	    public int getTotalManagedReceived() {
	    	return totalManagedReceived;
	    }
	    
	    public int getTotalManagedCompleted() {
	    	return totalManagedCompleted;
	    }

	    public void reset () {
	    	setTotalUnmanagedReceived(0);
	    	setTotalManagedReceived(0);
	    	setTotalUnmanagedCompleted(0);
	    	setTotalManagedCompleted(0);
	    	save();
	    }
	    
		public void save () {
			dao.ofy().put(this);
		}

		public int getTotalUnmanagedReceived() {
			return totalUnmanagedReceived;
		}

		public void setTotalUnmanagedReceived(int totalUnmanagedReceived) {
			this.totalUnmanagedReceived = totalUnmanagedReceived;
		}

		public int getTotalUnmanagedCompleted() {
			return totalUnmanagedCompleted;
		}

		public void setTotalUnmanagedCompleted(int totalUnmanagedCompleted) {
			this.totalUnmanagedCompleted = totalUnmanagedCompleted;
		}

		public void setTotalManagedReceived(int totalManagedReceived) {
			this.totalManagedReceived = totalManagedReceived;
		}

		public void setTotalManagedCompleted(int totalManagedCompleted) {
			this.totalManagedCompleted = totalManagedCompleted;
		}
}
