package source.structural.defaultMethods;

public class IntermediateAbstractViewController extends AbstractViewController implements I2, I3 {

	@Override
	public void m() {
		System.out.println("m");
	}

	@Override
	public void o1(String s) {
		System.out.println("o1-overriden");
	}

}