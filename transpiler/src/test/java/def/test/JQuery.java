package def.test;

import jsweet.lang.Mixin;

@Mixin(target = def.jquery.JQuery.class)
public class JQuery extends def.jquery.JQuery {
	public native void modal();

	public native void modal(String action);

	public native JQuery material_select();
	
	public native JQuery myExtension();
	

}
