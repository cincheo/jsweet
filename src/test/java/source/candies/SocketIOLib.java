package source.candies;

import static def.express.Globals.express;
import static def.node.http.Globals.createServer;
import static def.socket_io.Globals.socket_io;
import static jsweet.dom.Globals.console;
import static jsweet.util.Globals.function;

import def.socket_io.socketio.Socket;
import def.express_serve_static_core.express_serve_static_core.Express;
import def.express_serve_static_core.express_serve_static_core.Request;
import def.express_serve_static_core.express_serve_static_core.Response;
import def.node.http.IncomingMessage;
import def.node.http.Server;
import def.node.http.ServerResponse;
import jsweet.lang.Array;
import jsweet.lang.Date;
import jsweet.lang.Interface;
import jsweet.lang.Object;

public class SocketIOLib {

	public static void main(String[] args) {
		Express app = express();

		Server server = createServer((IncomingMessage msg, ServerResponse resp) -> {
			console.log("server received request - url=" + msg.url + " method=" + msg.method);
			app.apply((Request) msg, (Response) resp, null);
		});

		server.listen(app.get.apply("port"), function(() -> {
			console.log("server listening on port [" + app.get.apply("port") + "]");
		}));

		def.socket_io.socketio.Server ioServer //
		= socket_io.listen.apply(server);

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