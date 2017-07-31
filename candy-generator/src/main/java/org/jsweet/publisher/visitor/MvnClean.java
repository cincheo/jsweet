package org.jsweet.publisher.visitor;

import java.io.File;

import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;
import org.jsweet.publisher.Main;

public class MvnClean implements FileVisitor {

    int count = 0;

    @Override
    public void onBegin() {
	System.out.println("=== Clean === ");
    }

    @Override
    public boolean visit(File file) {
	try {
	    // System.out.println("visiting: " + file);
	    if (file.isFile() && file.getName().equals("pom.xml")) {
		
		ProcessUtil.runCommand(Main.MAVEN_CMD, file.getParentFile(), false, (out) -> {
		    System.out.println(out);
		}, (p1) -> {
		}, () -> {
		    System.out.println("ERROR: cannot clean Maven directory");
		}, "clean");
		
		count++;

	    }
	    return true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean onEnd() {
	System.out.println("=== Cleaned " + count + " project(s) ===");
	System.out.println();
	return true;
    }

}
