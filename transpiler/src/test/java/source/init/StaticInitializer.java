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
package source.init;

import static jsweet.util.Lang.$export;

public class StaticInitializer {

	static int a = 100;
	static int b = -100;
	public final static String c = "100";
	public final static int fa = 100;
	public final static int fb = -100;
	static int ab = a + b;
	static int d = 100 + 100;

	public static final double MAX_VALUE = 90.0;
	public static final double MIN_VALUE = -90.0;
	public static final double testExpression = 150 + 44 + 1e3 - 90.0;

	static int n;

	static {
		n = 4;
	}

	static String s;

	static {
		s = "test";
	}

	public static void main(String[] args) {
		$export("n", StaticInitializer.n);
		$export("s", StaticInitializer.s);
	}

}
