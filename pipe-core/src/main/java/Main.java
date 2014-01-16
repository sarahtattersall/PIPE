import pipe.io.PetriNetIOImpl;
import pipe.io.PetriNetReader;
import pipe.models.PetriNetHolder;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String PETRINET = "/Users/st809/Documents/jaxb.xml";

    private static final String PROTOCOL =
            "/Users/st809/Documents/Imperial/PIPE/pipe-gui/src/main/resources/extras/examples/Simple Coloured Net.xml";

    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        Map<String, Place> places = new HashMap<String, Place>();
        Map<String, Transition> transitions = new HashMap<String, Transition>();
        Map<String, Token> tokens = new HashMap<String, Token>();
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
        //
        // get variables from our xml file, created before
        //        System.out.println();
        //        System.out.println("Output from our XML File: ");
        //        Unmarshaller um = context.createUnmarshaller();
        //        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        //        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy,  normalForwardStrategy, backwardsStrategy));
        //        um.setAdapter(new PlaceAdapter(places, tokens));
        //        um.setAdapter(new TransitionAdapter(transitions));
        //        um.setAdapter(new TokenAdapter(tokens));
        //        PetriNetHolder holder = (PetriNetHolder) um.unmarshal(new FileReader(PROTOCOL));

        PetriNetReader reader = new PetriNetIOImpl();
        PetriNet petriNet = reader.read(PROTOCOL);
        for (Place p : petriNet.getPlaces()) {
            System.out.println(p);
        }
        for (Arc arc1 : petriNet.getArcs()) {
            System.out.println(arc1);
        }
    }
}
