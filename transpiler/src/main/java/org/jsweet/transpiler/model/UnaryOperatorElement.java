package org.jsweet.transpiler.model;

/**
 * The model element that represents a binary operator (+, *, &&, ...).
 * 
 * @author Renaud Pawlak
 */
public interface UnaryOperatorElement extends ExtendedElement {

	/**
	 * The operator name.
	 */
	String getOperator();

	/**
	 * Gets the operator element.
	 */
	ExtendedElement getArgument();

}
