package org.jsweet.publisher.visitor;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;

public class RemoveExt implements FileVisitor {

    int count = 0;

    @Override
    public void onBegin() {
	System.out.println("=== Removing ext in group id === ");
    }

    @Override
    public boolean visit(File file) {
	try {
	    //System.out.println("visiting: " + file);
	    if (file.isFile() && file.getName().equals("pom.xml")) {
		String content = FileUtils.readFileToString(file);
		if (content.contains("<groupId>org.jsweet.candies.ext</groupId>")) {
		    System.out.println(file+": removing ext in group");
		    content = content.replace("<groupId>org.jsweet.candies.ext</groupId>",
			    "<groupId>org.jsweet.candies</groupId>");
		    FileUtils.writeStringToFile(file, content);
		    count++;
		}
		ProcessUtil.runCommand("git", file.getParentFile(), false, (out) -> {
		    System.out.println(out);
		}, (p1) -> {
		}, () -> {
		    System.out.println("ERROR: cannot add file");
		}, "add", file.toString());
		
	    }
	    return true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean onEnd() {
	System.out.println("=== Removed " + count + " ext ===");
	System.out.println();
	return true;
    }

}
