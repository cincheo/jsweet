/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.jsweet.transpiler.util.Position;
import org.jsweet.transpiler.util.SourceMap;

/**
 * A source file represents a Java source file and holds information on the
 * transpiled output files (Typescript and Javascript files).
 * 
 * @author Renaud Pawlak
 */
public class SourceFile {

	private static void addFiles(String extension, File file, LinkedList<File> files) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(extension, f, files);
			}
		} else if (file.getName().endsWith(extension)) {
			files.add(file);
		} else {
			System.out.println("ignoring unrecognized file: " + file);
		}
	}

	/**
	 * Gets all the Java source files found in the given dirs and their subdirs.
	 */
	public static SourceFile[] getSourceFiles(File... dirs) {
		return getSourceFiles(asList(dirs));
	}

	/**
	 * Gets all the Java source files found in the given dirs and their subdirs.
	 */
	public static SourceFile[] getSourceFiles(Iterable<File> dirs) {
		LinkedList<File> files = new LinkedList<>();
		for (File dir : dirs) {
			addFiles(".java", dir, files);
		}
		return toSourceFiles(files);
	}

	/**
	 * Converts files to source files.
	 */
	public static SourceFile[] toSourceFiles(File[] javaFiles) {
		return toSourceFiles(asList(javaFiles));
	}

	/**
	 * Converts files to source files.
	 */
	public static SourceFile[] toSourceFiles(Collection<File> javaFiles) {
		SourceFile[] dest = new SourceFile[javaFiles.size()];
		int i = 0;
		for (File javaFile : javaFiles) {
			dest[i++] = new SourceFile(javaFile);
		}
		return dest;
	}

	/**
	 * Converts file paths to source files.
	 */
	public static SourceFile[] toSourceFiles(String... javaFilePaths) {
		SourceFile[] dest = new SourceFile[javaFilePaths.length];
		int i = 0;
		for (String javaFilePath : javaFilePaths) {
			dest[i++] = new SourceFile(new File(javaFilePath));
		}
		return dest;
	}

	/**
	 * Converts source files to files.
	 */
	public static File[] toFiles(SourceFile... sourceFiles) {
		File[] dest = new File[sourceFiles.length];
		for (int i = 0; i < sourceFiles.length; i++) {
			dest[i] = sourceFiles[i].javaFile;
		}
		return dest;
	}

	/**
	 * Touch the given source files.
	 * 
	 * @see #touch()
	 */
	public static void touch(SourceFile... sourceFiles) {
		for (int i = 0; i < sourceFiles.length; i++) {
			sourceFiles[i].touch();
		}
	}

	/**
	 * Converts source files to file paths.
	 */
	public static String[] toPaths(SourceFile[] sourceFiles) {
		String[] dest = new String[sourceFiles.length];
		for (int i = 0; i < sourceFiles.length; i++) {
			dest[i] = sourceFiles[i].javaFile.getPath();
		}
		return dest;
	}

	public static SourcePosition findOriginPosition(SourcePosition position, SourceFile[] sourceFiles) {
		return findOriginPosition(position, Arrays.asList(sourceFiles));
	}
	
	/**
	 * Finds the mapped position in one of the origin Java source file.
	 * 
	 * @param position
	 *            the position in one of the generated TypeScript file
	 * @param sourceFiles
	 *            the origin source files
	 * @return the origin position
	 */
	public static SourcePosition findOriginPosition(SourcePosition position, Collection<SourceFile> sourceFiles) {
		for (SourceFile sourceFile : sourceFiles) {
			if (sourceFile.tsFile != null && sourceFile.tsFile.getAbsolutePath().endsWith(position.getFile().getPath())) {
				if (sourceFile.getSourceMap() != null) {
					Position inputPosition = sourceFile.getSourceMap().findInputPosition(position.getStartLine(), position.getStartColumn());
					if (inputPosition != null) {
						return new SourcePosition(sourceFile.getJavaFile(), null, inputPosition);
					}
				}
			}
		}
		return null;
	}

	private File javaFile;

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	long javaFileLastTranspiled = 0;

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	File tsFile;

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	File jsFile;

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	File jsMapFile;

	/**
	 * The Java source directory.
	 */
	File javaSourceDir;

	/**
	 * The Java file relatively to the source directory.
	 */
	File javaSourceDirRelativeFile;

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	long jsFileLastTranspiled = 0;

	private SourceMap sourceMap;

	/**
	 * Creates a source file from a file.
	 */
	public SourceFile(File javaFile) {
		this.javaFile = javaFile;
	}

	@Override
	public String toString() {
		return javaFile.toString();
	}

	/**
	 * Gets the Java file.
	 */
	public File getJavaFile() {
		return javaFile;
	}

	/**
	 * Sets the Java file.
	 */
	public void setJavaFile(File javaFile) {
		this.javaFile = javaFile;
	}

	/**
	 * Gets the Typescript file (null until transpiled by
	 * {@link JSweetTranspiler}).
	 */
	public File getTsFile() {
		return tsFile;
	}

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	protected void setTsFile(File tsFile) {
		this.tsFile = tsFile;
	}

	/**
	 * Gets the Javascript file (null until transpiled by
	 * {@link JSweetTranspiler}).
	 */
	public File getJsFile() {
		return jsFile;
	}

	/**
	 * Internally used by {@link JSweetTranspiler}.
	 */
	protected void setJsFile(File jsFile) {
		this.jsFile = jsFile;
	}

	/**
	 * Gets the Javascript map file (null if not generated).
	 */
	public File getJsMapFile() {
		return jsMapFile;
	}

	/**
	 * Gets the timestamp of the last Java file transpilation.
	 */
	public long getJavaFileLastTranspiled() {
		return javaFileLastTranspiled;
	}

	/**
	 * Gets the timestamp of the last generation of the Javascript file.
	 */
	public long getJsFileLastTranspiled() {
		return jsFileLastTranspiled;
	}

	@Override
	public int hashCode() {
		if (javaFile != null) {
			return javaFile.hashCode();
		} else {
			return super.hashCode();
		}
	}

	/**
	 * Clears the transpilation information as if the file was not transpiled.
	 */
	public void touch() {
		tsFile = null;
		jsFile = null;
		jsMapFile = null;
		javaFileLastTranspiled = 0;
		jsFileLastTranspiled = 0;
	}

	public SourceMap getSourceMap() {
		return sourceMap;
	}

	public void setSourceMap(SourceMap sourceMap) {
		this.sourceMap = sourceMap;
	}

}
