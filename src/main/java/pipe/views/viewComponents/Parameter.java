/*
 * Created on 
 * Author is 
 */
package pipe.views.viewComponents;

import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.ParameterName;

import java.awt.*;


public abstract class Parameter extends Note
{
         
   String name;
   
   boolean valueChanged = false;
   
   
   Parameter(int x, int y){
      super (x, y);
      _copyPasteable = false;
      note.setLineWrap(false);
      note.setWrapStyleWord(false);
   }
   

   public String getName() {
      return name;
   }

   
   public HistoryItem setParameterName(String _name) {
      String oldName = name;
      name = _name;
      valueChanged = true;
      return new ParameterName(this, oldName, name);
   }
   
   
   public String toString(){
      return name;
   }
   
   
   public abstract void enableEditMode();

   
   public abstract void update();

   
   public void paintComponent(Graphics g) {
      //updateBounds();
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.transform(ZoomController.getTransform(_zoomPercentage));
      g2.setStroke(new BasicStroke(1.5f));
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
                          RenderingHints.VALUE_STROKE_NORMALIZE);

      if (_selected && !_ignoreSelection) {
         g2.setPaint(Constants.SELECTION_FILL_COLOUR);
         g2.fill(noteRect);
         g2.setPaint(Constants.SELECTION_LINE_COLOUR);
      } else {
         g2.setPaint(Constants.ELEMENT_FILL_COLOUR);         
         g2.fill(noteRect);
         g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
      }
      g2.draw(noteRect);
   }   
   
}
