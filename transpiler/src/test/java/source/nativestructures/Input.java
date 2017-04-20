package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class Input {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) throws IOException {

		Reader r = new StringReader("this is a test");

		trace.push(""+new Character((char)r.read()));
		trace.push(""+new Character((char)r.read()));
		r.skip(1);
		trace.push(""+new Character((char)r.read()));
		r.reset();
		trace.push(""+new Character((char)r.read()));
		r.close();
		$export("trace", trace.join(","));

	}
}

