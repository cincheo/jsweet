open module org.jsweet {
	requires transitive jdk.compiler;
	requires transitive java.compiler;
	requires transitive jdk.unsupported;
	requires transitive log4j;
	requires transitive typescript.java.core;
	requires transitive org.apache.commons.lang3;
	requires org.apache.commons.text;
	requires transitive commons.io;
	requires transitive com.google.debugging.sourcemap;
	requires transitive jsap;
	requires static java.desktop;
	exports org.jsweet;
	exports org.jsweet.transpiler;
	exports org.jsweet.transpiler.candy;
	exports org.jsweet.transpiler.eval;
	exports org.jsweet.transpiler.extension;
	exports org.jsweet.transpiler.model;
	exports org.jsweet.transpiler.model.support;
	exports org.jsweet.transpiler.util;
}