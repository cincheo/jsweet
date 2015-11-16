/* Copyright 2015 CINCHEO SAS
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
package org.jsweet.test.transpiler.source.structural;

import static jsweet.util.Globals.$export;

public class AutoImportClassesInSamePackageUsed {

	public void m1() {
		$export("m1", true);
	}

	void m2() {
		$export("m2", true);
	}

	public static void sm1() {
		$export("sm1", true);
	}

	static void sm2() {
		$export("sm2", true);
	}
	
}
