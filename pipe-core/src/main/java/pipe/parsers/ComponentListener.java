package pipe.parsers;

import org.antlr.v4.runtime.misc.NotNull;

public class ComponentListener extends RateGrammarBaseListener {
    @Override public void exitToken_number(@NotNull RateGrammarParser.Token_numberContext ctx) {
        System.out.println(ctx.ID().getText());

    }
}
