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
package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;

import org.jsweet.transpiler.util.Util;
import org.junit.Test;

public class UtilTests {

	@Test
	public void testConvertToRelativePath() {
		assertEquals("../c", Util.getRelativePath("/a/b", "/a/c"));
		assertEquals("..", Util.getRelativePath("/a/b", "/a"));
		assertEquals("../e", Util.getRelativePath("/a/b/c", "/a/b/e"));
		assertEquals("d", Util.getRelativePath("/a/b/c", "/a/b/c/d"));
		assertEquals("d/e", Util.getRelativePath("/a/b/c", "/a/b/c/d/e"));
		assertEquals("../../../d/e/f", Util.getRelativePath("/a/b/c", "/d/e/f"));
		assertEquals("../..", Util.getRelativePath("/a/b/c", "/a"));
		assertEquals("..", Util.getRelativePath("/a/b/c", "/a/b"));
	}
	
}
