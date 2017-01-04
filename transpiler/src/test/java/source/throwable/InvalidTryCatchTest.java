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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import def.js.RangeError;
import def.js.TypeError;

public class InvalidTryCatchTest {

	static String readFirstLineFromFile(String path) throws IOException {
	    try {
	    	
	    } catch(RangeError e1) {
	    	
	    } catch(TypeError e2) {
	    	
	    }
	    try (BufferedReader br =
	                   new BufferedReader(new FileReader(path))) {
	        return br.readLine();
	    }
	}
	
}
