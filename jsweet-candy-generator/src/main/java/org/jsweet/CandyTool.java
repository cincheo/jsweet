/* 
 * Candy Tool - http://www.jsweet.org
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

import java.io.File;
import java.util.Arrays;

import org.jsweet.candies.GenerateSourcesTool;
import org.jsweet.candies.InitProjectTool;

/**
 * executable tool to generate / package candies
 * 
 * @author Louis Grignon
 */
public class CandyTool {

	public static void main(String[] args) throws Throwable {

		if (args.length < 1) {
			System.out.println("usage: java -jar candy-tool.jar <command> [<args>]");
			System.out.println();
			System.out.println("A list of available commands:");
			System.out.println("init-project        create a candy project, GitHub & Maven ready");
			System.out.println("generate-sources    generate candy sources from a TypeScript definition file");
			System.exit(1);
		}

		String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
		switch (args[0]) {
		case "generate-sources":
			GenerateSourcesTool.main(commandArgs);
			break;
		case "init-project":
			InitProjectTool.main(commandArgs);
			break;

		default:
			throw new RuntimeException("command not found: " + args[1]);
		}
	}

	public static File getResourceFile(String uri) {
		try {
			// TODO : put files in resources and extract resources from all-in-one jar
			// URL url = Thread.currentThread().getContextClassLoader() //
			// .getResource(uri);
			// File file = new File(url.toURI());
			// return file;
			return new File(uri);
		} catch (Exception e) {
			throw new RuntimeException("cannot get resource file: " + uri, e);
		}
	}
}
