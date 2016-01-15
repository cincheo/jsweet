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
package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;

public class TestTranspilationHandler extends ConsoleTranspilationHandler {

	List<JSweetProblem> reportedProblems = new ArrayList<>();
	List<SourcePosition> reportedSourcePositions = new ArrayList<>();

	@Override
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		super.report(problem, sourcePosition, message);

		// TODO : this should be cleaner. Warnings should be added to a side
		// list so we could assert that problems size == 0 even with warnings
		if (problem == JSweetProblem.CANDY_VERSION_DISCREPANCY) {
			return;
		}

		reportedProblems.add(problem);
		reportedSourcePositions.add(sourcePosition);
	}

	public void assertReportedProblems(JSweetProblem... expectedProblems) {
		List<JSweetProblem> expectedProblemsList = Arrays.asList(expectedProblems);
		assertEquals(expectedProblemsList, reportedProblems);
	}

	public List<JSweetProblem> getReportedProblems() {
		return reportedProblems;
	}

	public List<SourcePosition> getReportedSourcePositions() {
		return reportedSourcePositions;
	}

}
