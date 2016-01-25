package org.jsweet.transpiler;

import java.io.File;

public interface JSweetOptions {

	/**
	 * Tells if the transpiler preserves the generated TypeScript source line
	 * numbers wrt the Java original source file (allows for Java debugging
	 * through js.map files).
	 */
	boolean isPreserveSourceLineNumbers();

	/**
	 * Gets the current TypeScript output directory.
	 */
	File getTsOutputDir();

	/**
	 * Gets the current JavaScript output directory.
	 */
	File getJsOutputDir();

	/**
	 * Gets the module kind when transpiling to code using JavaScript modules.
	 */
	ModuleKind getModuleKind();

	/**
	 * Gets the directory where JavaScript bundles are generated when the bundle
	 * option is activated.
	 */
	File getBundlesDirectory();

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

	boolean isIgnoreJavaFileNameError();

}