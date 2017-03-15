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
 * An AST node for a Java method invocation.
 * 
 * @author Renaud Pawlak
 */
public class MethodInvocationElementSupport extends ExtendedElementSupport implements MethodInvocationElement {

	public MethodInvocationElementSupport(JCMethodInvocation tree) {
		super(tree);
	}

	@Override
	public JCMethodInvocation getTree() {
		return (JCMethodInvocation) tree;
	}

	public List<ExtendedElement> getArguments() {
		return getTree().args.stream().map(a -> ExtendedElementFactory.INSTANCE.create(a)).collect(Collectors.toList());
	}

	@Override
	public int getArgumentCount() {
		return getTree().args.size();
	}

	@Override
	public List<ExtendedElement> getArgumentTail() {
		return getTree().args.tail.stream().map(a -> ExtendedElementFactory.INSTANCE.create(a))
				.collect(Collectors.toList());
	}

	@Override
	public ExtendedElement getArgument(int i) {
		return ExtendedElementFactory.INSTANCE.create(getTree().args.get(i));
	}

	@Override
	public String getMethodName() {
		JCTree tree = getTree().meth;
		if (tree instanceof JCIdent) {
			return tree.toString();
		} else if (tree instanceof JCFieldAccess) {
			return ((JCFieldAccess) tree).name.toString();
		} else {
			return null;
		}
	}

	@Override
	public ExecutableElement getMethod() {
		JCTree tree = getTree().meth;
		if (tree instanceof JCIdent) {
			return (ExecutableElement) ((JCIdent) tree).sym;
		} else if (tree instanceof JCFieldAccess) {
			return (ExecutableElement) ((JCFieldAccess) tree).sym;
		} else {
			return null;
		}
	}

	@Override
	public ExtendedElement getTargetExpression() {
		JCTree tree = getTree().meth;
		if (tree instanceof JCFieldAccess) {
			return ExtendedElementFactory.INSTANCE.create(((JCFieldAccess) tree).selected);
		} else {
			return null;
		}
	}

}
