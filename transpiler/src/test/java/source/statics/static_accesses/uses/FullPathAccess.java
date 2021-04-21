package source.statics.static_accesses.uses;

public class FullPathAccess {

	public void m() {
		System.out.println("> "+source.statics.static_accesses.definitions.TestEnumStaticAccess.A);
		source.statics.static_accesses.definitions.TestClassStaticAccess.m();
	}	
	
}
