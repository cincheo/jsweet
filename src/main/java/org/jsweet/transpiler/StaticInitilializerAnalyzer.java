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
package org.jsweet.transpiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

import org.apache.log4j.Logger;
import org.jsweet.transpiler.util.DirectedGraph;
import org.jsweet.transpiler.util.ReferenceGrabber;

import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
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

	@Override
	public void visitTopLevel(JCCompilationUnit compilationUnit) {
		currentTopLevel = compilationUnit;
		getGraph().add(compilationUnit);
		super.visitTopLevel(compilationUnit);
		currentTopLevel = null;
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (pass == 1) {
			typesToCompilationUnits.put(classdecl.sym, currentTopLevel);
		} else {
			for (JCTree member : classdecl.defs) {
				if (member instanceof JCVariableDecl) {
					JCVariableDecl field = (JCVariableDecl) member;
					if (field.getModifiers().getFlags().contains(Modifier.STATIC) && field.getInitializer() != null) {
						ReferenceGrabber refGrabber = new ReferenceGrabber();
						refGrabber.scan(field.getInitializer());
						for (TypeSymbol type : refGrabber.referencedTypes) {
							if (!context.useModules || currentTopLevel.packge.equals(type.packge())) {
								JCCompilationUnit target = typesToCompilationUnits.get(type);
								if (!currentTopLevel.equals(target)) {
									logger.debug("adding static initializer dependency: " + currentTopLevel.getSourceFile() + " -> " + target.getSourceFile());
									getGraph().addEdge(target, currentTopLevel);
								}
							}
						}
					}
				}
			}
		}
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
