/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler.util;

/**
 * Represents a generic immutable position.
 * 
 * @author Renaud Pawlak
 */
public final class Position implements Comparable<Position> {
	private final int position;
	private final int line;
	private final int column;

	/**
	 * Creates a new position.
	 */
	public Position(int position, int line, int column) {
		super();
		this.position = position;
		this.line = line;
		this.column = column;
	}

	/**
	 * Creates a new position.
	 */
	public Position(int line, int column) {
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
			return this.line - position.line;
		} else {
			return this.column - position.column;
		}
	}

	@Override
	public String toString() {
		return "(" + line + "," + column + ")[" + position + "]";
	}

	/**
	 * The position in the stream (-1 if not used).
	 */
	public final int getPosition() {
		return position;
	}

	/**
	 * The position's line.
	 */
	public final int getLine() {
		return line;
	}

	/**
	 * The position's column.
	 */
	public final int getColumn() {
		return column;
	}
}
