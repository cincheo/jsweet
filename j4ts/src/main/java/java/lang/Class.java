/*
 * Copyright 2006 Google Inc.
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

import static jsweet.util.Globals.array;

import java.lang.reflect.Type;

import def.js.Array;
import def.js.Function;
import javaemul.internal.annotations.DoNotInline;

/**
 * Generally unsupported. This class is provided so that the GWT compiler can
 * choke down class literal references.
 * <p>
 * NOTE: The code in this class is very sensitive and should keep its
 * dependencies upon other classes to a minimum.
 *
 * @param <T>
 *            the type of the object
 */
public final class Class<T> implements Type {

	// these two arrays map JavaScript constructors and Java classes at runtime
	private static Array<Function> constructors = new Array<Function>();
	private static Array<Class<?>> classes = new Array<Class<?>>();

	static Function getConstructorForClass(Class<?> clazz) {
		int index = (int) classes.indexOf(clazz);
		return index == -1 ? null : constructors.$get(index);
	}

	static Class<?> getClassForConstructor(Function constructor) {
		int index = (int) constructors.indexOf(constructor);
		return index == -1 ? null : classes.$get(index);
	}

	static void mapConstructorToClass(Function constructor, Class<?> clazz) {
		constructors.push(constructor);
		classes.push(clazz);
	}

	private static final int PRIMITIVE = 0x00000001;
	private static final int INTERFACE = 0x00000002;
	private static final int ARRAY = 0x00000004;
	private static final int ENUM = 0x00000008;

	/**
	 * Create a Class object for an array.
	 * <p>
	 *
	 * Arrays are not registered in the prototype table and get the class
	 * literal explicitly at construction.
	 * <p>
	 */
	private static <T> Class<?> getClassLiteralForArray(Class<T> leafClass, int dimensions) {
		Class<?>[] arrayLiterals = leafClass.arrayLiterals = leafClass.arrayLiterals == null ? new Class<?>[0]
				: leafClass.arrayLiterals;
		return arrayLiterals[dimensions] != null ? arrayLiterals[dimensions]
				: (arrayLiterals[dimensions] = leafClass.createClassLiteralForArray(dimensions));
	};

	private Class<?> createClassLiteralForArray(int dimensions) {
		Class<?> clazz = new java.lang.Class<>();
		clazz.modifiers = ARRAY;
		clazz.superclass = Object.class;
		if (dimensions > 1) {
			clazz.componentType = getClassLiteralForArray(this, dimensions - 1);
		} else {
			clazz.componentType = this;
		}
		return clazz;
	}

	/**
	 * Create a Class object for a class.
	 *
	 * @skip
	 */
	@DoNotInline
	static <T> Class<T> createForClass(String packageName, String compoundClassName, String typeId,
			Class<? super T> superclass) {
		Class<T> clazz = createClassObject(packageName, compoundClassName, typeId);
		// maybeSetClassLiteral(typeId, clazz);
		clazz.superclass = superclass;
		return clazz;
	}

	/**
	 * Create a Class object for an enum.
	 *
	 * @skip
	 */
	@DoNotInline
	static <T> Class<T> createForEnum(String packageName, String compoundClassName, String typeId,
			Class<? super T> superclass, def.js.Function enumConstantsFunc, def.js.Function enumValueOfFunc) {
		Class<T> clazz = createClassObject(packageName, compoundClassName, typeId);
		// maybeSetClassLiteral(typeId, clazz);
		clazz.modifiers = (enumConstantsFunc != null) ? ENUM : 0;
		clazz.superclass = clazz.enumSuperclass = superclass;
		clazz.enumConstantsFunc = enumConstantsFunc;
		clazz.enumValueOfFunc = enumValueOfFunc;
		return clazz;
	}

	/**
	 * Create a Class object for an interface.
	 *
	 * @skip
	 */
	@DoNotInline
	static <T> Class<T> createForInterface(String packageName, String compoundClassName) {
		Class<T> clazz = createClassObject(packageName, compoundClassName, null);
		clazz.modifiers = INTERFACE;
		return clazz;
	}

	/**
	 * Create a Class object for a primitive.
	 *
	 * @skip
	 */
	@DoNotInline
	static Class<?> createForPrimitive(String className, String primitiveTypeId) {
		Class<?> clazz = createClassObject("", className, primitiveTypeId);
		// clazz.typeId = primitiveTypeId;
		clazz.modifiers = PRIMITIVE;
		return clazz;
	}

	/**
	 * Used by {@link WebModePayloadSink} to create uninitialized instances.
	 */
	@DoNotInline
	static Object getPrototypeForClass(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return null;
		}
		return getConstructorForClass(clazz).prototype;
	};

	/**
	 * Creates the class object for a type and initiliazes its fields.
	 */
	private static <T> Class<T> createClassObject(String packageName, String compoundClassName, String typeId) {
		Class<T> clazz = new Class<T>();
		clazz.packageName = packageName;
		clazz.compoundName = compoundClassName;
		return clazz;
	}

	/**
	 * Install class literal into prototype.clazz field (if type is
	 * instantiable) such that Object.getClass() returning this.clazz returns
	 * the literal. Also stores typeId on class literal for looking up
	 * prototypes given a literal. This is used for deRPC at the moment, but may
	 * be used to implement Class.newInstance() in the future.
	 *
	 * If the prototype for typeId has not yet been created, then install the
	 * literal into a placeholder array to differentiate the two cases.
	 */
	// private static void maybeSetClassLiteral(jsweet.lang.Object typeId,
	// Class<?> clazz) {
	// var proto;
	// if (!typeId) {
	// // Type is not instantiable, hence not registered in the metadata table.
	// return;
	// }
	// clazz.@Class::typeId = typeId;
	// // Guarantees virtual method won't be pruned by using a JSNI ref
	// // This is required because deRPC needs to call it.
	// var prototype = @Class::getPrototypeForClass(Ljava/lang/Class;)(clazz);
	// // A class literal may be referenced prior to an async-loaded vtable
	// setup
	// // For example, class literal lives in inital fragment,
	// // but type is instantiated in another fragment
	// if (!prototype) {
	// // Leave a place holder for now to be filled in by __defineClass__ later.
	// // TODO(rluble): Do not rely on the fact that if the entry is an array it
	// is a placeholder.
	// @com.google.gwt.lang.Runtime::prototypesByTypeId[typeId] = [clazz];
	// return;
	// }
	// // Type already registered in the metadata table, install the class
	// literal in the appropriate
	// // prototype field.
	// prototype.@java.lang.Object::___clazz = clazz;
	// };

	/**
	 * Initiliazes {@code clazz} names from metadata.
	 * <p>
	 * Written in JSNI to minimize dependencies (on String.+).
	 */
	private static void initializeNames(Class<?> clazz) {
		if (clazz.isArray()) {
			Class<?> componentType = clazz.componentType;
			if (componentType.isPrimitive()) {
				clazz.typeName = "[" + componentType.typeId;
			} else if (!componentType.isArray()) {
				clazz.typeName = "[L" + componentType.getName() + ";";
			} else {
				clazz.typeName = "[" + componentType.getName();
			}
			clazz.canonicalName = componentType.getCanonicalName() + "[]";
			clazz.simpleName = componentType.getSimpleName() + "[]";
			return;
		}

		String packageName = clazz.packageName;
		String[] compoundName = clazz.compoundName.split("/");
		clazz.typeName = array(new String[] { packageName, array(compoundName).join("$") }).join(".");
		clazz.canonicalName = array(new String[] { packageName, array(compoundName).join(".") }).join(".");
		clazz.simpleName = compoundName[compoundName.length - 1];
	}

	/**
	 * Sets the class object for primitives.
	 * <p>
	 * Written in JSNI to minimize dependencies (on (String)+).
	 */
	static void synthesizePrimitiveNamesFromTypeId(Class<?> clazz, def.js.Object primitiveTypeId) {
		clazz.typeName = "Class$" + primitiveTypeId;
		clazz.canonicalName = clazz.typeName;
		clazz.simpleName = clazz.typeName;
	}

	def.js.Function enumValueOfFunc;

	int modifiers;

	private Class<?> componentType;

	private def.js.Function enumConstantsFunc;

	private Class<? super T> enumSuperclass;

	private Class<? super T> superclass;

	private String simpleName;

	private String typeName;

	private String canonicalName;

	private String packageName;

	private String compoundName;

	private String typeId;

	private Class<?>[] arrayLiterals;

	// Assign a sequential id to each class literal to avoid calling hashCode
	// which bring Impl as
	// a dependency.
	private int sequentialId = nextSequentialId++;

	private static int nextSequentialId = 1;

	/**
	 * Not publicly instantiable.
	 *
	 * @skip
	 */
	private Class() {
		// Initialize in constructor to avoid V8 invalidating hidden classes.
		typeName = null;
		simpleName = null;
		packageName = null;
		compoundName = null;
		canonicalName = null;
		typeId = null;
		arrayLiterals = null;
	}

	public boolean desiredAssertionStatus() {
		// This body is ignored by the JJS compiler and a new one is
		// synthesized at compile-time based on the actual compilation
		// arguments.
		return false;
	}

	private void ensureNamesAreInitialized() {
		if (typeName != null) {
			return;
		}
		initializeNames(this);
	}

	public String getCanonicalName() {
		ensureNamesAreInitialized();
		return canonicalName;
	}

	public Class<?> getComponentType() {
		return componentType;
	}

	public native T[] getEnumConstants() /*-{
											return this.@Class::enumConstantsFunc
											&& (this.@Class::enumConstantsFunc)();
											}-*/;

	public String getName() {
		ensureNamesAreInitialized();
		return typeName;
	}

	public String getSimpleName() {
		ensureNamesAreInitialized();
		return simpleName;
	}

	public Class<? super T> getSuperclass() {
		return superclass;
	}

	public boolean isArray() {
		return (modifiers & ARRAY) != 0;
	}

	public boolean isEnum() {
		return (modifiers & ENUM) != 0;
	}

	public boolean isInterface() {
		return (modifiers & INTERFACE) != 0;
	}

	public boolean isPrimitive() {
		return (modifiers & PRIMITIVE) != 0;
	}

	@Override
	public String toString() {
		return (isInterface() ? "interface " : (isPrimitive() ? "" : "class ")) + getName();
	}

	/**
	 * Used by Enum to allow getSuperclass() to be pruned.
	 */
	Class<? super T> getEnumSuperclass() {
		return enumSuperclass;
	}
}
