package source.api;

import static jsweet.util.Lang.$export;

import def.dom.intl.DateTimeFormatOptions;
import def.js.Date;

public class Dates {
	public static void main(String[] args) {

		double JANUARY = 0;
		Date d = new Date(Date.UTC(2020, JANUARY, 01, 0, 0, 0));

		String localeString = d.toLocaleString("fr", new DateTimeFormatOptions() {
			{
			}
		});

		System.out.println(localeString);
		$export("localeString", localeString);
	}
}
