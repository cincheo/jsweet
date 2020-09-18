package source.syntax;

import jsweet.lang.Async;

interface DTO<T> {
	T getView();
}

class MyDTO implements DTO<String> {
	@Override
	public String getView() {
		return null;
	}
}

public class AsyncAwaitPropagation implements Interface {

	@Async static <T> T test(T t) {
		return null;
	}

	@Async static <T> DTO<T> test2() {
		return null;
	}
	
	@Async int m1() {
		return 0;
	}

	public int m2() {
		return 1 + m1();
	}

    int m2(int i) {
        return i + m1();
    }
	
	void m3() {
		if (m1() == 1) {
			// do something
		} else {
			// do something else
		}
	}

	public int m4() {
		return m2();
	}

    int m5() {
        return m2(2);
    }

	String m5(DTO dto) {
		String s = "2";
		String s2 = test(s);
		DTO dto2 = test(dto);
		MyDTO s3 = (MyDTO)test2().getView();
		return s2;
	}

    
}

interface Interface {
    int m2();
}