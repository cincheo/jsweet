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

import java.io.File;
import java.util.Map;

public interface JSweetOptions {

	/**
	 * Returns the configuration from the configuration file.
	 */
	Map<String, Map<String, Object>> getConfiguration();

	/**
	 * Tells if the transpiler generates js.map files for Java debugging.
	 */
	boolean isPreserveSourceLineNumbers();

	/**
	 * Gets the source root for the source map files. Use this option if the
	 * Java source is located in a different place at runtime.
	 */
	File getSourceRoot();

	/**
	 * Gets the current TypeScript output directory.
	 */
	File getTsOutputDir();

	/**
	 * Gets the current JavaScript output directory.
	 */
	File getJsOutputDir();

	/**
	 * Gets the current .d.ts output directory (only if the declaration option
	 * is set).
	 * 
	 * <p>
	 * By default, declarations are placed in the JavaScript output directory.
	 */
	File getDeclarationsOutputDir();

	/**
	 * Gets the module kind when transpiling to code using JavaScript modules.
	 */
	ModuleKind getModuleKind();

	/**
	 * Tells if this transpiler generates JavaScript bundles for running in a
	 * Web browser.
	 */
	boolean isBundle();

	/**
	 * Gets the expected Java source code encoding.
	 */
	String getEncoding();

	/**
	 * Tells if this transpiler skips the root directories (packages annotated
	 * with @jsweet.lang.Root) so that the generated file hierarchy starts at
	 * the root directories rather than including the entire directory
	 * structure.
	 */
	boolean isNoRootDirectories();

	/**
	 * Tells if the transpiler should ignore the 'assert' statements or generate
	 * appropriate code.
	 */
	boolean isIgnoreAssertions();

	/**
	 * Generates output code even if the main class is not placed within a file
	 * of the same name.
	 */
	boolean isIgnoreJavaFileNameError();

	/**
	 * Generates d.ts files along with the js files.
	 */
	boolean isGenerateDeclarations();

	/**
	 * The directory where the transpiler should put the extracted JavaScript
	 * files from candies. Candies can bundle one or more JavaScript files that
	 * will be extracted to this directory.
	 */
	File getExtractedCandyJavascriptDir();

	/**
	 * If false, do not compile TypeScript output (let an external TypeScript
	 * compiler do so). Default is true.
	 */
	boolean isGenerateJsFiles();

	/**
	 * If false, do not generate TypeScript output (just validate the Java
	 * source code and do not transpile anything). Default is true.
	 */
	boolean isGenerateTsFiles();

	/**
	 * If true, JSweet will keep track of implemented interfaces in objects at
	 * runtime, so that the instanceof operator can work properly with
	 * interfaces.
	 * 
	 * <p>
	 * If false, instanceof will always return false for an interface. Also, if
	 * false, method overloading will not work efficiently when the arguments
	 * are interfaces, leading to 'invalid overload' errors.
	 * 
	 * <p>
	 * Programmers may want to disable the instanceof operator to have lighter
	 * objects and less polluted JavaScript code. However, they must remain in a
	 * pure JavaScript use case.
	 */
	boolean isInterfaceTracking();

	/**
	 * If true, JSweet will keep track of the class names in the corresponding
	 * constructors, so that the object.getClass().getName() can work properly.
	 */
	boolean isSupportGetClass();

	/**
	 * If true, JSweet will implement a lazy initialization mechanism of static
	 * fields and initializers, to emulate the Java behavior.
	 */
	boolean isSupportSaticLazyInitialization();

	/**
	 * Generated definitions from def.* packages in d.ts files.
	 */
	boolean isGenerateDefinitions();

	/**
	 * If true, JSweet will ignore any message reported by TypeScript (including
	 * error) and the compilation will be successful if no Java error is raised.
	 */
	boolean isIgnoreTypeScriptErrors();

	/**
	 * If true, JSweet will ignore any message reported by Java (including
	 * error).
	 */
	boolean isIgnoreJavaErrors();

	/**
	 * Gets the file containing the header to be added to the generated files.
	 * 
	 * @return the header file, null if undefined
	 */
	File getHeaderFile();

	/**
	 * If true, the transpiler generates code that can be debugged with
	 * JavaScript.
	 */
	boolean isDebugMode();
}