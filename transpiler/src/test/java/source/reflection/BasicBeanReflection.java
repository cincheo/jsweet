package source.reflection;

import java.lang.reflect.Method;

public class BasicBeanReflection {

	public static Method getReadMethod(Class<?> clazz, String name) {
		final String name1 = "is" + name;
		final String name2 = "get" + name;
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(name1) || m.getName().equals(name2)) {
				return m;
			}
		}
		return null;
	}

	public static Method getWriteMethod(Class<?> clazz, String name) {
		final String name1 = "set" + name;
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(name1)) {
				return m;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		assert MyBean.class.getMethods().length == 5;
		Method m = getReadMethod(MyBean.class, "B");
		assert m != null;
		assert m.getDeclaringClass() == MyBean.class;
		MyBean b = new MyBean();
		b.setB(true);
		assert b.isB() == (boolean) m.invoke(b);
		Method m2 = getWriteMethod(MyBean.class, "S");
		m2.invoke(b, "abc");
		assert "abc" == b.getS();
	}

}

class MyBean {

	boolean b;

	String s;

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

}
