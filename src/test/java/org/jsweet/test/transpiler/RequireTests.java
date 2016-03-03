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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsweet.transpiler.ModuleKind;
import org.junit.Test;

import source.blocksgame.Ball;
import source.blocksgame.BlockElement;
import source.blocksgame.Factory;
import source.blocksgame.GameArea;
import source.blocksgame.GameManager;
import source.blocksgame.Globals;
import source.blocksgame.Player;
import source.blocksgame.util.AnimatedElement;
import source.blocksgame.util.Collisions;
import source.blocksgame.util.Direction;
import source.blocksgame.util.Line;
import source.blocksgame.util.MobileElement;
import source.blocksgame.util.Point;
import source.blocksgame.util.Rectangle;
import source.blocksgame.util.Vector;
import source.require.TopLevel1;
import source.require.TopLevel2;
import source.require.a.A;
import source.require.a.Use1;
import source.require.a.Use2;
import source.require.a.b.B1;
import source.require.a.b.B2;
import source.require.b.ClassImport;
import source.require.b.ClassImportImplicitRequire;
import source.require.b.GlobalsImport;

public class RequireTests extends AbstractTest {

	@Test
	public void testClassImportImplicitRequire() {
		transpile(ModuleKind.none, (logHandler) -> {
			assertTrue(logHandler.getReportedProblems().size() > 0);
		} , getSourceFile(A.class), getSourceFile(B1.class), getSourceFile(B2.class), getSourceFile(ClassImportImplicitRequire.class));

		// we cannot evaluate this test without the express module
		transpile(ModuleKind.commonjs, (logHandler) -> {
			assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
		} , getSourceFile(A.class), getSourceFile(B1.class), getSourceFile(B2.class), getSourceFile(ClassImportImplicitRequire.class));

	}

	@Test
	public void testClassImport() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(A.class), getSourceFile(ClassImport.class));
	}

	@Test
	public void testBlocksgame() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(Point.class), getSourceFile(Vector.class), getSourceFile(AnimatedElement.class), getSourceFile(Line.class),
				getSourceFile(MobileElement.class), getSourceFile(Rectangle.class), getSourceFile(Direction.class), getSourceFile(Collisions.class),
				getSourceFile(Ball.class), getSourceFile(Globals.class), getSourceFile(BlockElement.class), getSourceFile(Factory.class),
				getSourceFile(GameArea.class), getSourceFile(GameManager.class), getSourceFile(Player.class));
	}

	@Test
	public void testGlobalsImport() {
		transpile(logHandler -> {
			logHandler.assertReportedProblems();
		} , getSourceFile(source.require.globals.Globals.class), getSourceFile(GlobalsImport.class));
	}

	@Test
	public void testImportsFromTopTevels() {
		eval((logHandler, result) -> {
			logHandler.assertReportedProblems();
			assertTrue(result.get("mInvoked"));
		} , getSourceFile(A.class), getSourceFile(TopLevel1.class), getSourceFile(TopLevel2.class));
	}

	@Test
	public void testImportsFromNonTopTevels() {
		eval((logHandler, result) -> {
			logHandler.assertReportedProblems();
			assertTrue(result.get("mInvokedOnB1"));
		} , getSourceFile(B1.class), getSourceFile(Use1.class), getSourceFile(Use2.class));
	}

}
