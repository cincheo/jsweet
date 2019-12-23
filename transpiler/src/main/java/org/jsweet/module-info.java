open module org.jsweet {
	requires transitive jdk.compiler;
	requires transitive jdk.unsupported;
	requires transitive log4j;
	requires transitive typescript.java.core;
	requires transitive org.apache.commons.lang3;
	requires org.apache.commons.text;
	requires transitive commons.io;
	requires transitive jsap;
	exports org.jsweet;
}