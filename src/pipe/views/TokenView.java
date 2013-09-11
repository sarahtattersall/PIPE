package pipe.views;

import pipe.controllers.TokenController;
import pipe.models.Token;
import pipe.models.interfaces.IObserver;
import pipe.utilities.math.Matrix;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


public class TokenView implements Serializable, IObserver
{
    private final Token _model;
    private TokenController _controller;
	private Matrix previousIncidenceMatrix;

    public TokenView(TokenController controller, Token model)
    {
        _controller = controller;
        _model = model;
        _model.registerObserver(this);
    }

    public TokenView(boolean enabled, String id, Color color)
    {
        _model = new Token(id, enabled, 0, color);
    }
    
    public Color getColor()
    {
        return _model.getColor();
    }

    public void setColor(Color colour)
    {
        _model.setColor(colour);
    }

    void paint(Graphics canvas, Insets insets, int offset, int tempTotalMarking, int currentMarking)
    {
        if(tempTotalMarking > 5)
            paintAsANumber(canvas, insets, offset, currentMarking);
        else
            paintAsAnOval(canvas, insets, tempTotalMarking, currentMarking);
    }

    void paintAsAnOval(Graphics canvas, Insets insets, int tempTotalMarking, int currentMarking)
    {
        int x = insets.left;
        int y = insets.top;
        canvas.setColor(getColor());
        int WIDTH = 4;
        int HEIGHT = 4;
        for(int i = 0; i < currentMarking; i++)
        {

            switch(tempTotalMarking)
            {
                case 5:
                    canvas.drawOval(x + 6, y + 6, WIDTH, HEIGHT);
                    canvas.fillOval(x + 6, y + 6, WIDTH, HEIGHT);
                    break;
                case 4:
                    canvas.drawOval(x + 18, y + 20, WIDTH, HEIGHT);
                    canvas.fillOval(x + 18, y + 20, WIDTH, HEIGHT);
                    break;
                case 3:
                    canvas.drawOval(x + 6, y + 20, WIDTH, HEIGHT);
                    canvas.fillOval(x + 6, y + 20, WIDTH, HEIGHT);
                    break;
                case 2:
                    canvas.drawOval(x + 18, y + 6, WIDTH, HEIGHT);
                    canvas.fillOval(x + 18, y + 6, WIDTH, HEIGHT);
                    break;
                case 1:
                    canvas.drawOval(x + 12, y + 13, WIDTH, HEIGHT);
                    canvas.fillOval(x + 12, y + 13, WIDTH, HEIGHT);
                    break;
                case 0:
                    break;
                default:
                    break;
            }
            tempTotalMarking--;
        }
    }

    void paintAsANumber(Graphics canvas, Insets insets, int offset, int currentMarking)
    {
        int x = insets.left;
        int y = insets.top;
        canvas.setColor(getColor());
        if(currentMarking > 999)
            canvas.drawString(String.valueOf(currentMarking), x, y + 10 + offset);
        else if(currentMarking > 99)
            canvas.drawString(String.valueOf(currentMarking), x + 3, y + 10 + offset);
        else if(currentMarking > 9)
            canvas.drawString(String.valueOf(currentMarking), x + 7, y + 10 + offset);
        else if(currentMarking != 0)
            canvas.drawString(String.valueOf(currentMarking), x + 12, y + 10 + offset);
    }


    @Override
    public void update()
    {
        //paint
    }

    public void update(Graphics canvas, Insets insets, int offset, int tempTotalMarking, int currentMarking)
    {
        paint(canvas, insets, offset, tempTotalMarking, currentMarking);
    }

    public Token getModel()
    {
        return _model;
    }

    public void setCurrentMarking(int marking)
    {
        _model.setCurrentMarking(marking);
    }

    public int getCurrentMarking()
    {
        return _model.getCurrentMarking();
    }

    public Matrix getPreviousIncidenceMatrix(){
    	return previousIncidenceMatrix;
    }
    public Matrix getIncidenceMatrix()
    {
    	previousIncidenceMatrix=_model.getIncidenceMatrix();
        return previousIncidenceMatrix;
    	//return _model.getIncidenceMatrix();
    }

    public int[][] getIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
    	
        return _model.getIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public int[][] simpleMatrix()
    {
        return _model.simpleMatrix();
    }

    public void setIncidenceMatrix(Matrix incidenceMatrix)
    {
        _model.setIncidenceMatrix(incidenceMatrix);
    }

    public Matrix getForwardsIncidenceMatrix()
    {
        return _model.getForwardsIncidenceMatrix();
    }

    public int[][] getForwardsIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getForwardsIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public int[][] simpleForwardsIncidenceMatrix()
    {
        return _model.simpleForwardsIncidenceMatrix();
    }

    public void setForwardsIncidenceMatrix(Matrix forwardsIncidenceMatrix)
    {
        _model.setForwardsIncidenceMatrix(forwardsIncidenceMatrix);
    }

    public Matrix getBackwardsIncidenceMatrix()
    {
        return _model.getBackwardsIncidenceMatrix();
    }

    public int[][] getBackwardsIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getBackwardsIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public int[][] simpleBackwardsIncidenceMatrix()
    {
        return _model.simpleBackwardsIncidenceMatrix();
    }

    public void setBackwardsIncidenceMatrix(Matrix backwardsIncidenceMatrix)
    {
        _model.setBackwardsIncidenceMatrix(backwardsIncidenceMatrix);
    }

    public Matrix getInhibitionMatrix()
    {
        return _model.getInhibitionMatrix();
    }

    public int[][] getInhibitionMatrix(ArrayList<InhibitorArcView> inhibitorArrayView,ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getInhibitionMatrix(inhibitorArrayView, transitionsArray, placesArray);
    }

    public void setInhibitionMatrix(Matrix inhibitionMatrix)
    {
        _model.setInhibitionMatrix(inhibitionMatrix);
    }

    public void incrementLock()
    {
        _model.incrementLock();
    }

    public void decrementLock()
    {
        _model.decrementLock();
    }

    public boolean isLocked()
    {
        return _model.isLocked();
    }

    public int getLockCount()
    {
        return _model.getLockCount();
    }

    public void setLockCount(int newLockCount)
    {
        _model.setLockCount(newLockCount);
    }

    public boolean isEnabled()
    {
        return _model.isEnabled();
    }

    public void setEnabled(boolean enabled)
    {
        _model.setEnabled(enabled);
    }

    public String getID()
    {
        return _model.getId();
    }

    public void setID(String id)
    {
        _model.setId(id);
    }

    public boolean hasSameId(TokenView otherTokenView)
    {
        return otherTokenView.getID().equals(getID());
    }

    public void createIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        _model.createIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public void createInhibitionMatrix(ArrayList<InhibitorArcView> inhibitorsArray,ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        _model.createInhibitionMatrix(inhibitorsArray, transitionsArray, placesArray);
    }

}
