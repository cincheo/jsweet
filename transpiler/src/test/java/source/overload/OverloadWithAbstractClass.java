package source.overload;

import static def.dom.Globals.console;

public class OverloadWithAbstractClass {

	public static abstract class AbstractTest {

		public void draw(int x, int y) {
			draw(x, y, 1, -1, 1);
		}

		public abstract void draw(int i_1, int i_2, int i_3, int i_4, int i_5);

	}

	public static class AbstractTestImpl extends AbstractTest {

		@Override
		public void draw(int i_1, int i_2, int i_3, int i_4, int i_5) {
			console.log("Hello world.");
		}

	}

	public static void main(String[] args) {
		AbstractTest test = new AbstractTestImpl();
		test.draw(0, 0);
	}
}

interface Graph {

	String getEdges();

	Object createVertex();

	String getEdges(String name);

}

abstract class AbstractGraph implements Graph {
    public abstract int read() throws Exception;

    public int read(byte[] buffer) throws Exception {
        return read(buffer, 0, buffer.length);
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws Exception {
        if (read() == 100) {
            return 0;
        }
        return 2;
    }
}

class MyGraph extends AbstractGraph {

    @Override
    public int read() throws Exception {
        return 100;
    }
    
	@Override
	public Object createVertex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEdges(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
