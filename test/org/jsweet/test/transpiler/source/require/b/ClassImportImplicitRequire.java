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
package org.jsweet.test.transpiler.source.require.b;

import static def.express.Globals.express;
import static jsweet.util.Globals.$export;

import org.jsweet.test.transpiler.source.require.a.A;
import org.jsweet.test.transpiler.source.require.a.b.B1;
import org.jsweet.test.transpiler.source.require.a.b.B2;

import def.express.express.Express;

public class ClassImportImplicitRequire {

	public static void main(String[] args) {
		$export("mainInvoked", true);

		Express app = express();

		A a = new A();
		a.m();
		B1 b1 = new B1();
		b1.m();
		B2 b2 = new B2();
		b2.m();

	}

}
