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
 * a new string with all of the occurances of the old character in the source
 * string replaced with the new character. See the String.replace(char, char)
 * method in the JDK for a complete description of how this function works.
 */
public class Replace implements Function {
	/**
	 * Returns the name of the function - "replace".
	 * 
	 * @return The name of this function class.
	 */
	public String getName() {
		return "replace";
	}

	/**
	 * Executes the function for the specified argument. This method is called
	 * internally by Evaluator.
	 * 
	 * @param evaluator
	 *            An instance of Evaluator.
	 * @param arguments
	 *            A string argument that will be converted into one string and
	 *            two character arguments. The first argument is the source
	 *            string to replace the charactes in. The second argument is the
	 *            old character to replace in the source string. The third
	 *            argument is the new character to replace the old character
	 *            with in the source string. The string and character
	 *            argument(s) HAS to be enclosed in quotes. White space that is
	 *            not enclosed within quotes will be trimmed. Quote characters
	 *            in the first and last positions of any string argument (after
	 *            being trimmed) will be removed also. The quote characters used
	 *            must be the same as the quote characters used by the current
	 *            instance of Evaluator. If there are multiple arguments, they
	 *            must be separated by a comma (",").
	 * 
	 * @return Returns a string with every occurence of the old character
	 *         replaced with the new character.
	 * 
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(final Evaluator evaluator, final String arguments)
			throws FunctionException {
		String result = null;
		String exceptionMessage = "One string argument and two character "
				+ "arguments are required.";

		ArrayList values = FunctionHelper.getStrings(arguments, 
				EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR);

		if (values.size() != 3) {
			throw new FunctionException(exceptionMessage);
		}

		try {
			String argumentOne = FunctionHelper.trimAndRemoveQuoteChars(
					(String) values.get(0), evaluator.getQuoteCharacter());

			String argumentTwo = FunctionHelper.trimAndRemoveQuoteChars(
					(String) values.get(1), evaluator.getQuoteCharacter());

			String argumentThree = FunctionHelper.trimAndRemoveQuoteChars(
					(String) values.get(2), evaluator.getQuoteCharacter());

			char oldCharacter = ' ';
			if (argumentTwo.length() == 1) {
				oldCharacter = argumentTwo.charAt(0);
			} else {
				throw new FunctionException(exceptionMessage);
			}

			char newCharacter = ' ';
			if (argumentThree.length() == 1) {
				newCharacter = argumentThree.charAt(0);
			} else {
				throw new FunctionException(exceptionMessage);
			}

			result = argumentOne.replace(oldCharacter, newCharacter);
		} catch (FunctionException fe) {
			throw new FunctionException(fe.getMessage(), fe);
		} catch (Exception e) {
			throw new FunctionException(exceptionMessage, e);
		}

		return new FunctionResult(result, 
				FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
	}
}