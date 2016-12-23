package source.require.a;

import source.require.a.Outer.Inner;

public class InnerClassAccess {
	public static void main(String[] args) {
		Inner inner = new Inner();
		inner.m();
	}
}
