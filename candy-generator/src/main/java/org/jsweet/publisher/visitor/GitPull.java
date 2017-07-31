package org.jsweet.publisher.visitor;

import java.io.File;

import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;

public class GitPull implements FileVisitor {

    int count = 0;
    int errorCount = 0;

    @Override
    public void onBegin() {
	System.out.println("=== Updating projects === ");
    }

    @Override
    public boolean visit(File file) {
	try {
	    // System.out.println("visiting: " + file);
	    if (file.isDirectory() && new File(file, ".git").exists()) {
		System.out.println();
		System.out.println(file + ": cleaning git project...");
		ProcessUtil.runCommand("git", file, false, (out) -> {
		    System.out.println(out);
		}, (p) -> {
		    count++;
		}, () -> {
		    System.out.println("ERROR: cannot clean project!");
		    errorCount++;
		}, "clean", "-f", "-d");
		System.out.println(file + ": updating git project...");
		ProcessUtil.runCommand("git", file, false, (out) -> {
		    System.out.println(out);
		}, (p) -> {
		    count++;
		}, () -> {
		    System.out.println("ERROR: cannot pull project!");
		    errorCount++;
		}, "pull", "origin", "master");
	    }
	    return true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean onEnd() {
	System.out.println("=== " + count + " git project(s) updated successfully, " + errorCount + " error(s) ===");
	System.out.println();
	return errorCount == 0;
    }

}
