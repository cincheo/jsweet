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

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.util.Util;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

/**
 * See {@link ExtendedElement}.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class ExtendedElementSupport<T extends Tree> implements ExtendedElement {

	protected final T tree;

	/**
	 * Creates an extended element, wrapping the given javac tree node.
	 */
	public ExtendedElementSupport(T tree) {
		Objects.requireNonNull(tree);
		this.tree = tree;
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
		return Util.getType(tree);
	}

	/**
	 * Gets this element's type, as a standard element.
	 */
	public Element getTypeAsElement() {
		TypeMirror type = getType();
		return type == null ? null : Util.getElement(type);
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
		return JSweetContext.current.get().util.isConstant(
				(ExpressionTree) getTree());
	}

	@Override
	public boolean isStringLiteral() {
		return getTree().getKind() == Kind.STRING_LITERAL;
	}

	protected ExtendedElement createElement(Tree tree) {
		return ExtendedElementFactory.INSTANCE.create(tree);
	}

	@Override
	public SourcePosition getSourcePosition() {
		return JSweetContext.current.get().util.getSourcePosition(tree, JSweetContext.currentCompilationUnit.get());
	}
}
