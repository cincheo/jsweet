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

import static org.jsweet.transpiler.model.ExtendedElementFactory.toTree;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;

/**
 * See {@link MethodInvocationElement}.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class MethodInvocationElementSupport extends ExtendedElementSupport<MethodInvocationTree>
		implements MethodInvocationElement {

	public MethodInvocationElementSupport(CompilationUnitTree compilationUnit, MethodInvocationTree tree,
			JSweetContext context) {
		super(compilationUnit, tree, context);
	}

	public List<ExtendedElement> getArguments() {
		return tree.getArguments().stream().map(this::createElement).collect(Collectors.toList());
	}

	@Override
	public int getArgumentCount() {
		return tree.getArguments().size();
	}

	@Override
	public List<ExtendedElement> getArgumentTail() {
		return tree.getArguments().stream().skip(1).map(this::createElement).collect(Collectors.toList());
	}

	@Override
	public ExtendedElement getArgument(int i) {
		return createElement(tree.getArguments().get(i));
	}

	@Override
	public String getMethodName() {
		Tree methodTree = tree.getMethodSelect();
		if (methodTree instanceof IdentifierTree) {
			return methodTree.toString();
		} else if (methodTree instanceof MemberSelectTree) {
			return ((MemberSelectTree) methodTree).getIdentifier().toString();
		} else {
			return null;
		}
	}

	@Override
	public ExecutableElement getMethod() {
		Tree methTree = tree.getMethodSelect();
		return (ExecutableElement) trees().getElement(trees().getPath(compilationUnit, methTree));
	}

	@Override
	public ExtendedElement getTargetExpression() {
		Tree methTree = tree.getMethodSelect();
		if (methTree instanceof MemberSelectTree) {
			return createElement(((MemberSelectTree) methTree).getExpression());
		} else {
			return null;
		}
	}

	@Override
	public TypeMirror getTargetType() {
		ExtendedElement targetExpression = getTargetExpression();
		return targetExpression == null ? null : util().getTypeForTree(toTree(targetExpression), compilationUnit);
	}
}
