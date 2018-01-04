package com.josiejune.documentdispatch.servlets.views;

import java.util.Date;

import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

public class FormatUtil {
	
//	public static String formatDate (Date date) {
//		
//		SimpleDateFormat formatter;
//
//		formatter = new SimpleDateFormat("MM/dd h:mm a", Locale.US);
//		
//		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
//		return formatter.format(date);
//	}
//	
	public static DateTimeValue getDateTimeValue (Date date) {
		GregorianCalendar gcCreated = (GregorianCalendar) 
		Calendar.getInstance(new ULocale("en_US@@calendar=gregorian"));

		java.util.Calendar calCreated = java.util.Calendar.getInstance();
		calCreated.setTime(date);

		gcCreated.set(Calendar.YEAR, calCreated.get(java.util.Calendar.YEAR));
		gcCreated.set(Calendar.DAY_OF_MONTH, calCreated.get(java.util.Calendar.DAY_OF_MONTH));
		gcCreated.set(Calendar.HOUR_OF_DAY, calCreated.get(java.util.Calendar.HOUR_OF_DAY));
		gcCreated.set(Calendar.MINUTE, calCreated.get(java.util.Calendar.MINUTE));
		gcCreated.set(Calendar.SECOND, calCreated.get(java.util.Calendar.SECOND));
		gcCreated.set(Calendar.MILLISECOND, calCreated.get(java.util.Calendar.MILLISECOND));

		gcCreated.setTime(date);

		int hourCreated = calCreated.get(java.util.Calendar.HOUR_OF_DAY);

		DateTimeValue dtvCreated = new DateTimeValue(
				calCreated.get(java.util.Calendar.YEAR),
				calCreated.get(java.util.Calendar.MONTH),
				calCreated.get(java.util.Calendar.DAY_OF_MONTH),
				hourCreated,
				calCreated.get(java.util.Calendar.MINUTE),
				calCreated.get(java.util.Calendar.SECOND),
				calCreated.get(java.util.Calendar.MILLISECOND));

		dtvCreated.getObjectToFormat().setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return dtvCreated;
	}
}
