package source.calculus;

import static jsweet.util.Lang.$export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Operators {

	static boolean expr1 =false;
	static boolean expr2 =false;
	
	static boolean expr1() {
		expr1 = true;
		return expr1;
	}

	static boolean expr2() {
		expr2 = true;
		return expr2;
	}
	
	public static void main(String[] args) {
		boolean b1 = true;
		boolean b2 = !b1;

		boolean b3 = b1 | b2;
		assert b3;
		b3 &= b1;
		// assert b3 == true;
		b1 |= b3;
		// assert b1;

		b1 = b1 ^ b2;
		assert b1;

		int i1 = 1;
		int i2 = 3;
		int i3 = i1 | i2;
		i1 &= i3;

		assert xor(true, false);
		assert !xor(true, true);

		testWithObjects();
		
		Operators op = new Operators();
		assert op.m(Arrays.asList("a", "b"));
		
		assert op.c.size() == 2;
		assert op.c.equals(Arrays.asList("a", "b"));
		
		assert expr1() || expr2();
		assert expr1;
		assert !expr2;
		assert expr1() | expr2();
		assert expr1;
		assert expr2;
		
		testBitwiseShortcut();
	}
	
	private static void testBitwiseShortcut() {
		long mask = 0;
		mask |= 1;
		
		$export("bitwise_or_assign", mask);
		
		mask = mask << 1;
		$export("bitwise_leftshift", mask);
		
		mask <<= 1;
		$export("bitwise_leftshift_assign", mask);
		
		char ch = (char) 3;
        mask |= (1L << ch);
		$export("bitwise_leftshift_char", mask);
		
		mask |= ch;
		$export("bitwise_or_assign_char", mask);
		
		mask &= ch;
		$export("bitwise_and_assign_char", mask);
		
		mask <<= ch;
		$export("bitwise_lshift_assign_char", mask);

		mask >>= ch;
		$export("bitwise_rshift_assign_char", mask);
		
		mask /= ch;
		$export("bitwise_div_assign_char", mask);
		
		mask -= ch;
		$export("bitwise_minus_assign_char", mask);
		
		mask %= ch;
		$export("bitwise_modulo_assign_char", mask);
		
		mask *= ch;
		$export("bitwise_multiply_assign_char", mask);
		
		mask += ch;
		$export("bitwise_plus_assign_char", mask);
		
		mask ^= ch;
		$export("bitwise_xor_assign_char", mask);
		
		char cch = 60;
		cch += 5;
		$export("bitwise_add_to_char", cch);
		
		cch = 'C' / 3;
		cch *= ch;
		$export("bitwise_multiply_assign_char_to_char", cch);
	}

	Collection<String> c = new ArrayList<String>();
	
	public boolean add(String e) {
		return c.add(e);
		//return true;
	}

	public boolean m(Collection<String> c) {
		boolean changed = false;
		for (String e : c) {
			changed |= add(e);
		}
		return changed;
	}

	public static void testWithObjects() {
		Boolean b1 = true;
		Boolean b2 = !b1;

		Boolean b3 = b1 | b2;
		assert b3;
		b3 &= b1;
		// assert b3 == true;
		b1 |= b3;
		// assert b1;

		b1 = b1 ^ b2;
		assert b1;

	}

	public static Boolean xor(Boolean x, Boolean y) {
		return x ^ y;
	}

	public static boolean work(int s1, int s2) {
		boolean b = s1 == -1 ^ s2 == -1;
		return b;
	}
}
