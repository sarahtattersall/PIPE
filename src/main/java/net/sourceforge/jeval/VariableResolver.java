package net.sourceforge.jeval;

import net.sourceforge.jeval.function.FunctionException;

/**
 * This interface can be implement with a custom resolver and set onto the
 * Evaluator class. It will then be used to resolve variables when they are
 * replaced in an expression as it gets evaluated. Varaibles resolved by the
 * resolved will override any varibles that exist in the variable map of an
 * Evaluator instance.
 */
public interface VariableResolver {

    /**
     * Returns a variable value for the specified variable name.
     *
     * @param variableName
     *            The name of the variable to return the variable value for.
     *
     * @return A variable value for the specified variable name. If the variable
     *         name can not be resolved, then null should be returned.
     *         
     * @throws Can throw a FunctionException if needed.
     */
    public String resolveVariable(String variableName) throws FunctionException;
}