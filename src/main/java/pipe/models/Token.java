package pipe.models;

import pipe.exceptions.TokenLockedException;
import pipe.gui.ApplicationSettings;
import pipe.models.interfaces.IObserver;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.utilities.math.Matrix;
import pipe.views.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Token extends Observable implements Serializable, PetriNetComponent
{
    private String id;
    private boolean enabled;
    private int currentMarking;
    private int lockCount = 0; // So that users cannot change this class while
    // places are marked with it

    private Color color;
    private Matrix incidenceMatrix;
    private Matrix forwardsIncidenceMatrix;
    private Matrix backwardsIncidenceMatrix;
    private Matrix inhibitionMatrix;

    public Token()
    {
        this("", false, 0, Color.BLACK);
    }

    public Token(String id, boolean enabled, int currentMarking, Color color)
    {
        this.id = id;
        this.enabled = enabled;
        this.currentMarking = currentMarking;
        this.color = color;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getCurrentMarking()
    {
        return currentMarking;
    }

    public void setCurrentMarking(int currentMarking)
    {
        this.currentMarking = currentMarking;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     *
     * @param enabled
     * @throws TokenLockedException if the Token is locked
     */
    public void setEnabled(boolean enabled) throws TokenLockedException
    {
    	if (!isLocked()) {
            this.enabled = enabled;
        }
    	else {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("TokenSetController.updateOrAddTokenView: Enabled TokenView is in use for ")
                          .append(getLockCount())
                          .append(" Places.  It may not be disabled unless tokens are removed from those Places.\n")
                          .append("Details: ")
                          .append(this.toString());

            throw new TokenLockedException(messageBuilder.toString());
        }
    }

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("TokenView:");
		sb.append(" Enabled=");
		sb.append(isEnabled());
		sb.append(", Id=");
		sb.append(getId());
		sb.append(", Color=");
		sb.append(getColor());
		sb.append(", Lock count=");
		sb.append(getLockCount());
		return sb.toString();
	}

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void incrementLock()
    {
        lockCount++;
    }

    public void decrementLock()
    {
        lockCount--;
    }

    public boolean isLocked()
    {
        return lockCount > 0;
    }

    public int getLockCount()
    {
        return lockCount;
    }

    public void setLockCount(int newLockCount)
    {
        lockCount = newLockCount;
    }

    public Matrix getIncidenceMatrix()
    {
        return incidenceMatrix;
    }

    public int[][] getIncidenceMatrix(Collection<ArcView> arcsArray, Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        if(incidenceMatrix == null || incidenceMatrix.matrixChanged)
        {
            createIncidenceMatrix(arcsArray, transitionsArray, placesArray);
        }
        return (incidenceMatrix != null ? incidenceMatrix.getArrayCopy() : null);
    }

    // New method for TransModel.java
    public int[][] simpleMatrix()
    {
        return incidenceMatrix.getArrayCopy();
    }

    public void setIncidenceMatrix(Matrix incidenceMatrix)
    {
        this.incidenceMatrix = incidenceMatrix;
    }

    public void createIncidenceMatrix(Collection<ArcView> arcsArray,Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        createForwardIncidenceMatrix(arcsArray, transitionsArray, placesArray);
        createBackwardsIncidenceMatrix(arcsArray, transitionsArray, placesArray);
        incidenceMatrix = new Matrix(forwardsIncidenceMatrix);
        incidenceMatrix = incidenceMatrix.minus(backwardsIncidenceMatrix);
        incidenceMatrix.matrixChanged = false;
    }

    public Matrix getForwardsIncidenceMatrix()
    {
        return forwardsIncidenceMatrix;
    }

    public int[][] getForwardsIncidenceMatrix(Collection<ArcView> arcsArray,
            Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        if(forwardsIncidenceMatrix == null
                || forwardsIncidenceMatrix.matrixChanged)
        {
            createForwardIncidenceMatrix(arcsArray, transitionsArray,
                                         placesArray);
        }
        return (forwardsIncidenceMatrix != null ? forwardsIncidenceMatrix
                .getArrayCopy() : null);
    }

    public int[][] simpleForwardsIncidenceMatrix()
    {
        return forwardsIncidenceMatrix.getArrayCopy();
    }

    public void setForwardsIncidenceMatrix(Matrix forwardsIncidenceMatrix)
    {
        this.forwardsIncidenceMatrix = forwardsIncidenceMatrix;
    }

    public Matrix getBackwardsIncidenceMatrix()
    {
        return backwardsIncidenceMatrix;
    }

    public int[][] getBackwardsIncidenceMatrix(Collection<ArcView> arcsArray,
            Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        if(backwardsIncidenceMatrix == null
                || backwardsIncidenceMatrix.matrixChanged)
        {
            createBackwardsIncidenceMatrix(arcsArray, transitionsArray,
                                           placesArray);
        }
        return (backwardsIncidenceMatrix != null ? backwardsIncidenceMatrix
                .getArrayCopy() : null);
    }

    public int[][] simpleBackwardsIncidenceMatrix()
    {
        return backwardsIncidenceMatrix.getArrayCopy();
    }

    public void setBackwardsIncidenceMatrix(Matrix backwardsIncidenceMatrix)
    {
        this.backwardsIncidenceMatrix = backwardsIncidenceMatrix;
    }

    public Matrix getInhibitionMatrix()
    {
        return inhibitionMatrix;
    }

    public int[][] getInhibitionMatrix(Collection<InhibitorArcView> inhibitorArrayView,
            Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        if(inhibitionMatrix == null || inhibitionMatrix.matrixChanged)
        {
            createInhibitionMatrix(inhibitorArrayView, transitionsArray,
                                   placesArray);
        }
        return (inhibitionMatrix != null ? inhibitionMatrix.getArrayCopy()
                : null);
    }

    public void setInhibitionMatrix(Matrix inhibitionMatrix)
    {
        this.inhibitionMatrix = inhibitionMatrix;
    }

    void createForwardIncidenceMatrix(Collection<ArcView> arcsArray, Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        int placeSize = placesArray.size();
        int transitionSize = transitionsArray.size();

        forwardsIncidenceMatrix = new Matrix(placeSize, transitionSize);

        for(ArcView arcView : arcsArray)
        {
            if(arcView != null)
            {
                PetriNetViewComponent pn = arcView.getTarget();
                if(pn != null)
                {
                    if(pn instanceof PlaceView)
                    {
                        PlaceView placeView = (PlaceView) pn;
                        pn = arcView.getSource();
                        if(pn != null)
                        {
                            if(pn instanceof TransitionView)
                            {
                                TransitionView transitionView = (TransitionView) pn;

                                //TODO: Broken transitions
                                int transitionNo = 0; //transitionsArray.indexOf(transitionView);
                                int placeNo = 0;//placesArray.indexOf(placeView);
                                List<MarkingView> markings = arcView.getWeight();
                                for(MarkingView token : markings)
                                {
                                    if(token.getToken().getID().equals(id))
                                    {
                                    	int marking=token.getCurrentMarking();
                                    	if(marking==0){
                                    		marking=1;
                                    	}
                                        try
                                        {
                                        	//System.out.println("compare: "+ token.getCurrentMarking()+ " raw: "+ token.getCurrentFunctionalMarking());
                                            forwardsIncidenceMatrix.set(
                                                    placeNo, transitionNo,
                                                    marking);//arcView.getWeightOfTokenClass(id));
                                           // System.out.println(arcView.getWeightFunctionOfTokenClass(id));
                                        }
                                        catch(Exception e)
                                        {
                                            JOptionPane.showMessageDialog(null, "Problem in forwardsIncidenceMatrix");
                                            System.out.println("p:" + placeNo + ";t:" + transitionNo + ";w:" + arcView.getWeight());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates Backwards Incidence Matrix from current Petri-Net
     * @param arcsArray
     * @param transitionsArray
     * @param placesArray
     */
    void createBackwardsIncidenceMatrix(Collection<ArcView> arcsArray, Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {// Matthew
        int placeSize = placesArray.size();
        int transitionSize = transitionsArray.size();

        backwardsIncidenceMatrix = new Matrix(placeSize, transitionSize);

        for(ArcView arcView : arcsArray)
        {
            if(arcView != null)
            {
                PetriNetViewComponent pn = arcView.getSource();
                if(pn != null)
                {
                    if(pn instanceof PlaceView)
                    {
                        PlaceView placeView = (PlaceView) pn;
                        pn = arcView.getTarget();
                        if(pn != null)
                        {
                            if(pn instanceof TransitionView)
                            {
                                TransitionView transitionView = (TransitionView) pn;
                                boolean isTransitionInfiniteServer = transitionView.isInfiniteServer();
                                int enablingDegree=1;
                                if(isTransitionInfiniteServer){
                                	enablingDegree=ApplicationSettings.getApplicationView().getCurrentPetriNetView().getEnablingDegree(transitionView);
                                }
                                //TODO: Broken this
                                int transitionNo = 0; //transitionsArray.indexOf(transitionView);
                                int placeNo = 0; //placesArray.indexOf(placeView);
                                List<MarkingView> markings = arcView.getWeight();
                                for(MarkingView token : markings)
                                {
                                    if(token.getToken().getID().equals(id))
                                    {
                                        try
                                        {
                                        	int marking=token.getCurrentMarking();
                                        	if(marking==0){
                                        		marking=1;
                                        	}
                                        	if(isTransitionInfiniteServer){
                                        		
                                        		backwardsIncidenceMatrix.set(
                                                        placeNo, transitionNo,
                                                        marking * enablingDegree);
                                        	}else{
                                        		backwardsIncidenceMatrix.set(
                                                        placeNo, transitionNo,
                                                        marking);//arcView.getWeightOfTokenClass(id));
                                        	}
                                        //	System.out.println("compare: "+ token.getCurrentMarking()+ " raw: "+ token.getCurrentFunctionalMarking()+"   "+arcView.getWeightFunctionOfTokenClass(id));
                                            
                                       //     System.out.println(arcView.getWeightFunctionOfTokenClass(id));
                                        }
                                        catch(Exception e)
                                        {
                                            JOptionPane.showMessageDialog(null, "Problem in backwardsIncidenceMatrix");
                                            System.out.println("p:" + placeNo + ";t:" + transitionNo + ";w:" + arcView.getWeight());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void createInhibitionMatrix(Collection<InhibitorArcView> inhibitorsArray,Collection<TransitionView> transitionsArray, Collection<PlaceView> placesArray)
    {
        int placeSize = placesArray.size();
        int transitionSize = transitionsArray.size();
        inhibitionMatrix = new Matrix(placeSize, transitionSize);

        for(InhibitorArcView inhibitorArcView : inhibitorsArray)
        {
            if(inhibitorArcView != null)
            {
                PetriNetViewComponent pn = inhibitorArcView.getSource();
                if(pn != null)
                {
                    if(pn instanceof PlaceView)
                    {
                        PlaceView placeView = (PlaceView) pn;
                        pn = inhibitorArcView.getTarget();
                        if(pn != null)
                        {
                            if(pn instanceof TransitionView)
                            {
                                TransitionView transitionView = (TransitionView) pn;
                                //TODO: Broken this
                                int transitionNo = 0; //transitionsArray.indexOf(transitionView);
                                int placeNo = 0; //placesArray.indexOf(placeView);
                                try
                                {
                                    inhibitionMatrix.set(placeNo, transitionNo,
                                                          1);
                                }
                                catch(Exception e)
                                {
                                    JOptionPane.showMessageDialog(null,
                                                                  "Problema a inhibitionMatrix");
                                    System.out.println("p:" + placeNo + ";t:"
                                                               + transitionNo + ";w:"
                                                               + inhibitorArcView.getWeight());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        visitor.visit(this);
    }
}
