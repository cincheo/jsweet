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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * This object represents a source map between an input source file and an
 * output source file.
 * 
 * @author Renaud Pawlak
 */
public class SourceMap {

	private List<SimpleEntry<Position, Position>> entries = new ArrayList<>();

	/**
	 * Adds an entry to the source map (entry must be added in order).
	 * 
	 * @param inputPosition
	 *            the input position in the input source file
	 * @param outputPosition
	 *            the output position in the output source file
	 */
	public final void addEntry(Position inputPosition, Position outputPosition) {
		entries.add(new SimpleEntry<>(inputPosition, outputPosition));
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
		if (outputLine < entries.get(0).getValue().getLine()) {
			return null;
		}
		if (outputLine > entries.get(entries.size() - 1).getValue().getLine()) {
			return null;
		}
		if (entries != null) {
			for (int i = 0; i < entries.size(); i++) {
				if (i == entries.size() - 1 || (entries.get(i).getValue().getLine() == outputLine || entries.get(i + 1).getValue().getLine() > outputLine)) {
					return entries.get(i).getKey();
				}
			}
		}
		return null;
	}

	/**
	 * Shifts the ouput positions by the given line offset.
	 */
	public final void shiftOutputPositions(int lineOffset) {
		for (Entry<Position, Position> entry : entries) {
			entry.setValue(new Position(entry.getValue().getLine() + lineOffset, entry.getValue().getColumn()));
		}
	}

}
