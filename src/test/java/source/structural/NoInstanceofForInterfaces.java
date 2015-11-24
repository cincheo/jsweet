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
package source.structural;

import jsweet.lang.Interface;

public class NoInstanceofForInterfaces {

	void m(Object o) {
		if (o instanceof I1) {
		}
		if (o instanceof I2) {
		}
		if (o instanceof C3) {
		}
	}

}

interface I1 {

}

@Interface
abstract class I2 {
}

class C3 {
}

