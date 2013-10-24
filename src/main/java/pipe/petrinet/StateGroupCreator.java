package pipe.petrinet;

import org.w3c.dom.Element;
import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;

import java.util.StringTokenizer;

public class StateGroupCreator implements ComponentCreator<StateGroup> {
    public StateGroup create(Element element) {
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
        StateGroup stateGroup = new StateGroup(id, name);

        String condition = element.getAttribute("statecondition");
        StateElement stateElement = tokenizeCondition(condition);
        stateGroup.addState(stateElement);

        return stateGroup;
    }

    private StateElement tokenizeCondition(String condition)
    {
        StringTokenizer tokenizer = new StringTokenizer(condition);
        String left = tokenizer.nextToken();
        String operator = tokenizer.nextToken();
        String right = tokenizer.nextToken();
        StateElement element = new StateElement(left, operator, right);
        return element;
    }
}
