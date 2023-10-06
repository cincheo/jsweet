package ts.internal.client.protocol;

public class Response<T> extends Message {
	
	/**
	 * Sequence number of the request message.
	 */
	private int request_seq;

	/**
	 * Outcome of the request.
	 */
	private boolean success;

	/**
	 * The command requested.
	 */
	private String command;

	/**
	 * Contains error message if success === false.
	 */
	private String message;

	/**
	 * Contains message body if success === true.
	 */
	private T body;

	public int getRequest_seq() {
		return request_seq;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getCommand() {
		return command;
	}

	public String getMessage() {
		return message;
	}

	public T getBody() {
		return body;
	}
}
