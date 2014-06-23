package pipe.gui;

import uk.ac.imperial.pipe.layout.Layout;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayoutForm {
    private final static String HIERARCHICAL = "Hierarchical";

    private final static String ORGANIC = "Organic";

    private final PetriNet petriNet;

    private JPanel panel1;

    private JComboBox comboBox1;

    private JSlider rankSpacingSlider;

    private JSlider interHierarchySlider;

    private JSlider parallelEdgeSlider;

    private JSlider intraCellSpacingSlider;

    private JButton layoutButton;

    private JPanel hierarchicalPanel;

    private JPanel organicPanel;

    private JSlider forceConstantSlider;

    private JSlider minDistanceSlider;

    private JRadioButton verticalRadioButton;

    private JRadioButton horizontalRadioButton;

    /**
     * Action called when the layout is changed
     */
    private final ChangeAction changeAction;

    public LayoutForm(PetriNet petriNet, ChangeAction changeAction) {
        this.changeAction = changeAction;
        hierarchicalPanel.setVisible(true);
        organicPanel.setVisible(false);
        intraCellSpacingSlider.setValue(150);
        this.petriNet = petriNet;
        layoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layoutPetriNet();
            }
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displaySettings();
            }
        });
    }

    private void layoutPetriNet() {
        String selectedItem = comboBox1.getSelectedItem().toString();
        switch (selectedItem) {
            case ORGANIC:
                Layout.layoutOrganic(petriNet, forceConstantSlider.getValue(), minDistanceSlider.getValue());
                break;
            case HIERARCHICAL:
                int orientation = getOrientation();
                Layout.layoutHierarchical(petriNet, rankSpacingSlider.getValue(),
                        interHierarchySlider.getValue(),
                        parallelEdgeSlider.getValue(), intraCellSpacingSlider.getValue(), orientation);
                break;
        }
        changeAction.changed(petriNet);
    }

    private int getOrientation() {
        if (verticalRadioButton.isSelected()) {
            return SwingConstants.NORTH;
        }
        return SwingConstants.WEST;
    }

    private void displaySettings() {
        String selectedItem = comboBox1.getSelectedItem().toString();
        switch (selectedItem) {
            case ORGANIC:
                hierarchicalPanel.setVisible(false);
                organicPanel.setVisible(true);
                break;
            case HIERARCHICAL:
                hierarchicalPanel.setVisible(true);
                organicPanel.setVisible(false);
                break;
        }
    }

    public Component getMainPanel() {
        return panel1;
    }

    /**
     * Called when the layout is changed
     */
    public interface ChangeAction {
        void changed(PetriNet petriNet);
    }
}
