package source.syntax;

import static jsweet.util.Lang.$export;

public class UnreachableCode {

	public static void main(String[] args) {
		String str = null;
		if (true) {
			str = "OUI";
		} else {
			str = "NON";
		}
		
		$export("reachableExecuted", str);
	}
}
