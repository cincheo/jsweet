package source.syntax;

import jsweet.lang.Async;

public class AsyncAwaitPropagation implements Interface {

    @Async
    int m1() {
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
}

interface Interface {
    int m2();
}