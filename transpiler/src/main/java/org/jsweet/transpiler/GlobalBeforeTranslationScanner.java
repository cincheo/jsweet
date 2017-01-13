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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWildcard;

/**
 * This AST scanner performs global analysis and fills up the context with
 * information that will be used by the translator.
 * 
 * @author Renaud Pawlak
 */
public class GlobalBeforeTranslationScanner<C extends JSweetContext> extends AbstractTreeScanner<C> {

	Set<JCVariableDecl> lazyInitializedStaticCandidates = new HashSet<>();

	/**
	 * Creates a new global scanner.
	 */
	public GlobalBeforeTranslationScanner(TranspilationHandler logHandler, C context) {
		super(logHandler, context, null);
	}

	@Override
	public void visitTopLevel(JCCompilationUnit topLevel) {
		if(topLevel.packge.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
			return;
		}
		this.compilationUnit = topLevel;
		super.visitTopLevel(topLevel);
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		for (JCTree def : classdecl.defs) {
			if (def instanceof JCVariableDecl) {
				JCVariableDecl var = (JCVariableDecl) def;
				MethodSymbol m = Util.findMethodDeclarationInType(context.types, classdecl.sym, var.name.toString(), null);
				if (m != null) {
					context.addFieldNameMapping(var.sym, JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX + var.name.toString());
				}
				if (getContext().options.isSupportSaticLazyInitialization() && var.getModifiers().getFlags().contains(Modifier.STATIC)) {
					if (!(var.getModifiers().getFlags().contains(Modifier.FINAL) && var.init != null && var.init instanceof JCLiteral)) {
						lazyInitializedStaticCandidates.add(var);
					}
				}
			} else if (def instanceof JCBlock) {
				if (((JCBlock) def).isStatic()) {
					context.countStaticInitializer(classdecl.sym);
				}
			}
		}
		super.visitClassDef(classdecl);
	}

	@Override
	public void visitMethodDef(JCMethodDecl methodDecl) {
		if (methodDecl.mods.getFlags().contains(Modifier.DEFAULT)) {
			getContext().addDefaultMethod(compilationUnit, getParent(JCClassDecl.class), methodDecl);
		}
		if (!getContext().ignoreWildcardBounds) {
			scan(methodDecl.params);
		}
	}

	@Override
	public void visitWildcard(JCWildcard wildcard) {
		Symbol container = null;
		JCMethodDecl method = getParent(JCMethodDecl.class);
		if (method != null) {
			container = method.sym;
		}
		if (container != null) {
			getContext().registerWildcard(container, wildcard);
			scan(wildcard.getBound());
		}
	}

	public void process(List<JCCompilationUnit> compilationUnits) {
		for (JCCompilationUnit compilationUnit : compilationUnits) {
			scan(compilationUnit);
		}
		for (JCVariableDecl var : lazyInitializedStaticCandidates) {
			if (context.getStaticInitializerCount(var.sym.enclClass()) == 0 && var.init == null || var.init instanceof JCLiteral) {
				continue;
			}
			context.lazyInitializedStatics.add(var.sym);
		}
	}

}
