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
@SuppressWarnings("serial")
public abstract class JSweetConfig {

	private static Logger logger = Logger.getLogger(JSweetConfig.class);

	private JSweetConfig() {
	}

	/**
	 * The properties coming from application.properties.
	 */
	public static Properties APPLICATION_PROPERTIES = new Properties();

	static {
		try (InputStream in = JSweetConfig.class.getResourceAsStream("/jsweet-transpiler-application.properties")) {
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
			boolean foundExtension = false;
			for (URL url : urlClassLoader.getURLs()) {
				if (url.getPath().endsWith("/" + EXTENSION_DIR)) {
					foundExtension = true;
					logger.debug("extension dir already in classpath");
					break;
				}
			}
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);

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
				if (toolsLib.exists()) {
					method.invoke(urlClassLoader, toolsLib.toURI().toURL());
					logger.debug("updated classpath with: " + toolsLib);
				}
			}
			if (!foundExtension) {
				method.invoke(urlClassLoader, new File(EXTENSION_DIR).toURI().toURL());
				logger.debug("updated classpath with: " + new File(EXTENSION_DIR).toURI().toURL());
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

	/**
	 * The constant for the JSweet lib package (where the definitions need to
	 * be).
	 */
	public final static String LIBS_PACKAGE = "def";
	private final static String JAVA_PACKAGE = "java";
	private final static String JAVAX_PACKAGE = "javax";
	private final static String ROOT_PACKAGE = "jsweet";
	/** The constant for the JSweet lang package. */
	public final static String LANG_PACKAGE = ROOT_PACKAGE + ".lang";
	/** The constant for the JSweet lang package. */
	public final static String LANG_PACKAGE_ALT = LIBS_PACKAGE + ".js";
	/** The constant for the JSweet util package. */
	public final static String UTIL_PACKAGE = ROOT_PACKAGE + ".util";
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
	public static final String DEPRECATED_UTIL_CLASSNAME = UTIL_PACKAGE + ".Globals";
	public static final String UTIL_CLASSNAME = UTIL_PACKAGE + ".Lang";
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
	/** The constant for dynamic invoke function. */
	public static final String INVOKE_FUCTION_NAME = "$invoke";
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

	public static final String ANONYMOUS_DEPRECATED_FUNCTION_NAME = "apply";
	public static final String ANONYMOUS_FUNCTION_NAME = "$apply";
	public static final String ANONYMOUS_DEPRECATED_STATIC_FUNCTION_NAME = "applyStatic";
	public static final String ANONYMOUS_STATIC_FUNCTION_NAME = "$applyStatic";

	public static final String EXTENSION_DIR = "jsweet_extension";

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

	/**
	 * The fully qualified name of a JavaScript Object in JSweet.
	 */
	public static final String OBJECT_CLASSNAME = JSweetConfig.LANG_PACKAGE_ALT + ".Object";

	/**
	 * Fully-qualified name for the JSweet <code>@Disabled</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_DISABLED = JSweetConfig.LANG_PACKAGE + ".Disabled";
	/**
	 * Fully-qualified name for the JSweet <code>@Erased</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_ERASED = JSweetConfig.LANG_PACKAGE + ".Erased";
	/**
	 * Fully-qualified name for the JSweet <code>@Async</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_ASYNC = JSweetConfig.LANG_PACKAGE + ".Async";
	/**
	 * Fully-qualified name for the JSweet <code>@SyntacticIterable</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_SYNTACTIC_ITERABLE = JSweetConfig.LANG_PACKAGE + ".SyntacticIterable";
	/**
	 * Fully-qualified name for the JSweet <code>@Ambient</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_AMBIENT = JSweetConfig.LANG_PACKAGE + ".Ambient";
	/**
	 * Fully-qualified name for the JSweet <code>@Mixin</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_MIXIN = JSweetConfig.LANG_PACKAGE + ".Mixin";
	/**
	 * Fully-qualified name for the JSweet <code>@ObjectType</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_OBJECT_TYPE = JSweetConfig.LANG_PACKAGE + ".ObjectType";
	/**
	 * Fully-qualified name for the JSweet <code>@Module</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_MODULE = JSweetConfig.LANG_PACKAGE + ".Module";
	/**
	 * Fully-qualified name for the JSweet <code>@Interface</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_INTERFACE = JSweetConfig.LANG_PACKAGE + ".Interface";
	/**
	 * Fully-qualified name for the JSweet <code>@Optional</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_OPTIONAL = JSweetConfig.LANG_PACKAGE + ".Optional";
	/**
	 * Fully-qualified name for the JSweet <code>@StringType</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_STRING_TYPE = JSweetConfig.LANG_PACKAGE + ".StringType";
	/**
	 * Fully-qualified name for the JSweet <code>@Root</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_ROOT = JSweetConfig.LANG_PACKAGE + ".Root";
	/**
	 * Fully-qualified name for the JSweet <code>@Name</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_NAME = JSweetConfig.LANG_PACKAGE + ".Name";
	/**
	 * Fully-qualified name for the JSweet <code>@Decorator</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_DECORATOR = JSweetConfig.LANG_PACKAGE + ".Decorator";
	/**
	 * Fully-qualified name for the JSweet <code>@Replace</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_REPLACE = JSweetConfig.LANG_PACKAGE + ".Replace";
	/**
	 * Fully-qualified name for the JSweet <code>@Generator</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_GENERATOR = JSweetConfig.LANG_PACKAGE + ".Generator";
	/**
	 * Fully-qualified name for the JSweet <code>@SNoDebug</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_NO_DEBUG = JSweetConfig.LANG_PACKAGE + ".NoDebug";
	/**
	 * Fully-qualified name for the JSweet <code>@WrapParameters</code> annotation (see JSweet core API).
	 */
	public static final String ANNOTATION_WRAP_PARAMETERS = JSweetConfig.LANG_PACKAGE + ".WrapParameters";
	/**
	 * Fully-qualified name for the built-in Java {@link FunctionalInterface} annotation.
	 */
	public static final String ANNOTATION_FUNCTIONAL_INTERFACE = FunctionalInterface.class.getName();

	/**
	 * This map contains the Java keywords that are taken into account in the
	 * generation for avoiding keyword clashes.
	 */
	public static final Set<String> JAVA_KEYWORDS = new HashSet<String>() {
		{
			// note TS keywords are removed from that list
			add("abstract");
			add("assert");
			// add("boolean");
			add("break");
			add("byte");
			add("case");
			add("catch");
			add("char");
			// add("class");
			add("const");
			add("continue");
			add("default");
			add("do");
			add("double");
			add("else");
			// add("enum");
			add("extends");
			add("final");
			add("finally");
			add("float");
			add("for");
			add("goto");
			add("if");
			// add("implements");
			add("import");
			add("instanceof");
			add("int");
			// add("interface");
			add("long");
			add("native");
			add("new");
			add("package");
			add("private");
			add("protected");
			add("public");
			add("return");
			add("short");
			add("static");
			add("strictfp");
			add("super");
			add("switch");
			add("synchronized");
			add("this");
			add("throw");
			add("throws");
			add("transient");
			add("try");
			// add("void");
			add("volatile");
			add("while");
		}
	};

	/**
	 * This map contains the JS keywords that are taken into account in the
	 * generation for avoiding keyword clashes.
	 */
	public static final Set<String> JS_KEYWORDS = new HashSet<String>() {
		{
			add("function");
			add("var");
			add("delete");
			add("typeof");
			add("in");
			add("arguments");
			add("eval");
		}
	};

	/**
	 * This map contains the TS keywords that are taken into account in strict
	 * mode (within classes).
	 */
	public static final Set<String> TS_STRICT_MODE_KEYWORDS = new HashSet<String>() {
		{
			add("as");
			add("implements");
			add("interface");
			add("let");
			add("package");
			add("private");
			add("protected");
			add("public");
			add("static");
			add("yield");
			add("symbol");
			add("type");
			add("from");
			add("of");
		}
	};

	/**
	 * This map contains the TS keywords that are taken into account at top
	 * level.
	 */
	public static final Set<String> TS_TOP_LEVEL_KEYWORDS = new HashSet<String>() {
		{
			add("require");
		}
	};

	/**
	 * This collection contains the forbidden characters in TS identifiers
	 */
	public static final Set<Character> TS_IDENTIFIER_FORBIDDEN_CHARS = new HashSet<Character>() {
		{
			add('-');
			add('+');
			add('~');
			add('&');
			add('#');
			add('%');
			add('|');
			add('°');
			add('@');
		}
	};

	/**
	 * The prefix to add to variables that clash with JS keywords.
	 */
	public static final String JS_KEYWORD_PREFIX = "__";

	/**
	 * The prefix to add to variables that clash with methods.
	 */
	public static final String FIELD_METHOD_CLASH_RESOLVER_PREFIX = "__";

	/**
	 * The default configuration file name.
	 */
	public static final String CONFIGURATION_FILE_NAME = "jsweetconfig.json";

	public static boolean isJDKReplacementMode() {
		return "java.lang".equals(LANG_PACKAGE);
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
	 * Tells if this qualified name belongs to one of the JSweet core package.
	 */
	public static boolean isJSweetPath(String qualifiedName) {
		return qualifiedName.startsWith(ROOT_PACKAGE + ".") || qualifiedName.startsWith(LANG_PACKAGE_ALT + ".")
				|| qualifiedName.startsWith(DOM_PACKAGE + ".");
	}

}
