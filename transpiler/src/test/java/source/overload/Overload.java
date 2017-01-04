/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.overload;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.string;

import def.js.Date;

public class Overload {

	public Overload() {
		this(null);
	}
	
	public Overload(String s) {
	}
	
	String m() {
		return this.m("default");
	}

	String m(String s) {
		return this.m(s, 1);
	}

	private String m(String s, int i) {
		return s + i;
	}

	public static void main(String[] args) {
		Overload o = new Overload();
		$export("res1", o.m());
		$export("res2", o.m("s1"));
		$export("res3", o.m("s2", 2));
	}

	final static String DATE_FORMAT = ""; 
	
	public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT);
    }

    public static String formatDate(Date date, String format) {
        if (!Overload.isDate(date)) {
            return "";
        }
        def.js.String dateFormatted = string(format);

        dateFormatted = string(dateFormatted.replace("yyyy", formatNumber(date.getFullYear(), 4)));
        dateFormatted = string(dateFormatted.replace("MM", formatNumber(date.getMonth() + 1, 2)));
        dateFormatted = string(dateFormatted.replace("dd", formatNumber(date.getDate(), 2)));
        dateFormatted = string(dateFormatted.replace("hh", formatNumber(date.getHours(), 2)));
        dateFormatted = string(dateFormatted.replace("mm", formatNumber(date.getMinutes(), 2)));
        dateFormatted = string(dateFormatted.replace("ss", formatNumber(date.getSeconds(), 2)));
        dateFormatted = string(dateFormatted.replace("SSS", formatNumber(date.getMilliseconds(), 3)));

        return string(dateFormatted);
    }
	
    public static String formatNumber(double n, int i) {
    	return "";
    }
	
    public static boolean isDate(Date date) {
    	return false;
    }
    
}

class OverloadCaller {
	public static void main(String[] args) {
		new Overload().m("a");
	}
}