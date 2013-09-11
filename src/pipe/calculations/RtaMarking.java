/*
 * Created on Mar 2, 2004
 *
 */
package pipe.calculations;

/**
 * @author Matthew Worthington/Edwin Chung added a new attribute
 * to include the information whether the state is tangible or
 * vanishing. Appropriate constructors and methods are added/modified
 * to check the equivalance of two markings.
 */

/**
 * 
 * @author Nadeem
 *
 * This class modified to make use of the new State class
 * 01/07/2005
 * 
 * 
 */
public class RtaMarking extends State{
	private int idnum;
	private static boolean isTangible;
	
	public RtaMarking(State markingInput, int idInput){
		super(markingInput);
		idnum = idInput;
	}
	
	public RtaMarking(State markingInput, int idInput, boolean Tangible){
		super(markingInput);
		idnum = idInput;
		isTangible = Tangible;
	}
	
	public RtaMarking(int[] markingInput, int idInput){
		super(markingInput);
		idnum = idInput;
	}
	
	public RtaMarking (int[] markingInput, String idInput) {
		super(markingInput);
	}
	public int[] getMarking(){
		return getState();
	}
	
	public String getID(){
		return "M" + idnum;
	}
	
	public int getIDNum(){
		return idnum;
	}
	
	public boolean getisTangible(){
		return isTangible;
	}
	public boolean equals (RtaMarking m1){

        return this.equals((State) m1) && isTangible == isTangible;
		
	}
}
