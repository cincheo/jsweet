package org.jsweet.input.typescriptdef.visitor;

import java.util.Arrays;
import java.util.Comparator;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.TypescriptDef2Java;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.util.Util;

public class PackageOrganizer extends Scanner {

	public PackageOrganizer(Context context) {
		super(context);
	}

	@Override
	public void visitCompilationUnit(CompilationUnit compilationUnit) {
		ModuleDeclaration currentModule = null;

		if (compilationUnit.getFile().getParentFile().getName().equals(TypescriptDef2Java.TS_CORE_LIB_DIR)) {
			// in case all files are placed in the core directory, discriminate
			// on the file name
			if (compilationUnit.getFile().getName().startsWith("lib.core")) {
				currentModule = new ModuleDeclaration(null, JSweetDefTranslatorConfig.LANG_PACKAGE, new Declaration[0]);
			} else {
				currentModule = new ModuleDeclaration(null, JSweetDefTranslatorConfig.DOM_PACKAGE, new Declaration[0]);
			}
		}
		if (compilationUnit.getFile().getParentFile().getName().equals(TypescriptDef2Java.TS_DOM_LIB_DIR)) {
			currentModule = new ModuleDeclaration(null, JSweetDefTranslatorConfig.DOM_PACKAGE, new Declaration[0]);
		}
		if (currentModule == null) {
			String name = Util.getLibPackageNameFromTsDefFile(compilationUnit.getFile());
			logger.trace("root package found: " + name + " for " + compilationUnit);
			currentModule = new ModuleDeclaration(null, name, new Declaration[0]);
			if (compilationUnit.isExternal()) {
				String filename = compilationUnit.getFile().getName();
				filename = filename.substring(0, filename.indexOf('.'));
				currentModule.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_NAME + "(\"" + filename + "\")");
				currentModule.setQuotedName(true);
			}
			if (!context.libModules.contains(name)) {
				context.libModulesCompilationUnits.put(name, compilationUnit);
				context.libModules.add(name);
				context.libModules.sort(new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {
						return -s1.compareTo(s2);
					}
				});
			} else {
				// this is the case when a group contains several definition
				// files
				context.reportWarning("root package has already been registered: " + name);
			}
		}

		for (Declaration declaration : compilationUnit.getMembers()) {
			currentModule.addMember(declaration);
		}
		compilationUnit.clearMembers();

		// TODO : the following code was supposed to handle a compilation unit
		// which would not be any of lang, dom, webworkers or lib
		// for (Declaration declaration : compilationUnit.getMembers()) {
		// if (!(declaration instanceof ModuleDeclaration)) {
		// if (currentModule == null) {
		// currentModule = new ModuleDeclaration(null,
		// JSweetConfig.GLOBALS_PACKAGE_NAME,
		// new Declaration[0]);
		// }
		// compilationUnit.removeMember(declaration);
		// currentModule.addMember(declaration);
		// }
		// }
		if (currentModule != null) {
			if (context.verbose) {
				logger.debug("creating module: " + currentModule + " - " + Arrays.asList(currentModule.getMembers()));
			}
			compilationUnit.addMember(currentModule);
		}
	}
}
