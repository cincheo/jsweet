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

import jsweet.lang.Erased;

/**
 * Wraps a primitive <code>long</code> as an object.
 */
public final class LongHelper extends NumberHelper implements Comparable<LongHelper> {

	@Erased
	private static final long serialVersionUID = 1L;

	/**
	 * Use nested class to avoid clinit on outer.
	 */
	static class BoxedValues {
		// Box values according to JLS - between -128 and 127
		static Long[] boxedValues = new Long[256];
	}

	public static final long MAX_VALUE = 0x7fffffffffffffffL;
	public static final long MIN_VALUE = 0x8000000000000000L;
	public static final int SIZE = 64;

	public static int bitCount(long i) {
		int high = (int) (i >> 32);
		int low = (int) i;
		return IntegerHelper.bitCount(high) + IntegerHelper.bitCount(low);
	}

	public static int compare(long x, long y) {
		if (x < y) {
			return -1;
		} else if (x > y) {
			return 1;
		} else {
			return 0;
		}
	}

	public static Long decode(String s) throws NumberFormatException {
		__Decode decode = __decodeNumberString(s);
		return valueOf(decode.payload, decode.radix);
	}

	/**
	 * @skip Here for shared implementation with Arrays.hashCode
	 */
	public static int hashCode(long l) {
		return (int) l;
	}

	public static long highestOneBit(long i) {
		int high = (int) (i >> 32);
		if (high != 0) {
			return ((long) IntegerHelper.highestOneBit(high)) << 32;
		} else {
			return IntegerHelper.highestOneBit((int) i) & 0xFFFFFFFFL;
		}
	}

	public static long lowestOneBit(long i) {
		return i & -i;
	}

	public static int numberOfLeadingZeros(long i) {
		int high = (int) (i >> 32);
		if (high != 0) {
			return IntegerHelper.numberOfLeadingZeros(high);
		} else {
			return IntegerHelper.numberOfLeadingZeros((int) i) + 32;
		}
	}

	public static int numberOfTrailingZeros(long i) {
		int low = (int) i;
		if (low != 0) {
			return IntegerHelper.numberOfTrailingZeros(low);
		} else {
			return IntegerHelper.numberOfTrailingZeros((int) (i >> 32)) + 32;
		}
	}

	public static long parseLong(String s) throws NumberFormatException {
		return parseLong(s, 10);
	}

	public static long parseLong(String s, int radix) throws NumberFormatException {
		return __parseAndValidateLong(s, radix);
	}

	public static long reverse(long i) {
		int high = (int) (i >>> 32);
		int low = (int) i;
		return ((long) IntegerHelper.reverse(low) << 32) | (IntegerHelper.reverse(high) & 0xffffffffL);
	}

	public static long reverseBytes(long i) {
		int high = (int) (i >>> 32);
		int low = (int) i;
		return ((long) IntegerHelper.reverseBytes(low) << 32) | (IntegerHelper.reverseBytes(high) & 0xffffffffL);
	}

	public static long rotateLeft(long i, int distance) {
		while (distance-- > 0) {
			i = i << 1 | ((i < 0) ? 1 : 0);
		}
		return i;
	}

	public static long rotateRight(long i, int distance) {
		long ui = i & MAX_VALUE; // avoid sign extension
		long carry = (i < 0) ? 0x4000000000000000L : 0; // MIN_VALUE
														// rightshifted 1
		while (distance-- > 0) {
			long nextcarry = ui & 1;
			ui = carry | (ui >> 1);
			carry = (nextcarry == 0) ? 0 : 0x4000000000000000L;
		}
		if (carry != 0) {
			ui = ui | MIN_VALUE;
		}
		return ui;
	}

	public static int signum(long i) {
		if (i == 0) {
			return 0;
		} else if (i < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	public static String toBinaryString(long value) {
		return toPowerOfTwoUnsignedString(value, 1);
	}

	public static String toHexString(long value) {
		return toPowerOfTwoUnsignedString(value, 4);
	}

	public static String toOctalString(long value) {
		return toPowerOfTwoUnsignedString(value, 3);
	}

	public static String toString(long value) {
		return String.valueOf(value);
	}

	public static String toString(long value, int intRadix) {
		if (intRadix == 10 || intRadix < CharacterHelper.MIN_RADIX || intRadix > CharacterHelper.MAX_RADIX) {
			return String.valueOf(value);
		}

		int intValue = (int) value;
		if (intValue == value) {
			return IntegerHelper.toString(intValue, intRadix);
		}

		/*
		 * If v is positive, negate it. This is the opposite of what one might
		 * expect. It is necessary because the range of the negative values is
		 * strictly larger than that of the positive values: there is no
		 * positive value corresponding to Long.MIN_VALUE.
		 */
		boolean negative = value < 0;
		if (!negative) {
			value = -value;
		}

		int bufLen = intRadix < 8 ? 65 : 23; // Max chars in result
												// (conservative)
		char[] buf = new char[bufLen];
		int cursor = bufLen;

		// Convert radix to long before hand to avoid costly conversion on each
		// iteration.
		long radix = intRadix;
		do {
			long q = value / radix;
			buf[--cursor] = CharacterHelper.forDigit((int) (radix * q - value));
			value = q;
		} while (value != 0);

		if (negative) {
			buf[--cursor] = '-';
		}

		return String.valueOf(buf, cursor, bufLen - cursor);
	}

	public static Long valueOf(long i) {
		if (i > -129 && i < 128) {
			int rebase = (int) i + 128;
			Long result = BoxedValues.boxedValues[rebase];
			if (result == null) {
				result = BoxedValues.boxedValues[rebase] = new Long(i);
			}
			return result;
		}
		return new Long(i);
	}

	public static Long valueOf(String s) throws NumberFormatException {
		return valueOf(s, 10);
	}

	public static Long valueOf(String s, int radix) throws NumberFormatException {
		return valueOf(parseLong(s, radix));
	}

	private static String toPowerOfTwoUnsignedString(long value, int shift) {
		final int radix = 1 << shift;
		if (IntegerHelper.MIN_VALUE <= value && value <= IntegerHelper.MAX_VALUE) {
			return IntegerHelper.toString((int) value, radix);
		}

		final int mask = radix - 1;
		final int bufSize = 64 / shift + 1;
		char[] buf = new char[bufSize];
		int pos = bufSize;
		do {
			buf[--pos] = CharacterHelper.forDigit(((int) value) & mask);
			value >>>= shift;
		} while (value != 0);

		return String.valueOf(buf, pos, bufSize - pos);
	}

	private final transient long value;

	public LongHelper(long value) {
		this.value = value;
	}

	public LongHelper(String s) {
		value = parseLong(s);
	}

	@Override
	public byte byteValue() {
		return (byte) value;
	}

	@Override
	public int compareTo(LongHelper b) {
		return compare(value, b.value);
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof LongHelper) && (((LongHelper) o).value == value);
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return hashCode(value);
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public short shortValue() {
		return (short) value;
	}

	@Override
	public String toString() {
		return toString(value);
	}
}
