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

@Interface
public class WrongConstructsInInterfaces {

	// field initializers are not allowed
	public long l = 4;
	
	// statics are not allowed
	static String s1;

	// private are not allowed
	private String s2;
	
	// constructors are not allowed
	public WrongConstructsInInterfaces() {
		l = 4;
	}
	
	native public void m1();
	
	// bodies are not allowed
	public void m2() {
		l = 4;
	}

	// statics are not allowed
	native static void m3();
	
	// initializers are not allowed
	{
		l = 4;
	}
	
	// static initializers are not allowed
	static {
		s1 = "";
	}
	
}
