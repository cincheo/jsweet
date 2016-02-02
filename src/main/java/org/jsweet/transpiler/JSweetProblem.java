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
package org.jsweet.transpiler;

/**
 * This enumeration holds all the possible JSweet transpilation problems
 * (warnings and errors) and the associated messages.
 * 
 * @author Renaud Pawlak
 */
public enum JSweetProblem {

	/**
	 * Raised when the JDK is not found (hence tools.jar).
	 */
	JAVA_COMPILER_NOT_FOUND(Severity.ERROR),
	/**
	 * Raised when the Java compiler reports an error.
	 */
	INTERNAL_JAVA_ERROR(Severity.ERROR),
	/**
	 * Raised when the Tsc transpiler reports an error.
	 */
	INTERNAL_TSC_ERROR(Severity.ERROR),
	/**
	 * Raised when the Tsc transpiler (or the expected version) is not found if
	 * required.
	 */
	TSC_CANNOT_START(Severity.ERROR),
	/**
	 * Raised when Node is not found.
	 */
	NODE_CANNOT_START(Severity.ERROR),
	/**
	 * Raised when the program tries to access a forbidden Java type.
	 * 
	 * <p>
	 * By default, all Java types are forbidden except for some classes in the
	 * java.lang: Object, String, Boolean, Number, Integer, Double, Void.
	 */
	JDK_TYPE(Severity.ERROR),
	/**
	 * Raised when the program tries to access a forbidden Java method.
	 */
	JDK_METHOD(Severity.ERROR),
	/**
	 * Raised when the program tries to access an erased method (Erased
	 * annotation).
	 */
	ERASED_METHOD(Severity.ERROR),
	/**
	 * Raised when the program misuses a class annotated with the Erased
	 * annotation.
	 */
	ERASED_CLASS_CONSTRUCTOR(Severity.ERROR),
	/**
	 * Raised when the program tries to use the synchronized keyword.
	 */
	SYNCHRONIZATION(Severity.ERROR),
	/**
	 * Raised when a method has the same name as a field, including fields
	 * defined in superclasses.
	 */
	METHOD_CONFLICTS_FIELD(Severity.ERROR),
	/**
	 * Raised when a field has the same name as a method, including methods
	 * defined in superclasses.
	 */
	FIELD_CONFLICTS_METHOD(Severity.ERROR),
	/**
	 * Raised when a method invocation is hidden by a local variable or parameter.
	 */
	HIDDEN_INVOCATION(Severity.ERROR),
	/**
	 * Raised when an inner class is found.
	 */
	INNER_CLASS(Severity.ERROR),
	/**
	 * Raised when a class initializer is something else than a field
	 * assignment.
	 */
	INVALID_INITIALIZER_STATEMENT(Severity.ERROR),
	/**
	 * Raised when a non-optional field has not been initialized.
	 */
	UNINITIALIZED_FIELD(Severity.ERROR),
	/**
	 * Raised when an @Optional annotation is set to a field that is not member
	 * of an interface.
	 */
	USELESS_OPTIONAL_ANNOTATION(Severity.WARNING),
	/**
	 * Raised when a local variable uses a forbidden JS keyword (var, function,
	 * typeof, ...).
	 */
	JS_KEYWORD_CONFLICT(Severity.WARNING),
	/**
	 * Raised when a private visibility is used in an interface.
	 */
	INVALID_PRIVATE_IN_INTERFACE(Severity.ERROR),
	/**
	 * Raised when a static modifier is used in an interface.
	 */
	INVALID_STATIC_IN_INTERFACE(Severity.ERROR),
	/**
	 * Raised when a method body is defined in an interface.
	 */
	INVALID_METHOD_BODY_IN_INTERFACE(Severity.ERROR),
	/**
	 * Raised when a field initializer is defined in an interface.
	 */
	INVALID_FIELD_INITIALIZER_IN_INTERFACE(Severity.ERROR),
	/**
	 * Raised when an initializer is defined in an interface.
	 */
	INVALID_INITIALIZER_IN_INTERFACE(Severity.ERROR),
	/**
	 * Raised when an overload is not valid.
	 */
	INVALID_OVERLOAD(Severity.ERROR),
	/**
	 * Raised when a member is named 'constructor'.
	 */
	CONSTRUCTOR_MEMBER(Severity.ERROR),
	/**
	 * Raised when a class annotated with @Interface is not abstract.
	 */
	INTERFACE_MUST_BE_ABSTRACT(Severity.ERROR),
	/**
	 * Raised when a native modifier is used at the wrong place (only allowed in
	 * definitions).
	 */
	NATIVE_MODIFIER_IS_NOT_ALLOWED(Severity.ERROR),
	/**
	 * Raised when instanceof is applied to an interface.
	 */
	INVALID_INSTANCEOF_INTERFACE(Severity.ERROR),
	/**
	 * Raised when the program tries to use a label.
	 */
	LABELS_ARE_NOT_SUPPORTED(Severity.ERROR),
	/**
	 * Raised when a try statement does not declare a catch or a finally clause.
	 */
	TRY_WITHOUT_CATCH_OR_FINALLY(Severity.ERROR),
	/**
	 * Raised when a try statement declares multiple catch clauses.
	 */
	TRY_WITH_MULTIPLE_CATCHES(Severity.ERROR),
	/**
	 * Raised when a try-with-resource statement is used in the program.
	 */
	UNSUPPORTED_TRY_WITH_RESOURCE(Severity.ERROR),
	/**
	 * Raised when a set is used in a global context (no enclosing class)
	 */
	GLOBAL_INDEXER_SET(Severity.ERROR),
	/**
	 * Raised when a get is used in a global context (no enclosing class)
	 */
	GLOBAL_INDEXER_GET(Severity.ERROR),
	/**
	 * Raised when a delete is used in a global context (no enclosing class)
	 */
	GLOBAL_DELETE(Severity.ERROR),
	/**
	 * Raised when a constructor is defined in a global class
	 */
	GLOBAL_CONSTRUCTOR_DEF(Severity.ERROR),
	/**
	 * Raised if global class is instantiated
	 */
	GLOBAL_CANNOT_BE_INSTANTIATED(Severity.ERROR),
	/**
	 * Raised when a string literal is expected.
	 */
	STRING_LITERAL_EXPECTED(Severity.ERROR),
	/**
	 * Raised when an enum defines a field.
	 */
	INVALID_FIELD_IN_ENUM(Severity.ERROR),
	/**
	 * Raised when an enum defines a constructor.
	 */
	INVALID_CONSTRUCTOR_IN_ENUM(Severity.ERROR),
	/**
	 * Raised when an enum defines a method.
	 */
	INVALID_METHOD_IN_ENUM(Severity.ERROR),
	/**
	 * Raised when an ambient method defines an implementation.
	 */
	INVALID_METHOD_BODY_IN_AMBIENT(Severity.ERROR),
	/**
	 * Raised when an ambient constructor defines an implementation that is not
	 * empty.
	 */
	INVALID_NON_EMPTY_CONSTRUCTOR_IN_AMBIENT(Severity.ERROR),
	/**
	 * Raised when an ambient member uses an invalid modifier.
	 */
	INVALID_MODIFIER_IN_AMBIENT(Severity.ERROR),
	/**
	 * Raised when an @Ambient is used on a method or a field.
	 */
	WRONG_USE_OF_AMBIENT(Severity.WARNING),
	/**
	 * Raised when an indexed set goes against indexed get type.
	 */
	INDEXED_SET_TYPE_MISMATCH(Severity.ERROR),
	/**
	 * Raised when an union type assignment is not compatible with one of the
	 * union-ed types.
	 */
	UNION_TYPE_MISMATCH(Severity.ERROR),
	/**
	 * Raised when an union type assignment is not compatible with one of the
	 * union-ed types.
	 */
	BUNDLE_WITH_COMMONJS(Severity.WARNING),
	/**
	 * Raised when a bundle cannot be done because of a cycle in the
	 * module/packages.
	 */
	BUNDLE_HAS_CYCLE(Severity.ERROR),
	/**
	 * Raised when a bundle cannot be done because it has no entries.
	 */
	BUNDLE_HAS_NO_ENTRIES(Severity.WARNING),
	/**
	 * Raised when a package is named after an invalid name (typically a
	 * TypeScript keyword).
	 */
	PACKAGE_NAME_CONTAINS_KEYWORD(Severity.ERROR),
	/**
	 * Raised when a wildcard import is used.
	 */
	WILDCARD_IMPORT(Severity.ERROR),
	/**
	 * Raised when a @Root package is enclosed in a @Root package.
	 */
	ENCLOSED_ROOT_PACKAGES(Severity.ERROR),
	/**
	 * Raised when a class is declared in a parent of a @Root package.
	 */
	CLASS_OUT_OF_ROOT_PACKAGE_SCOPE(Severity.ERROR), 
	/**
	 * Raised when using a candy which was generated for an older / newer version of the transpiler 
	 */
	CANDY_VERSION_DISCREPANCY(Severity.WARNING),
	/**
	 * Raised when a Globals class declares a non static member.
	 */
	GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS(Severity.ERROR),
	/**
	 * Raised when a Globals class declares a superclass.
	 */
	GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS(Severity.ERROR), 
	/**
	 * Raised when a class tries to extend a Globals class.
	 */
	GLOBALS_CLASS_CANNOT_BE_SUBCLASSED(Severity.ERROR),
	/**
	 * Raised when invoking a static method on this (this is allowed in Java, but not in JSweet).
	 */
	CANNOT_ACCESS_STATIC_MEMBER_ON_THIS(Severity.ERROR); 
	
	private Severity severity;

	/**
	 * Gets the severity of this problem.
	 */
	public Severity getSeverity() {
		return severity;
	}

	private JSweetProblem(Severity severity) {
		this.severity = severity;
	}

	/**
	 * Gets the associated message.
	 */
	public String getMessage(Object... params) {
		switch (this) {
		case JAVA_COMPILER_NOT_FOUND:
			return String.format(
					"Java compiler cannot be found: make sure that JAVA_HOME points to a JDK (version>=8) and not a JRE, or sets the transpiler jdkHome option",
					params);
		case INTERNAL_JAVA_ERROR:
			return String.format("%s", params);
		case INTERNAL_TSC_ERROR:
			return String.format("internal TypeScript error: %s", params);
		case NODE_CANNOT_START:
			return String.format("cannot find Node.js: install first and make sure that the 'node' command is in your execution path", params);
		case TSC_CANNOT_START:
			return String.format("cannot find TypeScript compiler: install first and make sure that the 'tsc' command is in your execution path", params);
		case JDK_TYPE:
			return String.format("invalid access to JDK type '%s' from JSweet", params);
		case JDK_METHOD:
			return String.format("invalid access to JDK method '%s' from JSweet", params);
		case ERASED_METHOD:
			return String.format("invalid access to erased method '%s'", params);
		case ERASED_CLASS_CONSTRUCTOR:
			return String.format("erased class constructors must take exactly one parameter", params);
		case SYNCHRONIZATION:
			return String.format("synchronization is not allowed in JSweet", params);
		case METHOD_CONFLICTS_FIELD:
			return String.format("method '%s' has the same name as a field in '%s'", params);
		case HIDDEN_INVOCATION:
			return String.format("invocation of '%s' is hidden by a local variable", params);
		case FIELD_CONFLICTS_METHOD:
			return String.format("field '%s' has the same name as a method in '%s'", params);
		case INNER_CLASS:
			return String.format("inner classes are not allowed in JSweet: '%s'", params);
		case INVALID_INITIALIZER_STATEMENT:
			return String.format("invalid initializer statement; only field assignments are allowed", params);
		case UNINITIALIZED_FIELD:
			return String.format("field %s is not optional (see @Optional) but has not been initialized", params);
		case USELESS_OPTIONAL_ANNOTATION:
			return String.format("useless @Optional field %s (fields are optional by default in classes, use @Interface to define %s as an interface)", params);
		case JS_KEYWORD_CONFLICT:
			return String.format("local variable name '%s' is not allowed and is automatically generated to '_jsweet_%s'", params);
		case INVALID_METHOD_BODY_IN_INTERFACE:
			return String.format("method '%s' cannot define a body in interface '%s'", params);
		case INVALID_PRIVATE_IN_INTERFACE:
			return String.format("member '%s' cannot be private in interface '%s'", params);
		case INVALID_STATIC_IN_INTERFACE:
			return String.format("member '%s' cannot be static in interface '%s'", params);
		case INVALID_FIELD_INITIALIZER_IN_INTERFACE:
			return String.format("field '%s' cannot be initialized in interface '%s'", params);
		case INVALID_INITIALIZER_IN_INTERFACE:
			return String.format("no initialization blocks are allowed in interface '%s'", params);
		case INVALID_OVERLOAD:
			return String.format("invalid overload of method '%s'", params);
		case CONSTRUCTOR_MEMBER:
			return String.format("invalid member name 'constructor'", params);
		case INTERFACE_MUST_BE_ABSTRACT:
			return String.format("@Interface '%s' must be abstract", params);
		case NATIVE_MODIFIER_IS_NOT_ALLOWED:
			return String.format("method '%s' cannot be native", params);
		case INVALID_INSTANCEOF_INTERFACE:
			return String.format("operator 'instanceof' cannot apply to interfaces", params);
		case LABELS_ARE_NOT_SUPPORTED:
			return String.format("labels are not supported", params);
		case TRY_WITHOUT_CATCH_OR_FINALLY:
			return String.format("try statement must define at least a catch or a finally clause", params);
		case UNSUPPORTED_TRY_WITH_RESOURCE:
			return String.format("try-with-resource statement is not supported", params);
		case TRY_WITH_MULTIPLE_CATCHES:
			return String.format("try statement cannot define more than one catch clause", params);
		case GLOBAL_INDEXER_GET:
			return String.format("indexer cannot be used in a global context", params);
		case GLOBAL_INDEXER_SET:
			return String.format("indexer cannot be used in a global context", params);
		case GLOBAL_DELETE:
			return String.format("static delete cannot be used in a global context", params);
		case STRING_LITERAL_EXPECTED:
			return String.format("string literal expected", params);
		case GLOBAL_CONSTRUCTOR_DEF:
			return String.format("global class cannot have constructor", params);
		case GLOBAL_CANNOT_BE_INSTANTIATED:
			return String.format("global classes cannot be instantiated", params);
		case INVALID_CONSTRUCTOR_IN_ENUM:
			return String.format("constructors are not allowed in enums", params);
		case INVALID_FIELD_IN_ENUM:
			return String.format("fields are not allowed in enums", params);
		case INVALID_METHOD_IN_ENUM:
			return String.format("methods are not allowed in enums", params);
		case INVALID_METHOD_BODY_IN_AMBIENT:
			return String.format("method '%s' is an ambiant declaration and cannot define an implementation", params);
		case INVALID_NON_EMPTY_CONSTRUCTOR_IN_AMBIENT:
			return String.format("constructor is an ambiant declaration and must have an empty body", params);
		case INVALID_MODIFIER_IN_AMBIENT:
			return String.format("modifier '%s' is not allowed in an ambiant declaration", params);
		case INDEXED_SET_TYPE_MISMATCH:
			return String.format("type mismatch, expecting '%s' (inferred from the indexed getter type)", params);
		case UNION_TYPE_MISMATCH:
			return String.format("type mismatch in union type", params);
		case BUNDLE_WITH_COMMONJS:
			return String.format("no bundle file generated: choose the 'commonjs' module kind when specifying a bundle file", params);
		case BUNDLE_HAS_CYCLE:
			return String.format("no bundle file generated: cycle detected in package graph %s", params);
		case BUNDLE_HAS_NO_ENTRIES:
			return String.format("no bundle file generated: no entries found, you must define at least one main method", params);
		case PACKAGE_NAME_CONTAINS_KEYWORD:
			return String.format("a package name cannot contain top-level keyword(s): '%s'", params);
		case WILDCARD_IMPORT:
			return String.format("imports cannot use * wildcards: please import a specific element", params);
		case ENCLOSED_ROOT_PACKAGES:
			return String.format("invalid package hierarchy: @Root package '%s' cannot be enclosed in @Root package '%s'", params);
		case CLASS_OUT_OF_ROOT_PACKAGE_SCOPE:
			return String.format("invalid package hierarchy: type '%s' is declared in a parent of @Root package '%s'", params);
		case WRONG_USE_OF_AMBIENT:
			return String.format("wrong use of @Ambient on '%s': only types and globals can be declared as ambients", params);
		case CANDY_VERSION_DISCREPANCY:
			return String.format("candy %s:%s was generated for a different version of the transpiler (current:%s, candy:%s)", params);
		case GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS:
			return String.format("Globals classes can only define static members", params);
		case GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS:
			return String.format("Globals classes cannot extend any class", params);
		case GLOBALS_CLASS_CANNOT_BE_SUBCLASSED:
			return String.format("Globals classes cannot be subclassed", params);
		case CANNOT_ACCESS_STATIC_MEMBER_ON_THIS:
			return String.format("Member '%s' is static and cannot be accessed on 'this'", params);
		}
		return null;
	}

}