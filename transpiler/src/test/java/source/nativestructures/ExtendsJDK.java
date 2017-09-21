package source.nativestructures;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;

public class ExtendsJDK {

	public static void main(String[] args) {
		new ExtendsJDK().m();
	}

	private void m() {
		KeySet keySet = new KeySet();
		KeySet2 keySet2 = new KeySet2();
		List2 l = new List2();
	}

	private class KeySet extends AbstractSet {

		@Override
		public Iterator iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}

class KeySet2 extends AbstractSet {

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}

class List2 extends ArrayList<String> {

	@Override
	public boolean add(String e) {
		return super.add(e);
	}
	
}
