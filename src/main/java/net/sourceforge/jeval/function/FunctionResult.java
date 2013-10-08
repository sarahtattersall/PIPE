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

package net.sourceforge.jeval.function;

/**
 * This is a wrapper for the result value returned from a function that not only
 * contains the result, but the type. All custom functions must return a
 * FunctionResult.
 */
public class FunctionResult {

	// The value returned from a function call.
	private String result;

	// The type of the result. Can be a numberic or string. Boolean values come
	// back as numeric values.
	private int type;

	/**
	 * Constructor.
	 * 
	 * @param result
	 *            The result value.
	 * @param type
	 *            The result type.
	 * 
	 * @throws FunctionException
	 *             Thrown if result type is invalid.
	 */
	public FunctionResult(String result, int type) throws FunctionException {

		if (type < FunctionConstants.FUNCTION_RESULT_TYPE_NUMERIC
				|| type > FunctionConstants.FUNCTION_RESULT_TYPE_STRING) {

			throw new FunctionException("Invalid function result type.");
		}

		this.result = result;
		this.type = type;
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
	 * Returns the result type.
	 * 
	 * @return The result type.
	 */
	public int getType() {
		return type;
	}
}
