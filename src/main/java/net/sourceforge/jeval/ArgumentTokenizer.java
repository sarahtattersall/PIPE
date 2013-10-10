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

import java.util.Enumeration;

/**
 * This class allow for tokenizer methods to be called on a String of arguments.
 */
public class ArgumentTokenizer implements Enumeration {

	/**
	 * The default delimitor.
	 */
	public final char defaultDelimiter = 
		EvaluationConstants.FUNCTION_ARGUMENT_SEPARATOR;

	// The arguments to be tokenized. This is updated every time the nextToken
	// method is called.
	private String arguments = null;

	// The separator between the arguments.
	private char delimiter = defaultDelimiter;

	/**
	 * Constructor that takes a String of arguments and a delimitoer.
	 * 
	 * @param arguments
	 *            The String of srguments to be tokenized.
	 * @param delimiter
	 *            The argument tokenizer.
	 */
	public ArgumentTokenizer(final String arguments, final char delimiter) {
		this.arguments = arguments;
		this.delimiter = delimiter;
	}

	/**
	 * Indicates if there are more elements.
	 * 
	 * @return True if there are more elements and false if not.
	 */
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	/**
	 * Indicates if there are more tokens.
	 * 
	 * @return True if there are more tokens and false if not.
	 */
	public boolean hasMoreTokens() {

		if (arguments.length() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the next element.
	 * 
	 * @return The next element.
	 */
	public Object nextElement() {
		return nextToken();
	}

	/**
	 * Returns the next token.
	 * 
	 * @return The next element.
	 */
	public String nextToken() {
		int charCtr = 0;
		int size = arguments.length();
		int parenthesesCtr = 0;
		String returnArgument = null;

		// Loop until we hit the end of the arguments String.
		while (charCtr < size) {
			if (arguments.charAt(charCtr) == '(') {
				parenthesesCtr++;
			} else if (arguments.charAt(charCtr) == ')') {
				parenthesesCtr--;
			} else if (arguments.charAt(charCtr) == delimiter
					&& parenthesesCtr == 0) {

				returnArgument = arguments.substring(0, charCtr);
				arguments = arguments.substring(charCtr + 1);
				break;
			}

			charCtr++;
		}

		if (returnArgument == null) {
			returnArgument = arguments;
			arguments = "";
		}

		return returnArgument;
	}
}
