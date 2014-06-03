package pipe.views;

import uk.ac.imperial.pipe.exceptions.TokenLockedException;
import uk.ac.imperial.pipe.models.petrinet.Token;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Observable;


/**
 * Used to display tokens as dots/numbers on the petri net in a place
 */
public class TokenView extends Observable  {
    private Token model;  // Steve Doubleday was final, but changed for replace(tokenView)

    public TokenView(Token model) {
        this.model = model;
    }

    public void update(Graphics canvas, Insets insets, int offset, int tempTotalMarking, int currentMarking) {
        paint(canvas, insets, offset, tempTotalMarking, currentMarking);
    }

    private void paint(Graphics canvas, Insets insets, int offset, int tempTotalMarking, int currentMarking) {
        if (tempTotalMarking > 5) {
            paintAsANumber(canvas, insets, offset, currentMarking);
        } else {
            paintAsAnOval(canvas, insets, tempTotalMarking, currentMarking);
        }
    }

    private void paintAsANumber(Graphics canvas, Insets insets, int offset, int currentMarking) {
        int x = insets.left;
        int y = insets.top;
        canvas.setColor(getColor());
        if (currentMarking > 999) {
            canvas.drawString(String.valueOf(currentMarking), x, y + 10 + offset);
        } else if (currentMarking > 99) {
            canvas.drawString(String.valueOf(currentMarking), x + 3, y + 10 + offset);
        } else if (currentMarking > 9) {
            canvas.drawString(String.valueOf(currentMarking), x + 7, y + 10 + offset);
        } else if (currentMarking != 0) {
            canvas.drawString(String.valueOf(currentMarking), x + 12, y + 10 + offset);
        }
    }

    public Color getColor() {
        return model.getColor();
    }

    public void setColor(Color colour) {
        model.setColor(colour);
    }

    private void paintAsAnOval(Graphics canvas, Insets insets, int tempTotalMarking, int currentMarking) {
        int x = insets.left;
        int y = insets.top;
        canvas.setColor(getColor());
        int WIDTH = 4;
        int HEIGHT = 4;
        int marking = tempTotalMarking;
        for (int i = 0; i < currentMarking; i++) {

            switch (marking) {
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
            marking--;
        }
    }

    public Token getModel() {
        return model;
    }


    //TODO: DELETE
    public int getCurrentMarking() {
        return 0;
    }

    //TODO: DELETE
    public void setCurrentMarking(int marking) {
    }

    public String getID() {
        return model.getId();
    }

    public void setID(String id) {
        model.setId(id);
    }

    /**
     * Sets enabled = false, and notifies any observers.  Observers should delete references to this TokenView.
     *
     * @throws TokenLockedException
     */
    public void disableAndNotifyObservers() throws TokenLockedException {
        setEnabled(false);
        setChanged();
        notifyObservers(null);
    }

    //TODO: DELETE
    public boolean isEnabled() {
        return true;
    }

    /**
     * Disabling a TokenView should be done through disableAndNotifyObservers()
     *
     * @param enabled
     * @throws TokenLockedException
     */
    //TODO: DELETE
    protected void setEnabled(boolean enabled) throws TokenLockedException {
    }

    protected String getNormalizedID() {
        return normalize(getID());
    }

    private String normalize(String target) {
        if (target == null) {
            return "";
        } else {
            return target.trim().toLowerCase();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TokenView:");
        builder.append(" Id=");
        builder.append(model.getId());
        builder.append(", Color=");
        builder.append(model.getColor());
        return builder.toString();
    }
}
