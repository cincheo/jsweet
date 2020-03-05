package source.structural.defaultMethods;

public class AClass implements I {

	@Override
	public String m1() {
		return "m1" + m2();
	}

	@Override
	public String m3() {
		return "m3";
	}
	
	public static void main(String[] args) {
		assert "m1m2m3".equals(new AClass().m1());
		assert "m4".equals(new AClass().m4());
	}

}
