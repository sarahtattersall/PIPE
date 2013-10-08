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
import net.sourceforge.jeval.operator.Operator;

/**
 * This class represents a function that has been parsed.
 */
public class ParsedFunction {

	// The function that has been parsed.
	// FIXME Make all class instance methods final if possible.
	private final Function function;

	// The arguments to the function.
	private final String arguments;

	// The unary operator for this object, if there is one.
	private final Operator unaryOperator;

	/**
	 * The constructor for this class.
	 * 
	 * @param function
	 *            The function that has been parsed.
	 * @param arguments
	 *            The arguments to the function.
	 * @param unaryOperator
	 *            The unary operator for this object, if there is one.
	 */
	public ParsedFunction(final Function function, final String arguments,
			final Operator unaryOperator) {

		this.function = function;
		this.arguments = arguments;
		this.unaryOperator = unaryOperator;
	}

	/**
	 * Returns the function that has been parsed.
	 * 
	 * @return The function that has been parsed.
	 */
	public Function getFunction() {
		return function;
	}

	/**
	 * Returns the arguments to the function.
	 * 
	 * @return The arguments to the function.
	 */
	public String getArguments() {
		return arguments;
	}

	/**
	 * Returns the unary operator for this object, if there is one.
	 * 
	 * @return The unary operator for this object, if there is one.
	 */
	public Operator getUnaryOperator() {
		return unaryOperator;
	}
}