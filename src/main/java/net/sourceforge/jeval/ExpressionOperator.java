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
 * Represents an operator being processed in the expression.
 */
public class ExpressionOperator {

	// The operator that this object represents.
	private Operator operator = null;

	// The unary operator for this object, if there is one.
	private Operator unaryOperator = null;

	/**
	 * Creates a new ExpressionOperator.
	 * 
	 * @param operator
	 *            The operator this object represents.
	 * @param unaryOperator
	 *            The unary operator for this object.
	 */
	public ExpressionOperator(final Operator operator,
			final Operator unaryOperator) {
		this.operator = operator;
		this.unaryOperator = unaryOperator;
	}

	/**
	 * Returns the operator for this object.
	 * 
	 * @return The operator for this object.
	 */
	public Operator getOperator() {
		return operator;
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