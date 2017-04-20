package source.statics;

import static jsweet.util.Lang.$export;

public class StaticInitializerWithNoFields {

	static {
		$export("ok", true);
	}

}

