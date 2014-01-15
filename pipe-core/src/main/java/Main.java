import pipe.models.PetriNet;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.models.component.Transition;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.models.strategy.arc.ForwardsNormalStrategy;
import pipe.models.strategy.arc.InhibitorStrategy;
import pipe.petrinet.adapters.modelAdapter.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String PETRINET = "/Users/st809/Documents/jaxb.xml";

    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        PetriNet net = new PetriNet();
        Token token = new Token("Default", true, 0, new Color(255,0,0));
        net.addToken(token);
        Transition transition = new Transition("T0", "T0");
        Place place = new Place("P0", "P0");
        net.addPlace(place);
        net.addTransition(transition);

        Map<Token, String> weights = new HashMap<Token, String>();
        weights.put(token, "5");
        InhibitorStrategy inhibitorStrategy = new InhibitorStrategy();
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, inhibitorStrategy);
        net.addArc(arc);


        JAXBContext context = JAXBContext.newInstance(PetriNet.class);
        Map<String, Place> places = new HashMap<String, Place>();
        Map<String, Transition> transitions = new HashMap<String, Transition>();
        Map<String, Token> tokens = new HashMap<String, Token>();

        ArcStrategy<Transition, Place> normalForwardStrategy = new ForwardsNormalStrategy();
        ArcStrategy<Place, Transition> backwardsStrategy = new BackwardsNormalStrategy();


        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


        m.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        m.setAdapter(new PlaceAdapter(places));
        m.setAdapter(new TransitionAdapter(transitions));
        m.setAdapter(new TokenAdapter(tokens));
        m.setAdapter(new TokenSetIntegerAdapter(tokens));

        // Write to System.out
        m.marshal(net, System.out);
        m.marshal(net, new File(PETRINET));

        // get variables from our xml file, created before
        System.out.println();
        System.out.println("Output from our XML File: ");
        Unmarshaller um = context.createUnmarshaller();
        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        um.setAdapter(new PlaceAdapter(places));
        um.setAdapter(new TransitionAdapter(transitions));
        um.setAdapter(new TokenAdapter(tokens));
        um.setAdapter(new TokenSetIntegerAdapter(tokens));
        PetriNet bookstore2 = (PetriNet) um.unmarshal(new FileReader(PETRINET));
        for (Place p : bookstore2.getPlaces()) {
            System.out.println(p);
        }
        for (Arc arc1 : bookstore2.getArcs()) {
            System.out.println(arc);
        }
    }
}
