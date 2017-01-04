/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
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
package org.jsweet.input.typescriptdef.ast;

import static org.apache.commons.lang3.StringUtils.strip;

import org.apache.commons.lang3.StringUtils;

/**
 * A reference declaration in the TypeScript source code.
 * 
 * @author Renaud Pawlak
 */
public class ReferenceDeclaration extends AbstractDeclaration {

	private String referencedName;

	public ReferenceDeclaration(Token token, String alias, String referencedName) {
		super(token, alias);

		this.referencedName = strip(referencedName, "[\"']");
	}

	@Override
	public void accept(Visitor v) {
		v.visitReferenceDeclaration(this);
	}

	@Override
	public Declaration copy() {
		ReferenceDeclaration copy = new ReferenceDeclaration(null, name, referencedName);
		copy.setDocumentation(getDocumentation());
		copy.name = name;
		copy.referencedName = referencedName;
		return copy;
	}

	public String getReferencedName() {
		return referencedName;
	}

	public boolean isImport() {
		return !isExport();
	}

	public boolean isExport() {
		return StringUtils.isBlank(name);
	}

	@Override
	public String toString() {
		if (isExport()) {
			return "export = " + referencedName;
		} else {
			return "import " + name + " = " + referencedName;
		}
	}

	public void setReferencedName(String referencedName) {
		this.referencedName = referencedName;
	}
}
