package org.jsweet.publisher.visitor;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;

public class PomFixer implements FileVisitor {

	private String targetExpression;
	private String replacementExpression;

	public PomFixer(String targetExpression, String replacementExpression) {
		this.targetExpression = targetExpression;
		this.replacementExpression = replacementExpression;
	}

	int count = 0;

	@Override
	public void onBegin() {
		System.out.println("=== Running pom fixer === ");
		System.out.println("- target: " + targetExpression);
		System.out.println("- replacement: " + replacementExpression);
	}

	@Override
	public boolean visit(File file) {
		try {
			// System.out.println("visiting: " + file);
			if (file.isFile() && file.getName().equals("pom.xml")) {
				String content = FileUtils.readFileToString(file);
				if (Pattern.compile(targetExpression).matcher(content).find()) {
					System.out.println(file + ": updating pom.xml");
					content = content.replaceAll(targetExpression, replacementExpression);
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
		System.out.println("=== Updated " + count + " poms ===");
		System.out.println();
		return true;
	}

}
