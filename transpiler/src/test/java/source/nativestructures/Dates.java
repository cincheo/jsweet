package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class Dates {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		TimeZone tz1 = TimeZone.getTimeZone("UTC");
		TimeZone tz2 = TimeZone.getDefault();
		trace.push(""+tz1.getID().equals(tz2.getID()));
		Calendar utcCalendar = new java.util.GregorianCalendar(TimeZone.getTimeZone("UTC"));
		utcCalendar.setTime(new Date());
		utcCalendar.setTimeInMillis(114937200000L);
		Calendar convertedCalendar = new java.util.GregorianCalendar();
		convertedCalendar.set(java.util.Calendar.YEAR, utcCalendar.get(java.util.Calendar.YEAR));
		convertedCalendar.set(java.util.Calendar.MONTH, utcCalendar.get(java.util.Calendar.MONTH));
		convertedCalendar.set(java.util.Calendar.DAY_OF_MONTH, utcCalendar.get(java.util.Calendar.DAY_OF_MONTH));
		convertedCalendar.set(java.util.Calendar.HOUR_OF_DAY, utcCalendar.get(java.util.Calendar.HOUR_OF_DAY));
		convertedCalendar.set(java.util.Calendar.MINUTE, utcCalendar.get(java.util.Calendar.MINUTE));
		convertedCalendar.set(java.util.Calendar.SECOND, utcCalendar.get(java.util.Calendar.SECOND));
		convertedCalendar.set(java.util.Calendar.MILLISECOND, utcCalendar.get(java.util.Calendar.MILLISECOND));

		trace.push(""+utcCalendar.getTimeInMillis());
		trace.push(""+convertedCalendar.get(Calendar.YEAR));
		trace.push(""+convertedCalendar.get(Calendar.MONTH));
		trace.push(""+convertedCalendar.get(Calendar.DAY_OF_MONTH));
		trace.push(""+convertedCalendar.get(Calendar.HOUR_OF_DAY));
		trace.push(""+convertedCalendar.get(Calendar.MINUTE));
		trace.push(""+convertedCalendar.get(Calendar.SECOND));
		
		$export("trace", trace.join(","));

	}

}
