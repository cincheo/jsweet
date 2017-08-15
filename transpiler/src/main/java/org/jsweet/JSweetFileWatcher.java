/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * A watch thread to recompile the project when changed.
 * 
 * @author EPOTH - ponthiaux.e@sfeir.com -/- ponthiaux.eric@gmail.com
 * @author Louis Grignon On the fly transpilation through maven
 * @author Renaud Pawlak adaptation to the command-line launcher
 */
@SuppressWarnings("restriction")
public class JSweetFileWatcher {

	private static final Logger logger = Logger.getLogger(JSweetFileWatcher.class);

	private TranspilationThread transpilationThread;

	private static final SensitivityWatchEventModifier SENSITIVITY_WATCH_EVENT_MODIFIER = SensitivityWatchEventModifier.HIGH;

	private static ReentrantLock __Lock = new ReentrantLock();

	private static LinkedList<String> __RandomKeysTrigger = new LinkedList<>();

	/**
	 * Holds the JSweet transpilation task, which is going to be executed when a
	 * watched file changes.
	 */
	protected TranspilationTask transpilationTask;

	/**
	 * Creates a new watcher with a JSweet transpilation task, which is going to
	 * be executed when a watched file changes.
	 * 
	 * @param transpilationTask
	 *            the task to be executed when a file changes
	 */
	public JSweetFileWatcher(TranspilationTask transpilationTask) {
		this.transpilationTask = transpilationTask;
	}

	/**
	 * Starts this watcher.
	 */
	public void execute() {

		logger.info("starting file watcher... ");

		transpilationThread = new TranspilationThread();
		transpilationThread.start();

		initialize();
	}

	private void initialize() {

		List<File> sourcePaths = transpilationTask.getInputDirList();

		try {

			for (;;) {

				WatchService watchService = FileSystems.getDefault().newWatchService();

				List<Path> watchedPaths = new ArrayList<>();

				logger.info("registering source paths");

				for (File sourceDirectory : sourcePaths) {
					Path path = sourceDirectory.toPath();
					watchedPaths.add(path);
					walkDirectoryTree(path, watchedPaths, watchService);
				}

				logger.info("done registering source paths");

				logger.info("listening for file change... ");

				try {
					watch(watchService);
				} catch (Exception exception) {
					watchService.close();
				}

				Thread.yield();

			}

		} catch (IOException ioException) {

			logger.error(ioException);

		}
	}

	private void walkDirectoryTree(Path startPath, List<Path> watchedPaths, WatchService watchService)
			throws IOException {

		RegisteringFileTreeScanner scanner = new RegisteringFileTreeScanner(watchedPaths, watchService);

		Files.walkFileTree(startPath, scanner);

	}

	private void watch(WatchService watchService) throws Exception {

		for (;;) {

			WatchKey key;

			try {

				key = watchService.take();

			} catch (InterruptedException x) {

				return;

			}

			for (WatchEvent<?> event : key.pollEvents()) {

				WatchEvent.Kind<?> kind = event.kind();

				if (kind == OVERFLOW) {
					continue;
				}

				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;

				Path filename = ev.context();
				if (kind == ENTRY_MODIFY || kind == ENTRY_CREATE || kind == ENTRY_DELETE) {

					logger.info("file change detected" + filename);

					__Lock.lock();

					__RandomKeysTrigger.add(UUID.randomUUID().toString());

					__Lock.unlock();

				}

			}

			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}

		/* */

		try {
			watchService.close();
		} catch (IOException ioException) {
			logger.error(ioException);
		}

		/* */
	}

	private static class RegisteringFileTreeScanner extends SimpleFileVisitor<Path> {

		private List<Path> directories;
		private WatchService watchService;

		public RegisteringFileTreeScanner(List<Path> directories, WatchService watchService) {

			this.directories = directories;
			this.watchService = watchService;

		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

			// --> DO NOTHING

			return CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path directory, IOException exc) {

			directories.add(directory);

			try {

				directory.register(

						this.watchService,

						new WatchEvent.Kind[] { ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE, OVERFLOW },

						SENSITIVITY_WATCH_EVENT_MODIFIER

				);

				logger.info("added [" + directory.toString() + "]");

			} catch (IOException ioException) {

				logger.error("cannot register [" + directory.toString() + "]");

			}

			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {

			// --> DO NOTHING

			return CONTINUE;
		}
	}

	private class TranspilationThread extends Thread {

		public TranspilationThread() {
			setPriority(Thread.MAX_PRIORITY);
		}

		public void run() {

			for (;;) {

				if (__Lock.tryLock()) {

					if (__RandomKeysTrigger.size() != 0) {
						__RandomKeysTrigger.removeLast();
						try {
							transpilationTask.run();
						} catch (Exception exception) {
							logger.info(exception.getMessage());
						}
					}
					__Lock.unlock();
				}
				yield();
			}
		}
	}
}
