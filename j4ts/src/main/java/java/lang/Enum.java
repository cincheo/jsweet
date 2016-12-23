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

import static javaemul.internal.InternalPreconditions.checkCriticalArgument;
import static javaemul.internal.InternalPreconditions.checkNotNull;
import static jsweet.util.Globals.$apply;

import java.io.Serializable;

/**
 * The first-class representation of an enumeration.
 *
 * @param <E>
 */
public abstract class Enum<E extends Enum<E>> implements Comparable<E>, Serializable {

	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
		def.js.Function enumValueOfFunc = checkNotNull(enumType).enumValueOfFunc;
		checkCriticalArgument(enumValueOfFunc != null);
		checkNotNull(name);
		return invokeValueOf(enumValueOfFunc, name);
	}

	protected static <T extends Enum<T>> def.js.Object createValueOfMap(T[] enumConstants) {
		def.js.Object result = new def.js.Object();
		for (T value : enumConstants) {
			put0(result, ":" + value.name(), value);
		}
		return result;
	}

	protected static <T extends Enum<T>> T valueOf(def.js.Object map, String name) {
		checkNotNull(name);

		T result = Enum.<T> get0(map, ":" + name);
		checkCriticalArgument(result != null, "Enum constant undefined: %s", name);
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> T get0(def.js.Object map, String name) {
		return (T) map.$get(name);
	};

	private static <T extends Enum<T>> T invokeValueOf(def.js.Function enumValueOfFunc, String name) {
		return $apply(enumValueOfFunc, name);
	};

	private static <T extends Enum<T>> void put0(def.js.Object map, String name, T value) {
		map.$set(name, value);
	};

	private final String name;

	private final int ordinal;

	protected Enum(String name, int ordinal) {
		this.name = name;
		this.ordinal = ordinal;
	}

	@Override
	public final int compareTo(E other) {
		// TODO: will a bridge method do the cast for us?
		// if (this.getDeclaringClass() != other.getDeclaringClass()) {
		// throw new ClassCastException();
		// }
		return this.ordinal - ((Enum) other).ordinal;
	}

//	@Override
//	public final boolean equals(Object other) {
//		return this == other;
//	}

	@SuppressWarnings("unchecked")
	public final Class<E> getDeclaringClass() {
//		Class clazz = getClass();
//		assert clazz != null : "clazz";
//
//		// Don't use getSuperclass() to allow that method to be pruned for most
//		// class types
//		Class superclass = clazz.getEnumSuperclass();
//		assert superclass != null : "superclass";
//
//		return (superclass == Enum.class) ? clazz : superclass;
		return null;
	}

//	@Override
//	public final int hashCode() {
//		return super.hashCode();
//	}

	public final String name() {
		return name != null ? name : "" + ordinal;
	}

	public final int ordinal() {
		return ordinal;
	}

	@Override
	public String toString() {
		return name();
	}
}
