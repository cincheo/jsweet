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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
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
 * @see JSweetContext
 * @author Renaud Pawlak
 */
public class GlobalBeforeTranslationScanner extends AbstractTreeScanner {

	Set<JCVariableDecl> lazyInitializedStaticCandidates = new HashSet<>();

	/**
	 * Creates a new global scanner.
	 */
	public GlobalBeforeTranslationScanner(TranspilationHandler logHandler, JSweetContext context) {
		super(logHandler, context, null);
	}

	@Override
	public void visitTopLevel(JCCompilationUnit topLevel) {
		if (topLevel.packge.getQualifiedName().toString().startsWith(JSweetConfig.LIBS_PACKAGE + ".")) {
			return;
		}
		this.compilationUnit = topLevel;
		super.visitTopLevel(topLevel);
	}

	@Override
	public void visitClassDef(JCClassDecl classdecl) {
		if (getCompilationUnit().docComments.hasComment(classdecl)) {
			context.docComments.put(classdecl.sym, getCompilationUnit().docComments.getCommentText(classdecl));
		}

		TypeMirror superClassType = context.modelTypes.erasure(((TypeElement) classdecl.sym).getSuperclass());
		if (superClassType.toString().startsWith("java.") && !superClassType.toString().equals(Object.class.getName())
				&& !context.types.isSubtype(classdecl.type, context.symtab.throwableType)
				&& !Util.isSourceElement(context.modelTypes.asElement(superClassType))) {
			// the class extends a JDK class
			context.addJdkSubclass(classdecl.sym.toString(), superClassType);
		}

		if (classdecl.sym.getEnclosingElement() instanceof ClassSymbol) {
			Type superClass = ((ClassSymbol) classdecl.sym.getEnclosingElement()).getSuperclass();
			if (superClass != null && superClass.tsym != null) {
				ClassSymbol clashingInnerClass = Util.findInnerClassDeclaration((ClassSymbol) superClass.tsym,
						classdecl.name.toString());
				if (clashingInnerClass != null && !context.hasClassNameMapping(clashingInnerClass)) {
					context.addClassNameMapping(classdecl.sym,
							"__" + classdecl.sym.getEnclosingElement().getQualifiedName().toString().replace('.', '_')
									+ "_" + clashingInnerClass.getSimpleName());
				}
			}
		} else {
			if (classdecl.sym.getKind() == ElementKind.ANNOTATION_TYPE
					&& context.hasAnnotationType(classdecl.sym, JSweetConfig.ANNOTATION_DECORATOR)) {
				context.registerDecoratorAnnotation(classdecl);
			}
		}

		boolean globals = false;
		if (JSweetConfig.GLOBALS_CLASS_NAME.equals(classdecl.name.toString())) {
			globals = true;
		}

		for (JCTree def : classdecl.defs) {
			if (def instanceof JCVariableDecl) {
				JCVariableDecl var = (JCVariableDecl) def;
				if (getCompilationUnit().docComments.hasComment(var)) {
					context.docComments.put(var.sym, getCompilationUnit().docComments.getCommentText(var));
				}

				if (!context.hasFieldNameMapping(var.sym)) {
					VarSymbol clashingField = null;
					clashingField = Util.findFieldDeclaration((ClassSymbol) classdecl.sym.getSuperclass().tsym,
							var.name);
					if (clashingField != null) {
						if (clashingField.isPrivate() && !context.hasFieldNameMapping(clashingField)) {
							context.addFieldNameMapping(var.sym, JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX
									+ classdecl.sym.toString().replace(".", "_") + "_" + var.name.toString());
						}
					}

					MethodSymbol m = Util.findMethodDeclarationInType(context.types, classdecl.sym, var.name.toString(),
							null);
					if (m != null) {
						context.addFieldNameMapping(var.sym,
								JSweetConfig.FIELD_METHOD_CLASH_RESOLVER_PREFIX + var.name.toString());
					}
				}
				if (var.getModifiers().getFlags().contains(Modifier.STATIC)) {
					if (!(var.getModifiers().getFlags().contains(Modifier.FINAL) && var.init != null
							&& var.init instanceof JCLiteral)) {
						lazyInitializedStaticCandidates.add(var);
					}
				}
			} else if (def instanceof JCBlock) {
				if (((JCBlock) def).isStatic()) {
					context.countStaticInitializer(classdecl.sym);
				}
			}
			if (globals && def instanceof JCMethodDecl) {
				if (((JCMethodDecl) def).sym.isStatic()) {
					context.registerGlobalMethod(classdecl, (JCMethodDecl) def);
				}
			}
		}
		super.visitClassDef(classdecl);
	}

	@Override
	public void visitMethodDef(JCMethodDecl methodDecl) {
		if (getCompilationUnit().docComments.hasComment(methodDecl)) {
			context.docComments.put(methodDecl.sym, getCompilationUnit().docComments.getCommentText(methodDecl));
		}

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
			if (context.getStaticInitializerCount(var.sym.enclClass()) == 0 && var.init == null
					|| Util.isLiteralExpression(var.init)) {
				continue;
			}
			context.lazyInitializedStatics.add(var.sym);
		}
	}

}
