package pipe.handlers;

import pipe.views.ConnectableView;
import pipe.views.viewComponents.NameLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


public class LabelHandler 
        extends javax.swing.event.MouseInputAdapter {
        //implements java.awt.event.MouseWheelListener { NOU-PERE, i rename aquesta classe a NameLabelHand
   
   private final ConnectableView _obj;
   
   private final NameLabel nl;
   
   private Point dragInit = new Point();
   
   
   public LabelHandler(NameLabel _nl, ConnectableView _obj) {
      this._obj = _obj;
      nl = _nl;
   }
   
   
   public void mouseClicked(MouseEvent e) {
      _obj.dispatchEvent(e);
   }
   
   
   public void mousePressed(MouseEvent e) {
      dragInit = e.getPoint(); //
      dragInit = javax.swing.SwingUtilities.convertPoint(nl, dragInit, _obj);
   }
  

   public void mouseDragged(MouseEvent e){
      // 
      if (!SwingUtilities.isLeftMouseButton(e)){
         return;
      }
      
      Point p = javax.swing.SwingUtilities.convertPoint(nl, e.getPoint(), _obj);
      //obj.setNameOffsetX((e.getXOnScreen() - dragInit.x)); //causes exception in Windows!
      //obj.setNameOffsetY((e.getYOnScreen() - dragInit.y)); //causes exception in Windows!
      //dragInit = e.getLocationOnScreen(); //causes exception in Windows!
      _obj.setNameOffsetX((p.x - dragInit.x));
      _obj.setNameOffsetY((p.y - dragInit.y));
      dragInit = p;
      _obj.update();
   }   
   
   public void mouseWheelMoved(MouseWheelEvent e) {
      _obj.dispatchEvent(e);
   }
   
}
