/* Copyright 2015 CINCHEO SAS
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
package org.jsweet.transpiler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * A set of utilities to launch external processes from Java.
 * 
 * @author Renaud Pawlak
 */
public class ProcessUtil {
	private final static Logger logger = Logger.getLogger(ProcessUtil.class);

	/**
	 * A static field that stores the user home directory.
	 */
	public static File USER_HOME_DIR = new File(System.getProperty("user.home"));

	/**
	 * Runs the given command.
	 * 
	 * @param command
	 *            the command name
	 * @param stdoutConsumer
	 *            consumes the standard output stream as lines of characters
	 * @param errorHandler
	 *            upcalled when the command does not terminate successfully
	 * @param args
	 *            the command-line arguments
	 * @return the process that was created to execute the command (exited at
	 *         this point)
	 */
	public static Process runCommand(String command, Consumer<String> stdoutConsumer, Runnable errorHandler, String... args) {
		return runCommand(command, null, false, stdoutConsumer, null, errorHandler, args);
	}

	/**
	 * Runs the given command in an asynchronous manner.
	 * 
	 * @param command
	 *            the command name
	 * @param stdoutConsumer
	 *            consumes the standard output stream as lines of characters
	 * @param endConsumer
	 *            called when the process actually ends
	 * @param errorHandler
	 *            upcalled when the command does not terminate successfully
	 * @param args
	 *            the command-line arguments
	 * @return the process that was created to execute the command (can be still
	 *         running at this point)
	 */
	public static Process runAsyncCommand(String command, Consumer<String> stdoutConsumer, Consumer<Process> endConsumer, Runnable errorHandler,
			String... args) {
		return runCommand(command, null, true, stdoutConsumer, endConsumer, errorHandler, args);
	}

	/**
	 * Runs the given command.
	 * 
	 * @param command
	 *            the command name
	 * @param directory
	 *            the working directory of the created process
	 * @param async
	 *            tells if the command should be run asynchronously (in a
	 *            separate thread)
	 * @param stdoutConsumer
	 *            consumes the standard output stream as lines of characters
	 * @param endConsumer
	 *            called when the process actually ends
	 * @param errorHandler
	 *            upcalled when the command does not terminate successfully
	 * @param args
	 *            the command-line arguments
	 * @return the process that was created to execute the command (can be still
	 *         running at this point if <code>async</code> is <code>true</code>)
	 */
	public static Process runCommand(String command, File directory, boolean async, Consumer<String> stdoutConsumer, Consumer<Process> endConsumer,
			Runnable errorHandler, String... args) {

		String[] cmd;
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = new String[] { "cmd", "/c" };
		} else {
			cmd = new String[0];
		}
		cmd = ArrayUtils.addAll(cmd, command);
		cmd = ArrayUtils.addAll(cmd, args);

		logger.debug("run command: " + StringUtils.join(cmd, " "));
		Process[] process = { null };
		try {

			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			processBuilder.redirectErrorStream(true);
			if (directory != null) {
				processBuilder.directory(directory);
			}

			process[0] = processBuilder.start();

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					try {
						try (BufferedReader in = new BufferedReader(new InputStreamReader(process[0].getInputStream()))) {
							String line;
							while ((line = in.readLine()) != null) {
								if (stdoutConsumer != null) {
									stdoutConsumer.accept(line);
								} else {
									logger.info(command + " - " + line);
								}
							}
						}

						process[0].waitFor();
						if (endConsumer != null) {
							endConsumer.accept(process[0]);
						}
						if (process[0].exitValue() != 0) {
							if (errorHandler != null) {
								errorHandler.run();
							}
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (errorHandler != null) {
							errorHandler.run();
						}
					}
				}
			};
			if (async) {
				new Thread(runnable).start();
			} else {
				runnable.run();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (errorHandler != null) {
				errorHandler.run();
			}
		}
		return process[0];
	}

	/**
	 * Installs a <code>node<code> package with <code>npm</code> (assumes that
	 * <code>node</code> is installed).
	 * 
	 * @param nodePackageName
	 *            the package name
	 * @param global
	 *            <code>true</code> for adding the <code>-g</code> option
	 */
	public static void installNodePackage(String nodePackageName, boolean global) {
		logger.debug("installing " + nodePackageName + " with npm");
		if (global) {
			runCommand("npm", USER_HOME_DIR, false, null, null, null, "install", nodePackageName, "-g");
		} else {
			runCommand("npm", USER_HOME_DIR, false, null, null, null, "install", nodePackageName);
		}
	}

	/**
	 * Uninstalls a <code>node<code> package with <code>npm</code> (assumes that
	 * <code>node</code> is installed).
	 * 
	 * @param nodePackageName
	 *            the package name
	 * @param global
	 *            <code>true</code> for adding the <code>-g</code> option
	 */
	public static void uninstallNodePackage(String nodePackageName, boolean global) {
		logger.debug("uninstalling " + nodePackageName + " with npm");
		if (global) {
			runCommand("npm", USER_HOME_DIR, false, null, null, null, "uninstall", nodePackageName, "-g");
		} else {
			runCommand("npm", USER_HOME_DIR, false, null, null, null, "uninstall", nodePackageName);
		}
	}

}
