package source.nativestructures;

import java.util.ArrayList;

class Y extends ArrayList<Integer> {

}

public class ExtendsJDKRegular {

	public static void main(String[] args) {
		// This all works in normal arraylists
		Y y = new Y();
		y.add(10);
		y.add(20);
		assert y.size() == 2;
		Y other = new Y();
		other.addAll(y);
		assert other.size() == 2;
		int index = 1;
		for (Integer i : y) {
			assert i == index * 10;
			index++;
		}
		index = 1;
		for (Integer i : other) {
			assert i == index * 10;
			index++;
		}
		ArrayList<Integer> realY = new ArrayList<>();
		realY.add(10);
		realY.add(20);
		assert realY.size() == 2;
		index = 1;
		for (Integer i : realY) {
			assert i == index * 10;
			index++;
		}
	}

}
