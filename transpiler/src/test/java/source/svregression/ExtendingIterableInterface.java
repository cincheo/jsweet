
package source.svregression;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ExtendingIterableInterface {
	public static void main(String[] args) {
		WrappedList list = new WrappedListImpl();  // WrappedList interface inherits the iterable method, and yet...
		for( int num : list ) {
			System.out.println( num );
		}
	}
}

interface WrappedList extends Iterable<Integer> {
	
	public void doSomething();
}

class WrappedListImpl implements WrappedList {
	
	private List<Integer> nums = new ArrayList<>();
		
	public WrappedListImpl() {
		this.nums.add( 3 );
		this.nums.add( 5 );
		this.nums.add( 8 );
	}
	
        public Iterator<Integer> iterator() {
		return this.nums.iterator();
	}
	
	public void doSomething() {
	}
}
