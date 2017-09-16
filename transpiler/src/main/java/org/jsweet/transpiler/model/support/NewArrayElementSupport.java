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

import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.NewArrayElement;

import com.sun.tools.javac.tree.JCTree.JCNewArray;

/**
 * See {@link NewArrayElement}.
 * 
 * @author Renaud Pawlak
 */
public class NewArrayElementSupport extends ExtendedElementSupport<JCNewArray> implements NewArrayElement {

	public NewArrayElementSupport(JCNewArray tree) {
		super(tree);
	}

	public List<ExtendedElement> getDimensions() {
		return tree.getDimensions().stream().map(a -> ExtendedElementFactory.INSTANCE.create(a)).collect(Collectors.toList());
	}

	@Override
	public int getDimensionCount() {
		return tree.getDimensions().size();
	}

	@Override
	public ExtendedElement getDimension(int i) {
		return ExtendedElementFactory.INSTANCE.create(tree.getDimensions().get(i));
	}

	
}
