package source.overload;

public class NonPublicRootMethod implements PrivateRootMethodInterface {

	@Override
	public void m(String s) {
		m(s, true);
	}
	
	private void m(String s, boolean b) {
		System.out.println(s);
	}

	@Override
	public void m2(String s) {
		m(s, true);
	}
	
	protected void m2(String s, boolean b) {
		System.out.println(s);
	}
	
}


interface PrivateRootMethodInterface {
	void m(String s);
	void m2(String s);
}