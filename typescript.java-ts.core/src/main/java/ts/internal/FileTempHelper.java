package ts.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ts.TypeScriptException;

public class FileTempHelper {

	private final static Stack<File> availableTempFileList = new Stack<File>();
	private final static Map<Integer, File> seq_to_tempfile_name = new HashMap<Integer, File>();

	public static String updateTempFile(String newText, int seq) throws TypeScriptException {
		Writer writer = null;
		try {
			File tempFile = getTempFile(seq);
			writer = new FileWriter(tempFile);
			writer.write(newText);
			writer.flush();
			return tempFile.getCanonicalPath();
		} catch (Exception e) {
			throw new TypeScriptException(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				throw new TypeScriptException(e);
			}
		}
	}

	/**
	 * Get the first unused temp file name to avoid conflicts.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static File getTempFile(int seq) throws IOException {
		File tempFile = null;
		synchronized (availableTempFileList) {
			if (!availableTempFileList.isEmpty()) {
				tempFile = availableTempFileList.pop();
			} else {
				tempFile = File.createTempFile("tmptsjava." + SequenceHelper.getTempSeq(), null);
				tempFile.deleteOnExit();
			}
			seq_to_tempfile_name.put(seq, tempFile);
		}
		return tempFile;
	}

	/**
	 * Post process after receiving a reload response
	 * 
	 * @param seq
	 */
	public static void freeTempFile(int seq) {
		synchronized (availableTempFileList) {
			File tempFile = seq_to_tempfile_name.remove(seq);
			if (tempFile != null) {
				availableTempFileList.push(tempFile);
			}
		}
	}

}
