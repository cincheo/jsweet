package org.jsweet.transpiler.model;

import javax.lang.model.type.ExecutableType;

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
	 * Returns the operator's type, which is a functional type taking two
	 * parameters.
	 */
	ExecutableType getOperatorType();
	
	/**
	 * Gets the left-hand side element.
	 */
	ExtendedElement getLeftHandSide();
	
	/**
	 * Gets the right-hand side element.
	 */
	ExtendedElement getRightHandSide();
	
}
