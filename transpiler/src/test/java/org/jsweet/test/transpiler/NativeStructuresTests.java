package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import source.nativestructures.Collections;
import source.nativestructures.Exceptions;
import source.nativestructures.Maps;
import source.nativestructures.NativeArrays;

public class NativeStructuresTests extends AbstractTest {

	@Test
	public void testCollections() {
		transpiler.setUseJavaApis(false);
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("1,a,1,b,3,4,d,a,d,0,0,0,a,a,2,a,true,false,3,c,c,a,b,c,a,b,c,b,1,c,b,a,b,c,a,0,true,true,it",
					result.<String> get("trace"));
		}, getSourceFile(Collections.class));
		transpiler.setUseJavaApis(true);
	}

	@Test
	public void testExceptions() {
		transpiler.setUseJavaApis(false);
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("test,test,finally,test2,test3", result.<String> get("trace"));
		}, getSourceFile(Exceptions.class));
		transpiler.setUseJavaApis(true);
	}

	@Test
	public void testMaps() {
		transpiler.setUseJavaApis(false);
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("2,a", result.<String> get("trace"));
		}, getSourceFile(Maps.class));
		transpiler.setUseJavaApis(true);
	}

	@Test
	public void testNativeArrays() {
		transpiler.setUseJavaApis(false);
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("3,a,b,c,true,false,false,false,true", result.<String> get("trace"));
		}, getSourceFile(NativeArrays.class));
		transpiler.setUseJavaApis(true);
	}

}