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
package org.jsweet;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class contains static constants and utilities.
 * 
 * @author Renaud Pawlak
 */
public abstract class JSweetConfig {

	private static Logger logger = Logger.getLogger(JSweetConfig.class);

	private JSweetConfig() {
	}

	/**
	 * The properties coming from application.properties.
	 */
	public static Properties APPLICATION_PROPERTIES = new Properties();

	static {
		try (InputStream in = JSweetConfig.class.getResourceAsStream("/application.properties")) {
			APPLICATION_PROPERTIES.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The version coming from Maven.
	 */
	public static String getVersionNumber() {
		return APPLICATION_PROPERTIES.getProperty("application.version");
	}

	/**
	 * The build date coming from Maven.
	 */
	public static String getBuildDate() {
		return APPLICATION_PROPERTIES.getProperty("application.buildDate");
	}

	/**
	 * Initialize the classpath to include tools.jar.
	 * 
	 * @param jdkHome
	 *            the jdkHome option value (if not set or not found, fall back
	 *            to the JAVA_HOME environment variable)
	 * @param handler
	 *            the transpilation handler that should report an error if
	 *            tools.jar is not found (if null uses the default logger)
	 */
	public static void initClassPath(String jdkHome) {
		try {
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			boolean found = false;
			for (URL url : urlClassLoader.getURLs()) {
				if (url.getPath().endsWith("/tools.jar") || url.getPath().endsWith("/Classes/classes.jar")) {
					found = true;
					logger.debug("tools.jar already in classpath");
					break;
				}
			}
			if (!found) {
				logger.debug("adding tools.jar in classpath");
				File toolsLib = null;
				if (!StringUtils.isBlank(jdkHome)) {
					logger.debug("lookup in " + jdkHome);
					toolsLib = new File(jdkHome, "lib/tools.jar");
					if (!toolsLib.exists()) {
						// we may be pointing to the JDK's jre
						toolsLib = new File(jdkHome, "../lib/tools.jar");
					}
					// for Mac
					if (!toolsLib.exists()) {
						toolsLib = new File(jdkHome, "/Classes/classes.jar");
					}
					if (!toolsLib.exists()) {
						toolsLib = new File(jdkHome, "../Classes/classes.jar");
					}
				}
				if (toolsLib == null || !toolsLib.exists()) {
					logger.debug("lookup in JAVA_HOME=" + System.getenv("JAVA_HOME"));
					toolsLib = new File(System.getenv("JAVA_HOME"), "lib/tools.jar");
					if (!toolsLib.exists()) {
						// we may be pointing to the JDK's jre
						toolsLib = new File(System.getenv("JAVA_HOME"), "../lib/tools.jar");
					}
					// for Mac
					if (!toolsLib.exists()) {
						toolsLib = new File(System.getenv("JAVA_HOME"), "/Classes/classes.jar");
					}
					if (!toolsLib.exists()) {
						toolsLib = new File(System.getenv("JAVA_HOME"), "../Classes/classes.jar");
					}
				}
				if (!toolsLib.exists()) {
					return;
				}

				Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				method.invoke(urlClassLoader, toolsLib.toURI().toURL());
				logger.debug("updated classpath with: " + toolsLib);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * The Maven group id where candies are deployed.
	 */
	public static String MAVEN_CANDIES_GROUP = "org.jsweet.candies";

	/**
	 * The Maven artifact full name for the Java override project.
	 */
	public static String MAVEN_JAVA_OVERRIDE_ARTIFACT = "jsweet-core-strict";

	private final static String JAVA_PACKAGE = "java";
	private final static String JAVAX_PACKAGE = "javax";
	private final static String ROOT_PACKAGE = "jsweet";
	/** The constant for the JSweet lang package. */
	public final static String LANG_PACKAGE = ROOT_PACKAGE + ".lang";
	/** The constant for the JSweet util package. */
	public final static String UTIL_PACKAGE = ROOT_PACKAGE + ".util";
	/** The constant for the JSweet dom package. */
	public final static String DOM_PACKAGE = ROOT_PACKAGE + ".dom";
	/**
	 * The constant for the JSweet lib package (where the definitions need to
	 * be).
	 */
	public final static String LIBS_PACKAGE = "def";
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
	/**
	 * The constant for the interface name that contains all the generated
	 * string types (short name).
	 */
	public static final String STRING_TYPES_INTERFACE_NAME = "StringTypes";
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
	public static final String INDEXED_GET_STATIC_FUCTION_NAME = "$getStatic";
	/** The constant for indexed assignment function. */
	public static final String INDEXED_SET_STATIC_FUCTION_NAME = "$setStatic";
	public static final String INDEXED_DELETE_STATIC_FUCTION_NAME = "$deleteStatic";
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

	/**
	 * The TypeScript module file names, when transpiling with modules (without
	 * extension).
	 */
	public static final String MODULE_FILE_NAME = "module";

	public static final String OBJECT_CLASSNAME = JSweetConfig.LANG_PACKAGE + ".Object";

	public static final String ANNOTATION_DISABLED = JSweetConfig.LANG_PACKAGE + ".Disabled";
	public static final String ANNOTATION_ERASED = JSweetConfig.LANG_PACKAGE + ".Erased";
	public static final String ANNOTATION_AMBIENT = JSweetConfig.LANG_PACKAGE + ".Ambient";
	public static final String ANNOTATION_MIXIN = JSweetConfig.LANG_PACKAGE + ".Mixin";
	public static final String ANNOTATION_OBJECT_TYPE = JSweetConfig.LANG_PACKAGE + ".ObjectType";
	public static final String ANNOTATION_MODULE = JSweetConfig.LANG_PACKAGE + ".Module";
	public static final String ANNOTATION_INTERFACE = JSweetConfig.LANG_PACKAGE + ".Interface";
	public static final String ANNOTATION_OPTIONAL = JSweetConfig.LANG_PACKAGE + ".Optional";
	public static final String ANNOTATION_STRING_TYPE = JSweetConfig.LANG_PACKAGE + ".StringType";
	public static final String ANNOTATION_ROOT = JSweetConfig.LANG_PACKAGE + ".Root";
	public static final String ANNOTATION_NAME = JSweetConfig.LANG_PACKAGE + ".Name";
	public static final String ANNOTATION_DECORATOR = JSweetConfig.LANG_PACKAGE + ".Decorator";
	public static final String ANNOTATION_FUNCTIONAL_INTERFACE = FunctionalInterface.class.getName();

	/**
	 * This map contains the Java keywords that are taken into account in the
	 * generation for avoiding keyword clashes.
	 */
	public static final Set<String> JAVA_KEYWORDS = new HashSet<String>();

	/**
	 * This map contains the JS keywords that are taken into account in the
	 * generation for avoiding keyword clashes.
	 */
	public static final Set<String> JS_KEYWORDS = new HashSet<String>();

	/**
	 * This map contains the TS keywords that are taken into account in strict
	 * mode (within classes).
	 */
	public static final Set<String> TS_STRICT_MODE_KEYWORDS = new HashSet<String>();

	/**
	 * This map contains the TS keywords that are taken into account at top
	 * level.
	 */
	public static final Set<String> TS_TOP_LEVEL_KEYWORDS = new HashSet<String>();

	/**
	 * The prefix to add to variables that clash with JS keywords.
	 */
	public static final String JS_KEYWORD_PREFIX = "__";

	/**
	 * The prefix to add to variables that clash with methods.
	 */
	public static final String FIELD_METHOD_CLASH_RESOLVER_PREFIX = "__";

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

		JS_KEYWORDS.add("function");
		JS_KEYWORDS.add("var");
		JS_KEYWORDS.add("typeof");
		JS_KEYWORDS.add("in");

		TS_STRICT_MODE_KEYWORDS.add("as");
		TS_STRICT_MODE_KEYWORDS.add("implements");
		TS_STRICT_MODE_KEYWORDS.add("interface");
		TS_STRICT_MODE_KEYWORDS.add("let");
		TS_STRICT_MODE_KEYWORDS.add("package");
		TS_STRICT_MODE_KEYWORDS.add("private");
		TS_STRICT_MODE_KEYWORDS.add("protected");
		TS_STRICT_MODE_KEYWORDS.add("public");
		TS_STRICT_MODE_KEYWORDS.add("static");
		TS_STRICT_MODE_KEYWORDS.add("yield");
		TS_STRICT_MODE_KEYWORDS.add("symbol");
		TS_STRICT_MODE_KEYWORDS.add("type");
		TS_STRICT_MODE_KEYWORDS.add("from");
		TS_STRICT_MODE_KEYWORDS.add("of");

		TS_TOP_LEVEL_KEYWORDS.add("require");
	}

	public static boolean isJDKReplacementMode() {
		return "java.lang".equals(LANG_PACKAGE);
	}

	/**
	 * Gets the JSweet object's fully qualified name.
	 */
	public static String getObjectClassName() {
		return LANG_PACKAGE + ".Object";
	}

	/**
	 * Tells if this qualified name belongs to the JDK (starts with
	 * {@value #JAVA_PACKAGE} or {@value #JAVAX_PACKAGE}).
	 */
	public static boolean isJDKPath(String qualifiedName) {
		return qualifiedName.startsWith(JAVA_PACKAGE + ".") || qualifiedName.startsWith(JAVAX_PACKAGE + ".");
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
		return qualifiedName.startsWith(ROOT_PACKAGE + ".");
	}

}
