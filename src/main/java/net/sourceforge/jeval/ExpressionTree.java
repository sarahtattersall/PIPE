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

import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionConstants;
import net.sourceforge.jeval.function.FunctionException;
import net.sourceforge.jeval.function.FunctionResult;
import net.sourceforge.jeval.operator.Operator;

/**
 * Represents an expression tree made up of a left operand, right operand,
 * operator and unary operator.
 */
public class ExpressionTree {

	// The left node in the tree.
	private Object leftOperand = null;

	// The right node in the tree.
	private Object rightOperand = null;

	// The operator for the two operands.
	private Operator operator = null;

	// The unary operator, if one exists.
	private Operator unaryOperator = null;

	// The Evaluator object processing this tree.
	private Evaluator evaluator = null;

	/**
	 * Creates a new ExpressionTree.
	 * 
	 * @param evaluator
	 *            The Evaluator object processing this tree.
	 * @param leftOperand
	 *            The left operand to place as the left node of the tree.
	 * @param rightOperand
	 *            The right operand to place as the right node of the tree.
	 * @param operator
	 *            The operator to place as the operator node of the tree.
	 * @param unaryOperator
	 *            The new unary operator for this tree.
	 */
	public ExpressionTree(final Evaluator evaluator, final Object leftOperand,
			final Object rightOperand, final Operator operator,
			final Operator unaryOperator) {

		this.evaluator = evaluator;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operator = operator;
		this.unaryOperator = unaryOperator;
	}

	/**
	 * Returns the left operand of this tree.
	 * 
	 * @return The left operand of this tree.
	 */
	public Object getLeftOperand() {
		return leftOperand;
	}

	/**
	 * Returns the right operand of this tree.
	 * 
	 * @return The right operand of this tree.
	 */
	public Object getRightOperand() {
		return rightOperand;
	}

	/**
	 * Returns the operator for this tree.
	 * 
	 * @return The operator of this tree.
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Returns the unary operator for this tree.
	 * 
	 * @return The unary operator of this tree.
	 */
	public Operator getUnaryOperator() {
		return unaryOperator;
	}

	/**
	 * Evaluates the operands for this tree using the operator and the unary
	 * operator.
	 * 
	 * @param wrapStringFunctionResults
	 *            Indicates if the results from functions that return strings
	 *            should be wrapped in quotes. The quote character used will be
	 *            whatever is the current quote character for this object.
	 * 
	 * @exception EvaluateException
	 *                Thrown is an error is encountered while processing the
	 *                expression.
	 */
	public String evaluate(final boolean wrapStringFunctionResults)
			throws EvaluationException {

		String rtnResult = null;

		// Get the left operand.
		String leftResultString = null;
		Double leftResultDouble = null;

		if (leftOperand instanceof ExpressionTree) {
			leftResultString = ((ExpressionTree) leftOperand)
					.evaluate(wrapStringFunctionResults);

			try {
				leftResultDouble = new Double(leftResultString);
				leftResultString = null;
			} catch (NumberFormatException exception) {
				leftResultDouble = null;
			}
		} else if (leftOperand instanceof ExpressionOperand) {

			final ExpressionOperand leftExpressionOperand = (ExpressionOperand) leftOperand;

			leftResultString = leftExpressionOperand.getValue();
			leftResultString = evaluator.replaceVariables(leftResultString);

			// Check if the operand is a string or not. If it not a string,
			// then it must be a number.
			if (!evaluator.isExpressionString(leftResultString)) {
				try {
					leftResultDouble = new Double(leftResultString);
					leftResultString = null;
				} catch (NumberFormatException nfe) {
					throw new EvaluationException("Expression is invalid.", nfe);
				}

				if (leftExpressionOperand.getUnaryOperator() != null) {
					leftResultDouble = new Double(leftExpressionOperand
							.getUnaryOperator().evaluate(
									leftResultDouble.doubleValue()));
				}
			} else {
				if (leftExpressionOperand.getUnaryOperator() != null) {
					throw new EvaluationException("Invalid operand for "
							+ "unary operator.");
				}
			}
		} else if (leftOperand instanceof ParsedFunction) {

			final ParsedFunction parsedFunction = (ParsedFunction) leftOperand;
			final Function function = parsedFunction.getFunction();
			String arguments = parsedFunction.getArguments();
			arguments = evaluator.replaceVariables(arguments);
			
			if (evaluator.getProcessNestedFunctions()) {
				arguments = evaluator.processNestedFunctions(arguments);
			}

			try {
				FunctionResult functionResult = 
					function.execute(evaluator, arguments);
				leftResultString = functionResult.getResult();

				if (functionResult.getType() == 
					FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC) {
					
					Double resultDouble = new Double(leftResultString);

					// Process a unary operator if one exists.
					if (parsedFunction.getUnaryOperator() != null) {
						resultDouble = new Double(parsedFunction
								.getUnaryOperator().evaluate(
										resultDouble.doubleValue()));
					}

					// Get the final result.
					leftResultString = resultDouble.toString();
				} 
				else if (functionResult.getType() == 
					FunctionConstants.FUNCTION_RESULT_TYPE_STRING) {
					
					// The result must be a string result.
					if (wrapStringFunctionResults) {
						leftResultString = evaluator.getQuoteCharacter()
								+ leftResultString
								+ evaluator.getQuoteCharacter();
					}

					if (parsedFunction.getUnaryOperator() != null) {
						throw new EvaluationException("Invalid operand for "
								+ "unary operator.");
					}
				}
			} catch (FunctionException fe) {
				throw new EvaluationException(fe.getMessage(), fe);
			}

			if (!evaluator.isExpressionString(leftResultString)) {
				try {
					leftResultDouble = new Double(leftResultString);
					leftResultString = null;
				} catch (NumberFormatException nfe) {
					throw new EvaluationException("Expression is invalid.", nfe);
				}
			}
		} else {
			if (leftOperand != null) {
				throw new EvaluationException("Expression is invalid.");
			}
		}

		// Get the right operand.
		String rightResultString = null;
		Double rightResultDouble = null;

		if (rightOperand instanceof ExpressionTree) {
			rightResultString = ((ExpressionTree) rightOperand)
					.evaluate(wrapStringFunctionResults);

			try {
				rightResultDouble = new Double(rightResultString);
				rightResultString = null;
			} catch (NumberFormatException exception) {
				rightResultDouble = null;
			}

		} else if (rightOperand instanceof ExpressionOperand) {

			final ExpressionOperand rightExpressionOperand = (ExpressionOperand) rightOperand;
			rightResultString = ((ExpressionOperand) rightOperand).getValue();
			rightResultString = evaluator.replaceVariables(rightResultString);

			// Check if the operand is a string or not. If it not a string,
			// then it must be a number.
			if (!evaluator.isExpressionString(rightResultString)) {
				try {
					rightResultDouble = new Double(rightResultString);
					rightResultString = null;
				} catch (NumberFormatException nfe) {
					throw new EvaluationException("Expression is invalid.", nfe);
				}

				if (rightExpressionOperand.getUnaryOperator() != null) {
					rightResultDouble = new Double(rightExpressionOperand
							.getUnaryOperator().evaluate(
									rightResultDouble.doubleValue()));
				}
			} else {
				if (rightExpressionOperand.getUnaryOperator() != null) {
					throw new EvaluationException("Invalid operand for "
							+ "unary operator.");
				}
			}
		} else if (rightOperand instanceof ParsedFunction) {

			final ParsedFunction parsedFunction = (ParsedFunction) rightOperand;
			final Function function = parsedFunction.getFunction();
			String arguments = parsedFunction.getArguments();
			arguments = evaluator.replaceVariables(arguments);
			
			if (evaluator.getProcessNestedFunctions()) {
				arguments = evaluator.processNestedFunctions(arguments);
			}

			try {				
				FunctionResult functionResult = 
					function.execute(evaluator, arguments);
				rightResultString = functionResult.getResult();

				if (functionResult.getType() == 
					FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC) {
					
					Double resultDouble = new Double(rightResultString);

					// Process a unary operator if one exists.
					if (parsedFunction.getUnaryOperator() != null) {
						resultDouble = new Double(parsedFunction
								.getUnaryOperator().evaluate(
										resultDouble.doubleValue()));
					}

					// Get the final result.
					rightResultString = resultDouble.toString();
				}
				else if (functionResult.getType() == 
					FunctionConstants.FUNCTION_RESULT_TYPE_STRING) {
					
					// The result must be a string result.
					if (wrapStringFunctionResults) {
						rightResultString = evaluator.getQuoteCharacter()
								+ rightResultString
								+ evaluator.getQuoteCharacter();
					}

					if (parsedFunction.getUnaryOperator() != null) {
						throw new EvaluationException("Invalid operand for "
								+ "unary operator.");
					}
				}
			} catch (FunctionException fe) {
				throw new EvaluationException(fe.getMessage(), fe);
			}

			if (!evaluator.isExpressionString(rightResultString)) {
				try {
					rightResultDouble = new Double(rightResultString);
					rightResultString = null;
				} catch (NumberFormatException nfe) {
					throw new EvaluationException("Expression is invalid.", nfe);
				}
			}
		} else if (rightOperand == null) {
			// Do nothing.
		} else {
			throw new EvaluationException("Expression is invalid.");
		}

		// Evaluate the the expression.
		if (leftResultDouble != null && rightResultDouble != null) {
			double doubleResult = operator.evaluate(leftResultDouble
					.doubleValue(), rightResultDouble.doubleValue());

			if (getUnaryOperator() != null) {
				doubleResult = getUnaryOperator().evaluate(doubleResult);
			}

			rtnResult = new Double(doubleResult).toString();
		} else if (leftResultString != null && rightResultString != null) {
			rtnResult = operator.evaluate(leftResultString, rightResultString);
		} else if (leftResultDouble != null && rightResultDouble == null) {
			double doubleResult = -1;

			if (unaryOperator != null) {
				doubleResult = unaryOperator.evaluate(leftResultDouble
						.doubleValue());
			} else {
				// Do not allow numeric (left) and
				// string (right) to be evaluated together.
				throw new EvaluationException("Expression is invalid.");
			}

			rtnResult = new Double(doubleResult).toString();
		} else {
			throw new EvaluationException("Expression is invalid.");
		}

		return rtnResult;
	}
}