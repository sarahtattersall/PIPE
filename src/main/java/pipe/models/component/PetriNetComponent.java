package pipe.models.component;

import pipe.models.interfaces.IObservable;
import pipe.models.visitor.PetriNetComponentVisitor;

/**
 * This class extends the IObservable interface since java.utils.Observable is
 * messy. See SO:
 * http://stackoverflow.com/questions/7281469/why-is-java-util-observable-not-an-abstract-class
 */
public interface PetriNetComponent extends IObservable {

    public boolean isSelectable();
    public boolean isDraggable();

    /**
     * Visitor pattern
     * @param visitor
     */
    public void accept(PetriNetComponentVisitor visitor);


    /**
     * @return objectId
     */
    String getId();

    void setId(String id);
    void setName(String name);

}
