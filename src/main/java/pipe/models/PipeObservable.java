package pipe.models;

import java.io.Serializable;
import java.util.Observable;

/**
 * A general sub-class of Observable, created for convenience in implementing the observer pattern in classes that already have an inheritance hierarchy, and that wish to use update(Object obj).  See {@link MarkingView} for an example of usage. 
 * <p>
 * The class wishing to be observed creates an instance of PipeObservable, passing itself in the constructor, and delegates the Observable methods to the PipeObservable
 * @author stevedoubleday
 */
public class PipeObservable extends Observable implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Object observable;

	public PipeObservable(Object observable)
	{
		super(); 
		this.observable = observable; 
	}
	/**
	 * setChanged() must be invoked prior to calling notifyObservers().  
	 */
	// This is a protected method in Observable, hence the need for this class. 
	public void setChanged()
	{
		super.setChanged();
	}
	public Object getObservable()
	{
		return observable; 
	}
	/**
	 * Sets the observable.  Required for serialization.
	 * @param observable
	 */
	public void setObservable(Object observable)
	{
		this.observable = observable;
	}
}
