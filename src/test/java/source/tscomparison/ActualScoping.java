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
package source.tscomparison;

import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import jsweet.dom.Element;

public class ActualScoping {
	public static void main(String[] args) {
		if (true) {
			// the variable is scoped
			boolean scoped = true;
		} else {
			// each block has its "scoped" variable
			boolean scoped = false;
		}

		for (int i = 0; i < 10; i++) {
			Element div = document.querySelector("#container div:nth-child(" + i + ")");
			div.addEventListener("click", (e) -> {
				/*
				 * div is the div variable corresponding to scope i.e. the div
				 * for this the current iteration loop
				 */
				div.textContent = "clicked";
			});
		}
	}
}