package pipe.parsers;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.petrinet.PetriNet;

import java.util.*;

public class PetriNetWeightParser implements FunctionalWeightParser<Double> {


    /**
     * Petri net to parse results aginst
     */
    private final PetriNet petriNet;

    /**
     * Evaluator for the PetriNet and functional expression
     */
    private final EvalVisitor evalVisitor;

    /**
     * Parses Transition Rates to determine their value and
     * the components they reference.
     *
     */
    public PetriNetWeightParser(PetriNet petriNet) {
        evalVisitor = new EvalVisitor(petriNet);
        this.petriNet = petriNet;
    }


    //TODO: Use memoization
    private Set<String> getReferencedComponents(ParseTree parseTree) {
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
    private boolean allComponentsInPetriNet(Set<String> components) {
        for (String id : components) {
            try {
                petriNet.getPlace(id);
            } catch (PetriNetComponentNotFoundException ignored) {
                return false;
            }
        }
        return true;
    }


    @Override
    public FunctionalResults<Double> evaluateExpression(String expression) {

        RateGrammarErrorListener errorListener = new RateGrammarErrorListener();
        ParseTree parseTree = GrammarUtils.parse(expression, errorListener);

        List<String> errors = new LinkedList<>();
        if (errorListener.hasErrors()) {
            errors.addAll(errorListener.getErrors());
        }

        Set<String> components = getReferencedComponents(parseTree);
        if (!allComponentsInPetriNet(components)) {
            errors.add("Not all referenced components exist in the Petri net!");
        }

        if (!errors.isEmpty()) {
            return new FunctionalResults<>(-1., errors, components);
        }

        Double result = evalVisitor.visit(parseTree);
        if (result < 0) {
            errors.add("Expression result cannot be less than zero!");
            return new FunctionalResults<>(-1., errors, components);
        }

        return new FunctionalResults<>(evalVisitor.visit(parseTree), components);
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
}
