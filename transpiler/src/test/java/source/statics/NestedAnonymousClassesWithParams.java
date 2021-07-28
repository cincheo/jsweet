package source.statics;

import static jsweet.util.Lang.$export;

public class NestedAnonymousClassesWithParams {

	public static void main(String[] args) {
		Interface1 interface1 = method("TestParam1");
		Interface2 interface2 = interface1.getInterface2();
		interface2.methodInterface2();
	}
	
	public static Interface1 method(String param1) {
		return new Interface1() {
			
			@Override
			public Interface2 getInterface2() {
				return new Interface2() {
					@Override
					public void methodInterface2() {
						$export("methodInterface2_val", val);
					}
					
					String val;
					
					{
						val = param1;
					}
				};
			}
		};
	}


	public static interface Interface1 {
		Interface2 getInterface2();
	}
	public static interface Interface2 {
		void methodInterface2();
	}
}
