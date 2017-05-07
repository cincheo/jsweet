package def.test2;

import jsweet.lang.Mixin;
import jsweet.lang.Name;

@Mixin(target = def.jquery.JQuery.class)
public class ExtendedJQuery extends def.jquery.JQuery {
	public native void modal();

	public native void modal(String action);

	public native ExtendedJQuery material_select();
	
	@Name("EXT")
	public native ExtendedJQuery myExtension();

}
