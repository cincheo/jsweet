package source.genericinterfaceperf;

public class ImplementationA1 implements InterfaceA<ImplementationB1>{

	public void methodA(ImplementationB1 implementationB) {
		implementationB.method();
	}

}
