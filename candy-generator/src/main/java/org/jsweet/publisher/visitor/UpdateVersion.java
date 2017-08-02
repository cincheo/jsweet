package org.jsweet.publisher.visitor;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.cincheo.filevisitor.FileVisitor;
import org.cincheo.filevisitor.ProcessUtil;

public class UpdateVersion implements FileVisitor {

	String version;
	String transpilerVersion;
	String transpilerCoreVersion;

	Pattern artifactVersionPattern = Pattern.compile("(<version>\\d*.\\d*.\\d*-)(\\w*)(</version>\\s*<properties>)");
	Pattern versionPattern = Pattern.compile("(<version>\\d*.\\d*.\\d*-)(\\w*)(</version>)");
	Pattern coreVersionPattern = Pattern.compile("(<version>\\d*-)(\\w*)(</version>)");
	Pattern transpilerVersionPattern = Pattern
			.compile("(<artifactId>jsweet-maven-plugin</artifactId>\\s*<version>)(\\d*.\\d*.\\d*-\\w*)(</version>)");
	Pattern transpilerVersionPropertyPattern = Pattern
			.compile("(<jsweet.transpiler.version>)(\\d*.\\d*.\\d*)(</jsweet.transpiler.version>)");

	public UpdateVersion(String version, String transpilerVersion) {
		this.version = version;
		this.transpilerVersion = transpilerVersion;
		this.transpilerCoreVersion = transpilerVersion.split("-")[0];
	}

	int count = 0;

	@Override
	public void onBegin() {
		System.out.println("=== Updating version === ");
	}

	@Override
	public boolean visit(File file) {
		try {
			// System.out.println("visiting: " + file);
			if (file.isFile() && file.getName().equals("pom.xml")) {
				System.out.println("update version: " + file);
				String content = FileUtils.readFileToString(file);
				Matcher m = artifactVersionPattern.matcher(content);
				if (!m.find()) {
					throw new RuntimeException("wrong version");
				}
				m = versionPattern.matcher(content);
				content = m.replaceAll("$1" + version + "$3");
				m = coreVersionPattern.matcher(content);
				content = m.replaceAll("$1" + version + "$3");
				m = transpilerVersionPropertyPattern.matcher(content);
				content = m.replaceAll("$1" + transpilerCoreVersion + "$3");
				m = transpilerVersionPattern.matcher(content);
				content = m.replaceAll("$1" + transpilerVersion + "$3");
				FileUtils.writeStringToFile(file, content);
				count++;

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
		System.out.println("=== Updated " + count + " project versions ===");
		System.out.println();
		return true;
	}

}
