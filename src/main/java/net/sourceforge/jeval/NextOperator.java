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
 * Represents the next operator in the expression to process.
 */
class NextOperator {

	// The operator this object represetns.
	private Operator operator = null;

	// The index of the operator in the expression.
	private int index = -1;

	/**
	 * Create a new NextOperator from an operator and index.
	 * 
	 * @param operator
	 *            The operator this object represents.
	 * @param index
	 *            The index of the operator in the expression.
	 */
	public NextOperator(final Operator operator, final int index) {
		this.operator = operator;
		this.index = index;
	}

	/**
	 * Returns the operator for this object.
	 * 
	 * @return The operator represented by this object.
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Returns the index for this object.
	 * 
	 * @return The index of the operator in the expression.
	 */
	public int getIndex() {
		return index;
	}
}