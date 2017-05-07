package source.typing;

import static def.test2.Globals.$;

import def.test2.ExtendedJQuery;
import def.test2.Globals;

public class MixinsWithDefsAndOtherName {
	public static void main(String[] args) {
		$(".modal").modal();
		$("select").material_select().addClass("animated");
		Globals.$("test").modal("");
		$("test").animate("test").addClass("animated");
		$(".modal").attr("class");
		ExtendedJQuery extendedJQuery = $("test").myExtension();
		extendedJQuery.addClass("test");
	}
}
