package source.genericinterfaceperf;

public class ImplementationA2 implements InterfaceA<ImplementationB2>{

	public void methodA(ImplementationB2 implementationB) {
		implementationB.method();
	}

}
