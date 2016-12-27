package org.jsweet.input.typescriptdef.test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.util.DirectedGraph;
import org.jsweet.util.Tree;
import org.jsweet.util.Tree.Matcher;
import org.jsweet.input.typescriptdef.util.Util;
import org.junit.Test;

public class PackagingTest {

	
	// angular2 => 3 modules
	// angularjs => 7 modules
	// atom => 2 modules
	// bingmaps => 7 modules
	// angular2 => modules
	// business-rules-engine => 4 modules
	// chrome => 3 modules
	// cordova => 2 + plugins
	// cordova-ionic => ?
	// codemirror => 3 package merge
	// d3 => plugins
	// datejs => modules
	// dojo => 64 modules
	// durandal => legacy!!
	// estree => 2 module merge
	// firebase => merge
	// freedom => globals merge
	// gihub-electron => 4 module merge
	// gsap => 3 toplevel merge
	// highcharts => 2 tolevel merge
	// iscroll => legacy + 2 toplevel merge
	@Test
	public void testTypings() {
		File[] groupFiles = new File(JSweetDefTranslatorConfig.TS_LIBS_DIR_NAME).listFiles();
		Map<Integer, Integer> counts = new HashMap<>();
		List<String> projects = new ArrayList<String>();
		List<String> notContainingProjects = new ArrayList<String>();
		for (File group : groupFiles) {
			String[] files = group.list();
			boolean contains = false;
			if (files != null) {
				for (String s : files) {
					if (s.equals(group.getName() + ".d.ts")) {
						contains = true;
					}
				}
			}
			if (!contains) {
				notContainingProjects.add(group.getName());
			}
			int n = files == null ? 0 : files.length;
			if (n > 1) {
				projects.add(group.getName() + " (" + n + ")");
			}
			Integer currentCount = counts.get(n);
			if (currentCount == null) {
				currentCount = 1;
			} else {
				currentCount++;
			}
			counts.put(n, currentCount);
		}
		System.out.println("=> " + counts);
		System.out.println("=> " + ((((double) projects.size()) / groupFiles.length) * 100) + "%");
		System.out.println("=> " + projects);
		System.out.println("=> " + ((((double) notContainingProjects.size()) / groupFiles.length) * 100) + "%" + " - "
				+ notContainingProjects);
	}

	@Test
	public void testDirectedGraph() {
		DirectedGraph<Integer> g = new DirectedGraph<Integer>();
		g.add(7, 5, 3, 11, 8, 2, 9, 10);
		g.buildEdges(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 == 5 && o2 == 3) {
					return 1;
				}
				if (o1 == 9 && o2 == 11) {
					return 1;
				}
				return 0;
			}
		});
		assertEquals(asList(5), g.getDestinationElements(3));
		List<Integer> sorted = g.topologicalSort(null);
		assertTrue(sorted.indexOf(5) > sorted.indexOf(3));
		assertTrue(sorted.indexOf(9) > sorted.indexOf(11));
		assertEquals(asList(5), g.getDestinationElements(3));
	}

	@Test
	public void testTree() {
		Tree<String, String> root = new Tree<String, String>(null);
		Tree<String, String> t = root.addChildElement("a");
		assertEquals(root, t.getParentTree());
		assertEquals(asList("a"), root.getChildElements());
		root.addPath(asList("a", "a"));
		root.addPath(asList("a", "b"));
		root.addPath(asList("a", "c"));
		assertEquals(asList("a"), root.getChildElements());
		assertEquals(asList("a", "b", "c"), t.getChildElements());
		final StringBuffer output = new StringBuffer();
		new Tree.Scanner<String, String>() {
			protected boolean enter(Tree<String, String> tree) {
				output.append(tree.getElement());
				if ("c".equals(tree.getElement())) {
					assertEquals(stack.size(), 2);
				}
				return true;
			}
		}.scan(root);
		assertEquals("nullaabc", output.toString());
	}

	@Test
	public void testMatcher() {
		Tree<String, String> root = new Tree<String, String>("root");
		Tree<String, String> t = root.addChildElement("a");
		assertEquals(root, t.getParentTree());
		assertEquals(asList("a"), root.getChildElements());
		root.addPath(asList("a", "a"));
		root.addPath(asList("a", "b"));
		root.addPath(asList("a", "c"));

		assertEquals(asList("a"), root.getChildElements());
		assertEquals(asList("a", "b", "c"), t.getChildElements());
		assertTrue(new Matcher("root", "a").matches(root));
		assertFalse(new Matcher("root", "c").matches(root));
		assertFalse(new Matcher("!root").matches(root));
		assertTrue(new Matcher("root", "!b").matches(root));
		assertTrue(new Matcher("root", "a", "a").matches(root));
		assertTrue(new Matcher("root", "a", "!d").matches(root));
		assertFalse(new Matcher("root", "a", "d").matches(root));

		List<String> path = asList("root", "a", "b");

		assertTrue(new Matcher("root", "a").matches(path));
		assertFalse(new Matcher("root", "c").matches(path));
		assertFalse(new Matcher("!root").matches(path));
		assertTrue(new Matcher("root", "!b").matches(path));
		assertFalse(new Matcher("root", "a", "a").matches(path));
		assertTrue(new Matcher("root", "a", "!d").matches(path));
		assertFalse(new Matcher("root", "a", "d").matches(path));

	}

	@Test
	public void testPackageNameForTypings() {
		assertEquals("def.angular2",
				Util.getLibPackageNameFromTsDefFile(new File("typings/angular2/http.d.ts")));
		assertEquals("def.any_db", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/any-db/any-db.d.ts")));
		assertEquals("def.any_db_transaction", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/any-db-transaction/any-db-transaction.d.ts")));
		assertEquals("def.js_cookie", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/js-cookie/js-cookie.d.ts")));
		assertEquals("def.js_data", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/js-data/js-data.d.ts")));
		assertEquals("def.js_data_angular", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/js-data-angular/js-data-angular.d.ts")));
		assertEquals("def.jquery_cookie", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/jquery.cookie/jquery.cookie.d.ts")));
		assertEquals("def.angularjs", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/angularjs/angular.d.ts")));
		assertEquals("def.angularjs", Util.getLibPackageNameFromTsDefFile(new File(
				"typings/angularjs/angular-route.d.ts")));

	}

}
