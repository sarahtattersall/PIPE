package pipe.handlers;

import pipe.views.ConnectableView;
import pipe.views.NameLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


public class LabelHandler 
        extends javax.swing.event.MouseInputAdapter {
        //implements java.awt.event.MouseWheelListener { NOU-PERE, i rename aquesta classe a NameLabelHand
   
   private final ConnectableView obj;
   
   private final NameLabel nl;
   
   private Point dragInit = new Point();
   
   
   public LabelHandler(NameLabel _nl, ConnectableView obj) {
      this.obj = obj;
      nl = _nl;
   }
   
   
   @Override
   public void mouseClicked(MouseEvent e) {
      obj.dispatchEvent(e);
   }
   
   
   @Override
   public void mousePressed(MouseEvent e) {
      dragInit = e.getPoint();
      dragInit = javax.swing.SwingUtilities.convertPoint(nl, dragInit, obj);
   }
  

   @Override
   public void mouseDragged(MouseEvent e){
      // 
      if (!SwingUtilities.isLeftMouseButton(e)){
         return;
      }

       dragInit = SwingUtilities.convertPoint(nl, e.getPoint(), obj);
   }
   
   @Override
   public void mouseWheelMoved(MouseWheelEvent e) {
      obj.dispatchEvent(e);
   }
   
}
