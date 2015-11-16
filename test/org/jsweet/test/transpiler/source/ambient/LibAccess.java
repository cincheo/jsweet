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
package org.jsweet.test.transpiler.source.ambient;

import jsweet.lang.Ambient;

public class LibAccess {
	public static void main(String[] args) {
		Base m = new org.jsweet.test.transpiler.source.ambient.Base();
		m.m1();

		//MixinInterface.class.cast(m).extension();

		((org.jsweet.test.transpiler.source.ambient.Extension) m).m2();

		//MixinInterface.class.cast(get()).extension();

		((Extension) get()).m2();
		
	}

	public static Base get() {
		return new Base();
	}
}

// TODO : force extends jsweet obj? + tests => only native and ctor => ctor with
// args
@Ambient
//@Interface
class Base extends jsweet.lang.Object {
	public Base() {
	}

	public native void m1();

}

// TODO : force interface on both sides
// @Extension(target = MixedIn.class)
@Ambient
//@Interface
class Extension extends Base {
	native public void m2();
}
