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

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;

import com.sun.tools.javac.tree.JCTree;

/**
 * The root class for AST nodes that represent Java program elements that are
 * not accessible through the {@link Element} API.
 * 
 * <p>
 * This class wraps Javac tree nodes to allow the JSweet printer adapters to use
 * an API with is independent from the Javac API.
 * 
 * @author Renaud Pawlak
 * @see PrinterAdapter
 */
public class ExtendedElementSupport implements ExtendedElement {
  
	protected JCTree tree;

	/**
	 * Creates an extended element, wrapping the given javac tree node.
	 */
	public ExtendedElementSupport(JCTree tree) {
		super();
		this.tree = tree;
	}

	/**
	 * Returns the wrapped javac tree node.
	 */
	public JCTree getTree() {
		return tree;
	}

	/**
	 * Gets the type that corresponds to this element, if any.
	 */
	public TypeMirror asType() {
		return tree.type;
	}

	/**
	 * Gets this element's type, as a standard element.
	 */
	public Element getTypeElement() {
		if (tree.type == null) {
			return null;
		} else {
			return tree.type.tsym;
		}
	}

	@Override
	public int hashCode() {
		return tree.hashCode();
	}

	@Override
	public String toString() {
		return tree.toString();
	}
}
