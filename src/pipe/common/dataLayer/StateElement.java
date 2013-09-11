package pipe.common.dataLayer;

import java.io.Serializable;


/**
 * State Element holds the data relating to the state condition
 * of an individual place within a state
 * e.g. P0 > 2 --> placeA = "P0", operator = ">", placeB = "2"
 *
 * @author Barry Kearns
 * @date August 2007
 */
public class StateElement 
        implements Serializable {
   
   private static final long serialVersionUID = 1L;
   
   private String placeA, operator, placeB;
   
   
   public StateElement(String placeA, String operator, String placeB) {
      this.placeA = placeA;
      this.operator = operator;
      this.placeB = placeB;
   }
   
   
   public String getPlaceA() {
      return placeA;
   }
   
   
   public String getOperator() {
      return operator;
   }
   
   
   public String getPlaceB() {
      return placeB;
   }
   
}
