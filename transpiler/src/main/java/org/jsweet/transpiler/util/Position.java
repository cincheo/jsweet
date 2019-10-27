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

/**
 * Represents a generic immutable position.
 * 
 * @author Renaud Pawlak
 */
public final class Position implements Comparable<Position> {
	private final long position;
	private final long line;
	private final long column;

	/**
	 * Creates a new position.
	 */
	public Position(long position, long line, long column) {
		super();
		this.position = position;
		this.line = line;
		this.column = column;
	}

	/**
	 * Creates a new position.
	 */
	public Position(long line, long column) {
		this(-1, line, column);
	}

	/**
	 * Creates a new position.
	 */
	public Position(Position position) {
		this(position.position, position.line, position.column);
	}

	@Override
	public int compareTo(Position position) {
		if (this.line != position.line) {
			return (int)(this.line - position.line);
		} else {
			return (int)(this.column - position.column);
		}
	}

	@Override
	public String toString() {
		return "(" + line + "," + column + ")[" + position + "]";
	}

	/**
	 * The position in the stream (-1 if not used).
	 */
	public final long getPosition() {
		return position;
	}

	/**
	 * The position's line.
	 */
	public final long getLine() {
		return line;
	}

	/**
	 * The position's column.
	 */
	public final long getColumn() {
		return column;
	}
}
