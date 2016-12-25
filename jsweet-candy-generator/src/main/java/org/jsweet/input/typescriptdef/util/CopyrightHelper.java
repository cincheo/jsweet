package org.jsweet.input.typescriptdef.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class CopyrightHelper {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("usage: CopyrightHelper <copyright-file> <root-java-dir>");
			System.exit(1);
		}
		File processedDir;
		processedDir = new File(args[1]);
		File copyrightFile = new File(args[0]);
		if (!copyrightFile.exists()) {
			System.err.println(copyrightFile + " does not exist");
			System.err.println("current directory: "+ new File("").getAbsolutePath());
			System.exit(1);
		}
		try {
			String copyrightText = FileUtils.readFileToString(copyrightFile);
			insertCopyright(processedDir, copyrightText);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

	private static void insertCopyright(File file, String licenceText) throws Exception {
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				insertCopyright(f, licenceText);
			} else {
				if (f.getName().endsWith(".java")) {
					System.out.println("inserting copyright in " + f);
					String text = FileUtils.readFileToString(f);
					int insertIndex = text.indexOf("package");
					int otherIndex = text.indexOf("/**");
					if (otherIndex != -1) {
						insertIndex = Math.min(insertIndex, otherIndex);
					}
					otherIndex = text.indexOf("@");
					if (otherIndex != -1) {
						insertIndex = Math.min(insertIndex, otherIndex);
					}
					text = text.substring(insertIndex);
					FileUtils.write(f, licenceText + "\n");
					FileUtils.write(f, text, true);
				}
			}
		}
	}

}
