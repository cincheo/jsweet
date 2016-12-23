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

		public final Position getInputPosition() {
			return inputPosition;
		}

		public final Position getOutputPosition() {
			return outputPosition;
		}

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
				if (i == e.length - 1 || (e[i].getOutputPosition().getLine() == outputLine || e[i + 1].getOutputPosition().getLine() > outputLine)) {
					while (i + 1 < e.length && e[i + 1].getOutputPosition().getLine() == outputLine && e[i].getOutputPosition().getColumn() < outputColumn) {
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
			entry.setOutputPosition(new Position(entry.getOutputPosition().getLine() + lineOffset, entry.getOutputPosition().getColumn()));
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

	public List<Entry> getSortedEntries(Comparator<Entry> comparator) {
		List<Entry> list = new ArrayList<Entry>(entries);
		list.sort(comparator);
		return list;
	}

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
