/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.utils;

/**
 * String utilities.
 *
 */
public class StringUtils {

	public static final String[] EMPTY_STRING = new String[0];

	/**
	 * Represents a failed index search.
	 */
	public static final int INDEX_NOT_FOUND = -1;

	private static final int NOT_FOUND = -1;

	public static int countMatches(final CharSequence str, final CharSequence sub) {
		if (isEmpty(str) || isEmpty(sub)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while ((idx = indexOf(str, sub, idx)) != INDEX_NOT_FOUND) {
			count++;
			idx += sub.length();
		}
		return count;
	}

	static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
		return cs.toString().indexOf(searchChar.toString(), start);
	}

	static int indexOf(final CharSequence cs, final int searchChar, int start) {
		if (cs instanceof String) {
			return ((String) cs).indexOf(searchChar, start);
		}
		final int sz = cs.length();
		if (start < 0) {
			start = 0;
		}
		for (int i = start; i < sz; i++) {
			if (cs.charAt(i) == searchChar) {
				return i;
			}
		}
		return NOT_FOUND;
	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static String normalizeSpace(String s) {
		if (s == null) {
			return null;
		}
		int len = s.length();
		if (len < 1) {
			return "";
		}
		int st = 0;
		int off = 0; /* avoid getfield opcode */
		char[] val = s.toCharArray(); /* avoid getfield opcode */
		int count = s.length();

		boolean parse = true;
		char c;
		while (parse) {
			c = val[off + st];
			parse = isParse(len, st, c);
			if (parse) {
				st++;
			}
		}
		parse = true;
		while ((st < len) && (val[off + len - 1] <= ' ')) {
			c = val[off + len - 1];
			parse = isParse(len, st, c);
			if (parse) {
				len--;
			}
		}
		return ((st > 0) || (len < count)) ? s.substring(st, len) : s;
	}

	private static boolean isParse(int len, int st, char c) {
		return (st < len) && (c == ' ' || c == '\r' || c == '\n' || c == '\t');
	}

}
