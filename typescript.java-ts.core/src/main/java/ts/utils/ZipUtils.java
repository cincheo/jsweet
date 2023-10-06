/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.tukaani.xz.XZInputStream;

import ts.internal.io.tar.TarEntry;
import ts.internal.io.tar.TarException;
import ts.internal.io.tar.TarInputStream;

/**
 * Zip, tar.gz Utilities.
 *
 */
public class ZipUtils {

	public static final String ZIP_EXTENSION = ".zip";
	public static final String TAR_GZ_EXTENSION = ".tar.gz";
	public static final String TAR_XZ_EXTENSION = ".tar.xz";
	private static final String BIN_FOLDER = "/bin";

	private ZipUtils() {
	}

	/**
	 * Returns true if the given file is a zip file and false otherwise.
	 * 
	 * @param file
	 * @return true if the given file is a zip file and false otherwise.
	 */
	public static boolean isZipFile(File file) {
		return file.isFile() && file.getName().toLowerCase().endsWith(ZIP_EXTENSION);
	}

	/**
	 * Returns true if the given file is a tar.gz file and false otherwise.
	 * 
	 * @param file
	 * @return true if the given file is a tar.gz file and false otherwise.
	 */
	public static boolean isTarGZFile(File file) {
		return file.isFile() && file.getName().toLowerCase().endsWith(TAR_GZ_EXTENSION);
	}

	/**
	 * Returns true if the given file is a tar.xz file and false otherwise.
	 * 
	 * @param file
	 * @return true if the given file is a tar.xz file and false otherwise.
	 */
	public static boolean isTarXZFile(File file) {
		return file.isFile() && file.getName().toLowerCase().endsWith(TAR_XZ_EXTENSION);
	}
	
	/**
	 * Extract zip file to destination folder.
	 *
	 * @param file
	 *            zip file to extract
	 * @param destination
	 *            destination folder
	 */
	public static void extractZip(File file, File destination) throws IOException {
		ZipInputStream in = null;
		OutputStream out = null;
		try {
			// Open the ZIP file
			in = new ZipInputStream(new FileInputStream(file));

			// Get the first entry
			ZipEntry entry = null;

			while ((entry = in.getNextEntry()) != null) {
				String outFilename = entry.getName();

				// Open the output file
				File extracted = new File(destination, outFilename);
				if (entry.isDirectory()) {
					extracted.mkdirs();
				} else {
					// Be sure that parent file exists
					File baseDir = extracted.getParentFile();
					if (!baseDir.exists()) {
						baseDir.mkdirs();
					}

					out = new FileOutputStream(extracted);

					// Transfer bytes from the ZIP file to the output file
					byte[] buf = new byte[1024];
					int len;

					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					// Close the stream
					out.close();
					// Preserve original modification date
					extracted.setLastModified(entry.getTime());
					if (extracted.getParent().contains(BIN_FOLDER)) {
						extracted.setExecutable(true);
					}
				}
			}
		} finally {
			// Close the stream
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Extract tar.gz file to destination folder.
	 *
	 * @param file
	 *            zip file to extract
	 * @param destination
	 *            destination folder
	 */
	public static void extractTarGZ(File file, File destination) throws IOException {
		extractTar(file, destination, true);
	}

	/**
	 * Extract tar.xz file to destination folder.
	 *
	 * @param file
	 *            zip file to extract
	 * @param destination
	 *            destination folder
	 */
	public static void extractTarXZ(File file, File destination) throws IOException {
		extractTar(file, destination, false);
	}
	
	/**
	 * Extract tar.gz/tar.xz file to destination folder.
	 *
	 * @param file
	 *            zip file to extract
	 * @param destination
	 *            destination folder
	 */
	private static void extractTar(File file, File destination, boolean tarGz) throws IOException {
		TarInputStream in = null;
		OutputStream out = null;
		try {
			// Open the ZIP file
			in = new TarInputStream(tarGz ? new GZIPInputStream(new FileInputStream(file))
					: new XZInputStream(new FileInputStream(file)));

			// Get the first entry
			TarEntry entry = null;

			while ((entry = in.getNextEntry()) != null) {
				String outFilename = entry.getName();

				switch (entry.getFileType()) {
				case TarEntry.DIRECTORY:
					File extractedDir = new File(destination, outFilename);
					if (extractedDir.isDirectory()) {
						extractedDir.mkdirs();
					}
					break;
				case TarEntry.FILE:
					File extractedFile = new File(destination, outFilename);
					// Be sure that parent file exists
					File baseDir = extractedFile.getParentFile();
					if (!baseDir.exists()) {
						baseDir.mkdirs();
					}

					out = new FileOutputStream(extractedFile);

					// Transfer bytes from the ZIP file to the output file
					byte[] buf = new byte[1024];
					int len;

					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					// Close the stream
					out.close();
					// Preserve original modification date
					extractedFile.setLastModified(entry.getTime());
					long mode = entry.getMode();
					if ((mode & 00100) > 0) {
						// Preserve execute permissions
						extractedFile.setExecutable(true, (mode & 00001) == 0);
					}
					break;
				case TarEntry.LINK:
					File linkFile = new File(destination, outFilename);
					// Be sure that parent file exists
					File linkBaseDir = linkFile.getParentFile();
					if (!linkBaseDir.exists()) {
						linkBaseDir.mkdirs();
					}
					Path target = Paths.get(entry.getLinkName());
					Files.createLink(linkFile.toPath(), target);
					break;
				case TarEntry.SYM_LINK:
					File symLinkFile = new File(destination, outFilename);
					// Be sure that parent file exists
					File symLinkBaseDir = symLinkFile.getParentFile();
					if (!symLinkBaseDir.exists()) {
						symLinkBaseDir.mkdirs();
					}
					Path symTarget = Paths.get(entry.getLinkName());
					Files.createSymbolicLink(symLinkFile.toPath(), symTarget);
					break;
				}
			}
		} catch (TarException e) {
			throw new IOException(e);
		} finally {
			// Close the stream
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}

	}

}
