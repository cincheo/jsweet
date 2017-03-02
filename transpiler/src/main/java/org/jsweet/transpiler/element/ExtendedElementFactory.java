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
package org.jsweet.transpiler.element;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewClass;

/**
 * A factory to create extended elements. It defines an overloaded create method
 * that wraps the given javac tree node with the appropriate element.
 * 
 * @author Renaud Pawlak
 */
public class ExtendedElementFactory {

	public final static ExtendedElementFactory INSTANCE = new ExtendedElementFactory();

	/**
	 * Creates a generic element (fallback).
	 */
	public ExtendedElement create(JCTree tree) {
		return new ExtendedElement(tree);
	}

	/**
	 * Creates a field access element.
	 */
	public FieldAccessElement create(JCFieldAccess tree) {
		return new FieldAccessElement(tree);
	}

	/**
	 * Creates a method invocation element.
	 */
	public MethodInvocationElement create(JCMethodInvocation tree) {
		return new MethodInvocationElement(tree);
	}

	/**
	 * Creates a new class element.
	 */
	public NewClassElement create(JCNewClass tree) {
		return new NewClassElement(tree);
	}

	/**
	 * Creates a literal element.
	 */
	public LiteralElement create(JCLiteral tree) {
		return new LiteralElement(tree);
	}

	/**
	 * Creates an identifier element.
	 */
	public IdentifierElement create(JCIdent tree) {
		return new IdentifierElement(tree);
	}

	/**
	 * Creates a case statement element.
	 */
	public CaseElement create(JCCase tree) {
		return new CaseElement(tree);
	}

}
