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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.DirectedGraph;
import org.jsweet.transpiler.util.ReferenceGrabber;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeScanner;

/**
 * This AST scanner creates a class dependency graph for each package, based on
 * static field initializers.
 * 
 * @author Renaud Pawlak
 */
public class StaticInitilializerAnalyzer extends TreeScanner {

	private JSweetContext context;
	private JCCompilationUnit currentTopLevel;
	private int pass = 1;
	private static final Logger logger = Logger.getLogger(StaticInitilializerAnalyzer.class);
	/**
	 * A map containing the static initializers dependencies for each package
	 * when using modules (empty otherwise).
	 */
	public Map<PackageSymbol, DirectedGraph<JCCompilationUnit>> staticInitializersDependencies = new HashMap<>();

	/**
	 * A map containing the static initializers dependencies when not using
	 * modules (empty otherwise).
	 */
	public DirectedGraph<JCCompilationUnit> globalStaticInitializersDependencies = new DirectedGraph<>();

	/**
	 * Maps the types to the compilation units in which they are declared.
	 */
	public Map<TypeSymbol, JCCompilationUnit> typesToCompilationUnits = new HashMap<>();

	/**
	 * Creates the analyzer.
	 */
	public StaticInitilializerAnalyzer(JSweetContext context) {
		this.context = context;
	}

	private DirectedGraph<JCCompilationUnit> getGraph() {
		if (context.useModules) {
			DirectedGraph<JCCompilationUnit> graph = staticInitializersDependencies.get(currentTopLevel.packge);
			if (graph == null) {
				graph = new DirectedGraph<>();
				staticInitializersDependencies.put(currentTopLevel.packge, graph);
			}
			return graph;
		} else {
			return globalStaticInitializersDependencies;
		}
	}

	Set<Type> currentTopLevelImportedTypes = new HashSet<>();

	@Override
	public void visitTopLevel(JCCompilationUnit compilationUnit) {
		if (compilationUnit.packge.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
			// skip definitions
			return;
		}
		currentTopLevel = compilationUnit;
		if (pass == 1) {
			getGraph().add(compilationUnit);
		} else {
			currentTopLevelImportedTypes.clear();
			for (JCImport i : compilationUnit.getImports()) {
				if (i.qualid.type != null) {
					currentTopLevelImportedTypes.add(i.qualid.type);
				}
				// TypeSymbol type = Util.getImportedType(i);
				// if (type != null) {
				// JCCompilationUnit target = typesToCompilationUnits.get(type);
				// if (target != null && getGraph().contains(target)) {
				// logger.debug("adding import dependency: " +
				// currentTopLevel.getSourceFile() + " -> " +
				// target.getSourceFile());
				// getGraph().addEdge(target, currentTopLevel);
				// }
				// }

			}
		}
		super.visitTopLevel(compilationUnit);
		currentTopLevel = null;
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (pass == 1) {
			typesToCompilationUnits.put(classdecl.sym, currentTopLevel);
		} else {
			if (classdecl.extending != null) {
				JCCompilationUnit target = typesToCompilationUnits.get(classdecl.extending.type.tsym);
				if (target != null && getGraph().contains(target)) {
					logger.debug("adding inheritance dependency: " + currentTopLevel.getSourceFile() + " -> "
							+ target.getSourceFile());
					getGraph().addEdge(target, currentTopLevel);
				}
			}

			for (JCTree member : classdecl.defs) {
				if (member instanceof JCVariableDecl) {
					JCVariableDecl field = (JCVariableDecl) member;
					if (field.getModifiers().getFlags().contains(Modifier.STATIC) && field.getInitializer() != null
							&& !Util.hasAnnotationType(field.sym, JSweetConfig.ANNOTATION_STRING_TYPE,
									JSweetConfig.ANNOTATION_ERASED)) {
						acceptReferences(field.getInitializer());
					}
				} else if (member instanceof JCBlock) {
					JCBlock initializer = (JCBlock) member;
					if (initializer.isStatic()) {
						acceptReferences(initializer);
					}
				}
			}
		}
		super.visitClassDef(classdecl);
	}

	private void acceptReferences(JCTree tree) {
		ReferenceGrabber refGrabber = new ReferenceGrabber();
		refGrabber.scan(tree);
		for (TypeSymbol type : refGrabber.referencedTypes) {
			if (!context.useModules || currentTopLevel.packge.equals(type.packge())) {
				JCCompilationUnit target = typesToCompilationUnits.get(type);
				if (target != null && !currentTopLevel.equals(target) && getGraph().contains(target)) {
					logger.debug("adding static initializer dependency: " + currentTopLevel.getSourceFile() + " -> "
							+ target.getSourceFile());
					getGraph().addEdge(target, currentTopLevel);
				}
			}
		}
	}

	// @Override
	// public void visitNewClass(JCNewClass newClass) {
	// if (pass == 1) {
	// return;
	// }
	// if (!Util.isInterface(newClass.type.tsym) &&
	// currentTopLevelImportedTypes.contains(newClass.type)) {
	// JCCompilationUnit target =
	// typesToCompilationUnits.get(newClass.type.tsym);
	// if (target != null && !currentTopLevel.equals(target) &&
	// getGraph().contains(target)) {
	// logger.debug("adding object construction dependency: " +
	// currentTopLevel.getSourceFile() + " -> " + target.getSourceFile());
	// getGraph().addEdge(target, currentTopLevel);
	// }
	// }
	// }

	boolean isImported(Type type) {
		for (JCImport i : currentTopLevel.getImports()) {
			if (i.type != null && i.type.tsym.equals(type.tsym)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Processes all the given compilation units.
	 */
	public void process(Collection<JCCompilationUnit> compilationUnits) {
		for (JCCompilationUnit cu : compilationUnits) {
			scan(cu);
		}
		pass++;
		for (JCCompilationUnit cu : compilationUnits) {
			scan(cu);
		}
	}

}
