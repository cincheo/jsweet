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
package org.jsweet.transpiler.model.support;

import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.ConstAnalyzer;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.util.Util;

import standalone.com.sun.source.tree.DoWhileLoopTree;
import standalone.com.sun.source.tree.EnhancedForLoopTree;
import standalone.com.sun.source.util.TreeScanner;
import standalone.com.sun.source.util.Trees;

/**
 * See {@link ForeachLoopElement}.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class ForeachLoopElementSupport extends ExtendedElementSupport<EnhancedForLoopTree>
        implements ForeachLoopElement {

    public ForeachLoopElementSupport(EnhancedForLoopTree tree) {
        super(tree);
    }

    @Override
    public ExtendedElement getBody() {
        return createElement(getTree().getStatement());
    }

    @Override
    public VariableElement getIterationVariable() {
        return (VariableElement) Util.getElement(getTree().getVariable());
    }

    @Override
    public ExtendedElement getIterableExpression() {
        return createElement(getTree().getExpression());
    }

    @Override
    public boolean hasControlFlowStatement() {
        boolean[] hasControlFlowStatement = { false };
        new TreeScanner<Void, Trees>() {
            @Override
            public Void visitBreak(standalone.com.sun.source.tree.BreakTree node, Trees p) {
                hasControlFlowStatement[0] = true;
                return null;
            }

            @Override
            public Void visitContinue(standalone.com.sun.source.tree.ContinueTree node, Trees p) {
                hasControlFlowStatement[0] = true;
                return null;
            }

            @Override
            public Void visitReturn(standalone.com.sun.source.tree.ReturnTree node, Trees p) {
                hasControlFlowStatement[0] = true;
                return null;
            }

            @Override
            public Void visitEnhancedForLoop(EnhancedForLoopTree node, Trees p) {
                // do not scan inner loops
                return null;
            }

            @Override
            public Void visitDoWhileLoop(DoWhileLoopTree node, Trees p) {
                // do not scan inner loops
                return null;
            }

            @Override
            public Void visitWhileLoop(standalone.com.sun.source.tree.WhileLoopTree node, Trees p) {
                // do not scan inner loops
                return null;
            }

            @Override
            public Void visitForLoop(standalone.com.sun.source.tree.ForLoopTree node, Trees p) {// do not scan inner loops
                return null;
            }

        }.scan(getTree().getStatement(), JSweetContext.current.get().trees);
        return hasControlFlowStatement[0];
    }

    @Override
    public boolean isIterationVariableModified() {
        ConstAnalyzer a = new ConstAnalyzer();
        a.scan(getTree().getStatement(), JSweetContext.current.get().trees);
        return a.getModifiedVariables().contains(getIterationVariable());
    }

}
