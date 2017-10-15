package source.structural;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class FieldInitialization {

	static Array<String> trace = new Array<>();
	
	public static void main(String[] args) {
		ChildC c = new ChildC("test");
		$export("trace", trace.join(","));
		assert c.childField == "test";
	}

}

class ParentC {
	Object parentField;

	public ParentC(Object field) {
		FieldInitialization.trace.push("Parent: "+field+"/"+this.parentField);
		setField(field);
	}

	public void setField(Object field) {
		FieldInitialization.trace.push("Parent.setField: "+field+"/"+this.parentField);
		this.parentField = field;
	}
}

class ChildC extends ParentC {
	Object childField;

	public ChildC(Object field) {
		super(field);
		FieldInitialization.trace.push("Child: "+field+"/"+this.childField);
	}

	@Override
	public void setField(Object field) {
		FieldInitialization.trace.push("Child.setField: "+field+"/"+this.childField);
		super.setField(field);
		childField = field;
		FieldInitialization.trace.push("Child.setField2: "+field+"/"+this.childField);
	}
}
