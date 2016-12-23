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
package javaemul.internal;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.io.Serializable;

import jsweet.lang.Erased;

/**
 * Wraps native <code>boolean</code> as an object.
 */
public final class BooleanHelper implements Comparable<BooleanHelper>, Serializable {

	@Erased
	private static final long serialVersionUID = 1L;
	public static final Boolean FALSE = false;
	public static final Boolean TRUE = true;

	public static final Class<Boolean> TYPE = Boolean.class;

	public static int compare(boolean x, boolean y) {
		return (x == y) ? 0 : (x ? 1 : -1);
	}

	public static int hashCode(boolean value) {
		// The Java API doc defines these magic numbers.
		return value ? 1231 : 1237;
	}

	public static boolean logicalAnd(boolean a, boolean b) {
		return a && b;
	}

	public static boolean logicalOr(boolean a, boolean b) {
		return a || b;
	}

	public static boolean logicalXor(boolean a, boolean b) {
		return a ^ b;
	}

	public static boolean parseBoolean(String s) {
		return "true".equalsIgnoreCase(s);
	}

	public static String toString(boolean x) {
		return String.valueOf(x);
	}

	public static Boolean valueOf(boolean b) {
		return b ? TRUE : FALSE;
	}

	public static Boolean valueOf(String s) {
		return valueOf(parseBoolean(s));
	}

	@Erased
	public BooleanHelper(boolean value) {
	}

	@Erased
	public BooleanHelper(String s) {
	}

	public boolean booleanValue() {
		return unsafeCast(checkNotNull(this));
	}

	private static boolean unsafeCast(Object value) {
		return (Boolean) value;
	};

	@Override
	public int compareTo(BooleanHelper b) {
		return compare(booleanValue(), b.booleanValue());
	}

	@Override
	public boolean equals(Object o) {
		return checkNotNull(this) == o;
	}

	@Override
	public int hashCode() {
		return hashCode(booleanValue());
	}

	@Override
	public String toString() {
		return toString(booleanValue());
	}
}
