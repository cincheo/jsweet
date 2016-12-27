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

import org.jsweet.input.typescriptdef.util.Util;

/**
 * A fully qualified declaration.
 * 
 * @author Louis Grignon
 * @author Renaud Pawlak
 */
public class QualifiedDeclaration<T extends Declaration> {
	private T declaration;
	private String qualifiedDeclarationName;

	public QualifiedDeclaration(T declaration, String qualifiedDeclarationName) {
		this.declaration = declaration;
		this.qualifiedDeclarationName = qualifiedDeclarationName;
	}

	public T getDeclaration() {
		return declaration;
	}

	public String getQualifiedDeclarationName() {
		return qualifiedDeclarationName;
	}

	@Override
	public String toString() {
		return qualifiedDeclarationName;
	}

	public String getQualifier() {
		return Util.getQualifier(qualifiedDeclarationName);
	}

	public String getSimpleName() {
		return Util.getSimpleName(qualifiedDeclarationName);
	}

}
