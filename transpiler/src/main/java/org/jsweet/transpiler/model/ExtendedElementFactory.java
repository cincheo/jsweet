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
package org.jsweet.transpiler.model;

import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.support.ArrayAccessElementSupport;
import org.jsweet.transpiler.model.support.AssignmentElementSupport;
import org.jsweet.transpiler.model.support.BinaryOperatorElementSupport;
import org.jsweet.transpiler.model.support.CaseElementSupport;
import org.jsweet.transpiler.model.support.CompilationUnitElementSupport;
import org.jsweet.transpiler.model.support.ExtendedElementSupport;
import org.jsweet.transpiler.model.support.ForeachLoopElementSupport;
import org.jsweet.transpiler.model.support.IdentifierElementSupport;
import org.jsweet.transpiler.model.support.ImportElementSupport;
import org.jsweet.transpiler.model.support.LiteralElementSupport;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.model.support.NewArrayElementSupport;
import org.jsweet.transpiler.model.support.NewClassElementSupport;
import org.jsweet.transpiler.model.support.UnaryOperatorElementSupport;
import org.jsweet.transpiler.model.support.VariableAccessElementSupport;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;

/**
 * A factory to create extended elements. It defines an overloaded create method
 * that wraps the given javac tree node with the appropriate element.
 * 
 * @author Renaud Pawlak
 */
public class ExtendedElementFactory {

	/**
	 * The default factory instance.
	 */
	public final static ExtendedElementFactory INSTANCE = new ExtendedElementFactory();

	/**
	 * Gets the javac tree from the given element.
	 * 
	 * @param element
	 *            the extended element to get the tree from.
	 * @return the corresponding javac tree
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Tree> T toTree(ExtendedElement element) {
		return ((ExtendedElementSupport<T>) element).getTree();
	}

	/**
	 * Creates an extended element out of a javac tree.
	 * 
	 * <p>
	 * Note that if the tree instance cannot be mapped to a specific extended
	 * element, a generic extended element is returned. The returned extended
	 * element can be mapped back to a javac tree using the
	 * {@link #toTree(ExtendedElement)} method.
	 */
	public ExtendedElement create(CompilationUnitTree compilationUnit, Tree tree, JSweetContext context) {
		if (tree == null) {
			return null;
		}
		switch (tree.getKind()) {
		case METHOD_INVOCATION:
			return new MethodInvocationElementSupport(compilationUnit, (MethodInvocationTree) tree, context);
		case MEMBER_SELECT:
			if (((JCFieldAccess) tree).sym instanceof VariableElement) {
				return new VariableAccessElementSupport(compilationUnit, tree, context);
			} else {
				return new ExtendedElementSupport<>(compilationUnit, tree, context);
			}
		case NEW_CLASS:
			return new NewClassElementSupport(compilationUnit, (JCNewClass) tree, context);
		case IDENT:
			if (((JCIdent) tree).sym instanceof VariableElement) {
				return new VariableAccessElementSupport(compilationUnit, tree, context);
			} else {
				return new IdentifierElementSupport(compilationUnit, (JCIdent) tree, context);
			}
		case LITERAL:
			return new LiteralElementSupport(compilationUnit, (JCLiteral) tree, context);
		case CASE:
			return new CaseElementSupport(compilationUnit, (JCCase) tree, context);
		case NEWARRAY:
			return new NewArrayElementSupport(compilationUnit, (JCNewArray) tree, context);
		case ARRAY_ACCESS:
			return new ArrayAccessElementSupport(compilationUnit,  (ArrayAccessTree) tree, context);
		case FOREACHLOOP:
			return new ForeachLoopElementSupport(compilationUnit, (JCEnhancedForLoop) tree, context);
		case ASSIGN:
			return new AssignmentElementSupport(compilationUnit, (JCAssign) tree, context);
		case IMPORT:
			return new ImportElementSupport(compilationUnit, (JCImport) tree, context);
		case TOPLEVEL:
			return new CompilationUnitElementSupport(compilationUnit, (CompilationUnitTree) tree, context);
		case AND:
		case OR:
		case BITAND:
		case BITXOR:
		case DIV:
		case EQ:
		case GE:
		case LE:
		case LT:
		case GT:
		case MINUS:
		case MOD:
		case MUL:
		case NE:
		case PLUS:
			return new BinaryOperatorElementSupport(compilationUnit, (JCBinary) tree, context);
		case NEG:
		case NOT:
		case POS:
		case PREDEC:
		case PREINC:
		case POSTDEC:
		case POSTINC:
			return new UnaryOperatorElementSupport(compilationUnit, (UnaryTree) tree, context);
		default:
			return new ExtendedElementSupport<>(tree);
		}
	}

}
