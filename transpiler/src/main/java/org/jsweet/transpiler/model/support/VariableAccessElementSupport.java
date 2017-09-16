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

import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.VariableAccessElement;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;

/**
 * See {@link VariableAccessElement}.
 * 
 * @author Renaud Pawlak
 */
public class VariableAccessElementSupport extends ExtendedElementSupport<JCTree> implements VariableAccessElement {

	public VariableAccessElementSupport(JCTree tree) {
		super(tree);
	}

	public ExtendedElement getTargetExpression() {
		if (tree instanceof JCFieldAccess) {
			return ExtendedElementFactory.INSTANCE.create(((JCFieldAccess) tree).selected);
		} else {
			return null;
		}
	}

	@Override
	public VariableElement getVariable() {
		if (tree instanceof JCFieldAccess) {
			return (VariableElement) ((JCFieldAccess) tree).sym;
		} else {
			return (VariableElement) ((JCIdent) tree).sym;
		}
	}

	@Override
	public Element getTargetElement() {
		return getVariable().getEnclosingElement();
	}

	@Override
	public String getVariableName() {
		if (tree instanceof JCFieldAccess) {
			return ((JCFieldAccess) tree).name.toString();
		} else {
			return tree.toString();
		}
	}

}
