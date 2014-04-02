package pipe.parsers;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.junit.Test;
import pipe.dsl.APetriNet;
import pipe.dsl.APlace;
import pipe.dsl.AToken;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.petrinet.PetriNet;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class EvalVisitorTest {
    private static final PetriNet EMPTY_PETRI_NET = new PetriNet();

    public ParseTree parseTreeForExpr(String expr) {
        CharStream input = new ANTLRInputStream(expr);
        RateGrammarLexer lexer = new RateGrammarLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        RateGrammarParser parser = new RateGrammarParser(tokens);
        return parser.program();
    }

    @Test
    public void parsesBasicInt() {
        ParseTree tree = parseTreeForExpr("2");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(2.0), result);
    }

    @Test
    public void parsesBasicIntegerAddition() {
        ParseTree tree = parseTreeForExpr("2 + 8");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(10.0), result);
    }


    @Test
    public void parsesBasicDoubleAddition() {
        ParseTree tree = parseTreeForExpr("2.5 + 8.3");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(10.8), result);
    }

    @Test
    public void parsesBasicIntegerSubtraction() {
        ParseTree tree = parseTreeForExpr("5 - 1");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(4.0), result);
    }

    @Test
    public void parsesBasicDoubleSubraction() {
        ParseTree tree = parseTreeForExpr("2.5 - 0.2");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(2.3), result);
    }


    @Test
    public void parsesBasicIntegerMultiplication() {
        ParseTree tree = parseTreeForExpr("5 * 2");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(10.0), result);
    }

    @Test
    public void parsesBasicDoubleMultiplication() {
        ParseTree tree = parseTreeForExpr("2.5 * 4");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(10.0), result);
    }


    @Test
    public void parsesBasicIntegerDivision() {
        ParseTree tree = parseTreeForExpr("5 / 2");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(2.5), result);
    }

    @Test
    public void parsesBasicDoubleDivision() {
        ParseTree tree = parseTreeForExpr("5 / 2.5");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(2), result);
    }


    @Test
    public void parsesBasicParentheses() {
        ParseTree tree = parseTreeForExpr("(2)");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(2), result);
    }

    @Test
    public void parsesAdditionParentheses() {
        ParseTree tree = parseTreeForExpr("(2 + 3) * 5");
        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(EMPTY_PETRI_NET);
        Double result = evalVisitor.visit(tree);

        assertEquals(new Double(25), result);
    }

    @Test
    public void parsesPlaceTokenNumber() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(
                APlace.withId("P0").and(4, "Default").tokens());

        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(petriNet);
        ParseTree parseTree = parseTreeForExpr("#(P0)");
        Double result = evalVisitor.visit(parseTree);

        assertEquals(new Double(4.0), result);

    }

    @Test
    public void parsesPlaceColorTokenNumber() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                AToken.called("Red").withColor(Color.RED)).andFinally(
                APlace.withId("P0").and(4, "Default").tokens().and(6, "Red").tokens());

        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(petriNet);
        ParseTree parseTree = parseTreeForExpr("#(P0, Red)");
        Double result = evalVisitor.visit(parseTree);

        assertEquals(new Double(6.0), result);
    }

    @Test
    public void parsesPlaceTokenNumberAsZeroIfDoesNotExist() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(
                APlace.withId("P0").and(4, "Default").tokens());

        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(petriNet);
        ParseTree parseTree = parseTreeForExpr("#(P1)");
        Double result = evalVisitor.visit(parseTree);

        assertEquals(new Double(0.0), result);
    }


    @Test
    public void parsesPlaceCapacity() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(
                APlace.withId("P0").andCapacity(10));

        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(petriNet);
        ParseTree parseTree = parseTreeForExpr("cap(P0)");
        Double result = evalVisitor.visit(parseTree);

        assertEquals(new Double(10.0), result);

    }

    @Test
    public void parsesPlaceCapacityAsZeroIfDoesNotExist() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(
                APlace.withId("P0").andCapacity(10));

        ParseTreeVisitor<Double> evalVisitor = new EvalVisitor(petriNet);
        ParseTree parseTree = parseTreeForExpr("cap(P1)");
        Double result = evalVisitor.visit(parseTree);

        assertEquals(new Double(0.0), result);
    }

}
