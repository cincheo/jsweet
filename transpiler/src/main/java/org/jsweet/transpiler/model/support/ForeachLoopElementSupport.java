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

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ForeachLoopElement;

import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.util.TreePath;

/**
 * See {@link ForeachLoopElement}.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class ForeachLoopElementSupport extends ExtendedElementSupport<EnhancedForLoopTree>
		implements ForeachLoopElement {

	public ForeachLoopElementSupport(TreePath treePath, EnhancedForLoopTree tree, Element element,
			JSweetContext context) {
		super(treePath, tree, element, context);
	}

	@Override
	public ExtendedElement getBody() {
		return createElement(getTree().getStatement());
	}

	@Override
	public VariableElement getIterationVariable() {
		return (VariableElement) util().getElementForTree(getTree().getVariable(), compilationUnit);
	}

	@Override
	public ExtendedElement getIterableExpression() {
		return createElement(getTree().getExpression());
	}

}
