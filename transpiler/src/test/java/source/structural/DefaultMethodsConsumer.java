package source.structural;

import def.js.Array;

public class DefaultMethodsConsumer implements IDefaultMethods<String> {

	@Override
	public void m1() {
	}
	
	@Override
	public boolean overload(int index, String p2) {
		return false;
	}
	
	Array<String> trace = new Array<>();
	
	@Override
	public Array<String> getTrace() {
		return trace;
	}
}
