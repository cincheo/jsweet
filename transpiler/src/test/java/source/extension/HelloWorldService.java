package source.extension;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloWorldService {
	@GET
	@Path("/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public HelloWorldDto getMsg(@PathParam("param") String msg) {
		String output = "service says : " + msg;
		return new HelloWorldDto(output);
	}
}