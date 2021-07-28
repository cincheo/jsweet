///* 
// * JSweet - http://www.jsweet.org
// * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *     http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package source.candies;
//
//import static def.body_parser.body_parser.Globals.json;
//import static def.body_parser.body_parser.Globals.urlencoded;
//import static def.express.Globals.express_lib_express;
//import static def.express.Globals.express_serve_static;
//import static def.node.Globals.__dirname;
//
//import def.body_parser.body_parser.OptionsDto;
//import def.express.express_lib_application.Application;
//import def.express.express_lib_router_index.RequestHandler;
//
//public class ExpressLib {
//
//	public static void main(String[] args) {
//
//		Application app = express_lib_express();
//
//		// Configuration
//		app.set("views", __dirname);
//		app.set("view engine", "jade");
//		app.use(urlencoded(new OptionsDto() {
//			{
//				extended = true;
//			}
//		}));
//		app.use(json());
//		app.use("/", // the URL throught which you want to access to you static
//						// content
//				(RequestHandler) express_serve_static(__dirname) // where your
//																	// static
//																	// content
//																	// is
//																	// located
//		// in your filesystem
//		);
//
//	}
//
//}
