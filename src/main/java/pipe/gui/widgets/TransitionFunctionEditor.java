package pipe.gui.widgets;

import parser.ExprEvaluator;
import pipe.controllers.TransitionController;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author yufei wang
 */

//for layout managers and more
public class TransitionFunctionEditor extends JPanel {

    private PetriNetView _pnmldata;
    private EscapableDialog _rootPane;
    private TransitionController transitionController;
    private TransitionEditorPanel _editor;

    public TransitionFunctionEditor(TransitionEditorPanel transitionEditorPanel,
                                    EscapableDialog guiDialog,
                                    PetriNetView pnmldata,
                                    TransitionController transitionController) {
        _editor = transitionEditorPanel;
        _pnmldata = pnmldata;
        _rootPane = guiDialog;
        this.transitionController = null;
        init(_pnmldata);
    }

    private void init(PetriNetView pnmldata) {

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
        Iterator iterator1 = pnmldata.getPetriNetObjects();
        Object pn;
        Vector<String> placename = new Vector<String>();
        if (iterator1 != null) {
            while (iterator1.hasNext()) {
                pn = iterator1.next();
                if (pn instanceof PlaceView) {
                    placename.add(((PlaceView) pn).getName());
                }
            }
        }
        JComboBox places = new JComboBox(placename);
        north.add(places);

        // PetriNetViewComponent
        //		Iterator iterator2 = pnmldata.getPetriNetObjects();
        //		Object pn2;
        //		Vector<String> TransitionNames = new Vector<String>();
        //		if (iterator2 != null) {
        //			while (iterator2.hasNext()) {
        //				pn = iterator2.next();
        //				if (pn instanceof TransitionView) {
        //					TransitionNames.add(((TransitionView) pn).getName());
        //				}
        //			}
        //		}
        //JComboBox transitions = new JComboBox(TransitionNames);
        //	north.add(transitions);

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
                    //TODD: DONT PASS NULL
                    ExprEvaluator parser = new ExprEvaluator(null);
                    if (parser.parseAndEvalExprForTransition(func) != null) {
                        _editor.setRate(func);
                        //transitionController.setRate(func);
                    }
                    exit();
                } catch (Exception e) {
                    System.err.println("Error in functional rates expression.");
                    String message =
                            " Expression is invalid. Please check your function.";
                    String title = "Error";
                    JOptionPane.showMessageDialog(null, message, title,
                            JOptionPane.YES_NO_OPTION);
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
