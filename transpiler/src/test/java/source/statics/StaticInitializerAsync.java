package source.statics;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.asyncReturn;
import static jsweet.util.Lang.await;

import jsweet.lang.Async;

class Other {
	@Async
	def.js.Promise<String> asyncOperation() {
		return asyncReturn("that's it");
	}
}

public class StaticInitializerAsync {
	static {
		Other o = new Other();
		String result = await(o.asyncOperation());
		$export("result", result);
	}
}