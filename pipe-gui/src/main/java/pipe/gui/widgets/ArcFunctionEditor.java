package pipe.gui.widgets;

import pipe.controllers.ArcController;
import pipe.models.component.place.Place;
import pipe.models.petrinet.ExprEvaluator;
import pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

public class ArcFunctionEditor extends JPanel {
    private PetriNet petriNet;
    private EscapableDialog _rootPane;
    private ArcController<?,?> arcController;

    /**
     * Token id
     */
    private String token;
    private ArcWeightEditorPanel awep;

    public ArcFunctionEditor(ArcWeightEditorPanel awep,
                             EscapableDialog guiDialog, PetriNet petriNet,
                             ArcController<?,?> arcController, String token) {
        this.awep = awep;
        this.petriNet = petriNet;
        _rootPane = guiDialog;
        this.arcController = arcController;
        this.token = token;
        init();
    }

    private void init() {
        final JTextArea function = new JTextArea();
        function.setText(arcController.getWeightForToken(token));

        JScrollPane scrollPane = new JScrollPane(function);
        scrollPane.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Weight expression input:"));

        JPanel north = new JPanel();
        north.setBorder(
                javax.swing.BorderFactory.createTitledBorder("Places input:"));

        Collection<String> placeNames = new LinkedList<>();
        for (Place place : petriNet.getPlaces()) {
            placeNames.add(place.getName());
        }

        JComboBox places = new JComboBox(placeNames.toArray());
        north.add(places);

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

                    //TODO: PASS THIS IN

                    ExprEvaluator parser = new ExprEvaluator(petriNet);
                    if (parser.parseAndEvalExpr(func, token) != -1) {
                        awep.setWeight(func, token);
                    }
                    else {
                        if (parser.parseAndEvalExpr(func, token) ==
                                -2) {
                            JOptionPane.showMessageDialog(null,
                                    "Please make sure division and floating numbers are " +
                                            "surrounded by ceil() or floor()");
                            return;
                        }
                        else {
                            System.err.println(
                                    "Error in functional rates expression.");
                            String message =
                                    " Expression is invalid. Please check your function.";
                            String title = "Error";
                            JOptionPane.showMessageDialog(null, message, title,
                                    JOptionPane.YES_NO_OPTION);
                            return;
                        }
                    }
                    exit();
//                } catch (MarkingDividedByNumberException e) {
//                    JOptionPane.showMessageDialog(null,
//                            "Marking-dependent arc weight divided by number not supported.\r\n" +
//                                    "Since this may cause non-integer arc weight.");
//                    return;
                } catch (Exception e) {
                    e.printStackTrace();
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

                String help = "Operators supported:\r\n" +
                        "+\r\n" +
                        "-\r\n" +
                        "*\r\n" +
                        "/\r\n" +
                        "max()\r\n" +
                        "min()\r\n" +
                        "ceil()\r\n" +
                        "floor()\r\n" +
                        "cap(place_name)";
                JOptionPane.showMessageDialog(null, help);
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

        this.setLayout(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(south, BorderLayout.SOUTH);
        this.setSize(100, 100);
        this.setVisible(true);

    }

    private void exit() {
        _rootPane.setVisible(false);
    }
}
