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

package net.sourceforge.jeval.operator;

import net.sourceforge.jeval.EvaluationException;

/**
 * This is the standard operator that is the parent to all operators found in
 * expressions.
 */
public abstract class AbstractOperator implements Operator {

	private String symbol = null;

	private int precedence = 0;

	private boolean unary = false;

	/**
	 * A constructor that takes the operator symbol and precedence as input.
	 * 
	 * @param symbol
	 *            The character(s) that makes up the operator.
	 * @param precedence
	 *            The precedence level given to this operator.
	 */
	public AbstractOperator(final String symbol, final int precedence) {

		this.symbol = symbol;
		this.precedence = precedence;
	}

	/**
	 * A constructor that takes the operator symbol, precedence, unary indicator
	 * and unary precedence as input.
	 * 
	 * @param symbol
	 *            The character(s) that makes up the operator.
	 * @param precedence
	 *            The precedence level given to this operator.
	 * @param unary
	 *            Indicates of the operator is a unary operator or not.
	 */
	public AbstractOperator(

	String symbol, int precedence, boolean unary) {

		this.symbol = symbol;
		this.precedence = precedence;
		this.unary = unary;
	}

	/**
	 * Evaluates two double operands.
	 * 
	 * @param leftOperand
	 *            The left operand being evaluated.
	 * @param rightOperand
	 *            The right operand being evaluated.
	 */
	public double evaluate(final double leftOperand, final double rightOperand) {
		return 0;
	}

	/**
	 * Evaluates two string operands.
	 * 
	 * @param leftOperand
	 *            The left operand being evaluated.
	 * @param rightOperand
	 *            The right operand being evaluated.
	 * 
	 * @return String The value of the evaluated operands.
	 * 
	 * @exception EvaluateException
	 *                Thrown when an error is found while evaluating the
	 *                expression.
	 */
	public String evaluate(final String leftOperand, final String rightOperand)
			throws EvaluationException {
		throw new EvaluationException("Invalid operation for a string.");
	}

	/**
	 * Evaluate one double operand.
	 * 
	 * @param operand
	 *            The operand being evaluated.
	 */
	public double evaluate(double operand) {
		return 0;
	}

	/**
	 * Returns the character(s) that makes up the operator.
	 * 
	 * @return The operator symbol.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Returns the precedence given to this operator.
	 * 
	 * @return The precedecne given to this operator.
	 */
	public int getPrecedence() {
		return precedence;
	}

	/**
	 * Returns the length of the operator symbol.
	 * 
	 * @return The length of the operator symbol.
	 */
	public int getLength() {
		return symbol.length();
	}

	/**
	 * Returns an indicator of if the operator is unary or not.
	 * 
	 * @return An indicator of if the operator is unary or not.
	 */
	public boolean isUnary() {
		return unary;
	}

	/**
	 * Determines if this operator is equal to another operator. Equality is
	 * determined by comparing the operator symbol of both operators.
	 * 
	 * @param object
	 *            The object to compare with this operator.
	 * 
	 * @return True if the object is equal and false if not.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the input object is not of the Operator type.
	 */
	public boolean equals(final Object object) {
		if (object == null) {
			return false;
		}

		if (!(object instanceof AbstractOperator)) {
			throw new IllegalStateException("Invalid operator object.");
		}

		AbstractOperator operator = (AbstractOperator) object;

		if (symbol.equals(operator.getSymbol())) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the String representation of this operator, which is the symbol.
	 * 
	 * @return The operator symbol.
	 */
	public String toString() {
		return getSymbol();
	}
}