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
package org.jsweet.test.transpiler;

import org.junit.Test;

import def.test.ClassWithObjectType;

public class DefinitionsTest extends AbstractTest {

	@Test
	public void testObjectType() {
		transpilerTest().getTranspiler().setGenerateDefinitions(true);
		transpile(h -> {
			h.assertNoProblems();
		}, getSourceFile(ClassWithObjectType.class));
	}

}
