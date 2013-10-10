/*
 * Copyright 2002-2007 Robert Breidecker.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.jeval;

import net.sourceforge.jeval.operator.Operator;

/**
 * Represents an operand being processed in the expression.
 */
public class ExpressionOperand {

	// The value of the operand.
	private String value = null;

	// The unary operator for the operand, if one exists.
	private Operator unaryOperator = null;

	/**
	 * Create a new ExpressionOperand.
	 * 
	 * @param value
	 *            The value for the new ExpressionOperand.
	 * @param unaryOperator
	 *            The unary operator for this object.
	 */
	public ExpressionOperand(final String value, final Operator unaryOperator) {
		this.value = value;
		this.unaryOperator = unaryOperator;
	}

	/**
	 * Returns the value of this object.
	 * 
	 * @return The value of this object.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the unary operator for this object.
	 * 
	 * @return The unary operator for this object.
	 */
	public Operator getUnaryOperator() {
		return unaryOperator;
	}
}