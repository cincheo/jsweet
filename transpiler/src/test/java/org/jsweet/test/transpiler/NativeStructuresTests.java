package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;

import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.extensions.RemoveJavaDependenciesFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import source.nativestructures.Collections;
import source.nativestructures.Exceptions;
import source.nativestructures.Maps;
import source.nativestructures.NativeArrays;
import source.nativestructures.NativeStringBuilder;

public class NativeStructuresTests extends AbstractTest {

	@BeforeClass
	public static void start() {
		createTranspiler(new RemoveJavaDependenciesFactory<>());
	}

	@AfterClass
	public static void end() {
		createTranspiler(new JSweetFactory<>());
	}

	@Test
	public void testCollections() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals(
					"1,a,1,b,3,4,d,a,d,0,0,0,a,a,2,a,true,false,3,c,c,a,b,c,a,b,c,b,1,c,b,a,b,c,a,0,true,true,it,true,1",
					result.<String> get("trace"));
		}, getSourceFile(Collections.class));
	}

	
	@Test
	public void testStringBuilder() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals(
					"a,abc,a,abc",
					result.<String> get("trace"));
		}, getSourceFile(NativeStringBuilder.class));
	}

	@Test
	public void testExceptions() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("test,test,finally,test2,test3", result.<String> get("trace"));
		}, getSourceFile(Exceptions.class));
	}

	@Test
	public void testMaps() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("2,a,true,[1,2],[a,b],1,true,1,2", result.<String> get("trace"));
		}, getSourceFile(Maps.class));
	}

	@Test
	public void testNativeArrays() {
		eval((logHandler, result) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			assertEquals("3,a,b,c,true,false,false,false,true", result.<String> get("trace"));
		}, getSourceFile(NativeArrays.class));
	}

}