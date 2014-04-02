package pipe.parsers;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Useful utilities for handling PetriNet functional weight grammar
 */
public class GrammarUtils {

    private GrammarUtils() {}

    /**
     *
     * Parses an expression
     *
     * @param expression string to parse
     * @return ParseTree
     */
    public static ParseTree parse(String expression) {
        CharStream input = new ANTLRInputStream(expression);
        RateGrammarLexer lexer = new RateGrammarLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        RateGrammarParser parser = new RateGrammarParser(tokens);
        parser.removeErrorListeners();

        ANTLRErrorListener errorListener = new RateGrammarErrorListener();

        parser.addErrorListener(errorListener);
        return parser.program();
    }
}
