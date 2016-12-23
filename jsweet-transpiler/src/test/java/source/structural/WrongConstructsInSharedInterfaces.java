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
package source.structural;

import jsweet.lang.Interface;
import jsweet.lang.Mode;

@Interface(Mode.SHARED)
public class WrongConstructsInSharedInterfaces {

	// field initializers are erased
	public long l = 4;

	// statics are erased
	static String s1;

	// private are made public
	private String s2;

	// constructors are erased
	public WrongConstructsInSharedInterfaces() {
		l = 4;
	}

	native public int m1();

	// bodies are erased
	public String m2() {
		l = 4;
		return "";
	}

	// statics are erased
	native static void m3();

	native public void m4();

	// bodies are erased
	public void m5() {
		l = 4;
	}

	// initializers are erased
	{
		l = 4;
	}

	// static initializers are erased
	static {
		s1 = "";
	}

}
