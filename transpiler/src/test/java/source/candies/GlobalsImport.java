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
package source.candies;

import static def.body_parser.body_parser.Globals.json;
import static def.body_parser.body_parser.Globals.urlencoded;
import static def.errorhandler.Globals.errorhandler;
import static def.express.Globals.express_lib_express;
import static def.express.Globals.express_serve_static;
import static def.jquery.Globals.$;
import static def.socket_io.Globals.socket_io;
import static jsweet.util.Lang.union;

import def.body_parser.body_parser.OptionsDto;
import def.errorhandler.errorhandler.Options;
import def.express.express_lib_application.Application;
import def.express.express_lib_router_index.RequestHandler;
import def.node.http.Server;

@SuppressWarnings("all")
public class GlobalsImport {

	public static void main(String[] args) {

		Application app = express_lib_express();

		RequestHandler h = json();

		urlencoded(new OptionsDto() {
			{
				extended = true;
			}
		});

		express_serve_static("test");

		Server server = null;

		errorhandler(new Options() {
			{
				log = union(true);
			}
		});

		def.socket_io.socketio.Server ioServer = socket_io.listen.apply(server);

		$("test").addClass("test");
	}

}
