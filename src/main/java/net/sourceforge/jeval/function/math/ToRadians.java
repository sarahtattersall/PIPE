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

package net.sourceforge.jeval.function.math;

import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionConstants;
import net.sourceforge.jeval.function.FunctionException;
import net.sourceforge.jeval.function.FunctionResult;

/**
 * This class is a function that executes within Evaluator. The function
 * converts an angle measured in degress to the equivalent angle measured in
 * radians. See the Math.toRadians(double) method in the JDK for a complete
 * description of how this function works.
 */
public class ToRadians implements Function {
	/**
	 * Returns the name of the function - "toRadians".
	 * 
	 * @return The name of this function class.
	 */
	public String getName() {
		return "toRadians";
	}

	/**
	 * Executes the function for the specified argument. This method is called
	 * internally by Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of Evaluator.
	 * @param arguments
	 *            A string argument that will be converted to a double value and
	 *            evaluated.
	 * 
	 * @return A measurement of the argument in radians.
	 * 
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(Evaluator evaluator, String arguments)
			throws FunctionException {
		Double result = null;
		Double number = null;

		try {
			number = new Double(arguments);
		} catch (Exception e) {
			throw new FunctionException("Invalid argument.", e);
		}

		result = new Double(Math.toRadians(number.doubleValue()));

		return new FunctionResult(result.toString(), 
				FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
	}
}