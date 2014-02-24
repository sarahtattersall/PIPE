package pipe.parsers;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TransitionRateCompiler {

    public static void main(String[] args) {
        CharStream input = new ANTLRInputStream("5 * #(P0) + 2 * #(P1)");
        RateGrammarLexer lexer = new RateGrammarLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        RateGrammarParser parser = new RateGrammarParser(tokens);
        ParseTree parseTree = parser.program();
        System.out.println(parseTree.toStringTree(parser)); // print LISP-style tree

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new ComponentListener(), parseTree);

    }
}
