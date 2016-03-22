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

import javax.lang.model.element.Modifier;

import org.jsweet.transpiler.util.AbstractTreeScanner;

import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;

/**
 * This AST scanner detects default methods for injecting them later on.
 * 
 * @author Renaud Pawlak
 */
public class DefaultMethodScanner extends AbstractTreeScanner {

	/**
	 * Creates a new default method scanner.
	 */
	public DefaultMethodScanner(TranspilationHandler logHandler, JSweetContext context, JCCompilationUnit compilationUnit) {
		super(logHandler, context, compilationUnit);
	}

	@Override
	public void visitMethodDef(JCMethodDecl methodDecl) {
		if (methodDecl.mods.getFlags().contains(Modifier.DEFAULT)) {
			getContext().addDefaultMethod(getParent(JCClassDecl.class), methodDecl);
		}
	}

}
