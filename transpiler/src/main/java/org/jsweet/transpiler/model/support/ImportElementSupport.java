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
import javax.lang.model.element.TypeElement;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.model.ImportElement;

import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;

/**
 * See {@link ImportElement}.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class ImportElementSupport extends ExtendedElementSupport<ImportTree> implements ImportElement {

	public ImportElementSupport(TreePath treePath, ImportTree tree, Element element, JSweetContext context) {
		super(treePath, tree, element, context);
	}

	@Override
	public boolean isStatic() {
		return getTree().isStatic();
	}

	@Override
	public TypeElement getImportedType() {
		Tree importedIdentifier = getTree().getQualifiedIdentifier();
		Element importedElement = util().getElementForTree(importedIdentifier, compilationUnit);
		if (getTree().isStatic() || importedElement == null || importedElement.asType() == null) {
			return null;

		}

		return (TypeElement) importedElement;
	}

	@Override
	public boolean isWildcard() {
		return getTree().toString().endsWith("*");
	}

}
