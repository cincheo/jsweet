package org.jsweet.transpiler.model;

import java.util.List;

public interface InvocationElement extends ExtendedElement {

	/**
	 * Gets the arguments that are passed to the invoked element.
	 */
	List<ExtendedElement> getArguments();

	/**
	 * Gets first argument that is passed to the invoked element.
	 */
	ExtendedElement getFirstArgument();

	/**
	 * Gets the arguments that are passed to the invoked element, omitting the
	 * first argument.
	 */
	List<ExtendedElement> getArgumentTail();

	/**
	 * Gets the number of arguments that are passed to the invoked element.
	 */
	int getArgumentCount();

}
