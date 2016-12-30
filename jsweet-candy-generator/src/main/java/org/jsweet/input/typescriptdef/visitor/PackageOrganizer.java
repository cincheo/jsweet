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

/**
 * Reorganizes TypeScript namespaces to fit the Java (JSweet) package structure.
 * 
 * @author Renaud Pawlak
 */
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
		} else if (compilationUnit.getFile().getParentFile().getName().equals(TypescriptDef2Java.TS_DOM_LIB_DIR)) {
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
		}
		
		if (!context.libModules.contains(currentModule.getName())) {
			context.libModulesCompilationUnits.put(currentModule.getName(), compilationUnit);
			context.libModules.add(currentModule.getName());
			context.libModules.sort(new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return -s1.compareTo(s2);
				}
			});
		} else {
			// this is the case when a group contains several definition
			// files
			context.reportWarning("root package has already been registered: " + currentModule.getName());
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
