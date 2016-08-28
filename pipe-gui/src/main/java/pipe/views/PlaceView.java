package pipe.views;

import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Token;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Graphical representation of a Place
 */
public class PlaceView extends ConnectableView<Place> implements PropertyChangeListener {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(PlaceView.class.getName());
	private boolean repaintedForTesting = false;

    /**
     * Constructor
     * @param model underlying model
     * @param parent parent of the view
     * @param controller Petri net controller of the Petri net the model is housed in
     * @param placeHandler mouse input handler for describing how this view responds to mouse events
     */
    public PlaceView(Place model, Container parent, PetriNetController controller, MouseInputAdapter placeHandler) {
        super(model.getId(), model, controller, controller.getPlaceController(model), parent, new Ellipse2D.Double(-model.getWidth()/2, -model.getHeight()/2, model.getWidth(), model.getHeight()));
        addListenerToModel(this); 
        setMouseListener(placeHandler);

        Rectangle bounds = shape.getBounds();
        Rectangle newBounds = new Rectangle((int)(model.getCentre().getX() + bounds.getX()), (int)(model.getCentre().getY() + bounds.getY()), (int) bounds.getWidth() + getComponentDrawOffset(), (int)bounds.getHeight() + getComponentDrawOffset());
        setBounds(newBounds);
    }


    /**
     *
     * Sets the view mouse listeners
     *
     * @param placeHandler mouse input handler for describing how this view responds to mouse events
     */
    private void setMouseListener(MouseInputAdapter placeHandler) {
        this.addMouseListener(placeHandler);
        this.addMouseWheelListener(placeHandler);
        this.addMouseMotionListener(placeHandler);
    }

    /**
     * Listens for changes in the model
     * All changes cause a repaint
     */
    protected void addListenerToModel(PropertyChangeListener listener) {
    	model.addPropertyChangeListener(listener); 
    }
    /** 
     * Any change to the underlying model cause a repaint
     */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint(); 
		repaintedForTesting = true; 
	}


    /**
     * Paints the Place component taking into account the n q12[umber of tokens from
     * the storeCurrentMarking
     *
     * @param g The PositionGraphics object onto which the Place is drawn.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        Rectangle bounds = shape.getBounds();
        g2.translate(bounds.getWidth()/2, bounds.getHeight()/2);

        if (hasCapacity()) {
            g2.setStroke(new BasicStroke(2.0f));
            setToolTipText("k = " + model.getCapacity());
        } else {
            g2.setStroke(new BasicStroke(1.0f));
            setToolTipText("k = \u221E");
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected() && !ignoreSelection) {
            g2.setColor(GUIConstants.SELECTION_FILL_COLOUR);
        } else {
            g2.setColor(GUIConstants.ELEMENT_FILL_COLOUR);
        }
        g2.fill(shape);

        if (isSelected() && !ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        }
        g2.draw(shape);

        g2.setStroke(new BasicStroke(1.0f));

        // Paints border round a tagged place - paint component is called after any action on the place, so this bit
        // of code doesn't have to be called specially

        if (this.isTagged()) {
            AffineTransform oldTransform = g2.getTransform();

            AffineTransform scaleTransform = new AffineTransform();
            scaleTransform.setToScale(1.2, 1.2);

            g2.transform(scaleTransform);

            g2.translate(-2, -2);

            g2.fill(shape);

            g2.translate(2, 2);

            g2.setTransform(oldTransform);
        }

        g2 = (Graphics2D) g.create();
        paintTokens(g2);

        g2.dispose();
    }

    /**
     * Displays tokens in the Place
     */
    private void paintTokens(Graphics2D g2) {
        int totalMarking = model.getNumberOfTokensStored();
        boolean displayTextualNumber = totalMarking > 5;
        if (displayTextualNumber) {
            displayTextualTokens(g2);
        } else {
            displayOvalTokens(g2);
        }

    }

    /**
     * Displays each token in the Place as an oval
     *
     * If the token does not exist in the petri net the color is displayed
     * as black
     *
     *
     * @param g2 graphics
     */
    private void displayOvalTokens(Graphics2D g2) {
        int offset = 0;

        Map<String, Integer> tokenCounts = model.getTokenCounts();
        for (Map.Entry<String, Integer> entry : tokenCounts.entrySet()) {
            String tokenId = entry.getKey();
            Integer count = entry.getValue();
            Insets insets = getInsets();
            Token token = null;
            try {
                token = petriNetController.getToken(tokenId);
                paintOvalTokens(g2, insets, token.getColor(), count, offset);
            } catch (PetriNetComponentNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                paintOvalTokens(g2, insets, Color.BLACK, count, offset);
            }
            offset += count;
        }
    }

    /**
     * Paints tokens as ovals on the place
     * Can only paint five tokens so tokenNumber represents which number the count is
     * starting at
     *
     * @param g2          graphics
     * @param insets      insets
     * @param color       colour of oval
     * @param count       number of token ovals to paint
     * @param tokenNumber token number
     */
    private void paintOvalTokens(Graphics2D g2, Insets insets, Color color, int count, int tokenNumber) {
        int x = insets.left;
        int y = insets.top;
        g2.setColor(color);
        int WIDTH = 4;
        int HEIGHT = 4;
        int position = tokenNumber;
        for (int i = 0; i < count; i++) {
            switch (position) {
                case 4:
                    g2.drawOval(x + 6, y + 6, WIDTH, HEIGHT);
                    g2.fillOval(x + 6, y + 6, WIDTH, HEIGHT);
                    break;
                case 3:
                    g2.drawOval(x + 18, y + 20, WIDTH, HEIGHT);
                    g2.fillOval(x + 18, y + 20, WIDTH, HEIGHT);
                    break;
                case 2:
                    g2.drawOval(x + 6, y + 20, WIDTH, HEIGHT);
                    g2.fillOval(x + 6, y + 20, WIDTH, HEIGHT);
                    break;
                case 1:
                    g2.drawOval(x + 18, y + 6, WIDTH, HEIGHT);
                    g2.fillOval(x + 18, y + 6, WIDTH, HEIGHT);
                    break;
                case 0:
                    g2.drawOval(x + 12, y + 13, WIDTH, HEIGHT);
                    g2.fillOval(x + 12, y + 13, WIDTH, HEIGHT);
                    break;
                default:
                    break;
            }
            position++;
        }
    }

    /**
     * Display each token in the place as a number textually
     *
     * If the token does not exist in the petri net the color is displayed
     * as black
     *
     * @param g2 graphics
     */
    private void displayTextualTokens(Graphics2D g2) {
        int offset = 0;

        Map<String, Integer> tokenCounts = model.getTokenCounts();
        for (Map.Entry<String, Integer> entry : tokenCounts.entrySet()) {
            String tokenId = entry.getKey();
            Integer count = entry.getValue();
            Insets insets = getInsets();
            try {
                Token token = petriNetController.getToken(tokenId);
                paintTextualTokens(g2, insets, token.getColor(), count, offset);
            } catch (PetriNetComponentNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                paintTextualTokens(g2, insets, Color.BLACK, count, offset);

            } offset += 10;
        }
    }

    /**
     * Paints tokens as a string representation of their number in the place
     *
     * @param g2     graphics
     * @param insets insets
     * @param color  color of token
     * @param count  number of tokens to represent
     * @param offset offset of textual representation relative to the place
     */
    private void paintTextualTokens(Graphics2D g2, Insets insets, Color color, int count, int offset) {
        int x = insets.left;
        int y = insets.top;
        g2.setColor(color);
        if (count > 999) {
            g2.drawString(String.valueOf(count), x, y + 10 + offset);
        } else if (count > 99) {
            g2.drawString(String.valueOf(count), x + 3, y + 10 + offset);
        } else if (count > 9) {
            g2.drawString(String.valueOf(count), x + 7, y + 10 + offset);
        } else if (count != 0) {
            g2.drawString(String.valueOf(count), x + 12, y + 10 + offset);
        }
    }

    /**
     *
     * @return true if the underlying model has a capacity
     */
    private boolean hasCapacity() {
        return model.getCapacity() > 0;
    }


    /**
     *
     * @return if the place is tagged
     */
    private boolean isTagged() {
        return false;
    }

    /**
     * Adds the place id label to the container
     * @param container to add itself to
     */
    @Override
    public void addToContainer(Container container) {
        addLabelToContainer(container);
    }


	protected boolean repaintedForTesting() {
		return repaintedForTesting;
	}



}

