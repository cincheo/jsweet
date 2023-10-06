package ts.client;

import ts.internal.client.protocol.Message;

/**
 * Server-initiated event message
 *
 * @param <T>
 */
public class Event<T> extends Message {

	/**
	 * Name fo event.
	 */
	private String event;

	/**
	 * Event-specific information
	 */
	private T body;

	public String getEvent() {
		return event;
	}

	public T getBody() {
		return body;
	}
}
