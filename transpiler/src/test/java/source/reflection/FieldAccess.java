package source.reflection;

public class FieldAccess {

	public static void main(String[] args)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		C c = new C();
		assert "abc".equals(c.getClass().getField("f1").get(c));
		C.class.getField("f2").set(c, "def");
		assert "def".equals(c.getClass().getDeclaredField("f2").get(c));
		c.getClass().getDeclaredField("f2").setAccessible(true);
		assert c.f2.equals(C.class.getDeclaredField("f2").get(c));
		C.class.getField("f3").set(c, "ghi");
		assert "ghi".equals(c.getClass().getField("f3").get(c));
	}

}

class C {

	private String f1 = "abc";
	public String f2;

}
