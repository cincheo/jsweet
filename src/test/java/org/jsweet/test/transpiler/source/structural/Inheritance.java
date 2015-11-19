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

import jsweet.lang.Interface;

class AloneInTheDarkClass extends jsweet.lang.Object {
	public AloneInTheDarkClass() {
		// super() call shouldn't be generated
		
		int i = 5;
	}
}

public class Inheritance extends SuperClass1 {
	public Inheritance() {
		super();
	}
}

class SuperClass1 extends SuperInterface1 {
	public SuperClass1() {
	}
}

@Interface
abstract class SuperInterface1 {
}

@Interface
abstract class SubInterface extends SuperInterface1 {
}

// TODO: this is weird... it works in Typescript... check what it means
@Interface
abstract class SubInterface1 extends SuperClass1 {
}
