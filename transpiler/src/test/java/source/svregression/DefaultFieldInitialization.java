
package source.svregression;

import static jsweet.util.Lang.$export;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class DefaultFieldInitialization {
	public static void main(String[] args) {
		Overloaded obj = new Overloaded(44);
		$export("1ary", obj.getNumber());
		
		obj = new Overloaded();
		$export("0ary", obj.getNumber());
	}
}

class Overloaded {
	
	private int number = 0;
	
	public Overloaded( int number ) {
		super();
		this.number = number;
	}
	
	public Overloaded() {
		this(23);
	}
	
	public int getNumber() {
		return this.number;
	}
}