package pipe.models.interfaces;

import pipe.io.NewStateRecord;

/**
 * Interface used to access an implementation that is dynamically created from
 * input by the user at run-time.
 * 
 * @author Oliver Haggarty August 2007
 * 
 */
public interface IDynamicMarking
{
	/**
	 * Return true if marking is classified as a Start State according to the
	 * logical expression created at run-time in the implementation
	 * 
	 * @param marking
	 * @return
	 */
	public boolean isStartMarking(NewStateRecord marking);

	/**
	 * Return true if marking is classified as a Target State according to the
	 * logical expression created at run-time in the implementation
	 * 
	 * @param marking
	 * @return
	 */
	public boolean isTargetMarking(NewStateRecord marking);
}
