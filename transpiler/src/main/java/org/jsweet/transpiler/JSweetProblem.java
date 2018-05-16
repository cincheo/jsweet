/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.transpiler;

import org.jsweet.JSweetConfig;

/**
 * This enumeration holds all the possible JSweet transpilation problems
 * (warnings and errors) and the associated messages.
 * 
 * @author Renaud Pawlak
 */
public enum JSweetProblem {

	/**
	 * A user error.
	 */
	USER_ERROR(Severity.ERROR),
	/**
	 * A user warning.
	 */
	USER_WARNING(Severity.WARNING),
	/**
	 * A user message.
	 */
	USER_MESSAGE(Severity.MESSAGE),
	/**
	 * Raised when the JDK is not found (hence tools.jar).
	 */
	JAVA_COMPILER_NOT_FOUND(Severity.ERROR),
	/**
	 * Raised when the Java compiler reports an error.
	 */
	INTERNAL_JAVA_ERROR(Severity.ERROR),
	/**
	 * Raised when the transpiler meets an error while scanning the program's
	 * AST.
	 */
	INTERNAL_TRANSPILER_ERROR(Severity.ERROR),
	/**
	 * Raised when the Tsc transpiler reports an error.
	 */
	INTERNAL_TSC_ERROR(Severity.ERROR),
	/**
	 * Raised when the Tsc transpiler reports an error that can be mapped to the
	 * original source file.
	 */
	MAPPED_TSC_ERROR(Severity.ERROR),
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
	 * Raised when Node version is obsolete and should be updated
	 */
	NODE_OBSOLETE_VERSION(Severity.WARNING),
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
	 * Raised when the program tries to use the synchronized keyword.
	 */
	SYNCHRONIZATION(Severity.WARNING),
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
	 * Raised when a method invocation is hidden by a local variable or
	 * parameter.
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
	 * Raised when an overload parameter is not valid.
	 */
	INVALID_OVERLOAD_PARAMETER(Severity.ERROR),
	/**
	 * Raised when a member is named 'constructor'.
	 */
	CONSTRUCTOR_MEMBER(Severity.ERROR),
	/**
	 * Raised when a native modifier is used at the wrong place (only allowed in
	 * definitions).
	 */
	NATIVE_MODIFIER_IS_NOT_ALLOWED(Severity.ERROR),
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
	 * Raised when trying to create a bundle with a module kind selected.
	 */
	BUNDLE_WITH_MODULE(Severity.ERROR),
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
	 * Raised when several @Root packages are used with the module option (the
	 * default package being considered as a @Root package).
	 */
	MULTIPLE_ROOT_PACKAGES_NOT_ALLOWED_WITH_MODULES(Severity.ERROR),
	/**
	 * Raised when using a candy which was generated for an older / newer
	 * version of the transpiler
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
	 * Raised when trying to access this from scope it isn't defined.
	 */
	CANNOT_ACCESS_THIS(Severity.ERROR),
	/**
	 * Raised when invoking a static method on this (this is allowed in Java,
	 * but not in JSweet).
	 */
	CANNOT_ACCESS_STATIC_MEMBER_ON_THIS(Severity.ERROR),
	/**
	 * Raised when a <code>$object</code> method is invoked with an odd number
	 * of parameters.
	 */
	UNTYPED_OBJECT_ODD_PARAMETER_COUNT(Severity.ERROR),
	/**
	 * Raised when a <code>$object</code> method is invoked with a key that is
	 * not a string literal.
	 */
	UNTYPED_OBJECT_WRONG_KEY(Severity.ERROR),
	/**
	 * Raised when a cycle is detected in static initializers.
	 */
	CYCLE_IN_STATIC_INITIALIZER_DEPENDENCIES(Severity.ERROR),
	/**
	 * Raised when an insert macro is not used properly.
	 */
	MISUSED_INSERT_MACRO(Severity.ERROR),
	/**
	 * Raised when a template literal is not used properly.
	 */
	MISUSED_TEMPLATE_MACRO(Severity.ERROR),
	/**
	 * Raised when a mixin does not have the same name as its target.
	 */
	WRONG_MIXIN_NAME(Severity.ERROR),
	/**
	 * Raised when a mixin targets itself.
	 */
	SELF_MIXIN_TARGET(Severity.ERROR),
	/**
	 * Raised when a decorator annotation does not declare any associated
	 * function.
	 */
	CANNOT_FIND_GLOBAL_DECORATOR_FUNCTION(Severity.ERROR);

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
		case USER_ERROR:
		case USER_WARNING:
		case USER_MESSAGE:
			return String.format("%s", params);
		case JAVA_COMPILER_NOT_FOUND:
			return String.format(
					"Java compiler cannot be found: make sure that JAVA_HOME points to a JDK (version>=8) and not a JRE, or sets the transpiler jdkHome option",
					params);
		case INTERNAL_JAVA_ERROR:
			return String.format("%s", params);
		case INTERNAL_TSC_ERROR:
			return String.format("internal TypeScript error: %s", params);
		case MAPPED_TSC_ERROR:
			return String.format("%s", params);
		case NODE_CANNOT_START:
			return String.format(
					"cannot find Node.js: install first and make sure that the 'node' command is in your execution path",
					params);
		case NODE_OBSOLETE_VERSION:
			return String.format("Node.js should be upgraded: %s < %s (recommended)", params);
		case TSC_CANNOT_START:
			return String.format(
					"cannot find TypeScript compiler: install first and make sure that the 'tsc' command is in your execution path",
					params);
		case JDK_TYPE:
			return String.format("invalid access to JDK type '%s' from JSweet", params);
		case JDK_METHOD:
			return String.format("invalid access to JDK method '%s' from JSweet", params);
		case ERASED_METHOD:
			return String.format("invalid access to erased method '%s'", params);
		case SYNCHRONIZATION:
			return String.format("synchronization is ignored in JSweet", params);
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
			return String.format("field '%s' is not optional (see @Optional) but has not been initialized", params);
		case USELESS_OPTIONAL_ANNOTATION:
			return String.format(
					"useless @Optional field %s (fields are optional by default in classes, use @Interface to define %s as an interface)",
					params);
		case JS_KEYWORD_CONFLICT:
			return String.format("local variable name '%s' is not allowed and is automatically generated to '"
					+ JSweetConfig.JS_KEYWORD_PREFIX + "%s'", params);
		case INVALID_METHOD_BODY_IN_INTERFACE:
			return String.format(
					"method '%s' cannot define a body in interface '%s' (try 'abstract' or 'native' modifiers)",
					params);
		case INVALID_PRIVATE_IN_INTERFACE:
			return String.format("member '%s' cannot be private in interface '%s'", params);
		case INVALID_FIELD_INITIALIZER_IN_INTERFACE:
			return String.format("field '%s' cannot be initialized in interface '%s'", params);
		case INVALID_INITIALIZER_IN_INTERFACE:
			return String.format("no initialization blocks are allowed in interface '%s'", params);
		case INVALID_OVERLOAD:
			return String.format("invalid overload of method '%s'", params);
		case INVALID_OVERLOAD_PARAMETER:
			return String.format("overloaded methods can only be invoked with literal parameters (constants)", params);
		case CONSTRUCTOR_MEMBER:
			return String.format("invalid member name 'constructor'", params);
		case NATIVE_MODIFIER_IS_NOT_ALLOWED:
			return String.format("method '%s' cannot be native", params);
		case TRY_WITHOUT_CATCH_OR_FINALLY:
			return String.format("try statement must define at least a catch or a finally clause", params);
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
		case BUNDLE_WITH_MODULE:
			return String.format("bundle and module options are exclusive: choose one or the other", params);
		case PACKAGE_NAME_CONTAINS_KEYWORD:
			return String.format("a package name cannot contain top-level keyword(s): '%s'", params);
		case WILDCARD_IMPORT:
			return String.format("imports cannot use * wildcards when using modules: please import a specific element",
					params);
		case ENCLOSED_ROOT_PACKAGES:
			return String.format(
					"invalid package hierarchy: @Root package '%s' cannot be enclosed in @Root package '%s'", params);
		case MULTIPLE_ROOT_PACKAGES_NOT_ALLOWED_WITH_MODULES:
			return String.format(
					"multipe @Root packages (including the default 'null' package) are not allowed when using modules, found packages: %s",
					params);
		case CLASS_OUT_OF_ROOT_PACKAGE_SCOPE:
			return String.format("invalid package hierarchy: type '%s' is declared in a parent of @Root package '%s'",
					params);
		case WRONG_USE_OF_AMBIENT:
			return String.format("wrong use of @Ambient on '%s': only types and globals can be declared as ambients",
					params);
		case CANDY_VERSION_DISCREPANCY:
			return String.format(
					"candy %s:%s was generated for a different version of the transpiler (current:%s, candy:%s)",
					params);
		case GLOBALS_CAN_ONLY_HAVE_STATIC_MEMBERS:
			return String.format("globals classes can only define static members", params);
		case GLOBALS_CLASS_CANNOT_HAVE_SUPERCLASS:
			return String.format("globals classes cannot extend any class", params);
		case GLOBALS_CLASS_CANNOT_BE_SUBCLASSED:
			return String.format("globals classes cannot be subclassed", params);
		case CANNOT_ACCESS_THIS:
			return String.format("'this' isn't defined in scope of '%s'", params);
		case CANNOT_ACCESS_STATIC_MEMBER_ON_THIS:
			return String.format("member '%s' is static and cannot be accessed on 'this'", params);
		case UNTYPED_OBJECT_ODD_PARAMETER_COUNT:
			return String.format(
					"wrong parameter count: method '$object' expects a list of key/value pairs as parameters", params);
		case UNTYPED_OBJECT_WRONG_KEY:
			return String.format(
					"wrong key: method '$object' expects a list of key/value pairs as parameters, where keys are string literals",
					params);
		case CYCLE_IN_STATIC_INITIALIZER_DEPENDENCIES:
			return String.format("a cycle was detected in static intializers involving '%s'", params);
		case INTERNAL_TRANSPILER_ERROR:
			return String.format("internal transpiler error");
		case MISUSED_INSERT_MACRO:
			return String.format("the '%s' macro argument must be a raw string literal", params);
		case MISUSED_TEMPLATE_MACRO:
			return String.format("the '%s' macro last argument must be a raw string literal", params);
		case WRONG_MIXIN_NAME:
			return String.format("the '%s' mixin must have the same root-relative name as its target ('%s')", params);
		case SELF_MIXIN_TARGET:
			return String.format(
					"the '%s' mixin targets itself but should target another interface/declaration of the same name",
					params);
		case CANNOT_FIND_GLOBAL_DECORATOR_FUNCTION:
			return String.format(
					"the '%s' decorator annotation should be implemented in a global function of the same name, but this function cannot be found",
					params);
		}
		return null;
	}

}