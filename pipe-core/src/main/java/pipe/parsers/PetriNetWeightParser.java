package pipe.parsers;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.petrinet.PetriNet;

import java.util.*;

public class PetriNetWeightParser extends AbstractFunctionalWeightParser<Double> {



    private final PetriNet petriNet;

    /**
     * Parses Transition Rates to determine their value and
     * the components they reference.
     *
     * @param expression functional transition rate expression
     */
    public PetriNetWeightParser(PetriNet petriNet, String expression) {
        super(expression);
        this.petriNet = petriNet;

        if (!allComponentsInPetriNet()) {
            errors.add("Not all referenced components exist in the Petri net!");
        }
    }


    //TODO: Use memoization
    public Set<String> getReferencedComponents() {
        ParseTreeWalker walker = new ParseTreeWalker();
        ComponentListener listener = new ComponentListener();
        walker.walk(listener, parseTree);
        return listener.getComponentIds();
    }



    /**
     *
     * @return true if all referenced components in expression
     * are valid in the Petri net
     */
    private boolean allComponentsInPetriNet() {
        Set<String> placeComponents = getReferencedComponents();
        for (String id : placeComponents) {
            try {
                petriNet.getPlace(id);
            } catch (PetriNetComponentNotFoundException ignored) {
                return false;
            }
        }
        return true;
    }


    @Override
    public Double evaluateExpression() throws UnparsableException {
        return getValue(new EvalVisitor(petriNet));
    }

    /**
     * Listener that registers the id's of Petri net components the parse tree
     * references whilst walking the tree
     */
    static class ComponentListener extends RateGrammarBaseListener {

        Set<String> componentIds = new HashSet<>();

        @Override
        public void exitToken_number(
                @NotNull
                RateGrammarParser.Token_numberContext ctx) {
            componentIds.add(ctx.ID().getText());
        }

        public Set<String> getComponentIds() {
            return componentIds;
        }
    }

    /**
     * Listener that registers error in string format to be reported
     * back to the user whilst parsing the tree
     */
    class RateGrammarErrorListener extends BaseErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                                String msg, RecognitionException e) {
            List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
            Collections.reverse(stack);
            errors.add(String.format("line %d:%d %s", line, charPositionInLine, msg));
        }
    }


}
