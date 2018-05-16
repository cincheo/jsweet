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

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;

import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.MethodInvocationElement;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;

/**
 * See {@link MethodInvocationElement}.
 * 
 * @author Renaud Pawlak
 */
public class MethodInvocationElementSupport extends ExtendedElementSupport<JCMethodInvocation> implements MethodInvocationElement {

	public MethodInvocationElementSupport(JCMethodInvocation tree) {
		super(tree);
	}

	public List<ExtendedElement> getArguments() {
		return tree.args.stream().map(a -> ExtendedElementFactory.INSTANCE.create(a)).collect(Collectors.toList());
	}

	@Override
	public int getArgumentCount() {
		return tree.args.size();
	}

	@Override
	public List<ExtendedElement> getArgumentTail() {
		return tree.args.tail.stream().map(a -> ExtendedElementFactory.INSTANCE.create(a))
				.collect(Collectors.toList());
	}

	@Override
	public ExtendedElement getArgument(int i) {
		return ExtendedElementFactory.INSTANCE.create(tree.args.get(i));
	}

	@Override
	public String getMethodName() {
		JCTree methTree = tree.meth;
		if (methTree instanceof JCIdent) {
			return methTree.toString();
		} else if (methTree instanceof JCFieldAccess) {
			return ((JCFieldAccess) methTree).name.toString();
		} else {
			return null;
		}
	}

	@Override
	public ExecutableElement getMethod() {
		JCTree methTree = tree.meth;
		if (methTree instanceof JCIdent) {
			return (ExecutableElement) ((JCIdent) methTree).sym;
		} else if (methTree instanceof JCFieldAccess) {
			return (ExecutableElement) ((JCFieldAccess) methTree).sym;
		} else {
			return null;
		}
	}

	@Override
	public ExtendedElement getTargetExpression() {
		JCTree methTree = tree.meth;
		if (methTree instanceof JCFieldAccess) {
			return ExtendedElementFactory.INSTANCE.create(((JCFieldAccess) methTree).selected);
		} else {
			return null;
		}
	}

}
