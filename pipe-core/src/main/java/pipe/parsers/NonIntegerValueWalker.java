package pipe.parsers;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class NonIntegerValueWalker {
    private final org.antlr.v4.runtime.tree.ParseTree parseTree;

    public NonIntegerValueWalker(String expression) {
        parseTree = GrammarUtils.parse(expression);
    }

    public boolean isPossiblyNotInteger() {
        ParseTreeWalker walker = new ParseTreeWalker();
        NonIntegerListener listener = new NonIntegerListener();
        walker.walk(listener, parseTree);
        return listener.possibleNonIntegerValue;
    }


    /**
     * Sees if any of the modifications could
     */
    private static class NonIntegerListener extends RateGrammarBaseListener {

        boolean possibleNonIntegerValue = false;

        @Override
        public void exitMultOrDiv(RateGrammarParser.MultOrDivContext ctx) {
            if (ctx.op.getType() == RateGrammarParser.DIV) {
                possibleNonIntegerValue = true;
            }
        }

        @Override
        public void exitDouble(RateGrammarParser.DoubleContext ctx) {
            possibleNonIntegerValue = true;
        }
    }
}

