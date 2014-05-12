package pipe.gui.widgets;

import pipe.controllers.AnnotationController;

import javax.swing.*;

/**
 * Annotation panel used to change text of the annotation
 */
public class AnnotationEditorPanel extends javax.swing.JPanel {

    private final AnnotationController annotationController;

    private javax.swing.JTextArea textArea;

    /**
     * Creates new form ParameterPanel
     */
    public AnnotationEditorPanel(AnnotationController annotationController) {
        this.annotationController = annotationController;
        initComponents();
        textArea.setText(annotationController.getText());
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        JPanel panel = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();
        textArea = new javax.swing.JTextArea();
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton();
        JButton cancelButton = new JButton();

        setLayout(new java.awt.GridBagLayout());

        setMaximumSize(new java.awt.Dimension(239, 208));
        setMinimumSize(new java.awt.Dimension(239, 208));
        panel.setLayout(new java.awt.GridLayout(1, 0));

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edit Annotation"));
        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        panel.add(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(panel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(75, 25));
        okButton.setMinimumSize(new java.awt.Dimension(75, 25));
        okButton.setPreferredSize(new java.awt.Dimension(75, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        buttonPanel.add(okButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        buttonPanel.add(cancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(buttonPanel, gridBagConstraints);

    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        annotationController.setText(textArea.getText());
        //TODO: USE ANNOTATION CONTROLLER INSTEAD AND AFF HISTORY ITEM
        exit();
    }

    private void exit() {
        //Provisional!
        getRootPane().getParent().setVisible(false);
//        this.getParent().getParent().getParent().getParent().setVisible(false);
    }

    private void cancelButtonActionPerformed(
            java.awt.event.ActionEvent evt) {
        exit();
    }
}
