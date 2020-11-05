package source.overload;

import static jsweet.util.Lang.$export;

public class OverloadWithFunctionalnterface {
    static String result = "";
    
	public static void main(String[] args) {
	    
	    MyLambda l = () -> {
            $export("result_MyLambda", "called");    
        };
        a(1, l);
        
        MyLambdaImpl li = new MyLambdaImpl();
        a(1, li);

        MyLambda2 l2 = () -> {
            $export("result_MyLambda2", "called");    
        };
        a(1, l2);

        MyLambdaImpl2 li2 = new MyLambdaImpl2();
        a(1, li2);
        
	    $export("result", result);
	}
    
    static void a(int a, MyLambda m) {
        result = "first";
        m.doIt();
    }
    
    static void a(int a, MyLambda2 m) {
        m.doIt2();;
    }
    
    static void a(int a, MyLambdaImpl m) {
        m.doItImpl();
    }
    
    static void a(int a, MyLambdaImpl2 m) {
        m.doItImpl2();
    }
	
	static void a(int a, String b) {
	    result = "second";
	}
}

@FunctionalInterface
interface MyLambda {
    void doIt();
}

@FunctionalInterface
interface MyLambdaParent {
    void doItImpl();
}

class MyLambdaImpl implements MyLambdaParent {
    @Override
    public void doItImpl() {
        $export("result_MyLambdaImpl", "called");    
    }
}

interface MyLambda2 {
    void doIt2();
}

interface MyLambdaParent2 {
    void doItImpl2();
}

class MyLambdaImpl2 implements MyLambdaParent2 {
    @Override
    public void doItImpl2() {
        $export("result_MyLambdaImpl2", "called");    
    }
}