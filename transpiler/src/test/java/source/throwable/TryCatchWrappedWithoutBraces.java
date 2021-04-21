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

import static jsweet.util.Lang.$export;

import def.js.Error;

public class TryCatchWrappedWithoutBraces {

	public static void main(String[] args) {

		// wrapping if without braces
		boolean anyway = false;
		if (anyway)
			try {
				$export("case2_tryExecuted", true);	
			} catch (Throwable t) {
				$export("case2_catchExecuted", true);
			}
		else
			$export("case2_elseExecuted", true);
	}

}
