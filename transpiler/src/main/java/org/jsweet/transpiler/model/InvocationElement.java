package org.jsweet.transpiler.model;

import java.util.List;

/**
 * The model element for a method invocation.
 * 
 * @author Renaud Pawlak
 */
public interface InvocationElement extends ExtendedElement {

	/**
	 * Gets the arguments that are passed to the invoked element.
	 */
	List<ExtendedElement> getArguments();

	/**
	 * Gets the ith argument that is passed to the invoked element.
	 */
	ExtendedElement getArgument(int i);

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
