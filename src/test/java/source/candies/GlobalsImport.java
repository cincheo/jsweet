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
package source.candies;

import static def.body_parser.body_parser.Globals.json;
import static def.body_parser.body_parser.Globals.urlencoded;
import static def.express.Globals.express;
import static def.serve_static.Globals.serve_static;
import static def.socket_io.Globals.socket_io;
import static def.errorhandler.Globals.errorhandler;
import static def.jquery.Globals.$;

import def.body_parser.body_parser.OptionsDto;
import def.errorhandler.Options;
import def.express.express.Express;
import def.express.express.RequestHandler;
import def.node.http.Server;

public class GlobalsImport {

	public static void main(String[] args) {

		Express app = express();

		RequestHandler h = json();

		urlencoded(new OptionsDto() {
			{
				extended = true;
			}
		});

		serve_static("test");

		Server server = null;

		errorhandler(new Options() {
			{
				log = true;
			}
		});

		socket_io();

		$("test").addClass("test");
		
	}

}
