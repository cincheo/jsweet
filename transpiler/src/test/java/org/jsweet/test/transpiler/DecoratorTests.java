/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.test.transpiler;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.junit.Assert;
import org.junit.Test;

import def.decorator.MyDecorator;
import source.decorator.Decorated;
import source.decorator.DecoratedWithDef;
import source.decorator.SimpleDecorator;
import source.decorator.other.Globals;
import source.decorator.other.MyOtherDecorator;

/**
 * Tests for decorators in JSweet.
 * 
 * @author Renaud Pawlak
 */
public class DecoratorTests extends AbstractTest {

	@Test
	public void testSimpleDecorator() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertNoProblems();
			// TODO: with node.js, the set handler is not invoked... (but it
			// works with Chrome)
			Assert.assertTrue(r.get("trace").equals(//
					"Set: name => null,Set: name => remo,Get: name => remo,"
							+ "Call: saySomething(\"I love playing\",\"halo\") => \"remo jansen says: I love playing halo\","
							+ "Set: name => Remo,Get: name => Remo")
					// case when the platform does not invoke the set handler
					|| r.get("trace").equals(//
							"Get: name => remo,"
									+ "Call: saySomething(\"I love playing\",\"halo\") => \"remo jansen says: I love playing halo\","
									+ "Get: name => Remo"));
		}, getSourceFile(SimpleDecorator.class));
	}

	@Test
	public void testDecorated() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(Decorated.class), getSourceFile(MyOtherDecorator.class), getSourceFile(Globals.class));
	}

	@Test
	public void testWrongDecorated() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.CANNOT_FIND_GLOBAL_DECORATOR_FUNCTION);
		}, getSourceFile(Decorated.class), getSourceFile(MyOtherDecorator.class));
	}

	@Test
	public void testDecoratedWithDef() {
		transpile(logHandler -> {
			logHandler.assertNoProblems();
		}, getSourceFile(DecoratedWithDef.class), getSourceFile(MyDecorator.class));
	}
	
}
