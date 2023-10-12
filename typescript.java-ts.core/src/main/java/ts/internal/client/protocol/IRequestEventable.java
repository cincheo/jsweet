package ts.internal.client.protocol;

import java.util.List;

import ts.client.Event;

public interface IRequestEventable<T extends Event> {

	List<String> getKeys();

	boolean accept(T body);

	List<T> getEvents();
}
