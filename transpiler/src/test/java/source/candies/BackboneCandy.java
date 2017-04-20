package source.candies;

import static jsweet.util.Lang.function;
import static jsweet.util.Lang.union;

import java.util.function.Function;

import def.backbone.backbone.Collection;
import def.backbone.backbone.Model;
import def.backbone.backbone.ObjectHash;
import jsweet.util.Lang;

public class BackboneCandy {

}

class TodoStruct extends ObjectHash {
	public String content;
	public int order;
	public boolean done;
}

// Our basic **Todo** model has `content`, `order`, and `done` attributes.
class Todo extends Model {

	// Default attributes for the todo.
	@Override
	public TodoStruct defaults() {
		return new TodoStruct() {
			{
				content = "empty todo...";
				done = false;
			}
		};
	}

	// Ensure that each todo created has `content`.
	@Override
	public void initialize() {
		if (this.get("content") == null) {
			this.set(new TodoStruct() {
				{
					content = defaults().content;
				}
			});
		}
	}

	// Toggle the `done` state of this todo item.
	public void toggle() {
		this.save(new TodoStruct() {
			{
				done = !(boolean) get("done");
			}
		});
	}

	// Remove this Todo from *localStorage* and delete its view.
	@Override
	public Object clear() {
		return this.destroy();
	}

}

class TodoList extends Collection<Todo> {

	// Reference to this collection's model.
	Class<Todo> model = Todo.class;

	Function<String, String> test;

	// Save all of the todo items under the `"todos"` namespace.
	// TODO: the Store class in backbone does not define any parameter in the
	// constructor... maybe a mistake in the original TypeScript exammle, which
	// declare the Store class as an "any" variable...
	// Store localStorage = new Store("todos-backbone");
	// Store localStorage = new Store("todos-backbone");

	// Filter down the list of all todo items that are finished.
	Todo[] done() {
		return this.filter((todo, i, __) -> {
			return (Boolean) todo.get("done");
		});
	}

	// Filter down the list to only todo items that are still not finished.
	// TODO: this is wrong... there is a problem with varargs... to be
	// investigated
	Todo[] remaining() {
		return this.without(Lang.any(this.done()));
	}

	// We keep the Todos in sequential order, despite being saved by unordered
	// GUID in the database. This generates the next order number for new items.
	int nextOrder() {
		if (this.length == 0)
			return 1;
		return (Integer) this.last().get("order") + 1;
	}

	{
		comparator = union(function((Todo todo) -> {
			return (Integer) todo.get("order");
		}));
	}

}

class Child extends TodoList {

	{
		test = (String s) -> {
			return "";
		};
	}

}
