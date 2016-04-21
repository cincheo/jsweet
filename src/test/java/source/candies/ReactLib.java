package source.candies;

import static def.react.react.Globals.createElement;
import static jsweet.dom.Globals.clearInterval;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.setInterval;
import static jsweet.util.Globals.$map;
import static jsweet.util.Globals.any;
import static jsweet.util.Globals.array;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.union;

import def.react.jsx.Element;
import def.react.react.Component;
import def.react.react.ComponentClass;
import def.react.react.DOMAttributes;
import def.react.react.FormEvent;
import def.react.react.HTMLAttributes;
import def.react.react.ReactDOM;
import def.react.react.ReactElement;
import jsweet.lang.Date;
import jsweet.lang.Interface;
import jsweet.lang.Optional;

@Interface
abstract class Contact {
	int key;
	String name;
	@Optional
	String email;
	@Optional
	String description;
}

interface Constants {
	HTMLAttributes EMPTY = null;
}

public class ReactLib implements Constants {

	@SuppressWarnings("unchecked")
	public static <P, S, C extends Component<P, S>> ReactElement<P> createElementFromClass(Class<C> componentClass, P props) {
		return createElement((ComponentClass<P>) any(componentClass), props);
	}

	public static void example1() {
		ReactElement<?> rootElement = createElement("div", EMPTY, //
				createElement("h1", EMPTY, "Contacts"), //
				createElement("ul", EMPTY, //
						createElement("li", EMPTY, //
								createElement("h2", EMPTY, "James Nelson"), //
								createElement("a", new HTMLAttributes() {
									{
										href = "mailto:james@jamesknelson.com";
									}
								}, "james@jamesknelson.com")), //
						createElement("li", EMPTY, //
								createElement("h2", EMPTY, "Joe Citizen"), //
								createElement("a", new HTMLAttributes() {
									{
										href = "mailto:joe@example.com";
									}
								}, "joe@example.com"))));

		ReactDOM.render(rootElement, document.getElementById("react-app"));
	}

	public static void example2() {
		Contact[] contacts = { new Contact() {
			{
				key = 1;
				name = "James Nelson";
				email = "james@jamesknelson.com";
			}
		}, new Contact() {
			{
				key = 2;
				name = "Bob";
			}
		}, new Contact() {
			{
				key = 3;
				name = "Renaud Pawlak";
				email = "rp@rp.com";
			}
		}, new Contact() {
			{
				key = 4;
				name = "John Smith";
				email = "JS@hello.com";
			}
		} };

		Object[] listElements = array(array(contacts).filter((contact, __, ___) -> {
			return contact.email != null;
		})).map((contact, __, ___) -> {
			return createElement("li", (HTMLAttributes) $map("key", contact.key),
					//
					createElement("h2", EMPTY, contact.name), //
					createElement("a", new HTMLAttributes() {
				{
					href = "mailto:" + contact.email;
				}
			}, contact.email));
		});

		ReactElement<?> rootElement = createElement("div", (HTMLAttributes) $map(), //
				createElement("h1", EMPTY, "Contacts"), //
				createElement("ul", EMPTY, listElements));

		ReactDOM.render(rootElement, document.getElementById("react-app"));

	}

	public static void example3() {
		Contact[] contacts = { new Contact() {
			{
				key = 1;
				name = "James Nelson";
				email = "james@jamesknelson.com";
				description = "hello";
			}
		}, new Contact() {
			{
				key = 2;
				name = "Bob";
				description = "hello";
			}
		}, new Contact() {
			{
				key = 3;
				name = "Renaud Pawlak";
				email = "rp@rp.com";
				description = "hello";
			}
		}, new Contact() {
			{
				key = 4;
				name = "John Smith";
				email = "JS@hello.com";
				description = "hello";
			}
		} };

		Object[] listElements = array(array(contacts).filter((contact, __, ___) -> {
			return contact.email != null;
		})).map((contact, __, ___) -> {
			return createElementFromClass(ContactItem.class, contact);
		});

		ReactElement<?> rootElement = createElement("div", (HTMLAttributes) $map(), //
				createElement("h1", EMPTY, "Contacts"), //
				createElement("ul", EMPTY, listElements));

		ReactDOM.render(rootElement, document.getElementById("react-app"));

	}

	public static void example4() {
		ReactDOM.render(createElementFromClass(Timer.class, (Object) null), document.getElementById("react-app"));
	}

	public static void example5() {
		ReactDOM.render(createElementFromClass(TodoApp.class, (Object) null), document.getElementById("react-app"));
	}

	public static void main(String[] args) {
		example5();
	}
}

class ContactItem extends Component<Contact, Object> {

	public Element render() {
		Contact contact = union(props);
		return any(createElement("li", (HTMLAttributes) $map(), //
				createElement("h2", (HTMLAttributes) $map(), contact.name), //
				createElement("a", (HTMLAttributes) $map("href", "mailto:" + contact.email), contact.email), //
				createElement("h2", (HTMLAttributes) $map(), contact.description)) //
		);
	}

}

class TimerState {
	int secondsElapsed = 0;
}

class Timer extends Component<Object, TimerState> {
	String displayName = "Timer";
	int interval;

	TimerState state = new TimerState() {
		{
			secondsElapsed = 0;
		}
	};

	public void tick() {
		this.setState(new TimerState() {
			{
				secondsElapsed = state.secondsElapsed + 1;
			}
		});
	}

	public void componentDidMount() {
		interval = any(setInterval(function(this::tick), 1000));
	}

	public void componentWillUnmount() {
		clearInterval(interval);
	}

	public Element render() {
		return any(createElement("div", (DOMAttributes) null, "Seconds Elapsed: ", this.state.secondsElapsed));
	}
}

@Interface
class TodoListProps {
	TodoProps[] items;
}

@Interface
class TodoProps {
	double id;
	String text;
}

@Interface
class TodoAppState {
	@Optional
	TodoProps[] items;
	String text;
}

class TodoList2 extends Component<TodoListProps, Object> implements Constants {

	private Element createItem(TodoProps item, Double __, TodoProps[] ___) {
		return any(createElement("li", (HTMLAttributes) $map("key", item.id), item.text));
	}

	public Element render() {
		TodoListProps props = union(this.props);
		return any(createElement("ul", EMPTY, array(array(props.items).map(this::createItem))));
	}
}

class TodoApp extends Component<Object, TodoAppState> implements Constants {

	TodoAppState state = new TodoAppState() {
		{
			items = new TodoProps[0];
			text = "";
		}
	};

	public void onChange(FormEvent e) {
		this.setState(new TodoAppState() {
			{
				text = (String) e.target.$get("value");
			}
		});
	}

	public void handleSubmit(FormEvent e) {
		e.preventDefault();
		TodoProps[] nextItems = array(state.items).concat(new TodoProps() {
			{
				text = state.text;
				id = Date.now();
			}
		});
		String nextText = "";
		this.setState(new TodoAppState() {
			{
				items = nextItems;
				text = nextText;
			}
		});
	}

	public Element render() {
		return any(createElement("div", EMPTY, //
				createElement("h3", EMPTY, "TODO"), //
				ReactLib.createElementFromClass(TodoList2.class, new TodoListProps() {
					{
						items = state.items;
					}
				}), //
				createElement("form", new HTMLAttributes() {
					{
						onSubmit = TodoApp.this::handleSubmit;
					}
				}, //
						createElement("input", new HTMLAttributes() {
							{
								onChange = TodoApp.this::onChange;
								value = union(state.text);
							}
						}), //
						createElement("button", EMPTY, "Add #" + (this.state.items.length + 1)))));
	}
}
