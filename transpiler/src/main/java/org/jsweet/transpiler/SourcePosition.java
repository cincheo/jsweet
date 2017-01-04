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
package org.jsweet.transpiler;

import java.io.File;

import org.jsweet.transpiler.util.Position;

import com.sun.tools.javac.tree.JCTree;

/**
 * A non-mutable position in a source file.
 * 
 * @author Renaud Pawlak
 */
public final class SourcePosition {
	/**
	 * Creates a new source position from all indexes.
	 * 
	 * @param file
	 *            the source file
	 * @param sourceElement
	 *            the source element if any
	 * @param startLine
	 *            the start line in the source file
	 * @param startColumn
	 *            the start column in the source file
	 * @param endLine
	 *            the end line in the source file
	 * @param endColumn
	 *            the end columb in the source file
	 */
	public SourcePosition(File file, JCTree sourceElement, int startLine, int startColumn, int endLine, int endColumn) {
		super();
		this.file = file;
		this.startPosition = new Position(startLine, startColumn);
		this.endPosition = new Position(endLine, endColumn);
		this.sourceElement = sourceElement;
	}

	/**
	 * A simple constructor with no element and a single position (start = end).
	 * 
	 * @param file
	 *            the source file
	 * @param sourceElement
	 *            the source element if any
	 * @param line
	 *            the position's line
	 * @param column
	 *            the position's column
	 */
	public SourcePosition(File file, JCTree sourceElement, int line, int column) {
		this(file, sourceElement, new Position(line, column));
	}

	/**
	 * Creates a new source position from start and end positions.
	 * 
	 * @param file
	 *            the source file
	 * @param sourceElement
	 *            the source element if any
	 * @param startPosition
	 *            the start position in the source file
	 * @param endPosition
	 *            the end position in the source file
	 */
	public SourcePosition(File file, JCTree sourceElement, Position startPosition, Position endPosition) {
		super();
		this.file = file;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.sourceElement = sourceElement;
	}

	/**
	 * Creates a new source position from a given position (will start and end
	 * at the same position).
	 * 
	 * @param file
	 *            the source file
	 * @param sourceElement
	 *            the source element if any
	 * @param position
	 *            the position this source position with start and end at
	 */
	public SourcePosition(File file, JCTree sourceElement, Position position) {
		super();
		this.file = file;
		this.endPosition = this.startPosition = position;
		this.sourceElement = sourceElement;
	}

	private final File file;
	private final Position startPosition;
	private final Position endPosition;
	private final JCTree sourceElement;

	/**
	 * The source file.
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * The start position in the source file.
	 */
	public final Position getStartPosition() {
		return this.startPosition;
	}

	/**
	 * The end position in the source file.
	 */
	public final Position getEndPosition() {
		return this.endPosition;
	}

	/**
	 * The start line in the source file.
	 */
	public final int getStartLine() {
		return this.startPosition.getLine();
	}

	/**
	 * The start column in the source file.
	 */
	public final int getStartColumn() {
		return this.startPosition.getColumn();
	}

	/**
	 * The end line in the source file.
	 */
	public final int getEndLine() {
		return this.endPosition.getLine();
	}

	/**
	 * The end column in the source file.
	 */
	public final int getEndColumn() {
		return this.endPosition.getColumn();
	}

	/**
	 * The source element (can be null).
	 */
	public final JCTree getSourceElement() {
		return sourceElement;
	}

	@Override
	public final String toString() {
		return "" + file + "(" + getStartLine() + "," + getStartColumn() + ")";
	}
}
