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

/**
 * This class can be used to wrap the result of an expression evaluation. It
 * contains useful methods for evaluating the contents of the result.
 */
public class EvaluationResult {

	// The value returned from the evaluation of an expression.
	private String result;

	// The quote character specified in the evaluation of the expression.
	private char quoteCharacter;

	/**
	 * Constructor.
	 * 
	 * @param result
	 *            The value returned from the evaluation of an expression.
	 * @param quoteCharacter
	 *            The quote character specified in the evaluation of the
	 *            expression.
	 */
	public EvaluationResult(String result, char quoteCharacter) {

		this.result = result;
		this.quoteCharacter = quoteCharacter;
	}

	/**
	 * Returns the quote character.
	 * 
	 * @return The quote character.
	 */
	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	/**
	 * Sets the quote character.
	 * 
	 * @param quoteCharacter
	 *            The quoteCharacter to set.
	 */
	public void setQuoteCharacter(char quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}

	/**
	 * Returns the result value.
	 * 
	 * @return The result value.
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Sets the result value.
	 * 
	 * @param result
	 *            The result to set.
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Returns true if the result value is equal to the value of a Boolean true
	 * string (1.0).
	 * 
	 * @return True if the result value is equal to the value of a Boolean true
	 *         string (1.0).
	 */
	public boolean isBooleanTrue() {

		if (result != null
				&& EvaluationConstants.BOOLEAN_STRING_TRUE.equals(result)) {

			return true;
		}

		return false;
	}

	/**
	 * Returns true if the result value is equal to the value of a Boolean false
	 * string (0.0).
	 * 
	 * @return True if the result value is equal to the value of a Boolean false
	 *         string (0.0).
	 */
	public boolean isBooleanFalse() {

		if (result != null
				&& EvaluationConstants.BOOLEAN_STRING_FALSE.equals(result)) {

			return true;
		}

		return false;
	}

	/**
	 * Returns true if the result value starts with a quote character and ends
	 * with a quote character.
	 * 
	 * @return True if the result value starts with a quote character and ends
	 *         with a quote character.
	 */
	public boolean isString() {

		if (result != null && result.length() >= 2) {

			if (result.charAt(0) == quoteCharacter
					&& result.charAt(result.length() - 1) == quoteCharacter) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Returns a Double for the result value.
	 * 
	 * @return A Double for the result value.
	 * 
	 * @throws NumberFormatException
	 *             Thrown if the result value is not a double.
	 */
	public Double getDouble() throws NumberFormatException {

		return new Double(result);
	}

	/**
	 * Returns the unwrapped string for the result value. An unwrapped string is
	 * a string value without the quote characters that wrap the result value.
	 * For a string to be returned, then the first character must be a quote
	 * character and the last character must be a quote character. Otherwise, a
	 * null value is returned.
	 * 
	 * @return The normal string for the result value. Null will be returned if
	 *         the result value is not of a string type.
	 */
	public String getUnwrappedString() {

		if (result != null && result.length() >= 2) {

			if (result.charAt(0) == quoteCharacter
					&& result.charAt(result.length() - 1) == quoteCharacter) {

				return result.substring(1, result.length() - 1);
			}
		}

		return null;
	}
}
