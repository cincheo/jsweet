/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
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
package org.jsweet;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.jsweet.input.typescriptdef.ast.Context;

/**
 * This class contains static constants and utilities.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public abstract class JSweetDefTranslatorConfig {

	private JSweetDefTranslatorConfig() {
	}

	/**
	 * The properties coming from application.properties.
	 */
	public static Properties APPLICATION_PROPERTIES = new Properties();

	static {
		try (InputStream in = JSweetDefTranslatorConfig.class.getResourceAsStream("/application.properties")) {
			APPLICATION_PROPERTIES.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the version number (not including suffix).
	 */
	public static String getVersionNumber() {
		return APPLICATION_PROPERTIES.getProperty("application.version").split("-")[0];
	}

	/**
	 * The Maven group id where candies are deployed.
	 */
	public final static String MAVEN_CANDIES_GROUP = "org.jsweet.candies";

	private final static String JAVA_PACKAGE = "java";

	/** The constant for the JSweet lang package. */
	public final static String LANG_PACKAGE = "def.js";

	public final static String ANNOTATIONS_PACKAGE = "jsweet.lang";

	/** The constant for the JSweet util package. */
	public final static String UTIL_PACKAGE = "jsweet.util";
	/**
	 * The constant for the JSweet lib package (where the definitions need to
	 * be).
	 */
	public final static String LIBS_PACKAGE = "def";
	/** The constant for the JSweet dom package. */
	public final static String DOM_PACKAGE = LIBS_PACKAGE + ".dom";
	/**
	 * The constant for the package generates top-level classes (one cannot use
	 * unnamed package in Java).
	 */
	public static final String GLOBALS_PACKAGE_NAME = "globals";
	/**
	 * The constant for the classes where members are generated as top-level
	 * elements (global variables and functions).
	 */
	public static final String GLOBALS_CLASS_NAME = "Globals";
	/** The constant for predefined utilities. */
	public static final String UTIL_CLASSNAME = UTIL_PACKAGE + ".Globals";

	/** The constant for the function classes package. */
	public static final String FUNCTION_CLASSES_PACKAGE = UTIL_PACKAGE + ".function";
	/** The constant for the tuple classes package. */
	public static final String TUPLE_CLASSES_PACKAGE = UTIL_PACKAGE + ".tuple";
	/** The constant for the tuple classes prefix. */
	public static final String TUPLE_CLASSES_PREFIX = "Tuple";
	/** The constant for the package containing union types. */
	public static final String UNION_PACKAGE = UTIL_PACKAGE + ".union";
	/** The constant for the Union core class full name. */
	public static final String UNION_CLASS_NAME = UNION_PACKAGE + ".Union";
	/** The constant for indexed access function. */
	public static final String INDEXED_GET_FUCTION_NAME = "$get";
	/** The constant for indexed assignment function. */
	public static final String INDEXED_SET_FUCTION_NAME = "$set";
	public static final String INDEXED_DELETE_FUCTION_NAME = "$delete";
	public static final String NEW_FUNCTION_NAME = "$new";
	public static final String ANONYMOUS_FUNCTION_NAME = "apply";
	public static final String ANONYMOUS_STATIC_FUNCTION_NAME = "applyStatic";

	/**
	 * Default name of the directory where the TypeScript definition files can
	 * be found.
	 */
	public static final String TS_LIBS_DIR_NAME = "typings";

	/**
	 * The constant for main functions (translate to global code, which is
	 * executed when the file is loaded).
	 */
	public static final String MAIN_FUNCTION_NAME = "main";

	public static final String OBJECT_CLASSNAME = JSweetDefTranslatorConfig.LANG_PACKAGE + ".Object";

	public static final String ANNOTATION_DISABLED = ANNOTATIONS_PACKAGE + ".Disabled";
	public static final String ANNOTATION_ERASED = ANNOTATIONS_PACKAGE + ".Erased";
	public static final String ANNOTATION_SYNTACTIC_ITERABLE = ANNOTATIONS_PACKAGE + ".SyntacticIterable";
	public static final String ANNOTATION_AMBIENT = ANNOTATIONS_PACKAGE + ".Ambient";
	public static final String ANNOTATION_MIXIN = ANNOTATIONS_PACKAGE + ".Mixin";
	public static final String ANNOTATION_OBJECT_TYPE = ANNOTATIONS_PACKAGE + ".ObjectType";
	public static final String ANNOTATION_INTERFACE = ANNOTATIONS_PACKAGE + ".Interface";
	public static final String ANNOTATION_OPTIONAL = ANNOTATIONS_PACKAGE + ".Optional";
	public static final String ANNOTATION_STRING_TYPE = ANNOTATIONS_PACKAGE + ".StringType";
	public static final String ANNOTATION_MODULE = ANNOTATIONS_PACKAGE + ".Module";
	public static final String ANNOTATION_ROOT = ANNOTATIONS_PACKAGE + ".Root";
	public static final String ANNOTATION_NAME = ANNOTATIONS_PACKAGE + ".Name";
	public static final String ANNOTATION_EXTENDS = ANNOTATIONS_PACKAGE + ".Extends";
	public static final String ANNOTATION_FUNCTIONAL_INTERFACE = FunctionalInterface.class.getName();

	/**
	 * This map contains the Java keywords that are taken into account in the
	 * generation for avoiding keyword clashes.
	 */
	public static final Set<String> JAVA_KEYWORDS = new HashSet<String>();

	public static final Set<String> JAVA_TS_KEYWORDS = new HashSet<String>();

	/**
	 * This map contains the JS keywords that are taken into account in the
	 * generation for avoiding keyword clashes.
	 */
	public static final Set<String> JS_KEYWORDS = new HashSet<String>();

	static {
		// note TS keywords are removed from that list
		JAVA_KEYWORDS.add("abstract");
		JAVA_KEYWORDS.add("assert");
		// JAVA_KEYWORDS.add("boolean");
		JAVA_KEYWORDS.add("break");
		JAVA_KEYWORDS.add("byte");
		JAVA_KEYWORDS.add("case");
		JAVA_KEYWORDS.add("catch");
		JAVA_KEYWORDS.add("char");
		// JAVA_KEYWORDS.add("class");
		JAVA_KEYWORDS.add("const");
		JAVA_KEYWORDS.add("continue");
		JAVA_KEYWORDS.add("default");
		JAVA_KEYWORDS.add("do");
		JAVA_KEYWORDS.add("double");
		JAVA_KEYWORDS.add("else");
		// JAVA_KEYWORDS.add("enum");
		JAVA_KEYWORDS.add("extends");
		JAVA_KEYWORDS.add("final");
		JAVA_KEYWORDS.add("finally");
		JAVA_KEYWORDS.add("float");
		JAVA_KEYWORDS.add("for");
		JAVA_KEYWORDS.add("goto");
		JAVA_KEYWORDS.add("if");
		// JAVA_KEYWORDS.add("implements");
		JAVA_KEYWORDS.add("import");
		JAVA_KEYWORDS.add("instanceof");
		JAVA_KEYWORDS.add("int");
		// JAVA_KEYWORDS.add("interface");
		JAVA_KEYWORDS.add("long");
		JAVA_KEYWORDS.add("native");
		JAVA_KEYWORDS.add("new");
		JAVA_KEYWORDS.add("package");
		JAVA_KEYWORDS.add("private");
		JAVA_KEYWORDS.add("protected");
		JAVA_KEYWORDS.add("public");
		JAVA_KEYWORDS.add("return");
		JAVA_KEYWORDS.add("short");
		JAVA_KEYWORDS.add("static");
		JAVA_KEYWORDS.add("strictfp");
		JAVA_KEYWORDS.add("super");
		JAVA_KEYWORDS.add("switch");
		JAVA_KEYWORDS.add("synchronized");
		JAVA_KEYWORDS.add("this");
		JAVA_KEYWORDS.add("throw");
		JAVA_KEYWORDS.add("throws");
		JAVA_KEYWORDS.add("transient");
		JAVA_KEYWORDS.add("try");
		// JAVA_KEYWORDS.add("void");
		JAVA_KEYWORDS.add("volatile");
		JAVA_KEYWORDS.add("while");

		JAVA_TS_KEYWORDS.add("boolean");
		JAVA_TS_KEYWORDS.add("class");
		JAVA_TS_KEYWORDS.add("enum");
		JAVA_TS_KEYWORDS.add("implements");
		JAVA_TS_KEYWORDS.add("interface");
		JAVA_TS_KEYWORDS.add("void");

		JS_KEYWORDS.add("function");
		JS_KEYWORDS.add("var");
		JS_KEYWORDS.add("typeof");
	}

	/**
	 * This function return a Javascript-friendly identifier from a
	 * Java-formatted one.
	 * 
	 * @param identifier
	 *            the Java-formatted identifier
	 * @return the Javascript-friendly identifier
	 */
	public static String toJsIdentifier(String identifier) {
		if (!identifier.isEmpty() && Character.isUpperCase(identifier.charAt(0))
				&& JSweetDefTranslatorConfig.JAVA_KEYWORDS.contains(identifier.toLowerCase())) {
			return identifier.toLowerCase();
		}
		// TODO: translate $$
		// if (Character.isDigit(identifier.charAt(0))) {
		// return "$$" + identifier.charAt(0) + "$$" + identifier.substring(1);
		// }
		return identifier;
	}

	/**
	 * The constant for the interface name that contains generated string types
	 * (short name / typed strings).
	 */
	public static final String STRING_TYPES_INTERFACE_NAME = "StringTypes";

	/**
	 * Each lib module has holds a list of string types, this methods returns
	 * the full class name of this StringTypes class for the given lib module
	 * 
	 * @see Context#getLibModule(String)
	 */
	public static String getStringTypesClassName(String libModule) {
		return libModule + "." + STRING_TYPES_INTERFACE_NAME;
	}

	public static boolean isJDKReplacementMode() {
		return "java.lang".equals(LANG_PACKAGE);
	}

	/**
	 * Gets the JSweet object's fully qualified name.
	 */
	public static String getObjectClassName() {
		return OBJECT_CLASSNAME;
	}

	/**
	 * Tells if this qualified name belongs to the JDK (starts with
	 * {@value #JAVA_PACKAGE}).
	 */
	public static boolean isJDKPath(String qualifiedName) {
		return qualifiedName.startsWith(JAVA_PACKAGE + ".");
	}

	/**
	 * Tells if this qualified name belongs to any TypeScript library definition
	 * (starts with {@value #LIBS_PACKAGE}).
	 */
	public static boolean isLibPath(String qualifiedName) {
		return qualifiedName.startsWith(LIBS_PACKAGE + ".");
	}

	/**
	 * Tells if this qualified name belongs to one of the JSweet core package
	 * (starts with {@value #ROOT_PACKAGE}).
	 */
	public static boolean isJSweetPath(String qualifiedName) {
		return qualifiedName.startsWith(LANG_PACKAGE) //
				|| qualifiedName.startsWith(UTIL_PACKAGE) //
				|| qualifiedName.startsWith(DOM_PACKAGE);
	}

}
