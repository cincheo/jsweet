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
package source.syntax;

import static jsweet.util.Globals.$export;

public class FinalVariablesRuntime {

	static String seq = "";

	Holder[] list = { new Holder(1), new Holder(2), new Holder(3), new Holder(4) };

	void m1() {
		for (int i = 0; i < list.length; i++) {
			Holder h = list[i];
			h.r = () -> {
				seq += h.id;
			};
		}
		for (Holder h1 : list) {
			seq += h1.id;
			h1.r.run();
		}
	}

	public static void main(String[] args) {
		new FinalVariablesRuntime().m1();
		$export("out", seq);
	}
}

class Holder {
	int id;
	Runnable r;

	public Holder(int id) {
		this.id = id;
	}
}
