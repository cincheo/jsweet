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
package java.lang;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.io.PrintStream;

import javaemul.internal.ArrayHelper;
import javaemul.internal.DateUtil;
import javaemul.internal.HashCodes;

/**
 * General-purpose low-level utility methods. GWT only supports a limited subset
 * of these methods due to browser limitations. Only the documented methods are
 * available.
 */
public final class System {

	/**
	 * Does nothing in web mode. To get output in web mode, subclass PrintStream
	 * and call {@link #setErr(PrintStream)}.
	 */
	public static PrintStream err = new PrintStream(null);

	/**
	 * Does nothing in web mode. To get output in web mode, subclass
	 * {@link PrintStream} and call {@link #setOut(PrintStream)}.
	 */
	public static PrintStream out = new PrintStream(null);

	public static void arraycopy(Object src, int srcOfs, Object dest, int destOfs, int len) {
		checkNotNull(src, "src");
		checkNotNull(dest, "dest");

		// TODO: right now, all checks are disabled but could be reactivated
		// when appropriate support is available

		// Class<?> srcType = src.getClass();
		// Class<?> destType = dest.getClass();

		// TODO: support isArray() as a macro
		// checkArrayType(srcType.isArray(), "srcType is not an array");
		// checkArrayType(destType.isArray(), "destType is not an array");

		// TODO: support getComponentType() as a macro
		// Class<?> srcComp = srcType.getComponentType();
		// Class<?> destComp = destType.getComponentType();
		// checkArrayType(arrayTypeMatch(srcComp, destComp), "Array types don't
		// match");

		int srclen = ArrayHelper.getLength(src);
		int destlen = ArrayHelper.getLength(dest);
		if (srcOfs < 0 || destOfs < 0 || len < 0 || srcOfs + len > srclen || destOfs + len > destlen) {
			throw new IndexOutOfBoundsException();
		}
		/*
		 * If the arrays are not references or if they are exactly the same
		 * type, we can copy them in native code for speed. Otherwise, we have
		 * to copy them in Java so we get appropriate errors.
		 */
		// if ((!srcComp.isPrimitive() || srcComp.isArray()) &&
		// !srcType.equals(destType)) {
		// // copy in Java to make sure we get ArrayStoreExceptions if the
		// // values
		// // aren't compatible
		// Object[] srcArray = (Object[]) src;
		// Object[] destArray = (Object[]) dest;
		// if (src == dest && srcOfs < destOfs) {
		// // TODO(jat): how does backward copies handle failures in the
		// // middle?
		// // copy backwards to avoid destructive copies
		// srcOfs += len;
		// for (int destEnd = destOfs + len; destEnd-- > destOfs;) {
		// destArray[destEnd] = srcArray[--srcOfs];
		// }
		// } else {
		// for (int destEnd = destOfs + len; destOfs < destEnd;) {
		// destArray[destOfs++] = srcArray[srcOfs++];
		// }
		// }
		// } else
		if (len > 0) {
			ArrayHelper.copy(src, srcOfs, dest, destOfs, len);
		}
	}

	public static long currentTimeMillis() {
		return (long) DateUtil.now();
	}

	/**
	 * Has no effect; just here for source compatibility.
	 *
	 * @skip
	 */
	public static void gc() {
	}

	/**
	 * The compiler replaces getProperty by the actual value of the property.
	 */
	public static String getProperty(String key) {
		// TODO
		switch (key) {
		case "user.dir":
			return "";
		case "user.home":
			return "";
		case "user.name":
			return "jsweet";
		case "file.separator":
			return "/";
		case "java.home":
			return null;
		case "java.vendor":
			return "JSweet";
		case "java.vendor.url":
			return "http://www.jsweet.org";
		case "java.version":
			return "jsweet";
		case "os.arch":
			return "generic";
		case "os.name":
			return "generic";
		case "os.version":
			return "unknown";
		case "java.io.tmpdir":
			return "";
		default:
			return null;
		}
	}

	/**
	 * The compiler replaces getProperty by the actual value of the property.
	 */
	public static String getProperty(String key, String def) {
		String prop = getProperty(key);
		return prop == null ? def : prop;
	}

	public static int identityHashCode(Object o) {
		return HashCodes.getIdentityHashCode(o);
	}

	public static void setErr(PrintStream err) {
		System.err = err;
	}

	public static void setOut(PrintStream out) {
		System.out = out;
	}

//	private static boolean arrayTypeMatch(Class<?> srcComp, Class<?> destComp) {
//		if (srcComp.isPrimitive()) {
//			return srcComp.equals(destComp);
//		} else {
//			return !destComp.isPrimitive();
//		}
//	}
}
