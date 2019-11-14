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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * See {@link ExtendedElement}.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class ExtendedElementSupport<T extends Tree> implements ExtendedElement {

	protected final CompilationUnitTree compilationUnit;
	protected final T tree;
	protected final TreePath treePath;
	protected final Element element;
	protected final JSweetContext context;

	/**
	 * Creates an extended element, wrapping the given javac tree node.
	 */
	public ExtendedElementSupport(CompilationUnitTree compilationUnit, T tree, JSweetContext context) {
		this.compilationUnit = compilationUnit;
		this.context = context;

		this.tree = tree;
		this.treePath = context.trees.getPath(compilationUnit, tree);
		this.element = context.trees.getElement(treePath);
	}

	/**
	 * Returns the wrapped javac tree node.
	 */
	public T getTree() {
		return tree;
	}

	/**
	 * Gets the type that corresponds to this element, if any.
	 */
	public TypeMirror getType() {
		return util().getTypeForTree(tree, compilationUnit);
	}

	/**
	 * Gets this element's type, as a standard element.
	 */
	public Element getTypeAsElement() {
		TypeMirror type = getType();
		return type == null ? null : types().asElement(type);
	}

	@Override
	public int hashCode() {
		return tree.hashCode();
	}

	@Override
	public String toString() {
		return tree.toString();
	}

	@Override
	public boolean isConstant() {
		if (!(getTree() instanceof ExpressionTree)) {
			return false;
		}
		return context.util.isConstant((ExpressionTree) getTree(), compilationUnit);
	}

	@Override
	public boolean isStringLiteral() {
		return getTree().getKind() == Kind.STRING_LITERAL;
	}

	protected ExtendedElement createElement(Tree tree) {
		return ExtendedElementFactory.INSTANCE.create(compilationUnit, tree, context);
	}

	protected Util util() {
		return context.util;
	}

	protected Elements elements() {
		return context.elements;
	}

	protected Trees trees() {
		return context.trees;
	}

	protected Types types() {
		return context.types;
	}
}
