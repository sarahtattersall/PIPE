/*IMPORTANT NOTE: Do not remove any comments starting //#$# These are markings needed
 * when adding the logical expressions for deciding start and target states at runtime
 */
package pipe.models;

import pipe.io.NewStateRecord;
import pipe.models.interfaces.IDynamicMarking;
/**
 * Template class that is modified at run-time and saved as DynamicMarkingImpl.java, before
 * being compiled, loaded and used. User inputted logical if statements are added to the
 * code.
 * 
 * @author Oliver Haggarty - August 2007
 *
 */
public class DynamicMarking implements IDynamicMarking {
	//IMPORTANT NOTE: Do not remove any comments starting //#$#
	//These are required by the DynamicMarkingCompiler class to identify where to insert
	//additional code at runtime
	
	/**
	 * Identifies whether the marking is classified as a target state according to 
	 * the logical expression written at runtime
	 * @param marking Marking to be tested
	 * @return true if matches expression	 
	 */
	public boolean isTargetMarking(NewStateRecord marking) {
		int [] p = marking.getState();
		int id = marking.getID();
//#$#ADDTARGETEXPRESSIONHERE
			return true;
		else
			return false;
	}
	
	/**
	 * Identifies whether the marking is classified as a start state according to the 
	 * logical expression written at runtime.
	 * @param marking Marking to be tested
	 * @return true if matches expression
	 */
	public boolean isStartMarking(NewStateRecord marking) {
		int [] p = marking.getState();
		int id = marking.getID();
//#$#ADDSTARTEXPRESSIONHERE
			return true;
		else
			return false;
	}
}
