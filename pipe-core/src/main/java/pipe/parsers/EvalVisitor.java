package pipe.parsers;

import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.place.Place;
import pipe.models.petrinet.PetriNet;

public class EvalVisitor extends RateGrammarBaseVisitor<Double> {

    private PetriNet petriNet = new PetriNet();

    /**
     * Constructor for evaluating expressions that contain petri
     * net components, i.e places
     */
    public EvalVisitor(PetriNet petriNet) {

        this.petriNet = petriNet;
    }

    /**
     * Constructor for evaluating expressions that contain no places
     */
    public EvalVisitor() {

    }

    @Override
    public Double visitMultOrDiv(RateGrammarParser.MultOrDivContext ctx) {
        Double left = visit(ctx.expression(0));
        Double right = visit(ctx.expression(1));
        return (ctx.op.getType() == RateGrammarParser.MUL) ? left * right : left / right;
    }

    @Override
    public Double visitAddOrSubtract(RateGrammarParser.AddOrSubtractContext ctx) {
        Double left = visit(ctx.expression(0));
        Double right = visit(ctx.expression(1));
        return (ctx.op.getType() == RateGrammarParser.ADD) ? left + right : left - right;
    }

    @Override
    public Double visitParenExpression(RateGrammarParser.ParenExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Double visitToken_number(RateGrammarParser.Token_numberContext ctx) {
        try {
            Place place = getPlace(ctx.ID().getText());
            return (double) place.getNumberOfTokensStored();
        } catch (PetriNetComponentNotFoundException ignored) {
            return 0.0;
        }
    }

    //TODO: HANDLE COLOURS
    @Override
    public Double visitCapacity(RateGrammarParser.CapacityContext ctx) {
        try {
            Place place = getPlace(ctx.ID().getText());
            return (double) place.getCapacity();
        } catch (PetriNetComponentNotFoundException ignored) {
            return 0.0;
        }
    }

    @Override
    public Double visitInteger(RateGrammarParser.IntegerContext ctx) {
        return Double.valueOf(ctx.INT().getText());
    }

    @Override
    public Double visitDouble(RateGrammarParser.DoubleContext ctx) {
        return Double.valueOf(ctx.DOUBLE().getText());
    }

    public Place getPlace(String name) throws PetriNetComponentNotFoundException {
        return petriNet.getPlace(name);
    }

}
