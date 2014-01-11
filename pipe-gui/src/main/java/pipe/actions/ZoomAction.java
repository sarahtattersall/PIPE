package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ZoomAction extends GuiAction
{
    public ZoomAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
//        PipeApplicationView _pipeApplicationView = ApplicationSettings.getApplicationView();
//        boolean doZoom = false;
//        try
//        {
//            String actionName = (String) getValue(NAME);
//            PetriNetTab appView = _pipeApplicationView.getCurrentTab();
//            ZoomController zoomer = appView.getZoomController();
//            JViewport thisView = ((JScrollPane) _pipeApplicationView.getFrameForPetriNetTabs().getSelectedComponent()).getViewport();
//            String selection = null, strToTest = null;
//
//            double midpointX = ZoomController.getUnzoomedValue(thisView
//                                                                       .getViewPosition().x
//                                                                       + (thisView.getWidth() * 0.5), zoomer.getPercent());
//            double midpointY = ZoomController.getUnzoomedValue(thisView
//                                                                       .getViewPosition().y
//                                                                       + (thisView.getHeight() * 0.5), zoomer.getPercent());
//
//            if(actionName.equals("Zoom in"))
//            {
//                doZoom = zoomer.zoomIn();
//            }
//            else if(actionName.equals("Zoom out"))
//            {
//                doZoom = zoomer.zoomOut();
//            }
//            else
//            {
//                if(actionName.equals("Zoom"))
//                {
//                    selection = (String) _pipeApplicationView.zoomComboBox.getSelectedItem();
//                }
//                if(e.getSource() instanceof JMenuItem)
//                {
//                    selection = ((JMenuItem) e.getSource()).getText();
//                }
//                strToTest = validatePercent(selection);
//
//                if(strToTest != null)
//                {
//                    // BK: no need to zoom if already at that level
//                    if(zoomer.getPercent() == Integer.parseInt(strToTest))
//                    {
//                        return;
//                    }
//                    else
//                    {
//                        zoomer.setZoom(Integer.parseInt(strToTest));
//                        doZoom = true;
//                    }
//                }
//                else
//                {
//                    return;
//                }
//            }
//            if(doZoom)
//            {
//                _pipeApplicationView.updateZoomCombo();
//                appView.zoomTo(new java.awt.Point((int) midpointX,
//                                                  (int) midpointY));
//            }
//        }
//        catch(ClassCastException cce)
//        {
//            // zoom
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
    }


}
