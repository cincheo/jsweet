package org.jsweet.publisher.visitor;

import java.io.File;

import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;
import org.jsweet.publisher.Main;

public class MvnDeploy implements FileVisitor {

    int count = 0;
    int errorCount = 0;

    @Override
    public void onBegin() {
	System.out.println("=== Deploy === ");
    }

    @Override
    public boolean visit(File file) {
	try {
	    // System.out.println("visiting: " + file);
	    if (file.isFile() && file.getName().equals("pom.xml")) {

		ProcessUtil.runCommand("rm", file.getParentFile(), false, (out) -> {
		    System.out.println(out);
		}, (p1) -> {
		}, () -> {
		    System.out.println("ERROR: cannot clean resources");
		}, "-rf", "src/main/resources/META-INF/resources");

		ProcessUtil.runCommand(Main.MAVEN_CMD, file.getParentFile(), false, (out) -> {
		    System.out.println(out);
		}, (p1) -> {
		    count++;
			ProcessUtil.runCommand(Main.MAVEN_CMD, file.getParentFile(), false, (out) -> {
				System.out.println(out);
			}, (p2) -> {
			}, () -> {
				System.out.println("ERROR: cannot generate javadoc");
			}, "javadoc:javadoc");
		}, () -> {
		    errorCount++;
		    System.out.println("ERROR: cannot deploy");
		}, "clean", "deploy");

	    }
	    return true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean onEnd() {
	System.out.println("=== Deployed " + count + " project(s), " + errorCount + " error(s) ===");
	System.out.println();
	return true;
    }

}
