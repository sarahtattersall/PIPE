package pipe.models;

import pipe.exceptions.TokenLockedException;
import pipe.gui.ApplicationSettings;
import pipe.models.interfaces.IObserver;
import pipe.utilities.math.Matrix;
import pipe.views.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Token extends Observable implements Serializable
{
    private String _id;
    private boolean _enabled;
    private int _currentMarking;
    private int _lockCount; // So that users cannot change this class while
    // places are marked with it

    private Color _color;
    private Matrix _incidenceMatrix = null;
    private Matrix _forwardsIncidenceMatrix = null;
    private Matrix _backwardsIncidenceMatrix = null;
    private Matrix _inhibitionMatrix = null;

    public Token()
    {
        this("", false, 0, Color.BLACK);
    }

    public Token(String id, boolean enabled, int currentMarking, Color color)
    {
        _id =id;
        _enabled = enabled;
        _lockCount = 0;
        _currentMarking = currentMarking;
        ArrayList<IObserver> observers = new ArrayList<IObserver>();
        _color = color;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(String id)
    {
        _id = id;
    }

    public int getCurrentMarking()
    {
        return _currentMarking;
    }

    public void setCurrentMarking(int currentMarking)
    {
        _currentMarking = currentMarking;
    }

    public boolean isEnabled()
    {
        return _enabled;
    }

    public void setEnabled(boolean enabled) throws TokenLockedException
    {
    	if (!isLocked()) _enabled = enabled;
    	else throw new TokenLockedException("TokenSetController.updateOrAddTokenView: Enabled TokenView is in use for "+getLockCount()+" Places.  It may not be disabled unless markings are removed from those Places.\n" +
				"Details: "+toString());
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
		sb.append(getColor().toString());
		sb.append(", Lock count=");
		sb.append(getLockCount());
		return sb.toString();
	}



    public Color getColor()
    {
        return _color;
    }

    public void setColor(Color color)
    {
        _color = color;
    }

    public void incrementLock()
    {
        _lockCount++;
    }

    public void decrementLock()
    {
        _lockCount--;
    }

    public boolean isLocked()
    {
        return _lockCount > 0;
    }

    public int getLockCount()
    {
        return _lockCount;
    }

    public void setLockCount(int newLockCount)
    {
        _lockCount = newLockCount;
    }

    public Matrix getIncidenceMatrix()
    {
        return _incidenceMatrix;
    }

    public int[][] getIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        if(_incidenceMatrix == null || _incidenceMatrix.matrixChanged)
        {
            createIncidenceMatrix(arcsArray, transitionsArray, placesArray);
        }
        return (_incidenceMatrix != null ? _incidenceMatrix.getArrayCopy() : null);
    }

    // New method for TransModel.java
    public int[][] simpleMatrix()
    {
        return _incidenceMatrix.getArrayCopy();
    }

    public void setIncidenceMatrix(Matrix incidenceMatrix)
    {
        this._incidenceMatrix = incidenceMatrix;
    }

    public void createIncidenceMatrix(ArrayList<ArcView> arcsArray,ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        createForwardIncidenceMatrix(arcsArray, transitionsArray, placesArray);
        createBackwardsIncidenceMatrix(arcsArray, transitionsArray, placesArray);
        _incidenceMatrix = new Matrix(_forwardsIncidenceMatrix);
        _incidenceMatrix = _incidenceMatrix.minus(_backwardsIncidenceMatrix);
        _incidenceMatrix.matrixChanged = false;
    }

    public Matrix getForwardsIncidenceMatrix()
    {
        return _forwardsIncidenceMatrix;
    }

    public int[][] getForwardsIncidenceMatrix(ArrayList<ArcView> arcsArray,
                                              ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        if(_forwardsIncidenceMatrix == null
                || _forwardsIncidenceMatrix.matrixChanged)
        {
            createForwardIncidenceMatrix(arcsArray, transitionsArray,
                                         placesArray);
        }
        return (_forwardsIncidenceMatrix != null ? _forwardsIncidenceMatrix
                .getArrayCopy() : null);
    }

    public int[][] simpleForwardsIncidenceMatrix()
    {
        return _forwardsIncidenceMatrix.getArrayCopy();
    }

    public void setForwardsIncidenceMatrix(Matrix forwardsIncidenceMatrix)
    {
        this._forwardsIncidenceMatrix = forwardsIncidenceMatrix;
    }

    public Matrix getBackwardsIncidenceMatrix()
    {
        return _backwardsIncidenceMatrix;
    }

    public int[][] getBackwardsIncidenceMatrix(ArrayList<ArcView> arcsArray,
                                               ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        if(_backwardsIncidenceMatrix == null
                || _backwardsIncidenceMatrix.matrixChanged)
        {
            createBackwardsIncidenceMatrix(arcsArray, transitionsArray,
                                           placesArray);
        }
        return (_backwardsIncidenceMatrix != null ? _backwardsIncidenceMatrix
                .getArrayCopy() : null);
    }

    public int[][] simpleBackwardsIncidenceMatrix()
    {
        return _backwardsIncidenceMatrix.getArrayCopy();
    }

    public void setBackwardsIncidenceMatrix(Matrix backwardsIncidenceMatrix)
    {
        this._backwardsIncidenceMatrix = backwardsIncidenceMatrix;
    }

    public Matrix getInhibitionMatrix()
    {
        return _inhibitionMatrix;
    }

    public int[][] getInhibitionMatrix(ArrayList<InhibitorArcView> inhibitorArrayView,
                                       ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        if(_inhibitionMatrix == null || _inhibitionMatrix.matrixChanged)
        {
            createInhibitionMatrix(inhibitorArrayView, transitionsArray,
                                   placesArray);
        }
        return (_inhibitionMatrix != null ? _inhibitionMatrix.getArrayCopy()
                : null);
    }

    public void setInhibitionMatrix(Matrix inhibitionMatrix)
    {
        this._inhibitionMatrix = inhibitionMatrix;
    }

    void createForwardIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        int placeSize = placesArray.size();
        int transitionSize = transitionsArray.size();

        _forwardsIncidenceMatrix = new Matrix(placeSize, transitionSize);

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
                                int transitionNo = transitionsArray
                                        .indexOf(transitionView);
                                int placeNo = placesArray.indexOf(placeView);
                                for(MarkingView token : arcView.getWeight())
                                {
                                    if(token.getToken().getID().equals(_id))
                                    {
                                    	int marking=token.getCurrentMarking();
                                    	if(marking==0){
                                    		marking=1;
                                    	}
                                        try
                                        {
                                        	//System.out.println("compare: "+ token.getCurrentMarking()+ " raw: "+ token.getCurrentFunctionalMarking());
                                            _forwardsIncidenceMatrix.set(placeNo, transitionNo, marking);//arcView.getWeightOfTokenClass(_id));
                                           // System.out.println(arcView.getWeightFunctionOfTokenClass(_id));
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
    void createBackwardsIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {// Matthew
        int placeSize = placesArray.size();
        int transitionSize = transitionsArray.size();

        _backwardsIncidenceMatrix = new Matrix(placeSize, transitionSize);

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
                                int transitionNo = transitionsArray.indexOf(transitionView);
                                int placeNo = placesArray.indexOf(placeView);
                                for(MarkingView token : arcView.getWeight())
                                {
                                    if(token.getToken().getID().equals(_id))
                                    {
                                        try
                                        {
                                        	int marking=token.getCurrentMarking();
                                        	if(marking==0){
                                        		marking=1;
                                        	}
                                        	if(isTransitionInfiniteServer){
                                        		
                                        		_backwardsIncidenceMatrix.set(placeNo, transitionNo, marking*enablingDegree);
                                        	}else{
                                        		_backwardsIncidenceMatrix.set(placeNo, transitionNo, marking);//arcView.getWeightOfTokenClass(_id));
                                        	}
                                        //	System.out.println("compare: "+ token.getCurrentMarking()+ " raw: "+ token.getCurrentFunctionalMarking()+"   "+arcView.getWeightFunctionOfTokenClass(_id));
                                            
                                       //     System.out.println(arcView.getWeightFunctionOfTokenClass(_id));
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

    public void createInhibitionMatrix(ArrayList<InhibitorArcView> inhibitorsArray,ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        int placeSize = placesArray.size();
        int transitionSize = transitionsArray.size();
        _inhibitionMatrix = new Matrix(placeSize, transitionSize);

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
                                int transitionNo = transitionsArray
                                        .indexOf(transitionView);
                                int placeNo = placesArray.indexOf(placeView);
                                try
                                {
                                    _inhibitionMatrix.set(placeNo, transitionNo,
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
}
