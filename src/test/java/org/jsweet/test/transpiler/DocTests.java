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

import static org.junit.Assert.assertEquals;

import org.jsweet.transpiler.ModuleKind;
import org.junit.Test;

import source.doc.LanguageSpecifications;

public class DocTests extends AbstractTest {

	@Test
	public void testLanguageSpecifications() {
		eval(ModuleKind.none, (ctx, result) -> {
			assertEquals(4, result.<Number> get("main_n"));
			assertEquals(true, result.<Boolean> get("finished"));
		}, getSourceFile(LanguageSpecifications.class));

	}

}
