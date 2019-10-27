package org.jsweet.transpiler.model;

/**
 * The model element that represents a binary operator (+, *, &&, ...).
 * 
 * @author Renaud Pawlak
 */
public interface BinaryOperatorElement extends ExtendedElement {

	/**
	 * The operator name.
	 */
	String getOperator();

	/**
	 * Gets the left-hand side element.
	 */
	ExtendedElement getLeftHandSide();
	
	/**
	 * Gets the right-hand side element.
	 */
	ExtendedElement getRightHandSide();
	
}
