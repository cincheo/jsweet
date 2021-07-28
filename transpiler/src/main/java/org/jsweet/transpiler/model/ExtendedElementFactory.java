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
import org.jsweet.transpiler.model.support.AssignmentWithOperatorElementSupport;
import org.jsweet.transpiler.model.support.BinaryOperatorElementSupport;
import org.jsweet.transpiler.model.support.CaseElementSupport;
import org.jsweet.transpiler.model.support.CompilationUnitElementSupport;
import org.jsweet.transpiler.model.support.ExecutableElementSupport;
import org.jsweet.transpiler.model.support.ExtendedElementSupport;
import org.jsweet.transpiler.model.support.ForeachLoopElementSupport;
import org.jsweet.transpiler.model.support.IdentifierElementSupport;
import org.jsweet.transpiler.model.support.ImportElementSupport;
import org.jsweet.transpiler.model.support.LiteralElementSupport;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.model.support.NewArrayElementSupport;
import org.jsweet.transpiler.model.support.NewClassElementSupport;
import org.jsweet.transpiler.model.support.TypeCastElementSupport;
import org.jsweet.transpiler.model.support.UnaryOperatorElementSupport;
import org.jsweet.transpiler.model.support.VariableAccessElementSupport;
import org.jsweet.transpiler.model.support.VariableElementSupport;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;

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
     * @param element the extended element to get the tree from.
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
    public ExtendedElement create(Tree tree) {
        switch (tree.getKind()) {
        case METHOD:
            return new ExecutableElementSupport((MethodTree) tree);
        case VARIABLE:
            return new VariableElementSupport((VariableTree) tree);
        case TYPE_CAST:
            return new TypeCastElementSupport((TypeCastTree) tree);
        case METHOD_INVOCATION:
            return new MethodInvocationElementSupport((MethodInvocationTree) tree);
        case MEMBER_SELECT:
            if (Util.getElement(tree) instanceof VariableElement) {
                return new VariableAccessElementSupport((MemberSelectTree)tree);
            } else {
                return new ExtendedElementSupport<>(tree);
            }
        case NEW_CLASS:
            return new NewClassElementSupport((NewClassTree) tree);
        case IDENTIFIER:
            if (Util.getElement(tree) instanceof VariableElement) {
                return new VariableAccessElementSupport(tree);
            } else {
                return new IdentifierElementSupport((IdentifierTree) tree);
            }
        case BOOLEAN_LITERAL:
        case CHAR_LITERAL:
        case DOUBLE_LITERAL:
        case FLOAT_LITERAL:
        case INT_LITERAL:
        case LONG_LITERAL:
        case NULL_LITERAL:
        case STRING_LITERAL:
            return new LiteralElementSupport((LiteralTree) tree);
        case CASE:
            return new CaseElementSupport((CaseTree) tree);
        case NEW_ARRAY:
            return new NewArrayElementSupport((NewArrayTree) tree);
        case ARRAY_ACCESS:
            return new ArrayAccessElementSupport((ArrayAccessTree) tree);
        case ENHANCED_FOR_LOOP:
            return new ForeachLoopElementSupport((EnhancedForLoopTree) tree);
        case AND_ASSIGNMENT:
        case DIVIDE_ASSIGNMENT:
        case LEFT_SHIFT_ASSIGNMENT:
        case MINUS_ASSIGNMENT:
        case MULTIPLY_ASSIGNMENT:
        case OR_ASSIGNMENT:
        case PLUS_ASSIGNMENT:
        case REMAINDER_ASSIGNMENT:
        case RIGHT_SHIFT_ASSIGNMENT:
        case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
        case XOR_ASSIGNMENT:
            return new AssignmentWithOperatorElementSupport((CompoundAssignmentTree) tree);
        case ASSIGNMENT:
            return new AssignmentElementSupport((AssignmentTree) tree);
        case IMPORT:
            return new ImportElementSupport((ImportTree) tree);
        case COMPILATION_UNIT:
            return new CompilationUnitElementSupport((CompilationUnitTree) tree);
        case MINUS:
        case PLUS:
        case MULTIPLY:
        case DIVIDE:
        case AND:
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case OR:
        case XOR:
        case CONDITIONAL_AND:
        case CONDITIONAL_OR:
        case EQUAL_TO:
        case GREATER_THAN:
        case GREATER_THAN_EQUAL:
        case LESS_THAN:
        case LESS_THAN_EQUAL:
        case NOT_EQUAL_TO:
        case REMAINDER:
        case UNSIGNED_RIGHT_SHIFT:
            return new BinaryOperatorElementSupport((BinaryTree) tree);
        case POSTFIX_DECREMENT:
        case PREFIX_DECREMENT:
        case POSTFIX_INCREMENT:
        case PREFIX_INCREMENT:
        case UNARY_MINUS:
        case UNARY_PLUS:
        case BITWISE_COMPLEMENT:
        case LOGICAL_COMPLEMENT:
            return new UnaryOperatorElementSupport((UnaryTree) tree);
        default:
            return new ExtendedElementSupport<>(tree);
        }
    }

}
