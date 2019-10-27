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
package org.jsweet.transpiler.util;

import static org.jsweet.transpiler.util.Util.toJavaFileObjects;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetOptions;

import com.sun.tools.javac.code.Lint.LintCategory;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.Tree.CompilationUnitTree;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;

/**
 * This utility class encapsulates a <code>javac</code> compilation environment.
 * 
 * <p>
 * Note that this class is not used yet... we should refactor so that the
 * transpiler uses it instead of inlined javac management.
 * 
 * @author Renaud Pawlak
 */
public class JavaCompilationEnvironment {
	/**
	 * Creates a new compilation environment with the given options and
	 * classpath.
	 */
	public static JavaCompilationEnvironment create(JSweetOptions jsweetOptions, String classPath) {
		JSweetContext context = new JSweetContext(jsweetOptions);
		Options options = Options.instance(context);
		options.put(Option.CLASSPATH, classPath);
		options.put(Option.XLINT, "path");
		context.put(Log.outKey, new PrintWriter(System.out));

		// options.put(Option.XLINT_CUSTOM.text + "-" +
		// LintCategory.OPTIONS.option, "true");
		// options.remove(Option.XLINT_CUSTOM.text +
		// LintCategory.OPTIONS.option);

		options.put(Option.XLINT_CUSTOM.text + "-" + LintCategory.OVERRIDES.option, "true");

		JavacFileManager.preRegister(context);
		JavaFileManager fileManager = context.get(JavaFileManager.class);

		Log log = Log.instance(context);
		log.emitWarnings = false;
		log.suppressNotes = true;
		Types javacTypes = Types.instance(context);

		JavaCompiler compiler = JavaCompiler.instance(context);
		compiler.attrParseOnly = true;
		compiler.verbose = false;
		compiler.genEndPos = true;
		compiler.keepComments = true;

		Names names = Names.instance(context);
		Symtab symtab = Symtab.instance(context);

		return new JavaCompilationEnvironment(fileManager, compiler, options, context, log, javacTypes, names, symtab);
	}

	/**
	 * The Java file manager.
	 */
	public final JavaFileManager fileManager;
	/**
	 * The Java compiler.
	 */
	public final JavaCompiler compiler;
	/**
	 * The compilation context.
	 */
	public final JSweetContext context;
	/**
	 * The log object.
	 */
	public final Log log;
	/**
	 * The compiler's type holder.
	 */
	public final Types types;
	/**
	 * The compiler's symbol table.
	 */
	public final Symtab symtab;
	/**
	 * The compiler's name holder.
	 */
	public final Names names;
	/**
	 * The compiler's options.
	 */
	public final Options options;

	private JavaCompilationEnvironment( //
			JavaFileManager fileManager, //
			JavaCompiler compiler, //
			Options options, //
			JSweetContext context, //
			Log log, //
			Types javacTypes, //
			Names names, //
			Symtab symtab) {
		this.fileManager = fileManager;
		this.compiler = compiler;
		this.options = options;
		this.context = context;
		this.log = log;
		this.types = javacTypes;
		this.names = names;
		this.symtab = symtab;
	}

	/**
	 * Parses and attributes the given files within this compilation
	 * environment.
	 */
	public List<CompilationUnitTree> parseAndAttributeJavaFiles(List<File> javaFiles) throws IOException {
		List<JavaFileObject> sources = toJavaFileObjects(fileManager, javaFiles);
		List<CompilationUnitTree> compilationUnits = compiler.enterTrees(compiler.parseFiles(sources));
		compiler.attribute(compiler.todo);
		return compilationUnits;
	}

}