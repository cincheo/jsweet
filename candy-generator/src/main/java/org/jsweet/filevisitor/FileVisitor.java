package org.jsweet.filevisitor;

import java.io.File;

/**
 * An interface to be implemented to create actual actions on files being
 * visited.
 */
public interface FileVisitor {

	/**
	 * Applies a list of file visitors to a root file, which will be scanned
	 * recursively, including sub-directories.
	 * 
	 * @param root
	 *            the root file to be scanned
	 * @param fileVisitors
	 *            the file visitors to be applied while scanning
	 */
	public static void scan(File root, FileVisitor... fileVisitors) {
		FileScanner.scan(root, fileVisitors);
	}

	/**
	 * This method is called right before the visiting process begins.
	 */
	public void onBegin();

	/**
	 * This method is called each time a file is visited.
	 * 
	 * @param the
	 *            file being visited
	 * @return should return true in order to continue, or false to end visiting
	 *         (on a directory, returning false will skip all the remaining
	 *         files in the directory)
	 */
	public boolean visit(File file);

	/**
	 * This method is called right after the visiting process ends.
	 */
	public boolean onEnd();
}

class FileScanner {
	static void scan(File root, FileVisitor... fileVisitors) {
		for (FileVisitor fileVisitor : fileVisitors) {
			fileVisitor.onBegin();
			scan(root, fileVisitor);
			if (!fileVisitor.onEnd()) {
				break;
			}
		}
	}

	private static void scan(File f, FileVisitor fileVisitor) {
		if (fileVisitor.visit(f)) {
			if (f.isDirectory()) {
				for (File child : f.listFiles()) {
					if (child.isFile()) {
						scan(child, fileVisitor);
					}
				}
				for (File child : f.listFiles()) {
					if (child.isDirectory()) {
						scan(child, fileVisitor);
					}
				}
			}
		}
	}

}
