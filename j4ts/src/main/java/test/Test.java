package test;

import static def.dom.Globals.console;
import static def.dom.Globals.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import def.dom.HTMLElement;
import jsweet.util.Globals;

public class Test {

	public static void assertEquals(Object o1, Object o2) {
		if (!Globals.equalsStrict(o1, o2)) {
			throw new Error("invalid assertion: " + o1 + "!=" + o2);
		}
	}

	public static void assertTrue(boolean b) {
		if (!b) {
			throw new Error("invalid assertion");
		}
	}

	public static void assertFalse(boolean b) {
		if (b) {
			throw new Error("invalid assertion");
		}
	}

	public static void test() {
		try {
			testArrays();
			testList();
			testMap();
			testSet();
			testString();
			testIO();
			// not available
			// testMath();
			HTMLElement result = document.getElementById("result");
			if (result != null) {
				result.innerHTML = "Success!";
			}
		} catch (Exception e) {
			console.error(e);
			HTMLElement result = document.getElementById("result");
			if (result != null) {
				result.innerHTML = "Failure: " + e.getMessage();
			}
		}
	}

	public static void testArrays() {
		console.info("testing arrays");
		String[] srcArray = { "a", "b", "c" };
		String[] dstArray = new String[srcArray.length - 1];
		System.arraycopy(srcArray, 1, dstArray, 0, srcArray.length - 1);
		assertEquals(2, dstArray.length);
		assertEquals("b", dstArray[0]);
		assertEquals("c", dstArray[1]);
		int[] myArray = { 3, 2, 1 };
		assertEquals(3, myArray[0]);
		Arrays.sort(myArray);
		assertEquals(1, myArray[0]);
		console.info("end testing arrays");
	}

	public static void testList() {
		console.info("testing lists");
		List<String> l = new ArrayList<String>();
		l.add("a");
		l.add("b");
		l.add("c");
		assertEquals(l.toString(), "[a, b, c]");
		assertEquals(l.subList(1, 3).toString(), "[b, c]");
		assertEquals(l.remove("b"), true);
		assertEquals(l.remove("d"), false);
		assertEquals(l.remove(1), "c");
		l.add("c");
		// l.remove("b");
		assertEquals(l.toString(), "[a, c]");
		assertEquals(l.size(), 2);
		assertEquals(l.get(1), "c");
		assertEquals(l.indexOf("a"), 0);
		String res = "";
		for (String s : l) {
			res += s;
		}
		assertEquals("ac", res);
		Iterator<String> it = l.iterator();
		assertTrue(it.hasNext());
		assertEquals("a", it.next());
		assertTrue(it.hasNext());
		assertEquals("c", it.next());
		assertFalse(it.hasNext());
		console.info("end testing lists");
	}

	public static void testSet() {
		console.info("testing sets");
		Set<String> s = new HashSet<String>();
		s.add("a");
		s.add("a");
		s.add("b");
		s.add("c");
		s.add("c");
		assertEquals(s.toString(), "[a, b, c]");
		s.remove("b");
		assertTrue(s.contains("a"));
		assertTrue(s.contains("c"));
		assertFalse(s.contains("b"));
		assertEquals(s.size(), 2);
		// BitSet
		console.info("testing bit sets");
		BitSet bs = BitSet.valueOf(new long[] { 255 });
		assertTrue(bs.get(0));
		assertTrue(bs.get(1));
		assertTrue(bs.get(7));
		assertFalse(bs.get(8));
		BitSet bs2 = BitSet.valueOf(new long[] { 1 });
		assertTrue(bs2.get(0));
		assertFalse(bs2.get(1));
		bs.and(bs2);
		assertTrue(bs.get(0));
		assertFalse(bs.get(1));
		console.info("end testing sets");
	}

	public static void testMap() {
		console.info("testing maps");
		Map<String, String> m = new HashMap<String, String>();
		m.put("a", "aa");
		m.put("b", "bb");
		m.put("c", "cc");
		assertEquals(m.size(), 3);
		assertEquals("bb", m.get("b"));
		m.remove("aa");
		assertEquals(m.size(), 3);
		m.remove("a");
		assertEquals(m.size(), 2);
		console.info("end testing maps");
	}

	public static void testString() {
		console.info("testing strings");
		StringBuilder sb = new StringBuilder();
		sb.append(true);
		sb.append('c');
		sb.append("test");
		sb.deleteCharAt(sb.length() - 1);
		assertEquals("truectes", sb.toString());
		sb.append("abc", 0, 1);
		assertEquals("truectesa", sb.toString());
		StringBuffer sb2 = new StringBuffer();
		sb2.append(true);
		sb2.append('c');
		sb2.append("test");
		sb2.deleteCharAt(sb2.length() - 1);
		assertEquals("truectes", sb2.toString());
		assertEquals('a', Character.toLowerCase('A'));
		assertEquals("abc", "ABC".toLowerCase());
		console.info("end testing strings");
	}

	public static void testIO() throws IOException {
		console.info("testing io");
		ByteArrayInputStream s = new ByteArrayInputStream("abc".getBytes());
		assertEquals(Character.getNumericValue('a'), s.read());
		console.info("end testing io");
	}

	// java.math is not available yet and should be implemented as a wrapper to
	// bignumber.js

	// static BigInteger fatFactorial(int b) {
	// if (BigInteger.ONE.equals(BigInteger.valueOf(b))) {
	// return BigInteger.ONE;
	// } else {
	// return BigInteger.valueOf(b).multiply(fatFactorial(b - 1));
	// }
	// }
	//
	// public static void testMath() {
	// console.info("testing math");
	// BigInteger b3 = new BigInteger("10");
	// b3.toString();
	// BigInteger b1 = new BigInteger("987654321987654321000000000");
	// BigInteger b2 = new BigInteger("987654321987654321000000000");
	// // BigInteger product = b1.multiply(b2);
	// BigInteger division = b1.divide(b2);
	// assertEquals("1", division.toString());
	// assertEquals("30414093201713378043612608166064768844377641568960512000000000000",
	// fatFactorial(50).toString());
	// assertTrue(new BigDecimal("0").compareTo(BigDecimal.ZERO) == 0);
	// assertTrue(new BigDecimal("0.00").compareTo(BigDecimal.ZERO) == 0);
	// assertTrue(new BigDecimal("0").equals(BigDecimal.ZERO));
	// assertFalse(new BigDecimal("0.00").equals(BigDecimal.ZERO));
	// BigDecimal a = new BigDecimal("2.5");
	// assertTrue(a.toString().equals("2.5"));
	// BigDecimal b = new BigDecimal("1.5");
	// assertEquals("2", a.setScale(0, BigDecimal.ROUND_HALF_EVEN).toString());
	// assertEquals("2", b.setScale(0, BigDecimal.ROUND_HALF_EVEN).toString());
	// assertEquals("4", a.add(b).toString());
	// console.info("end testing math");
	// }

}
