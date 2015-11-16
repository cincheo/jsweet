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


public enum WrongConstructsInEnums {

	A, B, C;
	
	// fields are not allowed
	public long l = 4;
	
	static String s1;

	private String s2;
	
	// constructors are not allowed
	private WrongConstructsInEnums() {
		l = 4;
	}

	// methods of any kinds are not allowed
	native public void m1();
	
	public void m2() {
		l = 4;
	}

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
