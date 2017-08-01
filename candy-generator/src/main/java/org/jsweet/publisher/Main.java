package org.jsweet.publisher;

import java.io.File;

import org.cincheo.filevisitor.FileVisitor;
import org.jsweet.publisher.visitor.UpdateVersion;

// THIS IS WIP. One day we will have a really automatized publisher for the JSweet Candies Github organization...
public class Main {

	public static String MAVEN_CMD = "/usr/local/bin/mvn";

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("usage: <candiesRootDir> <candiesVersion> <transpilerVersion>");
		}
		File f = new File(args[0]);
		if (f.exists()) {
			// FileVisitor[] fileVisitors = new FileVisitor[] {
			// /*new UpdateFileVisitor(), */new RemoveExtFileVisitor(), new
			// CommitFileVisitor() };
			// FileVisitor[] fileVisitors = new FileVisitor[] { new
			// UpdateVersion("20170726", "rc1") };
			// FileVisitor[] fileVisitors = new FileVisitor[] { new MvnDeploy()
			// };
			FileVisitor.scan(f, new UpdateVersion(args[1], args[2]));
		}
	}

}
