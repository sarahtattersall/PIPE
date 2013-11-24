package pipe.models;

import pipe.models.visitor.PetriNetComponentVisitor;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 18/10/2013
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public interface PetriNetComponent {

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
