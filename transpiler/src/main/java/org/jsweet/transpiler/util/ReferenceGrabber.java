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
package org.jsweet.transpiler.util;

import java.util.HashSet;
import java.util.Set;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.TreeScanner;

/**
 * A utility scanner that grabs all references to types used within a code tree.
 * 
 * @author Renaud Pawlak
 */
public class ReferenceGrabber extends TreeScanner {

	/**
	 * The grabbed references.
	 */
	public Set<TypeSymbol> referencedTypes = new HashSet<>();

	/**
	 * Grab references on the given new-class tree.
	 */
	@Override
	public void visitNewClass(JCNewClass newClass) {
		add(newClass.clazz.type.tsym);
		super.visitNewClass(newClass);
	}

	/**
	 * Grab references on the given field-access tree.
	 */
	@Override
	public void visitSelect(JCFieldAccess fieldAccess) {
		if (fieldAccess.selected.type != null && (fieldAccess.selected.type.tsym instanceof ClassSymbol)) {
			add(fieldAccess.selected.type.tsym);
		}
		super.visitSelect(fieldAccess);
	}

	private void add(TypeSymbol type) {
		referencedTypes.add(type);
	}

}
