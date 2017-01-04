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

/**
 * Default abstract implementation for any AST node.
 * 
 * @author Renaud Pawlak
 */
public abstract class AbstractAstNode implements AstNode {

	private Token token;
	public int nodeTypeId = -1;
	private boolean hidden = false;

	public AbstractAstNode(Token token) {
		super();
		// if(token==null) {
		// throw new RuntimeException("token cannot be null");
		// }
		this.token = token;
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public String getLocation() {
		if (token != null) {
			return token.getLocation();
		} else {
			return "<unknown location>";
		}
	}

	@Override
	public String toString() {
		return token == null ? "null" : token.toString();
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
