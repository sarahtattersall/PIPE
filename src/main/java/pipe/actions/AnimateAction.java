package pipe.actions;

import pipe.gui.*;
import pipe.models.PipeApplicationModel;
import pipe.views.PetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AnimateAction extends GuiAction
    {
        private final int typeID;

        public AnimateAction(String name, int typeID, String tooltip, String keystroke)
        {
            super(name, tooltip, keystroke);
            this.typeID = typeID;
        }

        public void actionPerformed(ActionEvent ae)
        {
            PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
            PetriNetTab currentTab = pipeApplicationView.getCurrentTab();
            if(currentTab == null)
            {
                return;
            }

            AnimationHistory animationHistory = pipeApplicationView.getAnimationHistory();

            PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();

            switch(typeID)
            {
                case Constants.START:
                    try
                    {
                       pipeApplicationView.setAnimationMode(!currentTab.isInAnimationMode());
                        if(!currentTab.isInAnimationMode())
                        {
                            applicationModel.restoreMode();
                            PetriNetViewComponent.ignoreSelection(false);
                        }
                        else
                        {
                            applicationModel.setMode(typeID);
                            PetriNetViewComponent.ignoreSelection(true);
                            // Do we keep the selection??
                            currentTab.getSelectionObject().clearSelection();
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(pipeApplicationView, e.toString(),
                                                      "Animation Mode Error", JOptionPane.ERROR_MESSAGE);
                        applicationModel.startAction.setSelected(false);
                        currentTab.changeAnimationMode(false);
                    }
                    applicationModel.stepforwardAction.setEnabled(false);
                    applicationModel.stepbackwardAction.setEnabled(false);
                    break;

                case Constants.RANDOM:
                    animationHistory.clearStepsForward();
                    pipeApplicationView.getAnimator().doRandomFiring();
                    applicationModel.stepforwardAction.setEnabled(animationHistory.isStepForwardAllowed());
                    applicationModel.stepbackwardAction.setEnabled(animationHistory.isStepBackAllowed());
                    pipeApplicationView.getAnimator().updateArcAndTran();
                    break;

                case Constants.STEPFORWARD:
                    animationHistory.stepForward();
                    pipeApplicationView.getAnimator().stepForward();
                    applicationModel.stepforwardAction.setEnabled(animationHistory.isStepForwardAllowed());
                    applicationModel.stepbackwardAction.setEnabled(animationHistory.isStepBackAllowed());
                    pipeApplicationView.getAnimator().updateArcAndTran();
                    break;

                case Constants.STEPBACKWARD:
                    animationHistory.stepBackwards();
                    pipeApplicationView.getAnimator().stepBack();
                    applicationModel.stepforwardAction.setEnabled(animationHistory.isStepForwardAllowed());
                    applicationModel.stepbackwardAction.setEnabled(animationHistory.isStepBackAllowed());
                    pipeApplicationView.getAnimator().updateArcAndTran();
                    break;

                case Constants.ANIMATE:
                    Animator a = pipeApplicationView.getAnimator();

                    if(a.getNumberSequences() > 0)
                    {
                        a.setNumberSequences(0); // stop animation
                        setSelected(false);
                    }
                    else
                    {
                        applicationModel.stepbackwardAction.setEnabled(false);
                        applicationModel.stepforwardAction.setEnabled(false);
                        applicationModel.randomAction.setEnabled(false);
                        setSelected(true);
                        animationHistory.clearStepsForward();
                        pipeApplicationView.getAnimator().startRandomFiring();
                        pipeApplicationView.getAnimator().updateArcAndTran();
                    }
                    break;

                default:
                    break;
            }
        }

    }
