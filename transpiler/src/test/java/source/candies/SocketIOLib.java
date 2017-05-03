package source.candies;


import static def.dom.Globals.console;
import static def.express.Globals.express_lib_express;
import static def.node.http.Globals.createServer;
import static def.socket_io.Globals.socket_io;
import static jsweet.util.Lang.any;
import static jsweet.util.Lang.function;

import def.express.express_lib_application.Application;
import def.express.express_lib_router_index.RequestHandler;
import def.js.Array;
import def.js.Date;
import def.js.Object;
import def.js.String;
import def.node.http.IncomingMessage;
import def.node.http.Server;
import def.node.http.ServerResponse;
import def.socket_io.socketio.Socket;
import jsweet.lang.Interface;

public class SocketIOLib {

	public static void main(String[] args) {
		Application app = express_lib_express();

		Server server = createServer((IncomingMessage msg, ServerResponse resp) -> {
			console.log("server received request - url=" + msg.url + " method=" + msg.method);
			RequestHandler delegate = any(app);
			delegate.$apply(any(msg), any(resp), null);
		});

		server.listen(app.get("port"), function(() -> {
			console.log("server listening on port [" + app.get("port") + "]");
		}));

		def.socket_io.socketio.Server ioServer //
		= socket_io.listen.$apply(server);

		Object users = new Object();
		Array<Message> messages = new Array<>();

		// Connexion socket
		ioServer.sockets.on("connect", function((Socket socket) -> {

			// Web socket etablie
			double timeStamp = new Date().getTime();
			SocketRequest socketRequest = (SocketRequest) socket.request;
			String ip = socketRequest.connection.remoteAddress;
			console.log("[CONNECTED] " + timeStamp + " | client IP " + ip + " |");

			// refresh all available users
			int usersCount = Object.keys(users).length;
			for (String userIp : Object.keys(users)) {
				socket.emit("newuser", users.$get(userIp), usersCount);
			}

			socket.on("request:loggedUser", function(() -> {
				console.log("received: request logged user");

				socket.emit("response:loggedUser", users.$get(ip));
			}));

			socket.on("request:lastMessages", function(() -> {
				console.info("requesting last messages... Sending");
				socket.emit("response:lastMessages", messages);
			}));

			socket.on("request:loggedUsers", function(() -> {
				console.info("requesting logged user... sending");
				int i = 0;
				for (String userIp : Object.keys(users)) {
					socket.emit("newuser", users.$get(userIp), ++i);
				}
			}));
		}));
	}

}

class Message {
	public String text;

	public Message(String text) {
		this.text = text;
	}

}

@Interface
abstract class SocketConnection {
	public String remoteAddress;
}

@Interface
abstract class SocketRequest {
	public SocketConnection connection;
}