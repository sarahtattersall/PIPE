package pipe.gui.widgets;

import pipe.controllers.ArcController;
import uk.ac.imperial.pipe.animation.AnimationUtils;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.parsers.FunctionalResults;
import uk.ac.imperial.pipe.parsers.FunctionalWeightParser;
import uk.ac.imperial.pipe.parsers.PetriNetWeightParser;
import uk.ac.imperial.pipe.parsers.StateEvalVisitor;
import uk.ac.imperial.state.State;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ArcFunctionEditor extends JPanel {
    private PetriNet petriNet;

    private EscapableDialog rootPane;

    private ArcController<?, ?> arcController;

    /**
     * Token id
     */
    private String token;

    private ArcWeightEditorPanel weightEditorPanel;

    public ArcFunctionEditor(ArcWeightEditorPanel weightEditorPanel, EscapableDialog guiDialog, PetriNet petriNet,
                             ArcController<?, ?> arcController, String token) {
        this.weightEditorPanel = weightEditorPanel;
        this.petriNet = petriNet;
        rootPane = guiDialog;
        this.arcController = arcController;
        this.token = token;
        init();
    }

    private void init() {
        final JTextArea function = new JTextArea();
        function.setText(arcController.getWeightForToken(token));

        JScrollPane scrollPane = new JScrollPane(function);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Weight expression input:"));

        JPanel north = new JPanel();
        north.setBorder(javax.swing.BorderFactory.createTitledBorder("Places input:"));

        Collection<String> placeNames = new LinkedList<>();
        for (Place place : petriNet.getPlaces()) {
            placeNames.add(place.getName());
        }

        JComboBox places = new JComboBox(placeNames.toArray());
        north.add(places);

        JPanel south = new JPanel(new FlowLayout());
        JButton okbutton = new JButton("OK");
        JButton helpbutton = new JButton("Help");

        okbutton.addActionListener(new ArcOKAction(function));
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
        rootPane.setVisible(false);
    }

    /**
     * Concatenates a list of errors into a single string
     *
     * @param errors
     * @return single string representation of the error, which is just the errors
     * appended together and seperated via a comma.
     */
    private String concatenateErrors(List<String> errors) {
        StringBuilder builder = new StringBuilder();
        for (String error : errors) {
            builder.append(error).append(", ");
        }
        return builder.toString();
    }

    private final class ArcOKAction implements ActionListener {

        private final JTextArea function;

        private ArcOKAction(JTextArea function) {
            this.function = function;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String func = function.getText();
            if (func == null || func.equals("")) {
                exit();
                return;
            }

            State state = AnimationUtils.getState(petriNet);
            StateEvalVisitor evalVisitor = new StateEvalVisitor(petriNet, state);
            FunctionalWeightParser<Double> parser = new PetriNetWeightParser(evalVisitor, petriNet);
            FunctionalResults<Double> results = parser.evaluateExpression(func);
            if (!results.hasErrors()) {
                weightEditorPanel.setWeight(func, token);
            } else {
                List<String> errors = results.getErrors();
                String concatenated = concatenateErrors(errors);
                JOptionPane.showMessageDialog(null, concatenated);
                return;
            }
            exit();
        }
    }
}
