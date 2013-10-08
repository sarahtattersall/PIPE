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

package net.sourceforge.jeval.function.string;

import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionConstants;
import net.sourceforge.jeval.function.FunctionException;
import net.sourceforge.jeval.function.FunctionResult;

/**
 * This class is a function that executes within Evaluator. The function returns
 * the result of a Evaluator compatible expression. See the
 * Evaluator.evaluate(String) method for a complete description of how this
 * function works.
 */
public class Eval implements Function {
	/**
	 * Returns the name of the function - "eval".
	 * 
	 * @return The name of this function class.
	 */
	public String getName() {
		return "eval";
	}

	/**
	 * Executes the function for the specified argument. This method is called
	 * internally by Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of evaluator.
	 * @param arguments
	 *            A string expression that is compatible with Evaluator. *** THE
	 *            STRING ARGUMENT SHOULD NOT BE ENCLOSED IN QUOTES OR THE
	 *            EXPRESSION MAY NOT BE EVALUATED CORRECTLY.*** *** FUNCTION
	 *            CALLS ARE VALID WITHIN THE EVAL FUNCTION. ***
	 * 
	 * @return The evaluated result fot the input expression.
	 * 
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(final Evaluator evaluator,
			final String arguments) throws FunctionException {
		String result = null;

		try {
			result = evaluator.evaluate(arguments, false, true);
		} catch (EvaluationException ee) {
			throw new FunctionException(ee.getMessage(), ee);
		}

		int resultType = FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC;
		try {
			Double.parseDouble(result);
		} catch (NumberFormatException nfe) {
			resultType = FunctionConstants.FUNCTION_RESULT_TYPE_STRING;
		}

		return new FunctionResult(result, resultType);
	}
}