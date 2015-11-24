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
import static def.express.Globals.express;
import static def.express.express.Globals.Static;
import static def.node.Globals.__dirname;
import static jsweet.util.Globals.string;

import def.body_parser.body_parser.OptionsDto;
import def.express.express.Express;
import def.express.express.RequestHandler;

public class ExpressLib {

	public static void main(String[] args) {

		Express app = express();

		// Configuration
		app.set("views", __dirname);
		app.set("view engine", "jade");
		app.use(urlencoded(new OptionsDto() {
			{
				extended = true;
			}
		}));
		app.use(json());
		app.use("/", // the URL throught which you want to access to you static
						// content
				(RequestHandler) Static(string(__dirname)) // where your static
															// content is
															// located
		// in your filesystem
		);
		
	}

}
