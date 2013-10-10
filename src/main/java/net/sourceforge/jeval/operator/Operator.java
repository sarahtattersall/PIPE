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
 * An oerator than can specified in an expression.
 */
public interface Operator {

	/**
	 * Evaluates two double operands.
	 * 
	 * @param leftOperand
	 *            The left operand being evaluated.
	 * @param rightOperand
	 *            The right operand being evaluated.
	 */
	public abstract double evaluate(double leftOperand, double rightOperand);

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
	public abstract String evaluate(final String leftOperand,
			final String rightOperand) throws EvaluationException;

	/**
	 * Evaluate one double operand.
	 * 
	 * @param operand
	 *            The operand being evaluated.
	 */
	public abstract double evaluate(final double operand);

	/**
	 * Returns the character(s) that makes up the operator.
	 * 
	 * @return The operator symbol.
	 */
	public abstract String getSymbol();

	/**
	 * Returns the precedence given to this operator.
	 * 
	 * @return The precedecne given to this operator.
	 */
	public abstract int getPrecedence();

	/**
	 * Returns the length of the operator symbol.
	 * 
	 * @return The length of the operator symbol.
	 */
	public abstract int getLength();

	/**
	 * Returns an indicator of if the operator is unary or not.
	 * 
	 * @return An indicator of if the operator is unary or not.
	 */
	public abstract boolean isUnary();
}