package pipe.io;

import pipe.models.PetriNet;
import pipe.models.component.PetriNetHolder;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.models.component.Transition;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.models.strategy.arc.ForwardsNormalStrategy;
import pipe.models.strategy.arc.InhibitorStrategy;
import pipe.io.adapters.modelAdapter.*;

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
    public PetriNet read(String path) {

        InhibitorStrategy inhibitorStrategy = new InhibitorStrategy();
        ForwardsNormalStrategy normalForwardStrategy = new ForwardsNormalStrategy();
        BackwardsNormalStrategy backwardsStrategy = new BackwardsNormalStrategy();
        try {
            Unmarshaller um = initialiseUnmarshaller(inhibitorStrategy, normalForwardStrategy, backwardsStrategy);
            PetriNetHolder holder = (PetriNetHolder) um.unmarshal(new FileReader(path));
            PetriNet petriNet =  holder.getNet(0);
            normalForwardStrategy.setPetriNet(petriNet);
            backwardsStrategy.setPetriNet(petriNet);

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
        return new Token("Default", true, 0, new Color(0,0,0));
    }

    private Unmarshaller initialiseUnmarshaller(ArcStrategy<Place, Transition> inhibitorStrategy,
                                                ArcStrategy<Transition, Place> normalForwardStrategy,
                                                ArcStrategy<Place, Transition> backwardsStrategy) throws JAXBException {

        Unmarshaller um = context.createUnmarshaller();

        Map<String, Place> places = new HashMap<String, Place>();
        Map<String, Transition> transitions = new HashMap<String, Transition>();
        Map<String, Token> tokens = new HashMap<String, Token>();

        um.setAdapter(new ArcAdapter(places, transitions, tokens, inhibitorStrategy, normalForwardStrategy, backwardsStrategy));
        um.setAdapter(new PlaceAdapter(places, tokens));
        um.setAdapter(new TransitionAdapter(transitions));
        um.setAdapter(new TokenAdapter(tokens));
        um.setAdapter(new TokenSetIntegerAdapter(tokens));
        return um;
    }

}
