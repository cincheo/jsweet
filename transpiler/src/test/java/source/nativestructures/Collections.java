package source.nativestructures;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.any;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
@SuppressWarnings("serial")
public class Collections implements Cloneable, Serializable {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		
	    ArrayList<String> values = new ArrayList<String>();
	    values.add("derp");
	    String cccc = "wow cool";
	    values.add(cccc);
	    assert values.size() == 2;
	    values.remove(0);
	    assert values.size() == 1;
	    values.remove(cccc);
	    assert values.size() == 0;
		
		List<String> l = new ArrayList<String>();

		l.add("a");

		trace.push("" + l.size());
		trace.push(l.get(0));

		l.add("b");

		l.remove(0);

		trace.push("" + l.size());
		trace.push(l.get(0));

		l.add("a");
		l.add("b");

		trace.push("" + l.size());

		l.add(1, "d");

		trace.push("" + l.size());
		trace.push(l.get(1));
		trace.push(l.get(2));

		trace.push(l.subList(1, 2).get(0));

		l.clear();
		trace.push("" + l.size());

		// l.add("a");
		// l = java.util.Collections.EMPTY_LIST;

		trace.push("" + l.size());

		l.add("a");
		l = java.util.Collections.emptyList();

		trace.push("" + l.size());

		l.add("a");
		l = java.util.Collections.unmodifiableList(l);

		trace.push("" + l.get(0));

		List l2 = new ArrayList(l);

		trace.push("" + l2.get(0));

		l.addAll(l2);

		trace.push("" + l.size());
		trace.push("" + l.get(1));

		trace.push("" + l.contains("a"));
		trace.push("" + l.contains("b"));

		String[] array = { "a", "b", "c" };

		l = Arrays.asList(array);

		trace.push("" + l.size());
		trace.push("" + l.get(2));

		Vector v = new Vector(l);

		trace.push("" + v.elementAt(2));

		Enumeration<String> e = v.elements();
		while (e.hasMoreElements()) {
			String s = e.nextElement();
			trace.push("" + s);
		}

		Iterator it = v.iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
			trace.push("" + s);
		}

		trace.push("" + v.toArray()[1]);

		String[] a = Arrays.copyOf(l.toArray(new String[0]), 1);

		trace.push("" + a.length);

		Comparator<String> reverse = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		};

		String[] newArray = (String[]) v.toArray();

		Arrays.sort(newArray, reverse);

		trace.push(newArray[0]);
		trace.push(newArray[1]);
		trace.push(newArray[2]);

		Arrays.sort(newArray, 0, 2);

		trace.push(newArray[0]);
		trace.push(newArray[1]);
		trace.push(newArray[2]);

		l = new Vector<>(5);

		trace.push("" + l.size());

		trace.push("" + l.isEmpty());

		trace.push("" + (l instanceof Vector));

		int i = Integer.MIN_VALUE;
		i = Integer.MAX_VALUE;

		l.add("it");

		for (String s : l) {
			trace.push(s);
		}

		Set<String> s = java.util.Collections.emptySet();
		trace.push("" + s.isEmpty());

		s.add("test");
		s.add("test");

		trace.push("" + s.size());

		Collator c = Collator.getInstance();

		String[] a3 = new String[] { "c", "b", "a" };
		Arrays.sort(a3, c);

		trace.push("array[" + a3 + "]");

		trace.push("" + java.util.Collections.binarySearch(l, "it"));

		l.remove("it");

		trace.push("" + (java.util.Collections.binarySearch(l, "it") >= 0));

		double[][] points1 = new double[][] { new double[] { 1, 2 }, new double[] { 3, 4 }, new double[] { 5, 6 } };

		double[][] points2 = new double[][] { new double[] { 1, 2 }, new double[] { 3, 4 }, new double[] { 5, 6 } };

		trace.push("" + (java.util.Arrays.deepEquals(points1, points2)));

		List<String> l3 = Arrays.asList(a3);

		java.util.Collections.sort(l3);

		java.util.Collections.sort(l3, c);

		String[] array1 = new String[l3.size()];

		l3.toArray(array1);

		trace.push("array[" + array1.toString() + "]");

		Arrays.sort(array1, MyComparator);

		trace.push("array[" + array1.toString() + "]");

		java.util.Collections.reverse(l3);

		trace.push("" + l3 + "");

		Stack<String> stack = new Stack<>();
		stack.push("aa");
		stack.push("bb");
		stack.push("cc");

		trace.push("" + stack);

		trace.push("" + java.util.Collections.binarySearch(stack, "bb1"));

		trace.push("" + java.util.Collections.binarySearch(stack, "bb1", new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}));

		trace.push(stack.peek());

		stack.pop();

		trace.push("" + stack);

		Arrays.fill(array1, "a");

		trace.push("array[" + array1 + "]");

		l.toArray(new String[l.size()]);

		l = new ArrayList<>();
		l.add("a");
		l.add("b");
		l.add("c");

		List<String> ll = new ArrayList<>();
		ll.add("d");
		ll.add("e");

		l.addAll(1, ll);

		trace.push("" + l + "");

		Deque<String> dq = new LinkedList<>();

		dq.addLast("a");
		dq.addLast("b");
		dq.addFirst("c");

		trace.push("" + dq.isEmpty());

		trace.push("" + dq);

		trace.push(dq.pollFirst());

		trace.push("" + dq);

		trace.push(dq.pollLast());

		trace.push("" + dq);

		trace.push(dq.pollLast());

		trace.push("" + dq.pollLast());

		trace.push("" + dq.isEmpty());

		List<String> lll = null;

		trace.push("" + lll);

		Set<Integer> set = new HashSet<Integer>();
		set.add(5);
		set.add(6);
		Set<Integer> set2 = null;

		List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f");
		List<String> list2 = Arrays.asList("d", "e", "f", "g");

		list.removeAll(list2);
		trace.push("" + list);

		list = Arrays.asList("a", "b", "c", "d", "e", "f");

		list.retainAll(list2);
		trace.push("" + list);

		list = Arrays.asList("a", "b", "c", "d", "e", "f");

		trace.push("" + list.containsAll(list2));

		list = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

		trace.push("" + list.containsAll(list2));

		trace.push("" + java.util.Collections.disjoint(list, list2));

		list2 = Arrays.asList("h", "i");

		trace.push("" + java.util.Collections.disjoint(list, list2));

		Arrays.sort(newArray, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		
		java.util.Collections.sort(list2, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		
		HashSet<String> aSet = new HashSet<>();		
		Set<String> aSet2 = new HashSet<>();		
		Set<String> aSet3 = new TreeSet<>();		
		List<String> aList = new ArrayList<>();		
		
		List<String> copies = java.util.Collections.nCopies(7, "nyan");
		trace.push("" + copies);
		
		$export("trace", trace.join(","));

	}

	private static Comparator MyComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			return o2.toString().compareTo(o1.toString());
		}
	};
}

class TestClone {

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
