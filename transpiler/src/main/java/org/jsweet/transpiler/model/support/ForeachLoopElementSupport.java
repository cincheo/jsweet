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
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.ForeachLoopElement;

import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCContinue;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.TreeScanner;

/**
 * See {@link ForeachLoopElement}.
 * 
 * @author Renaud Pawlak
 */
public class ForeachLoopElementSupport extends ExtendedElementSupport<JCEnhancedForLoop> implements ForeachLoopElement {

	public ForeachLoopElementSupport(JCEnhancedForLoop tree) {
		super(tree);
	}

	@Override
	public ExtendedElement getBody() {
		return ExtendedElementFactory.INSTANCE.create(getTree().body);
	}

	@Override
	public VariableElement getIterationVariable() {
		return getTree().var.sym;
	}

	@Override
	public ExtendedElement getIterableExpression() {
		return ExtendedElementFactory.INSTANCE.create(getTree().expr);
	}
	
	@Override
	public boolean hasControlFlowStatement() {
	    boolean[] hasControlFlowStatement = { false };
	    new TreeScanner() {
	        @Override
	        public void visitBreak(JCBreak tree) {
	            hasControlFlowStatement[0] = true;
	        }
            @Override
            public void visitContinue(JCContinue tree) {
                hasControlFlowStatement[0] = true;
            }
            @Override
            public void visitReturn(JCReturn tree) {
                hasControlFlowStatement[0] = true;
            }
            @Override
            public void visitForeachLoop(JCEnhancedForLoop tree) {
                // do not scan inner loops
            }
            @Override
            public void visitDoLoop(JCDoWhileLoop tree) {
                // do not scan inner loops
            }
            @Override
            public void visitWhileLoop(JCWhileLoop tree) {
                // do not scan inner loops
            }
            @Override
            public void visitForLoop(JCForLoop tree) {
                // do not scan inner loops
            }
	    }.scan(getTree().body);
	    return hasControlFlowStatement[0];
	}

    @Override
    public boolean isIterationVariableModified() {
        ConstAnalyzer a = new ConstAnalyzer();
        a.scan(getTree().body);
        return a.getModifiedVariables().contains(getIterationVariable());
    }
	
}
