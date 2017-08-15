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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This object represents a source map between an input source file and an
 * output source file.
 * 
 * @author Renaud Pawlak
 */
public class SourceMap {

	/**
	 * An entry in the source map.
	 * 
	 * @see SourceMap
	 */
	public static final class Entry implements Comparable<Entry> {
		private final Position inputPosition;
		private Position outputPosition;

		private Entry(Position inputPosition, Position outputPosition) {
			super();
			this.inputPosition = inputPosition;
			this.outputPosition = outputPosition;
		}

		/**
		 * The position in the input file.
		 */
		public final Position getInputPosition() {
			return inputPosition;
		}

		/**
		 * The position in the output file.
		 */
		public final Position getOutputPosition() {
			return outputPosition;
		}

		/**
		 * Sets the position in the ouput file.
		 */
		public final void setOutputPosition(Position position) {
			this.outputPosition = position;
		}

		@Override
		public int compareTo(Entry entry) {
			return this.getInputPosition().compareTo(entry.getInputPosition());
		}

		@Override
		public String toString() {
			return this.getInputPosition().toString() + "->" + this.getOutputPosition().toString();
		}
	}

	private SortedSet<Entry> entries = new TreeSet<>();
	private List<Entry> insertionOrderEntries = new ArrayList<>();

	private int minOutputLine = 0;
	private int maxOutputLine = 0;

	/**
	 * Adds an entry to the source map (entry must be added in order).
	 * 
	 * @param inputPosition
	 *            the input position in the input source file
	 * @param outputPosition
	 *            the output position in the output source file
	 * @return the added entry (null if the entry cannot be added)
	 */
	public final Entry addEntry(Position inputPosition, Position outputPosition) {
		Entry entry = new Entry(inputPosition, outputPosition);
		entries.add(entry);
		insertionOrderEntries.add(entry);
		maxOutputLine = Math.max(maxOutputLine, outputPosition.getLine());
		return entry;
	}

	/**
	 * Finds the input position from an output position.
	 * 
	 * @param outputPosition
	 *            a position in the output source file
	 * @return the mapped position in the input source file
	 */
	public final Position findInputPosition(Position outputPosition) {
		return findInputPosition(outputPosition.getLine(), outputPosition.getColumn());
	}

	/**
	 * Finds the input position from an output position.
	 * 
	 * @param outputLine
	 *            a line in the output source file
	 * @param outputColumn
	 *            a column in the output source file
	 * @return the mapped position in the input source file
	 */
	public final Position findInputPosition(int outputLine, int outputColumn) {
		if (entries.isEmpty()) {
			return null;
		}
		if (outputLine < minOutputLine) {
			return null;
		}
		if (outputLine > maxOutputLine) {
			return null;
		}
		if (entries != null) {
			Entry[] e = entries.toArray(new Entry[0]);
			for (int i = 0; i < entries.size(); i++) {
				if (i == e.length - 1 || (e[i].getOutputPosition().getLine() == outputLine
						|| e[i + 1].getOutputPosition().getLine() > outputLine)) {
					while (i + 1 < e.length && e[i + 1].getOutputPosition().getLine() == outputLine
							&& e[i].getOutputPosition().getColumn() < outputColumn) {
						i++;
					}
					return e[i].getInputPosition();
				}
			}
		}
		return null;
	}

	/**
	 * Shifts the ouput positions by the given line offset.
	 */
	public final void shiftOutputPositions(int lineOffset) {
		for (Entry entry : entries) {
			entry.setOutputPosition(new Position(entry.getOutputPosition().getLine() + lineOffset,
					entry.getOutputPosition().getColumn()));
		}
		minOutputLine += lineOffset;
		maxOutputLine += lineOffset;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Entry entry : entries) {
			sb.append(entry.toString());
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Gets all the sorted entries in this source map agains the given
	 * comparator.
	 */
	public List<Entry> getSortedEntries(Comparator<Entry> comparator) {
		List<Entry> list = new ArrayList<Entry>(entries);
		list.sort(comparator);
		return list;
	}

	/**
	 * Removes the last inserted entry from this source map.
	 */
	public void removeLastInsertedEntry() {
		if (entries.isEmpty()) {
			return;
		}
		Entry e = insertionOrderEntries.remove(insertionOrderEntries.size() - 1);
		entries.remove(e);
		maxOutputLine = 0;
		for (Entry entry : entries) {
			maxOutputLine = Math.max(maxOutputLine, entry.getOutputPosition().getLine());
		}
	}

}
