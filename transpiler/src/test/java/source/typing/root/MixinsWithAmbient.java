package source.typing.root;

import static source.typing.root.Globals.$;

import jsweet.lang.Ambient;
import jsweet.lang.Erased;
import jsweet.lang.Mixin;

@Ambient
class Globals {
	@Erased
	public static native JQuery $(CharSequence query);
}

@Mixin(target = def.jquery.JQuery.class)
@Ambient
class JQuery extends def.jquery.JQuery {
	public native void modal();

	public native void modal(String action);

	public native void material_select();

}

public class MixinsWithAmbient {
	public static void main(String[] args) {
		$(".modal").modal();
		$("select").material_select();
		Globals.$("test").modal("");
		$("test").animate("test");
		$(".modal").attr("class");
	}
}
