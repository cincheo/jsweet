/*
 * Copyright (c) 2016, CINCHEO, renaud.pawlak@cincheo.fr
 * 
 * Apache 2 license.
 */

package java.util;

import static jsweet.util.Globals.array;
import static jsweet.util.Globals.equalsLoose;

import def.js.Array;

/**
 * Incomplete and naive implementation of the BitSet utility (mainly for
 * compatibility/compilation purpose).
 * 
 * @author Renaud Pawlak
 */
@SuppressWarnings("serial")
public class BitSet implements Cloneable, java.io.Serializable {

	private Boolean[] bits = {};

	public BitSet() {
	}

	public BitSet(int nbits) {
		while (nbits > 0) {
			array(bits).push(false);
			nbits--;
		}
	}

	public static BitSet valueOf(long[] longs) {
		BitSet bs = new BitSet();
		bs.bits = array(new Array<Boolean>(longs.length * 64));
		for (int n = 0; n < longs.length * 64; n++) {
			bs.bits[n] = ((longs[n / 64] & (1L << (n % 64))) != 0);
		}
		return bs;
	}

	public void flip(int bitIndex) {
		bits[bitIndex] = !bits[bitIndex];
	}

	public void flip(int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			flip(i);
		}
	}

	public void set(int bitIndex) {
		bits[bitIndex] = true;
	}

	public void set(int bitIndex, boolean value) {
		if (value) {
			set(bitIndex);
		} else {
			clear(bitIndex);
		}
	}

	public void set(int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			set(i);
		}
	}

	public void set(int fromIndex, int toIndex, boolean value) {
		if (value) {
			set(fromIndex, toIndex);
		} else {
			clear(fromIndex, toIndex);
		}
	}

	public void clear(int bitIndex) {
		bits[bitIndex] = false;
	}

	public void clear(int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			clear(i);
		}
	}

	public void clear() {
		bits = new Boolean[0];
	}

	public boolean get(int bitIndex) {
		return bits[bitIndex];
	}

	public BitSet get(int fromIndex, int toIndex) {
		BitSet bs = new BitSet();
		for (int i = fromIndex; i <= toIndex; i++) {
			array(bs.bits).push(bits[i]);
		}
		return bs;
	}

	public int length() {
		return bits.length;
	}

	public boolean isEmpty() {
		return bits.length == 0;
	}

	public int cardinality() {
		int sum = 0;
		for (int i = 0; i < bits.length; i++) {
			sum += bits[i] ? 1 : 0;
		}
		return sum;
	}

	public void and(BitSet set) {
		for (int i = 0; i < bits.length; i++) {
			bits[i] = bits[i] && set.get(i);
		}
	}

	public void or(BitSet set) {
		for (int i = 0; i < bits.length; i++) {
			bits[i] = bits[i] || set.get(i);
		}
	}

	public void xor(BitSet set) {
		for (int i = 0; i < bits.length; i++) {
			bits[i] = (bits[i] && !set.get(i)) || (!bits[i] && set.get(i));
		}
	}

	public void andNot(BitSet set) {
		for (int i = 0; i < bits.length; i++) {
			bits[i] = bits[i] && !set.get(i);
		}
	}

	public int size() {
		return bits.length;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof BitSet))
			return false;
		if (this == obj)
			return true;

		BitSet set = (BitSet) obj;

		if (set.bits.length != bits.length) {
			return false;
		}

		for (int i = 0; i < set.bits.length; i++) {
			if (!equalsLoose(set.bits[i], bits[i])) {
				return false;
			}
		}

		return true;
	}

	public Object clone() {
		BitSet bs = new BitSet();
		bs.bits = array(bits).slice(0, bits.length);
		return bs;
	}

}
