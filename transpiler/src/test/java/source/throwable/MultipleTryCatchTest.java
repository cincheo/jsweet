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
package source.throwable;

import static jsweet.util.Globals.$export;

import def.js.RangeError;
import def.js.SyntaxError;
import def.js.TypeError;

public class MultipleTryCatchTest {

	static void m() {
		$export("executed", true);
		throw new RangeError("test-message");
	}

	public static void main(String[] args) {
		try {
			MultipleTryCatchTest.m();
		} catch (RangeError e) {
			$export("message1", e.message);
		} catch (TypeError e) {
			$export("message2", e.message);
		} catch (SyntaxError e) {
			$export("message3", e.message);
		} finally {
			$export("finally_executed", true);
		}
	}
	
}
