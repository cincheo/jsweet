package source.structural;

public class PrivateMethodNameClashes {

    public static void main(String[] args) {
        assert 1 == new PrivateMethodNameClashesParent().access();
        assert 2 == new PrivateMethodNameClashesChild().access();
    }

}

class PrivateMethodNameClashesParent {

    public int access() {
        return m();
    }
	private int m() {
	    return 1;
	}
}

class PrivateMethodNameClashesChild extends PrivateMethodNameClashesParent {

    public int access() {
        return m();
    }
    private int m() {
        return 2;
    }
}

