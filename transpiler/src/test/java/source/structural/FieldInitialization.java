package source.structural;

public class FieldInitialization {

	public static void main(String[] args) {
		ChildC c = new ChildC("test");
		assert c.childField != null;
	}

}

class ParentC {
	Object parentField;

	public ParentC(Object field) {
		setField(field);
	}

	public void setField(Object field) {
		this.parentField = field;
	}
}

class ChildC extends ParentC {
	Object childField;

	public ChildC(Object field) {
		super(field);
	}

	@Override
	public void setField(Object field) {
		super.setField(field);
		childField = field;
	}
}
