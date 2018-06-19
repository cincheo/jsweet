package org.jsweet.test.transpiler.util;

import static org.junit.Assert.assertEquals;

import org.jsweet.test.transpiler.AbstractTest;
import org.jsweet.transpiler.util.Util;
import org.junit.Test;

public class UtilTest extends AbstractTest {

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
