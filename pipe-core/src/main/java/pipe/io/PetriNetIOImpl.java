package pipe.io;

import pipe.io.adapters.modelAdapter.*;
import pipe.models.PetriNetHolder;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import javax.xml.bind.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class PetriNetIOImpl implements PetriNetIO {

    private final JAXBContext context;

    public PetriNetIOImpl() throws JAXBException {
        context = JAXBContext.newInstance(PetriNetHolder.class);
    }

    @Override
    public void writeTo(String path, PetriNet petriNet) {
        try {
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            PetriNetHolder holder = new PetriNetHolder();
            holder.addNet(petriNet);
            m.marshal(holder, new File(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeTo(Writer stream, PetriNet petriNet) {
        try {
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            PetriNetHolder holder = new PetriNetHolder();
            holder.addNet(petriNet);
            m.marshal(holder, stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PetriNet read(String path) {

        try {
            Unmarshaller um = initialiseUnmarshaller();
            PetriNetHolder holder = (PetriNetHolder) um.unmarshal(new FileReader(path));
            PetriNet petriNet = holder.getNet(0);

            if (petriNet.getTokens().size() == 0) {
                Token token = createDefaultToken();
                petriNet.addToken(token);
            }


            return petriNet;

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //TODO: THROW EXCEPTIONS?
        return null;
    }

    private Token createDefaultToken() {
        return new Token("Default", true, 0, new Color(0, 0, 0));
    }

    private Unmarshaller initialiseUnmarshaller() throws JAXBException {

        Unmarshaller um = context.createUnmarshaller();

        Map<String, Place> places = new HashMap<>();
        Map<String, Transition> transitions = new HashMap<>();
        Map<String, Token> tokens = new HashMap<>();
        Map<String, RateParameter> rateParameters = new HashMap<>();

        um.setAdapter(new RateParameterAdapter(rateParameters));
        um.setAdapter(new ArcAdapter(places, transitions, tokens));
        um.setAdapter(new PlaceAdapter(places, tokens));
        um.setAdapter(new TransitionAdapter(transitions, rateParameters));
        um.setAdapter(new TokenAdapter(tokens));
        um.setAdapter(new TokenSetIntegerAdapter(tokens));
        return um;
    }

}
