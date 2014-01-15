import pipe.models.PetriNet;
import pipe.models.component.*;
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
    private static final String PROTOCOL = "/Users/st809/Documents/Imperial/PIPE/pipe-gui/src/main/resources/extras/examples/Courier Protocol.xml";

    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        Map<String, Place> places = new HashMap<String, Place>();
        Map<String, Transition> transitions = new HashMap<String, Transition>();
        Map<String, Token> tokens = new HashMap<String, Token>();

        ArcStrategy<Transition, Place> normalForwardStrategy = new ForwardsNormalStrategy();
        ArcStrategy<Place, Transition> backwardsStrategy = new BackwardsNormalStrategy();
        InhibitorStrategy inhibitorStrategy = new InhibitorStrategy();
        JAXBContext context = JAXBContext.newInstance(PetriNetHolder.class);

//        PetriNet net = new PetriNet();
//        Token token = new Token("Default", true, 0, new Color(255,0,0));
//        net.addToken(token);
//        Transition transition = new Transition("T0", "T0");
//        Place place = new Place("P0", "P0");
//        place.setTokenCount(token, 2);
//        net.addPlace(place);
//        net.addTransition(transition);
//
//        Map<Token, String> weights = new HashMap<Token, String>();
//        weights.put(token, "5");
//        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, inhibitorStrategy);
//        net.addArc(arc);
//
//
//
//
//
//        Marshaller m = context.createMarshaller();
//        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//
//
//        m.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
//        m.setAdapter(new PlaceAdapter(places, tokens));
//        m.setAdapter(new TransitionAdapter(transitions));
//        m.setAdapter(new TokenAdapter(tokens));
//        m.setAdapter(new TokenSetIntegerAdapter(tokens));
//
//        // Write to System.out
//        PetriNetHolder holder = new PetriNetHolder();
//        holder.addNet(net);
//        m.marshal(holder, System.out);
//        m.marshal(holder, new File(PETRINET));

        // get variables from our xml file, created before
        System.out.println();
        System.out.println("Output from our XML File: ");
        Unmarshaller um = context.createUnmarshaller();
        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        um.setAdapter(new PlaceAdapter(places, tokens));
        um.setAdapter(new TransitionAdapter(transitions));
        um.setAdapter(new TokenAdapter(tokens));
        PetriNetHolder holder = (PetriNetHolder) um.unmarshal(new FileReader(PROTOCOL));
        PetriNet petriNet = holder.getNet(0);
        for (Place p : petriNet.getPlaces()) {
            System.out.println(p);
        }
        for (Arc arc1 : petriNet.getArcs()) {
            System.out.println(arc1);
        }
    }
}
