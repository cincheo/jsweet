package source.genericinterfaceperf;
import jsweet.lang.Interface;

@Interface
public interface InterfaceA<T extends InterfaceB> {
	
	public void methodA(T implementationB);

}
