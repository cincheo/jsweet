package source.statics.static_accesses.uses;

import source.statics.static_accesses.definitions.TestClassStaticAccess;

public class AccessThroughInstance {
	
	public void m(TestClassStaticAccess instance) {
		TestClassStaticAccess.m();
		instance.m();
	}
}
