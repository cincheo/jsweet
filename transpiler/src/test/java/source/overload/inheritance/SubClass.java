package source.overload.inheritance;

public class SubClass extends SuperClass {

	@Override

	public boolean valuerModele() {

		super.valuerModele();

		System.out.println("SubClass.valuerModele()  : ");

		return true;

	}

	public static void main(String[] args) {

		new SubClass().valuerModele();

	}
}
