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
package org.jsweet.transpiler.util;

import static org.jsweet.transpiler.util.Util.toJavaFileObjects;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.jsweet.transpiler.JSweetContext;

import com.sun.tools.javac.code.Lint.LintCategory;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;

/**
 * This utility class encapsulates a <code>javac</code> compilation environment.
 * 
 * @author Renaud Pawlak
 */
public class JavaCompilationEnvironment {
	/**
	 * Creates a new compilation environment with the given classpath.
	 */
	public static JavaCompilationEnvironment create(String classPath) {
		JSweetContext context = new JSweetContext();
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
	public List<JCCompilationUnit> parseAndAttributeJavaFiles(List<File> javaFiles) throws IOException {
		List<JavaFileObject> sources = toJavaFileObjects(fileManager, javaFiles);
		List<JCCompilationUnit> compilationUnits = compiler.enterTrees(compiler.parseFiles(sources));
		compiler.attribute(compiler.todo);
		return compilationUnits;
	}

}