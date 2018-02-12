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
import java.util.List;
import java.util.Map;

import org.jsweet.transpiler.extension.PrinterAdapter;

/**
 * This interface holds all the JSweet transpiler configuration and options.
 * 
 * @author Renaud Pawlak
 */
public interface JSweetOptions {

	/**
	 * Constant string for the 'bundle' option.
	 */
	String bundle = "bundle";
	/**
	 * Constant string for the 'noRootDirectories' option.
	 */
	String noRootDirectories = "noRootDirectories";
	/**
	 * Constant string for the 'sourceMap' option.
	 */
	String sourceMap = "sourceMap";
	/**
	 * Constant string for the 'module' option.
	 */
	String module = "module";
	/**
	 * Constant string for the 'encoding' option.
	 */
	String encoding = "encoding";
	/**
	 * Constant string for the 'enableAssertions' option.
	 */
	String enableAssertions = "enableAssertions";
	/**
	 * Constant string for the 'declaration' option.
	 */
	String declaration = "declaration";
	/**
	 * Constant string for the 'tsOnly' option.
	 */
	String tsOnly = "tsOnly";
	/**
	 * Constant string for the 'ignoreDefinitions' option.
	 */
	String ignoreDefinitions = "ignoreDefinitions";
	/**
	 * Constant string for the 'header' option.
	 */
	String header = "header";
	/**
	 * Constant string for the 'disableSinglePrecisionFloats' option.
	 */
	String disableSinglePrecisionFloats = "disableSinglePrecisionFloats";
	/**
	 * Constant string for the 'targetVersion' option.
	 */
	String targetVersion = "targetVersion";
	/**
	 * Constant string for the 'tsout' option.
	 */
	String tsout = "tsout";
	/**
	 * Constant string for the 'dtsout' option.
	 */
	String dtsout = "dtsout";
	/**
	 * Constant string for the 'jsout' option.
	 */
	String jsout = "jsout";
	/**
	 * Constant string for the 'candiesJsOut' option.
	 */
	String candiesJsOut = "candiesJsOut";
	/**
	 * Constant string for the 'moduleResolution' option.
	 */
	String moduleResolution = "moduleResolution";
	/**
	 * Constant string for the 'extraSystemPath' option.
	 */
	String extraSystemPath = "extraSystemPath";

	/**
	 * All the supported options.
	 */
	String[] options = { bundle, noRootDirectories, sourceMap, module, encoding, enableAssertions, declaration, tsOnly,
			ignoreDefinitions, header, disableSinglePrecisionFloats, targetVersion, tsout, dtsout, jsout, candiesJsOut,
			moduleResolution, extraSystemPath };

	/**
	 * Returns the configuration from the configuration file.
	 */
	Map<String, Object> getConfiguration();

	/**
	 * Tells if the transpiler generates js.map files for Java debugging.
	 * 
	 * @deprecated use {@link #isGenerateSourceMaps()} instead
	 */
	@Deprecated
	boolean isPreserveSourceLineNumbers();

	/**
	 * Tells if the transpiler generates js.map files for Java debugging.
	 */
	boolean isGenerateSourceMaps();

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

	/**
	 * If true, JSweet has been launched in verbose mode.
	 */
	boolean isVerbose();

	/**
	 * By default, for a target version >=ES5, JSweet will force Java floats to
	 * be mapped to JavaScript numbers that will be constrained with the
	 * Math.fround function. If this option is true, then the calls to
	 * Math.fround are erased and the generated program will use the JavaScript
	 * default precision (double precision).
	 */
	boolean isDisableSinglePrecisionFloats();

	/**
	 * The targeted ECMAScript version.
	 */
	EcmaScriptComplianceLevel getEcmaTargetVersion();

	/**
	 * A list of adapter class names (fully qualified) to be used to extend the
	 * transpiler behavior. As the name suggests, an adapter is an instance of
	 * {@link PrinterAdapter} that adapts the TypeScript default printer in
	 * order to tune the generated code. All the adapter classes must be
	 * accessible within the 'jsweet_extension' directory to be created at the
	 * root of the transpiled project. The adapter classes can be provided as
	 * Java class files (*.class) or Java source files (*.java). In the latter
	 * case, they will be on-the-fly compiled by JSweet.
	 * 
	 * @see PrinterAdapter
	 */
	List<String> getAdapters();

	/**
	 * Determines how modules get resolved. Either "Node" for Node.js/io.js
	 * style resolution, or "Classic". See Tsc's <a href=
	 * "https://www.typescriptlang.org/docs/handbook/module-resolution.html">
	 * Module Resolution documentation</a> for more details.
	 * 
	 * @return the current module resolution strategy
	 */
	ModuleResolution getModuleResolution();

	/**
	 * The configuration file, which is by default the
	 * <code>jsweetconfig.json</code> file in the current project.
	 */
	File getConfigurationFile();

	/**
	 * @return true if module is set to somethind else than ModuleKind.none
	 */
	boolean isUsingModules();

	boolean isSkipTypeScriptChecks();

	/**
	 * @return true to enable tsc watch mode
	 */
	boolean isTscWatchMode();
}