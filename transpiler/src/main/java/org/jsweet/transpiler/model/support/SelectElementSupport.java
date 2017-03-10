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

import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.SelectElement;

import com.sun.tools.javac.tree.JCTree.JCFieldAccess;

/**
 * An AST node for a Java field access expression.
 * 
 * @author Renaud Pawlak
 */
public class SelectElementSupport extends ExtendedElementSupport implements SelectElement {

	public SelectElementSupport(JCFieldAccess tree) {
		super(tree);
	}

	@Override
	public JCFieldAccess getTree() {
		return (JCFieldAccess) tree;
	}

	public ExtendedElement getTargetExpression() {
		return ExtendedElementFactory.INSTANCE.create(getTree().selected);
	}

	@Override
	public Element getElement() {
		return getTree().sym;
	}

	@Override
	public String getName() {
		return getTree().name.toString();
	}

	@Override
	public Element getTargetElement() {
		return getTree().selected.type.tsym;
	}

}
