/* 
 * Copyright (C) 2015 Louis Grignon <louis.grignon@gmail.com>
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
package org.jsweet;

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
			System.out.println("scaffold      generate candy sources from a TypeScript definition file");
			System.out.println("init-project  create a candy project, GitHub & Maven ready");
			System.exit(1);
		}

		String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
		switch (args[0]) {
		case "scaffold":
			GenerateSourcesTool.main(commandArgs);
			break;
		case "init-project":
			InitProjectTool.main(commandArgs);
			break;

		default:
			throw new RuntimeException("command not found: " + args[1]);
		}
	}
}
