package pipe.common.dataLayer;

import pipe.views.PetriNetView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/*
 * State is the collection of conditions that constitute a source / destination
 * state for passage time analysis.
 *
 */
public class StateGroup 
        implements Serializable {
   
   private static final long serialVersionUID = 1L;
   
   private HashMap<String, StateElement> elements;
   private String id, name;
   
   
   public StateGroup() {
      id = "";
      name = "";
      elements = new HashMap<String, StateElement>();
   }
   
   
   public StateGroup(String id, String title) {
      this.id = id;
      this.name = title;
      elements = new HashMap<String, StateElement>();
   }

   // Constructor added for StateGroupEditor in Performance Query
	public StateGroup(PetriNetView pnml) {

		this.id = "";
		this.name = "";
		this.elements = new HashMap<String, StateElement>();
	}
   
   
   // We map the id of the place to the condition associated with that place
   public void addState(String placeId, String operator, String target) {
      elements.put(placeId, new StateElement(placeId, operator, target) );
   }
   
   
   /**
    * This method adds a State Element to the current state group.
    * If an element with an empty condition is passed, this has the effect of
    * removing any existing condition on that place.
    * @param newState The new State Element being added to the group
    */
   public void addState(StateElement newState) {
      if (newState.getOperator() == "" || newState.getPlaceB() == "") {
         if (newState.getPlaceA() != "") {
            elements.remove(newState.getPlaceA());
         }
      } else {
         elements.put(newState.getPlaceA(), newState);
      }
   }
   
   
   public void removeState(String id) {
      elements.remove(id);
   }
   
   
   public void setId(String id) {
      this.id = id;
   }
   
   
   public void setName(String title) {
      this.name = title;
   }
   
   
   public String getId() {
      return id;
   }
   
   
   public String getName() {
      return name;
   }
   
   
   public StateElement getCondition(String id) {
      return elements.get(id);
   }
   
   
   public int numElements() {
      return elements.size();
   }
   
   
   public String[] getConditions() {
      int i = 0;
      String[] conditions = new String[elements.size()];
      Iterator stateElementIter = elements.values().iterator();
      
      while (stateElementIter.hasNext()) {
         StateElement currElement = (StateElement)stateElementIter.next();
         conditions[i] = currElement.getPlaceA() + " " + 
                 currElement.getOperator() + " " + currElement.getPlaceB();
         i++;
      }
      
      return conditions;
   }
   
}
