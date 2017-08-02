package org.jsweet.publisher.visitor;

import java.io.File;

import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;

public class GitCommitPush implements FileVisitor {

	int count = 0;
	int errorCount = 0;

	private String commitMessage;

	public GitCommitPush(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	@Override
	public void onBegin() {
		System.out.println("=== Committing projects === ");
	}

	@Override
	public boolean visit(File file) {
		try {
			// System.out.println("visiting: " + file);
			if (file.isDirectory() && new File(file, ".git").exists()) {
				System.out.println();
				System.out.println(file + ": committing git project...");
				ProcessUtil.runCommand("git", file, false, (out) -> {
					System.out.println(out);
				}, (p1) -> {
					count++;
					System.out.println(file + ": pushing git project...");
					ProcessUtil.runCommand("git", file, false, (out) -> {
						System.out.println(out);
					}, (p2) -> {
					}, () -> {
						System.out.println("ERROR: cannot push project!");
						errorCount++;
					}, "push", "origin", "master");
				}, () -> {
					System.out.println("ERROR: cannot commit project!");
					errorCount++;
				}, "commit", "-m", commitMessage);
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
