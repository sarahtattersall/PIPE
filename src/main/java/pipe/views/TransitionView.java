package pipe.views;

import parser.ExprEvaluator;
import pipe.controllers.TransitionController;
import pipe.gui.*;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.TransitionEditorPanel;
import pipe.handlers.PetriNetObjectHandler;
import pipe.handlers.PlaceTransitionObjectHandler;
import pipe.historyActions.*;
import pipe.models.Marking;
import pipe.models.NormalArc;
import pipe.views.viewComponents.RateParameter;
import pipe.models.Transition;

import javax.swing.*;
import javax.swing.Timer;

import net.sourceforge.jeval.EvaluationException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;


public class TransitionView extends ConnectableView implements Serializable
{
    @Override
    public boolean isShowing() {
        return super.isShowing();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private GeneralPath _path;
    private Shape proximityTransition;
    private int _angle;
    private boolean _enabled;
    private boolean _enabledBackwards;
    public boolean _highlighted;
    private boolean _infiniteServer;
    private double _delay;
    private boolean _delayValid;
    private boolean _timed;
    private double _rootThreeOverTwo = 0.5 * Math.sqrt(3);
    private ArrayList _arcAngleList;
    private RateParameter _rateParameter;
    private GroupTransitionView _groupTransitionView;
    private final Transition _model;


	/** Place Width */
	public static final int TRANSITION_HEIGHT = Constants.PLACE_TRANSITION_HEIGHT;
	/** Place Width */
	public static final int TRANSITION_WIDTH = TRANSITION_HEIGHT / 3;

    private int _delayForShowingWarnings;
    private TransitionController _transitionController;
	private boolean fratetype;
	private boolean cratetype;
	private boolean rateExprSet;
	private String _functionalRateExpr;
	

//	private MyParser parser = new MyParser();


    public TransitionView(double positionXInput, double positionYInput)
    {
        this(positionXInput, positionYInput, "", "", Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y, false, false, 0, new Transition("", "", "1", 1));
    }

    public TransitionView(double positionXInput, double positionYInput, String id, String name, double nameOffsetX, double nameOffsetY, boolean timed, boolean infServer, int angleInput, Transition model)
    {
        super(positionXInput, positionYInput, id, name, nameOffsetX, nameOffsetY, model);
        _model = model;
        _componentHeight = TRANSITION_HEIGHT;
        _componentWidth = TRANSITION_HEIGHT;
        _nameOffsetX = nameOffsetX;
        _nameOffsetY = nameOffsetY;
        _timed = timed;
        _infiniteServer = infServer;
        constructTransition();
        _angle = 0;

        _enabled = false;
        _enabledBackwards = false;
        _highlighted = false;
        _rootThreeOverTwo = 0.5 * Math.sqrt(3);
        _arcAngleList = new ArrayList();
        _delayForShowingWarnings = 10000;

        setCentre((int) _positionX, (int) _positionY);
        rotate(angleInput);
        updateBounds();
        updateEndPoints();
    }

    public TransitionView(TransitionController transitionController, Transition model)
    {
        super(0,0,model);
        _transitionController = transitionController;
        _model = model;
        _model.registerObserver(this);
    }

    public void setDelayForShowingWarnings(int delayForShowingWarnings)
    {
        _delayForShowingWarnings = delayForShowingWarnings;
    }

    public TransitionView paste(double x, double y, boolean fromAnotherView, PetriNetView model)
    {
        TransitionView copy = new TransitionView((double) Grid.getModifiedX(x + this.getX() + Constants.PLACE_TRANSITION_HEIGHT / 2), (double) Grid.getModifiedY(y + this.getY() + Constants.PLACE_TRANSITION_HEIGHT / 2));
        String newName = this._nameLabel.getName() + "(" + this.getCopyNumber() + ")";
        boolean properName = false;

        while(!properName)
        {
            if(model.checkTransitionIDAvailability(newName))
            {
                copy._nameLabel.setName(newName);
                properName = true;
            }
            else
            {
                newName = newName + "'";
            }
        }

        this.newCopy(copy);

        copy._nameOffsetX = this._nameOffsetX;
        copy._nameOffsetY = this._nameOffsetY;

        copy._timed = this._timed;
        copy._model.setRateExpr(_model.getRateExpr());
        copy._angle = this._angle;

        copy._attributesVisible = this._attributesVisible;
        copy._model.setPriority(_model.getPriority());
        copy._path.transform(AffineTransform.getRotateInstance(Math.toRadians(copy._angle), TRANSITION_HEIGHT / 2, TRANSITION_HEIGHT / 2));
        copy._rateParameter = null;
        
        
        return copy;
    }

    public TransitionView copy()
    {
        TransitionView copy = new TransitionView((double) ZoomController.getUnzoomedValue(this.getX(), _zoomPercentage), (double) ZoomController.getUnzoomedValue(this.getY(), _zoomPercentage));
        copy._nameLabel.setName(this.getName());
        copy._nameOffsetX = this._nameOffsetX;
        copy._nameOffsetY = this._nameOffsetY;
        copy._timed = this._timed;
        copy._model.setRateExpr(getRate());
        copy._angle = this._angle;
        copy._attributesVisible = this._attributesVisible;
        copy._model.setPriority(_model.getPriority());
        copy.setOriginal(this);
        copy._rateParameter = this._rateParameter;
        return copy;
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(_selected && !_ignoreSelection)
        {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        }
        else
        {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }

        if(_timed)
        {
            if(_infiniteServer)
            {
                for(int i = 2; i >= 1; i--)
                {
                    g2.translate(2 * i, -2 * i);
                    g2.fill(_path);
                    Paint pen = g2.getPaint();
                    if(_highlighted)
                    {
                        g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
                    }
                    else if(_selected && !_ignoreSelection)
                    {
                        g2.setPaint(Constants.SELECTION_LINE_COLOUR);
                    }
                    else
                    {
                        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                    }
                    g2.draw(_path);
                    g2.setPaint(pen);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.fill(_path);
        }

        if(_highlighted)
        {
            g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
        }
        else if(_selected && !_ignoreSelection)
        {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        }
        else
        {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.draw(_path);
        if(!_timed)
        {
            if(_infiniteServer)
            {
                for(int i = 2; i >= 1; i--)
                {
                    g2.translate(2 * i, -2 * i);
                    Paint pen = g2.getPaint();
                    g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                    g2.fill(_path);
                    g2.setPaint(pen);
                    g2.draw(_path);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.draw(_path);
            g2.fill(_path);
        }
        try{
        	Double.parseDouble(getRateExpr());
        	if(this.isTimed())
            {
                setToolTipText("r = " + this.getRate());
            }
            else
            {
                setToolTipText("\u03c0 = " + this.getPriority() + "; w = " + this.getRate());
            }
        }catch(Exception e){
        	if(this.isTimed())
            {
                setToolTipText("r = " + this.getRateExpr()+ " = "+ this.getRate());
            }
            else
            {
                setToolTipText("\u03c0 = " + this.getPriority() + "; w = " + this.getRateExpr()+ " = " + this.getRate());
            }
        }
    }

    public HistoryItem rotate(int angleInc)
    {
        _angle = (_angle + angleInc) % 360;
        _path.transform(AffineTransform.getRotateInstance(Math.toRadians(angleInc), _componentWidth / 2, _componentHeight / 2));
        outlineTransition();

        Iterator arcIterator = _arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            ((ArcAngleCompare) arcIterator.next()).calcAngle();
        }
        Collections.sort(_arcAngleList);

        updateEndPoints();
        repaint();

        return new TransitionRotation(this, angleInc);
    }

    private void outlineTransition()
    {
        proximityTransition = (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(_path);
    }

    public boolean isEnabled(boolean animationStatus)
    {
        if(_groupTransitionView != null)
        {
            _groupTransitionView.isEnabled(animationStatus);
        }
        if(animationStatus)
        {
            if(_enabled)
            {
                _highlighted = true;
                return true;
            }
            else
            {
                _highlighted = false;
            }
        }
        return false;
    }

    public boolean isEnabledBackwards()
    {
        return _enabledBackwards;
    }

    public boolean isEnabled()
    {
        return _enabled;
    }

    public void setHighlighted(boolean status)
    {
        if(_groupTransitionView != null)
            _groupTransitionView.setHighlighted(status);
        _highlighted = status;
    }

    public HistoryItem setInfiniteServer(boolean status)
    {
        _infiniteServer = status;
        repaint();
        return new TransitionServerSemantic(this);
    }

    public boolean isInfiniteServer()
    {
        return _infiniteServer;
    }

    public void setEnabled(boolean status)
    {
        if(_enabled && !status)
            _delayValid = false;
        if(_groupTransitionView != null)
            _groupTransitionView.setEnabled(status);
        _enabled = status;

    }

    public void setEnabledBackwards(boolean status)
    {
        _enabledBackwards = status;
        if(_groupTransitionView != null)
        {
            _groupTransitionView.setEnabledBackwards(status);
        }
    }

    public void setEnabledFalse()
    {
        _enabled = false;
        _highlighted = false;
        if(_groupTransitionView != null)
        {
            _groupTransitionView.setEnabled(false);
        }
    }

    

    public int getAngle()
    {
        return _angle;
    }

    public int getPriority()
    {
        return _model.
                getPriority();
    }

    public HistoryItem setPriority(int newPriority)
    {
        int oldPriority = getPriority();

        _model.setPriority(newPriority);
        _nameLabel.setText(getAttributes());
        repaint();
        return new TransitionPriority(this, oldPriority, _model.getPriority());
    }

    public HistoryItem setTimed(boolean change)
    {
        _timed = change;
        _nameLabel.setText(getAttributes());
        repaint();
        return new TransitionTiming(this);
    }

    public boolean isTimed()
    {
        return _timed;
    }

    public void setDelay(double _delay)
    {
        this._delay = _delay;
        _delayValid = true;
    }

    public double getDelay()
    {
        return _delay;
    }

    public boolean isDelayValid()
    {
        return _delayValid;
    }

    public void setDelayValid(boolean _delayValid)
    {
        this._delayValid = _delayValid;
    }

    private void constructTransition()
    {
        _path = new GeneralPath();
        _path.append(new Rectangle2D.Double((_componentWidth - TRANSITION_WIDTH) / 2, 0, TRANSITION_WIDTH, TRANSITION_HEIGHT), false);
        outlineTransition();
    }

    public boolean contains(int x, int y)
    {
        int zoomPercentage = _zoomPercentage;

        double unZoomedX = (x - getComponentDrawOffset()) / (zoomPercentage / 100.0);
        double unZoomedY = (y - getComponentDrawOffset()) / (zoomPercentage / 100.0);

        ArcView someArcView = ApplicationSettings.getApplicationView().getCurrentTab()._createArcView;
        if(someArcView != null)
        {
            if((proximityTransition.contains((int) unZoomedX, (int) unZoomedY) || _path.contains((int) unZoomedX, (int) unZoomedY)) && areNotSameType(someArcView.getSource()))
            {
                if(someArcView.getTarget() != this)
                    someArcView.setTarget(this);
                someArcView.updateArcPosition();
                return true;
            }
            else
            {
                if(someArcView.getTarget() == this)
                {
                	if(!PlaceTransitionObjectHandler.isMouseDown()){
	                    someArcView.setTarget(null);
	                    removeArcCompareObject(someArcView);
	                    updateConnected();
                	}
                }
                return false;
            }
        }
        else
        {
            return _path.contains((int) unZoomedX, (int) unZoomedY);
        }
    }

    public void removeArcCompareObject(ArcView arcView)
    {
        Iterator arcIterator = _arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            if(((ArcAngleCompare) arcIterator.next())._arcView == arcView)
                arcIterator.remove();
        }
    }

    public void updateEndPoint(ArcView arcView)
    {
        boolean match = false;

        Iterator arcIterator = _arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if(thisArc._arcView == arcView || !arcView.inView())
            {
                thisArc.calcAngle();
                match = true;
                break;
            }
        }

        if(!match)
        {
            _arcAngleList.add(new ArcAngleCompare(arcView, this));
        }

        Collections.sort(_arcAngleList);
        updateEndPoints();
    }

    void updateEndPoints()
    {
        ArrayList top = new ArrayList();
        ArrayList bottom = new ArrayList();
        ArrayList left = new ArrayList();
        ArrayList right = new ArrayList();

        Iterator arcIterator = _arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            double thisAngle = thisArc.angle - Math.toRadians(_angle);
            if(Math.cos(thisAngle) > (_rootThreeOverTwo))
            {
                top.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(_angle + 90);
            }
            else if(Math.cos(thisAngle) < -_rootThreeOverTwo)
            {
                bottom.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(_angle + 270);
            }
            else if(Math.sin(thisAngle) > 0)
            {
                left.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(_angle + 180);
            }
            else
            {
                right.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(_angle);
            }
        }

        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(_angle + Math.PI));
        Point2D.Double transformed = new Point2D.Double();

        transform.concatenate(ZoomController.getTransform(_zoomPercentage));

        arcIterator = top.iterator();
        transform.transform(new Point2D.Double(1, 0.5 * TRANSITION_HEIGHT), transformed);
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if(thisArc.sourceOrTarget())
            {
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            }
            else
            {
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            }
        }

        arcIterator = bottom.iterator();
        transform.transform(new Point2D.Double(0, -0.5 * TRANSITION_HEIGHT), transformed);
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if(thisArc.sourceOrTarget())
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            else
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
        }

        arcIterator = left.iterator();
        double inc = TRANSITION_HEIGHT / (left.size() + 1);
        double current = TRANSITION_HEIGHT / 2 - inc;
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            transform.transform(new Point2D.Double(-0.5 * TRANSITION_WIDTH, current + 1), transformed);
            if(thisArc.sourceOrTarget())
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            else
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            current -= inc;
        }

        inc = TRANSITION_HEIGHT / (right.size() + 1);
        current = -TRANSITION_HEIGHT / 2 + inc;
        arcIterator = right.iterator();
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            transform.transform(new Point2D.Double(+0.5 * TRANSITION_WIDTH, current), transformed);
            if(thisArc.sourceOrTarget())
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            else
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft() + transformed.x, _positionY + centreOffsetTop() + transformed.y);
            current += inc;
        }
    }

    public void addedToGui()
    {
        super.addedToGui();
        update();
    }

    private String getAttributes()
    {
        if(_attributesVisible)
        {
//            if(isTimed())
//            {
//                if(_rateParameter != null)
//                    return "\nr=" + _rateParameter.getName();
//                else
//                    return "\nr=" + getRateExpr() +"="+getRate();
//            }
//            else
//            {
//                if(_rateParameter != null)
//                    return "\n" + '\u03c0' + "=" + _model.getPriority() + "\nw=" + _rateParameter.getName();
//                else
//                    return "\n" + '\u03c0' + "=" + _model.getPriority() + "\nw=" + getRateExpr()+"="+getRate();
//            }
            
            try{
            	Double.parseDouble(getRateExpr());
            	if(isInfiniteServer()){
            		return "\nr=" + "ED("+this.getId()+",M)="+getRate() ;
            	}
            	if(isTimed())
                {
                    if(_rateParameter != null)
                        return "\nr=" + _rateParameter.getName();
                    else
                        return "\nr=" +getRate();
                }
                else
                {
                    if(_rateParameter != null)
                        return "\n" + '\u03c0' + "=" + _model.getPriority() + "\nw=" + _rateParameter.getName();
                    else
                        return "\n" + '\u03c0' + "=" + _model.getPriority() + "\nw=" +getRate();
                }
            }catch(Exception e){
            	if(isInfiniteServer()){
            		return "\nr=" + "ED("+this.getId()+",M)="+getRate() ;
            	}
            	if(isTimed())
                {
                    if(_rateParameter != null)
                        return "\nr=" + _rateParameter.getName();
                    else
                        return "\nr=" + getRateExpr() +"="+getRate();
                }
                else
                {
                    if(_rateParameter != null)
                        return "\n" + '\u03c0' + "=" + _model.getPriority() + "\nw=" + _rateParameter.getName();
                    else
                        return "\n" + '\u03c0' + "=" + _model.getPriority() + "\nw=" + getRateExpr()+"="+getRate();
                }
            }
        }
        return "";
    }

    void setCentre(double x, double y)
    {
        super.setCentre(x, y);
        update();
    }

    public void toggleAttributesVisible()
    {
        _attributesVisible = !_attributesVisible;
        _nameLabel.setText(getAttributes());
    }

    public void showEditor()
    {
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);
        TransitionEditorPanel te = new TransitionEditorPanel(guiDialog.getRootPane(), this, ApplicationSettings.getApplicationView().getCurrentPetriNetView(), ApplicationSettings.getApplicationView().getCurrentTab());
        guiDialog.add(te);
        guiDialog.getRootPane().setDefaultButton(null);
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    public RateParameter getRateParameter()
    {
        return _rateParameter;
    }

    public HistoryItem setRateParameter(RateParameter rateParameter)
    {
        double oldRate = getRate();
        this._rateParameter = rateParameter;
        this._rateParameter.add(this);
        _model.setRateExpr(rateParameter.getValue());
        update();
        return new SetRateParameter(this, oldRate, this._rateParameter);
    }

    public HistoryItem clearRateParameter()
    {
        RateParameter oldRateParameter = _rateParameter;
        _rateParameter.remove(this);
        _rateParameter = null;
        update();
        return new ClearRateParameter(this, oldRateParameter);
    }

    public HistoryItem changeRateParameter(RateParameter rateParameter)
    {
        RateParameter oldRateParameter = this._rateParameter;
        this._rateParameter.remove(this);
        this._rateParameter = rateParameter;
        this._rateParameter.add(this);
        _model.setRateExpr(rateParameter.getValue());
        update();
        return new ChangeRateParameter(this, oldRateParameter,
                                       this._rateParameter);
    }

    public void update()
    {
        _nameLabel.setText(getAttributes());
        _nameLabel.zoomUpdate(_zoomPercentage);
        super.update();
        this.repaint();
    }

    public void delete()
    {
        if(_rateParameter != null)
        {
            _rateParameter.remove(this);
            _rateParameter = null;
        }
        ApplicationSettings.getApplicationView().getCurrentPetriNetView().deleteTransition(this.getId());
        super.delete();
    }

    class ArcAngleCompare implements Comparable
    {

        private final static boolean SOURCE = false;
        private final static boolean TARGET = true;
        private final ArcView _arcView;
        private final TransitionView _transitionView;
        private double angle;

        public ArcAngleCompare(ArcView arcView, TransitionView transitionView)
        {
            this._arcView = arcView;
            this._transitionView = transitionView;
            calcAngle();
        }

        public int compareTo(Object arg0)
        {
            double angle2 = ((ArcAngleCompare) arg0).angle;
            return (angle < angle2 ? -1 : (angle == angle2 ? 0 : 1));
        }

        private void calcAngle()
        {
            int index = sourceOrTarget() ? _arcView.getArcPath().getEndIndex() - 1 : 1;
            Point2D.Double p1 = new Point2D.Double(_positionX + centreOffsetLeft(), _positionY + centreOffsetTop());
            Point2D.Double p2 = new Point2D.Double(_arcView.getArcPath().getPoint(index).x, _arcView.getArcPath().getPoint(index).y);

            if(p1.y <= p2.y)
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y));
            else
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y)) + Math.PI;

            if(angle < (Math.toRadians(30 + _transitionView.getAngle())))
                angle += (2 * Math.PI);

            if(p1.equals(p2))
                angle = 0;
        }

        private boolean sourceOrTarget()
        {
            return (_arcView.getSource() == _transitionView ? SOURCE : TARGET);
        }

    }

    public void bindToGroup(GroupTransitionView groupTransitionView)
    {
        this._groupTransitionView = groupTransitionView;
    }

    public boolean isGrouped()
    {
        return _groupTransitionView != null;
    }

    public GroupTransitionView getGroup()
    {
        return _groupTransitionView;
    }

    public void ungroupTransition()
    {
        _groupTransitionView = null;
    }

    private ArrayList<TransitionView> groupTransitionsValidation()
    {
        if(!this.isSelected())
        {
            JOptionPane.showMessageDialog(null, "You can only choose this option on selected transitions", "Invalid selection", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
        ArrayList<PetriNetViewComponent> pns = view.getPNObjects();
        ArrayList<TransitionView> transitionsToHide = new ArrayList<TransitionView>();

        ArrayList<PlaceView> thisOutputPlaceViews = new ArrayList<PlaceView>();
        for(ArcView tempArcView : outboundArcs())
            thisOutputPlaceViews.add((PlaceView) (tempArcView.getTarget()));

        ArrayList<PlaceView> thisInputPlaceViews = new ArrayList<PlaceView>();
        for(ArcView tempArcView : inboundArcs())
            thisInputPlaceViews.add((PlaceView) (tempArcView.getSource()));

        ArrayList<PlaceView> currentOutputPlaceViews;
        ArrayList<PlaceView> currentInputPlaceViews;

        for(PetriNetViewComponent pn : pns)
        {
            if(pn.isSelected())
            {
                pn.deselect();
                if(pn instanceof TransitionView)
                {
                    if(this != pn)
                    {
                        currentOutputPlaceViews = new ArrayList<PlaceView>();

                        LinkedList<ArcView> outboundArcViews = ((TransitionView) pn).outboundArcs();
                        for(ArcView tempArcView : outboundArcViews)
                            currentOutputPlaceViews.add((PlaceView) (tempArcView.getTarget()));

                        if(!thisOutputPlaceViews.equals(currentOutputPlaceViews))
                        {
                            showWarningDialog("In order to be grouped, selected transitions must have the same output places");
                            return null;
                        }

                        currentInputPlaceViews = new ArrayList<PlaceView>();

                        LinkedList<ArcView> inboundArcViews = ((TransitionView) pn).inboundArcs();
                        for(ArcView tempArcView : inboundArcViews)
                            currentInputPlaceViews.add((PlaceView) (tempArcView.getSource()));

                        if(!thisInputPlaceViews.equals(currentInputPlaceViews))
                        {
                            showWarningDialog("In order to be grouped, selected transitions must have the same input places");
                            return null;
                        }
                    }
                    transitionsToHide.add(((TransitionView) pn));
                }
            }
        }

        if(transitionsToHide.size() < 2)
        {
            JOptionPane.showMessageDialog(null, "Please select 2 or more transitions to group", "Invalid selection", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return transitionsToHide;
    }

    private void showWarningDialog(String message)
    {
        JOptionPane pane = new JOptionPane(message,JOptionPane.ERROR_MESSAGE);
        final JDialog dialog = pane.createDialog(ApplicationSettings.getApplicationView(), "Invalid selection");
        ActionListener exiter = new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        };
        new Timer(_delayForShowingWarnings,exiter).start();
        dialog.show();
    }

    public int confirmOrTimeout(String message, String title) throws HeadlessException
    {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        return showWithTimeout(pane, null, title);
    }

    private int showWithTimeout(JOptionPane pane, Component parent, String title)
    {
        final JDialog dialog = pane.createDialog(parent, title);
        Thread timeoutThread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(2000);
                }
                catch(InterruptedException ex)
                {}
                javax.swing.SwingUtilities.invokeLater(new Runnable() //   from the event dispatch
                {
                    public void run()
                    {
                        dialog.hide();
                    }
                });            //   thread
            }
        };
        timeoutThread.start();
        dialog.show();
        Object selection = pane.getValue();                      // We get to this point when
        int result = JOptionPane.CLOSED_OPTION;                   // (1) The user makes a selection
        if(selection != null && selection instanceof Integer)        // or (2) the timeout thread closes
            result = ((Integer) selection).intValue();             // the dialog.
        return result;
    }

    public HistoryItem groupTransitions()
    {
        ArrayList<TransitionView> transitionsToHide = groupTransitionsValidation();
        GroupTransitionView newGroupTransitionView = new GroupTransitionView(this, this.getPositionX(), this.getPositionY());
        groupTransitionsHelper(transitionsToHide, newGroupTransitionView);
        return new GroupTransition(newGroupTransitionView);
    }

    public void groupTransitionsHelper(ArrayList<TransitionView> transitionsToHide, GroupTransitionView newGroupTransitionView)
    {
        if(transitionsToHide == null)
            return;

        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
        PetriNetView model = ApplicationSettings.getApplicationView().getCurrentPetriNetView();

        int i = 0;
        for(TransitionView transitionViewToGroup : transitionsToHide)
        {
            transitionViewToGroup.hideFromCanvas();
            transitionViewToGroup.hideAssociatedArcs();
            transitionViewToGroup.bindToGroup(newGroupTransitionView);
            newGroupTransitionView.addTransition(transitionViewToGroup);
            if(i == 0)
                newGroupTransitionView.setName(transitionViewToGroup.getName());
            else
                newGroupTransitionView.setName(newGroupTransitionView.getName() + "_" + transitionViewToGroup.getName());
            i++;
        }

        for(ArcView tempArcView : inboundArcs())
        {
            ArcView newArcView = new NormalArcView(tempArcView.getStartPositionX(), tempArcView.getStartPositionY(), tempArcView.getArcPath().getPoint(1).getX(), tempArcView.getArcPath().getPoint(1).getY(), tempArcView.getSource(), newGroupTransitionView, new LinkedList<MarkingView>(), "", false, new NormalArc(tempArcView.getSource().getModel(), newGroupTransitionView.getModel()));//, new LinkedList<Marking>()));
            newGroupTransitionView.addInbound(newArcView);
            tempArcView.getSource().addOutbound(newArcView);
            newArcView.addToView(view);
        }
        for(ArcView tempArcView : outboundArcs())
        {
            ArcView newArcView = new NormalArcView(tempArcView.getStartPositionX(), tempArcView.getStartPositionY(), tempArcView.getArcPath().getPoint(1).getX(), tempArcView.getArcPath().getPoint(1).getY(), newGroupTransitionView, tempArcView.getTarget(), new LinkedList<MarkingView>(), "", false, new NormalArc(newGroupTransitionView.getModel(), tempArcView.getSource().getModel()));//, new LinkedList<Marking>()));
            newGroupTransitionView.addOutbound(newArcView);
            tempArcView.getTarget().addInbound(newArcView);
            newArcView.addToView(view);
        }
        newGroupTransitionView.setVisible(true);
        newGroupTransitionView.getNameLabel().setVisible(true);
        view.addNewPetriNetObject(newGroupTransitionView);
        model.addPetriNetObject(newGroupTransitionView);
        newGroupTransitionView.repaint();
    }

    public void hideFromCanvas()
    {
        this.setVisible(false);
        this.getNameLabel().setVisible(false);
    }

    public void unhideFromCanvas()
    {
        this.setVisible(true);
        this.getNameLabel().setVisible(true);
    }


    public void hideAssociatedArcs()
    {
        for(ArcView tempArcView : outboundArcs())
            tempArcView.removeFromView();

        for(ArcView tempArcView : inboundArcs())
            tempArcView.removeFromView();
    }

    public void showAssociatedArcs()
    {
        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
        for(ArcView tempArcView : outboundArcs())
            tempArcView.addToView(view);
        for(ArcView tempArcView : inboundArcs())
            tempArcView.addToView(view);
    }
    /**
     * 
     * 
     * 
     * @return
     */
//	public String get_functionalRateExpr() {
//		return _model.getfunctionalRates();
//	}
	
//	public String getFRateExpr(){
//		if(_functionalRateExpr==null){
//			return "null";
//		}else{
//			return _functionalRateExpr;
//		}
//	}

//	public HistoryItem setRateExpr(String frx) {
//		String oldRate = _model.getfunctionalRates();
//		_model.setfunctionalRates(frx);
//		repaint();
//		return new TransitionRate(this,oldRate, _model.getfunctionalRates());
//	}
//	
//	public void setFRateExpr(String _functionalRateExpr){
//		_nameLabel.setText(getAttributes());
//		this._functionalRateExpr=_functionalRateExpr;
//	}

//	public void setRateType(String string) {
//		// TODO Auto-generated method stub
//		if(string.equals("F")){
//			fratetype=true;
//			cratetype=false;
//		}else if(string.equals("C")){
//			fratetype=false;
//			cratetype=true;
//		}
//	}
//	
	public boolean isConst(){
		try{
    		Double.parseDouble(_model.getRateExpr());
    		return true;
    	}catch(Exception e){
    		return false;
    	}
	}

//	public void setRateExprSet(boolean b) {
//		// TODO Auto-generated method stub
//		rateExprSet=b;
//	}
	
	public HistoryItem setRate(double rate)
    {	
        String oldRate = _model.getRateExpr();
        _model.setRateExpr(rate+"");
        _nameLabel.setText(getAttributes());
        repaint();
        return new TransitionRate(this, oldRate, _model.getRateExpr());
    }
	public HistoryItem setRate(String rate)
    {	
        String oldRate = _model.getRateExpr();
        _model.setRateExpr(rate+"");
        _nameLabel.setText(getAttributes());
        repaint();
        return new TransitionRate(this, oldRate, _model.getRateExpr());
    }
	
	public String getRateExpr(){
		return _model.getRateExpr();
	}

    public double getRate()
    {
    	if(isInfiniteServer()){
    		return (double)ApplicationSettings.getApplicationView().getCurrentPetriNetView().getEnablingDegree(this);
    	}
    	
    	if(_model.getRateExpr()==null)
    		return -1;
    	try{
    		return Double.parseDouble(_model.getRateExpr());
    	}catch(Exception e){
    		ExprEvaluator parser = new ExprEvaluator();
    		try{
    			return parser.parseAndEvalExprForTransition(_model.getRateExpr());
    		}catch(EvaluationException ee){
    			showErrorMessage();
    			return 1.0;
    		}
    		
    	}
    }
    
    private void showErrorMessage(){
    	String message = "Errors in marking-dependent transition rate expression."+
				"\r\n The computation should be aborted";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }
	/**
	 * 
	 * 
	 * 
	 * 
	 */

}
