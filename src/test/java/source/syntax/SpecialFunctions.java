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
package source.syntax;

public class SpecialFunctions {

	void m(ApplyI1 a1, ConstrI1 c1) {
		a1.$apply();
		a1.$apply("test");
		c1.$new("test");
	}
	
}

@jsweet.lang.Interface
abstract class ApplyI1 extends jsweet.lang.Object {
    abstract public String $apply();
    abstract public ApplyI1 $apply(String a);
}

@jsweet.lang.Interface
abstract class ConstrI1 extends jsweet.lang.Object {
	abstract public ConstrI1 $new(String a);
}
