package source.init;

import static jsweet.util.Lang.$export;

import jsweet.lang.Interface;

public class ParentInstanceAccess {

	public static void main(String[] args) {
		$export("field1", new ParentInstanceAccess().m1().field);
		$export("field2", new ParentInstanceAccess().m2().field);
	}

	String field = "test";

	DataStruct13 m1() {
		return new DataStruct13() {
			{
				field = ParentInstanceAccess.this.field;
			}
		};
	}

	DataStruct14 m2() {
		return new DataStruct14() {
			{
				field = ParentInstanceAccess.this.field;
			}
		};
	}

}

class DataStruct13 {
	String field;
}

@Interface
class DataStruct14 {
	String field;
}
