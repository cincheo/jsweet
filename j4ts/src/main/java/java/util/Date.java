/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.util;

import static def.dom.Globals.window;
import static jsweet.util.Globals.$apply;
import static jsweet.util.Globals.$new;

import java.io.Serializable;

/**
 * Represents a date and time.
 */
public class Date implements Cloneable, Comparable<Date>, Serializable {

	/**
	 * Encapsulates static data to avoid Date itself having a static
	 * initializer.
	 */
	private static class StringData {
		public static final String[] DAYS = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

		public static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
				"Nov", "Dec" };
	}

	public static long parse(String s) {
		double parsed = $apply(jsdateClass().$get("parse"), s);
		if (Double.isNaN(parsed)) {
			throw new IllegalArgumentException();
		}
		return (long) parsed;
	}

	// CHECKSTYLE_OFF: Matching the spec.
	public static long UTC(int year, int month, int date, int hrs, int min, int sec) {
		return (long) $apply(jsdateClass().$get("UTC"), year + 1900, month, date, hrs, min, sec, 0);
	}

	// CHECKSTYLE_ON

	/**
	 * Ensure a number is displayed with two digits.
	 *
	 * @return a two-character base 10 representation of the number
	 */
	protected static String padTwo(int number) {
		if (number < 10) {
			return "0" + number;
		} else {
			return String.valueOf(number);
		}
	}

	/**
	 * JavaScript Date instance.
	 */
	private final def.js.Object jsdate;

	static private def.js.Object jsdateClass() {
		return (def.js.Object) window.$get("Date");
	}

	public Date() {
		jsdate = $new(jsdateClass());
	}

	public Date(int year, int month, int date) {
		this(year, month, date, 0, 0, 0);
	}

	public Date(int year, int month, int date, int hrs, int min) {
		this(year, month, date, hrs, min, 0);
	}

	public Date(int year, int month, int date, int hrs, int min, int sec) {
		jsdate = $new(jsdateClass());
		$apply(jsdate.$get("setFullYear"), jsdate, year + 1900, month, date);
		$apply(jsdate.$get("setHours"), jsdate, hrs, min, sec, 0);
		fixDaylightSavings(hrs);
	}

	public Date(long date) {
		jsdate = new def.js.Date(date);
	}

	public Date(String date) {
		this(Date.parse(date));
	}

	public boolean after(Date when) {
		return getTime() > when.getTime();
	}

	public boolean before(Date when) {
		return getTime() < when.getTime();
	}

	public Object clone() {
		return new Date(getTime());
	}

	@Override
	public int compareTo(Date other) {
		return Long.compare(getTime(), other.getTime());
	}

	@Override
	public boolean equals(Object obj) {
		return ((obj instanceof Date) && (getTime() == ((Date) obj).getTime()));
	}

	public int getDate() {
		return (int) $apply(jsdate.$get("getDate"), jsdate);
	}

	public int getDay() {
		return (int) $apply(jsdate.$get("getDay"), jsdate);
	}

	public int getHours() {
		return (int) $apply(jsdate.$get("getHours"), jsdate);
	}

	public int getMinutes() {
		return (int) $apply(jsdate.$get("getMinutes"), jsdate);
	}

	public int getMonth() {
		return (int) $apply(jsdate.$get("getMonth"), jsdate);
	}

	public int getSeconds() {
		return (int) $apply(jsdate.$get("getSeconds"), jsdate);
	}

	public long getTime() {
		return (long) $apply(jsdate.$get("getTime"), jsdate);
	}

	public int getTimezoneOffset() {
		return (int) $apply(jsdate.$get("getTimezoneOffset"), jsdate);
	}

	public int getYear() {
		return (int) $apply(jsdate.$get("getFullYear"), jsdate) - 1900;
	}

	@Override
	public int hashCode() {
		long time = getTime();
		return (int) (time ^ (time >>> 32));
	}

	public void setDate(int date) {
		int hours = getHours();
		$apply(jsdate.$get("setDate"), jsdate, date);
		fixDaylightSavings(hours);
	}

	public void setHours(int hours) {
		$apply(jsdate.$get("setHours"), jsdate, hours);
		fixDaylightSavings(hours);
	}

	public void setMinutes(int minutes) {
		int hours = getHours() + minutes / 60;
		$apply(jsdate.$get("setMinutes"), jsdate, minutes);
		fixDaylightSavings(hours);
	}

	public void setMonth(int month) {
		int hours = getHours();
		$apply(jsdate.$get("setMonth"), jsdate, month);
		fixDaylightSavings(hours);
	}

	public void setSeconds(int seconds) {
		int hours = getHours() + seconds / (60 * 60);
		$apply(jsdate.$get("setSeconds"), jsdate, seconds);
		fixDaylightSavings(hours);
	}

	public void setTime(long time) {
		$apply(jsdate.$get("setTime"), jsdate, time);
	}

	public void setYear(int year) {
		int hours = getHours();
		$apply(jsdate.$get("setFullYear"), jsdate, year + 1900);
		fixDaylightSavings(hours);
	}

	public String toGMTString() {
		return $apply(jsdate.$get("getUTCDate"), jsdate) + " "
				+ StringData.MONTHS[(int) $apply(jsdate.$get("getUTCMonth"), jsdate)] + " "
				+ $apply(jsdate.$get("getUTCFullYear"), jsdate) + " "
				+ padTwo((int) $apply(jsdate.$get("getUTCHours"), jsdate)) + ":"
				+ padTwo((int) $apply(jsdate.$get("getUTCMinutes"), jsdate)) + ":"
				+ padTwo((int) $apply(jsdate.$get("getUTCSeconds"), jsdate)) + " GMT";
	}

	public String toLocaleString() {
		return jsdate.toLocaleString();
	}

	@Override
	public String toString() {
		// Compute timezone offset. The value that getTimezoneOffset returns is
		// backwards for the transformation that we want.
		int offset = -(int) getTimezoneOffset();
		String hourOffset = ((offset >= 0) ? "+" : "") + (offset / 60);
		String minuteOffset = padTwo(Math.abs(offset) % 60);

		return StringData.DAYS[(int) getDay()] + " " + StringData.MONTHS[(int) getMonth()] + " "
				+ padTwo((int) getDate()) + " " + padTwo((int) getHours()) + ":" + padTwo((int) getMinutes()) + ":"
				+ padTwo((int) getSeconds()) + " GMT" + hourOffset + minuteOffset + " "
				+ $apply(jsdate.$get("getFullYear"), jsdate);
	}

	private static final long ONE_HOUR_IN_MILLISECONDS = 60 * 60 * 1000;

	/*
	 * Some browsers have the following behavior:
	 *
	 * GAP // Assume a U.S. time zone with daylight savings // Set a
	 * non-existent time: 2:00 am Sunday March 8, 2009 var date = new Date(2009,
	 * 2, 8, 2, 0, 0); var hours = date.getHours(); // returns 1
	 *
	 * The equivalent Java code will return 3.
	 *
	 * OVERLAP // Assume a U.S. time zone with daylight savings // Set to an
	 * ambiguous time: 1:30 am Sunday November 1, 2009 var date = new Date(2009,
	 * 10, 1, 1, 30, 0); var nextHour = new Date(date.getTime() + 60*60*1000);
	 * var hours = nextHour.getHours(); // returns 1
	 *
	 * The equivalent Java code will return 2.
	 *
	 * To compensate, fixDaylightSavings adjusts the date to match Java
	 * semantics.
	 */

	/**
	 * Detects if the requested time falls into a non-existent time range due to
	 * local time advancing into daylight savings time or is ambiguous due to
	 * going out of daylight savings. If so, adjust accordingly.
	 */
	private void fixDaylightSavings(int requestedHours) {
		requestedHours %= 24;
		if (getHours() != requestedHours) {
			// Hours passed to the constructor don't match the hours in the
			// created JavaScript Date; this
			// might be due either because they are outside 0-24 range, there
			// was overflow from
			// minutes:secs:millis or because we are in the situation GAP and
			// has to be fixed.
			def.js.Object copy = $new(jsdateClass(), getTime());
			$apply(copy.$get("setDate"), ((int) $apply(copy.$get("getDate"), copy) + 1));
			int timeDiff = (int) $apply(jsdate.$get("getTimezoneOffset"), jsdate)
					- (int) $apply(copy.$get("getTimezoneOffset"), copy);

			// If the time zone offset is changing, advance the hours and
			// minutes from the initially requested time by the change amount
			if (timeDiff > 0) {
				// The requested time falls into a non-existent time range due
				// to
				// local time advancing into daylight savings time. If so, push
				// the requested
				// time forward out of the non-existent range.
				int timeDiffHours = timeDiff / 60;
				int timeDiffMinutes = timeDiff % 60;
				int day = (int) getDate();
				int badHours = (int) getHours();
				if (badHours + timeDiffHours >= 24) {
					day++;
				}
				def.js.Object newTime = $new(jsdateClass(), (int) $apply(jsdate.$get("getFullYear"), jsdate),
						getMonth(), day, requestedHours + timeDiffHours, getMinutes() + timeDiffMinutes, getSeconds(),
						(long) $apply(jsdate.$get("getMilliseconds"), jsdate));
				setTime($apply(newTime.$get("getMilliseconds"), newTime));
			}
		}

		// Check for situation OVERLAP by advancing the clock by 1 hour and see
		// if getHours() returns
		// the same. This solves issues like Safari returning '3/21/2015 23:00'
		// when time is set to
		// '2/22/2015'.
		long originalTimeInMillis = getTime();
		setTime(originalTimeInMillis + ONE_HOUR_IN_MILLISECONDS);
		if (getHours() != requestedHours) {
			// We are not in the duplicated hour, so revert the change.
			setTime(originalTimeInMillis);
		}
	}

}
