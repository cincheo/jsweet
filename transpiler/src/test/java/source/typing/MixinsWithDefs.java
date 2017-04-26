package source.typing;

import def.test.Globals;
import static def.test.Globals.$;

public class MixinsWithDefs {
	public static void main(String[] args) {
		$(".modal").modal();
		$("select").material_select();
		Globals.$("test").modal("");
		$("test").animate("test");
		$(".modal").attr("class");
	}
}
