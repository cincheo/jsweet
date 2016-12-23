/*
 * Copyright 2008 Google Inc.
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

package javaemul.internal;

import static javaemul.internal.InternalPreconditions.checkStringBounds;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Comparator;

//import def.sprintf_js.Globals;

/**
 * Intrinsic string class.
 */
public final class StringHelper {
	/*
	 * TODO(jat): consider whether we want to support the following methods;
	 *
	 * <ul> <li>deprecated methods dealing with bytes (I assume not since I
	 * can't see much use for them) <ul> <li>String(byte[] ascii, int hibyte)
	 * <li>String(byte[] ascii, int hibyte, int offset, int count)
	 * <li>getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) </ul>
	 * <li>methods which in JS will essentially do nothing or be the same as
	 * other methods <ul> <li>copyValueOf(char[] data) <li>copyValueOf(char[]
	 * data, int offset, int count) </ul> <li>methods added in Java 1.6 (the
	 * issue is how will it impact users building against Java 1.5) <ul>
	 * <li>isEmpty() </ul> <li>other methods which are not straightforward in JS
	 * <ul> <li>format(String format, Object... args) </ul> </ul>
	 *
	 * <p>Also, in general, we need to improve our support of non-ASCII
	 * characters. The problem is that correct support requires large tables,
	 * and we don't want to make users who aren't going to use that pay for it.
	 * There are two ways to do that: <ol> <li>construct the tables in such a
	 * way that if the corresponding method is not called the table will be
	 * elided from the output. <li>provide a deferred binding target selecting
	 * the level of compatibility required. Those that only need ASCII (or
	 * perhaps a different relatively small subset such as Latin1-5) will not
	 * pay for large tables, even if they do call toLowercase(), for example.
	 * </ol>
	 *
	 * Also, if we ever add multi-locale support, there are a number of other
	 * methods such as toLowercase(Locale) we will want to consider supporting.
	 * This is probably rare, but there will be some apps (such as a translation
	 * tool) which cannot be written without this support.
	 *
	 * Another category of incomplete support is that we currently just use the
	 * JS regex support, which is not exactly the same as Java. We should
	 * support Java syntax by mapping it into equivalent JS patterns, or
	 * emulating them.
	 *
	 * IMPORTANT NOTE: if newer JREs add new interfaces to String, please update
	 * {@link Devirtualizer} and {@link JavaResourceBase}
	 */
	public static final Comparator<String> CASE_INSENSITIVE_ORDER = new Comparator<String>() {
		@Override
		public int compare(String a, String b) {
			return a.compareToIgnoreCase(b);
		}
	};

	public static String copyValueOf(char[] v) {
		return valueOf(v);
	}

	public static String copyValueOf(char[] v, int offset, int count) {
		return valueOf(v, offset, count);
	}

	public static String valueOf(boolean x) {
		return "" + x;
	}

	public static String valueOf(char x) {
		return "" + x;
	}

	public static String valueOf(char x[], int offset, int count) {
		int end = offset + count;
		checkStringBounds(offset, end, x.length);
		// Work around function.prototype.apply call stack size limits:
		// https://code.google.com/p/v8/issues/detail?id=2896
		// Performance: http://jsperf.com/string-fromcharcode-test/13
		int batchSize = ArrayHelper.ARRAY_PROCESS_BATCH_SIZE;
		String s = "";
		for (int batchStart = offset; batchStart < end;) {
			int batchEnd = Math.min(batchStart + batchSize, end);
			s += fromCharCode(ArrayHelper.unsafeClone(x, batchStart, batchEnd));
			batchStart = batchEnd;
		}
		return s;
	}

	private static String fromCharCode(Object[] array) {
		return def.js.String.fromCharCode((double) (Object) array);
	}

	public static String valueOf(char[] x) {
		return valueOf(x, 0, x.length);
	}

	public static String valueOf(double x) {
		return "" + x;
	}

	public static String valueOf(float x) {
		return "" + x;
	}

	public static String valueOf(int x) {
		return "" + x;
	}

	public static String valueOf(long x) {
		return "" + x;
	}

	// valueOf needs to be treated special:
	// J2cl uses it for String concat and thus it can not use string
	// concatenation itself.
	public static String valueOf(Object x) {
		return x == null ? "null" : x.toString();
	}

	/**
	 * This method converts Java-escaped dollar signs "\$" into
	 * JavaScript-escaped dollar signs "$$", and removes all other lone
	 * backslashes, which serve as escapes in Java but are passed through
	 * literally in JavaScript.
	 *
	 * @skip
	 */
	private static String translateReplaceString(String replaceStr) {
		int pos = 0;
		while (0 <= (pos = replaceStr.indexOf("\\", pos))) {
			if (replaceStr.charAt(pos + 1) == '$') {
				replaceStr = replaceStr.substring(0, pos) + "$" + replaceStr.substring(++pos);
			} else {
				replaceStr = replaceStr.substring(0, pos) + replaceStr.substring(++pos);
			}
		}
		return replaceStr;
	}

	private static native int compareTo(String thisStr, String otherStr) /*-{
																			if (thisStr == otherStr) {
																			return 0;
																			}
																			return thisStr < otherStr ? -1 : 1;
																			}-*/;

	private static Charset getCharset(String charsetName) throws UnsupportedEncodingException {
		try {
			return Charset.forName(charsetName);
		} catch (UnsupportedCharsetException e) {
			throw new UnsupportedEncodingException(charsetName);
		}
	}

	static String fromCodePoint(int codePoint) {
		if (codePoint >= Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			char hiSurrogate = CharacterHelper.getHighSurrogate(codePoint);
			char loSurrogate = CharacterHelper.getLowSurrogate(codePoint);
			return String.valueOf(hiSurrogate) + String.valueOf(loSurrogate);
		} else {
			return String.valueOf((char) codePoint);
		}
	}

	public static String format(String formatString, Object... args) {
		return "";
		// TODO: reactivate
		//return Globals.sprintf(formatString, args);
	}

	private StringHelper() {
	}

}
