package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class WrongOverloadWithSpecialParameters {

	public static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new WrongOverloadWithSpecialParameters().m('a');
		new WrongOverloadWithSpecialParameters().m("a");
		new WrongOverloadWithSpecialParameters().read();
		$export("trace", trace.join(","));
	}

	public int read() {
		trace.push("read1");
		return read(null, 0, 0);
	}

	public int read(byte[] buffer, int byteOffset, int byteCount) {
		trace.push("read2");
		return 2;
	}
	
	// TODO: this overload is hidden by the string overload, so we should raise a warning!
	public void m(char c) {
		trace.push("m");
		m(String.valueOf(c));
	}

	public void m(String s) {
		trace.push("m");
	}
	
}

