package org.jsweet.test.transpiler.source.structural;

import static jsweet.dom.Globals.*;
import jsweet.dom.*;

public class NoWildcardsInImports {

	public static void main(String[] args) {
		HTMLElement element = document.getElementById("test");
		element.innerHTML = "test";
	}
	
}
