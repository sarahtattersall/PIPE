package pipe.parsers;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ComponentListener extends RateGrammarBaseListener {

    Set<String> componentIds = new HashSet<>();

    @Override public void exitToken_number(@NotNull RateGrammarParser.Token_numberContext ctx) {
        componentIds.add(ctx.ID().getText());
//        System.out.println(ctx.ID().getText());
    }

    public Set<String> getComponentIds() {
        return componentIds;
    }
}
