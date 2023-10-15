open module typescript.java.core {
	requires transitive com.google.gson;
	requires transitive minimal.json;
	requires transitive org.tukaani.xz;
	exports ts;
	exports ts.client;
	exports ts.client.diagnostics;
	exports ts.client.projectinfo;
	exports ts.cmd.tsc;
	exports ts.utils;
	exports ts.nodejs;
}