package pipe.gui.widgets;

import pipe.models.petrinet.ExprEvaluator;
import pipe.controllers.TransitionController;
import pipe.models.petrinet.FunctionalEvaluationException;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author yufei wang
 */

//for layout managers and more
public class TransitionFunctionEditor extends JPanel {

    private EscapableDialog _rootPane;
    private TransitionController transitionController;
    private TransitionEditorPanel _editor;

    public TransitionFunctionEditor(TransitionEditorPanel transitionEditorPanel,
                                    EscapableDialog guiDialog,
                                    TransitionController transitionController, PetriNet petriNet) {
        _editor = transitionEditorPanel;
        _rootPane = guiDialog;
        this.transitionController = transitionController;
        init(petriNet);
    }

    private void init(final PetriNet petriNet) {

        final JTextArea function = new JTextArea();
        function.setText(transitionController.getRateExpr());

        JScrollPane scrollPane = new JScrollPane(function);
        scrollPane.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Rate expression input:"));

        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        north.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Places and transitions input:"));

        //JPanel east = new JPanel(new BorderLayout());
        //JLabel placeLabel = new JLabel("Places");
        // PetriNetViewComponent
        Collection<String> placename = new LinkedList<String>();
        for (Place place : petriNet.getPlaces()) {
            placename.add(place.getName());
        }

        JComboBox places = new JComboBox(placename.toArray());
        north.add(places);

        Collection<String> transitionNames = new LinkedList<String>();
        for(Transition transition : petriNet.getTransitions()) {
            transitionNames.add(transition.getName());
        }

        JComboBox transitions = new JComboBox(transitionNames.toArray());
        north.add(transitions);

        JPanel south = new JPanel(new FlowLayout());
        JButton okbutton = new JButton("OK");
        JButton helpbutton = new JButton("Help");

        okbutton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    String func = function.getText();
                    if (func == null || func.equals("")) {
                        exit();
                        return;
                    }
                    ExprEvaluator parser = new ExprEvaluator(petriNet);
                    if (parser.parseAndEvalExprForTransition(func) != null) {
                        _editor.setRate(func);
                        //transitionController.setRate(func);
                    }
                    exit();
                } catch (FunctionalEvaluationException e) {
                    System.err.println("Error in functional rates expression.");
                    String message =
                            " Expression is invalid. " + e.getMessage();
                    String title = "Error";
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                }
            }
        });
        helpbutton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JTextArea ta = new JTextArea(20, 10);

                String help = "Operators supported:\r\n" +
                        "+\r\n" +
                        "-\r\n" +
                        "*\r\n" +
                        "/\r\n" +
                        "max()\r\n" +
                        "min()\r\n" +
                        "ceil()\r\n" +
                        "floor()\r\n";
                //				JScrollPane jsp = new JScrollPane(ta,
                //						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                //						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                //				ta.append(help);
                //				ta.setEditable(false);
                //				jsp.getViewport().add(ta);
                //				JOptionPane
                //						.showMessageDialog((Component) evt.getSource(), jsp);

                //String message = " Functional rate expression is invalid. Please check your function.";
                String title = "Info";
                JOptionPane.showMessageDialog(null,
                        help);//, title, JOptionPane.YES_NO_OPTION);
            }

        });
        south.add(okbutton);
        south.add(helpbutton);

        places.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JComboBox cb = (JComboBox) evt.getSource();
                String placeName = (String) cb.getSelectedItem();
                function.replaceSelection(placeName);
            }
        });

        //		transitions.addActionListener(new ActionListener() {
        //			public void actionPerformed(ActionEvent evt) {
        //				JComboBox cb = (JComboBox)evt.getSource();
        //		        String transitionName = (String)cb.getSelectedItem();
        //		        function.replaceSelection(transitionName);
        //			}
        //		});

        //_rootPane.setDefaultButton(okbutton);

        this.setLayout(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(south, BorderLayout.SOUTH);
        this.setSize(60, 40);
        this.setVisible(true);

    }

    private void exit() {
        _rootPane.setVisible(false);
    }

}
