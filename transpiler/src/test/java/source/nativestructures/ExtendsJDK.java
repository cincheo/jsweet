package source.nativestructures;

import java.util.AbstractSet;
import java.util.Iterator;

public class ExtendsJDK {

	public static void main(String[] args) {
		new ExtendsJDK().m();
	}

	private void m() {
		KeySet keySet = new KeySet();
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
