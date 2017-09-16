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

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCUnary;

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
	public static <T extends JCTree> T toTree(ExtendedElement element) {
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
	public ExtendedElement create(JCTree tree) {
		if (tree == null) {
			return null;
		}
		switch (tree.getTag()) {
		case APPLY:
			return new MethodInvocationElementSupport((JCMethodInvocation) tree);
		case SELECT:
			if (((JCFieldAccess) tree).sym instanceof VariableElement) {
				return new VariableAccessElementSupport(tree);
			} else {
				return new ExtendedElementSupport<>(tree);
			}
		case NEWCLASS:
			return new NewClassElementSupport((JCNewClass) tree);
		case IDENT:
			if (((JCIdent) tree).sym instanceof VariableElement) {
				return new VariableAccessElementSupport(tree);
			} else {
				return new IdentifierElementSupport((JCIdent) tree);
			}
		case LITERAL:
			return new LiteralElementSupport((JCLiteral) tree);
		case CASE:
			return new CaseElementSupport((JCCase) tree);
		case NEWARRAY:
			return new NewArrayElementSupport((JCNewArray) tree);
		case INDEXED:
			return new ArrayAccessElementSupport((JCArrayAccess) tree);
		case FOREACHLOOP:
			return new ForeachLoopElementSupport((JCEnhancedForLoop) tree);
		case ASSIGN:
			return new AssignmentElementSupport((JCAssign) tree);
		case IMPORT:
			return new ImportElementSupport((JCImport) tree);
		case TOPLEVEL:
			return new CompilationUnitElementSupport((JCCompilationUnit) tree);
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
			return new BinaryOperatorElementSupport((JCBinary) tree);
		case NEG:
		case NOT:
		case POS:
		case PREDEC:
		case PREINC:
		case POSTDEC:
		case POSTINC:
			return new UnaryOperatorElementSupport((JCUnary) tree);
		default:
			return new ExtendedElementSupport<>(tree);
		}
	}

}
