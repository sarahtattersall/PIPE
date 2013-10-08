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

import java.util.ArrayList;

import net.sourceforge.jeval.EvaluationConstants;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionConstants;
import net.sourceforge.jeval.function.FunctionException;
import net.sourceforge.jeval.function.FunctionHelper;
import net.sourceforge.jeval.function.FunctionResult;

/**
 * This class is a function that executes within Evaluator. The function returns
 * the index within the source string of the first occurrence of the substring,
 * starting at the specified index. See the String.indexOf(String, int) method
 * in the JDK for a complete description of how this function works.
 */
public class IndexOf implements Function {
	/**
	 * Returns the name of the function - "indexOf".
	 * 
	 * @return The name of this function class.
	 */
	public String getName() {
		return "indexOf";
	}

	/**
	 * Executes the function for the specified argument. This method is called
	 * internally by Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of Evaluator.
	 * @param arguments
	 *            A string argument that will be converted into two string
	 *            arguments and one integer argument. The first argument is the
	 *            source string, the second argument is the substring and the
	 *            third argument is the index. The string argument(s) HAS to be
	 *            enclosed in quotes. White space that is not enclosed within
	 *            quotes will be trimmed. Quote characters in the first and last
	 *            positions of any string argument (after being trimmed) will be
	 *            removed also. The quote characters used must be the same as
	 *            the quote characters used by the current instance of
	 *            Evaluator. If there are multiple arguments, they must be
	 *            separated by a comma (",").
	 * 
	 * @return Returns The index at where the substring is found. If the
	 *         substring is not found, then -1 is returned.
	 * 
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(final Evaluator evaluator, final String arguments)
			throws FunctionException {
		Integer result = null;
		String exceptionMessage = "Two string arguments and one integer "
				+ "argument are required.";

		ArrayList values = FunctionHelper.getTwoStringsAndOneInteger(arguments,
				EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

		if (values.size() != 3) {
			throw new FunctionException(exceptionMessage);
		}

		try {
			String argumentOne = FunctionHelper.trimAndRemoveQuoteChars(
					(String) values.get(0), evaluator.getQuoteCharacter());
			String argumentTwo = FunctionHelper.trimAndRemoveQuoteChars(
					(String) values.get(1), evaluator.getQuoteCharacter());
			int index = ((Integer) values.get(2)).intValue();
			result = new Integer(argumentOne.indexOf(argumentTwo, index));
		} catch (FunctionException fe) {
			throw new FunctionException(fe.getMessage(), fe);
		} catch (Exception e) {
			throw new FunctionException(exceptionMessage, e);
		}

		return new FunctionResult(result.toString(), 
				FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC);
	}
}