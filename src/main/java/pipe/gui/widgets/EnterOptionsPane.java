package pipe.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Class that creates a dialog box for the user to enter options for a response time
 * analysis
 *
 * @author Oliver Haggarty
 *
 */
public class EnterOptionsPane 
        extends JPanel {
   
   //Components in panel
   private final JTextField startStatesField;
    private final JTextField targetStatesField;
    private final JTextField stepField;
    private final JTextField TStopField;
    private final JTextField TStartField;
    private final JTextField bufferField;
    private final JTextArea errorField;
   private JScrollPane scroller;
   private final JCheckBox calcRTbox;
    private final JCheckBox calcCDFbox;
   private final HadoopPane hadpne;
   
   
   /**
    * Constructs the dialog box and displays it on screen
    *
    * @param defaultStatus
    */
   public EnterOptionsPane(boolean defaultStatus) {
      super(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;

       JLabel startStLbl = new JLabel("Start states: ");
      c.gridx = 0;
      c.gridy = 0;
      this.add(startStLbl, c);
      startStatesField = new JTextField(20);
      c.weightx = 0.5;
      c.gridx = 1;
      c.gridwidth = 5;
      this.add(startStatesField, c);

       JLabel targetStLbl = new JLabel("Target States: ");
      c.weightx = 0;
      c.gridx = 0;
      c.gridy = 1;
      c.gridwidth = 1;
      this.add(targetStLbl, c);
      targetStatesField = new JTextField(20);
      c.weightx = 0.5;
      c.gridx = 1;
      c.gridwidth = 5;
      this.add(targetStatesField, c);

       JLabel TStartLbl = new JLabel("T Start: ");
      c.weightx = 0;
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 1;
      this.add(TStartLbl, c);
      TStartField = new JTextField(5);
      c.weightx = 0.5;
      c.gridx = 1;
      this.add(TStartField, c);

       JLabel TStopLbl = new JLabel("T Stop: ");
      //c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.gridx = 2;
      c.gridy = 2;
      c.gridwidth = 1;
      this.add(TStopLbl, c);
      TStopField = new JTextField(5);
      c.weightx = 0.5;
      c.gridx = 3;
      this.add(TStopField, c);

       JLabel stepLbl = new JLabel("Step size: ");
      c.weightx = 0;
      c.gridx = 4;
      this.add(stepLbl, c);
      stepField = new JTextField(5);
      c.weightx = 0.5;
      c.gridx = 5;
      this.add(stepField, c);
      
      calcRTbox = new JCheckBox("Calculate Response Time");
      c.weightx = 0.5;
      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 3;
      this.add(calcRTbox, c);
      
      calcCDFbox = new JCheckBox("Calculate CDF");
      c.weightx = 0.5;
      c.gridx = 0;
      c.gridy = 4;
      c.gridwidth = 3;
      this.add(calcCDFbox, c);

       JLabel bufferLbl = new JLabel("Buffer Size (MB): ");
      c.weightx = 0;
      c.gridx = 3;
      c.gridy = 3;
      c.gridwidth = 2;
      this.add(bufferLbl, c);
      
      bufferField = new JTextField();
      c.weightx = 0.5;
      c.gridx = 5;
      c.gridy = 3;
      c.gridwidth = 1;
      this.add(bufferField, c);
      
      hadpne = new HadoopPane();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = 5;
      c.gridwidth = 6;
      this.add(hadpne, c);
      
      errorField = new JTextArea();
      errorField.setOpaque(false);
      errorField.setEditable(false);
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = 6;
      c.gridwidth = 6;
      this.add(errorField, c);
      
      //startStatesField.setText(/*"(#start) == 1"*/"#(P1) == 6 && #(P2) == 6");
      //targetStatesField.setText(/*"(#P12) == 1"*/"#(P12s) == 1");
      //stepField.setText("0.5");
      //TStartField.setText("1");
      //TStopField.setText("20");
      errorField.setBorder(new TitledBorder(new EtchedBorder(), "Error Messages"));
      hadpne.chkbx.setSelected(defaultStatus);
      /*if(defaultStatus == true)
       //hadpne.numMapsField.setText("256");
       else
       hadpne.numMapsField.setText("");*/
      //startStatesField.setVisible(true);
      
      this.setPreferredSize(new Dimension(400, 250));
      this.setBorder(new TitledBorder(new EtchedBorder(),"Input Fields"));
   }
   
   
   /**
    * Returns contents of StartStatea text field
    * @return
    */
   public String getStartStates() {
      return startStatesField.getText();
   }
   
   
   /**
    * Returns contents of TargetStates text fiedl
    * @return
    */
   public String getTargetStates() {
      return targetStatesField.getText();
   }
   
   
   /**
    * Returns contents of StepSize text field as a double
    * @return
    * @throws NumberFormatException
    */
   public double getStepSize() throws NumberFormatException {
      return Double.parseDouble(stepField.getText());
   }
   
   
   /**
    * Return contents of TStart text field as a double
    * @return
    * @throws NumberFormatException
    */
   public double getTStart() throws NumberFormatException {
      return Double.parseDouble(TStartField.getText());
   }
   
   
   /**
    * Returns contents fo TStop text field as a double
    * @return
    * @throws NumberFormatException
    */
   public double getTStop() throws NumberFormatException {
      return Double.parseDouble(TStopField.getText());
   }
   
   
   /**
    * Returns true if user has selected to analyse as a MapReduce job
    * @return
    */
   public boolean isMapRedJob() {
      return hadpne.chkbx.isSelected();
   }
   
   
   public boolean isRT() {
      return calcRTbox.isSelected();
   }
   
   
   public boolean isCDF() {
      return calcCDFbox.isSelected();
   }
   
   
   /**
    * Returns the number of maps entered by the user as an integer
    * @return
    */
   public int getNumMaps() {
      return Integer.parseInt(hadpne.numMapsField.getText());
   }
   
   
   public int getBufferSize() {
      return Integer.parseInt(bufferField.getText());
   }
   
   
   public void setRT(boolean exp) {
      calcRTbox.setSelected(exp);
   }
   
   
   public void setCDF(boolean exp) {
      calcCDFbox.setSelected(exp);
   }
   
   
   public void setStartStates(String exp) {
      startStatesField.setText(exp);
   }
   
   
   public void setTargetStates(String exp) {
      targetStatesField.setText(exp);
   }
   
   
   public void setTStart(String exp) {
      TStartField.setText(exp);
   }
   
   
   public void setTStop(String exp) {
      TStopField.setText(exp);
   }
   
   
   public void setStepSize(String exp) {
      stepField.setText(exp);
   }
   
   
   public void setNumMaps(String exp) {
      hadpne.numMapsField.setText(exp);
   }
   
   
   public void setRunAsMap(boolean exp) {
      hadpne.chkbx.setSelected(exp);
   }
   
   
   public void setBufferSize(String exp) {
      bufferField.setText(exp);
   }
   
   
   /**
    * Sets the contents of the Error Message text area
    * @param msg
    */
   public void setErrorMessage(String msg) {
      errorField.setForeground(Color.RED);
      errorField.setText(msg);
   }

   
   
   /**
    * Associated panel that contains input options related to selecting whether
    * the analysis should be run locally or as a MapReduce job
    *
    * @author Oliver Haggarty - August 2007
    *
    */
   private class HadoopPane extends JPanel {
      private final JCheckBox chkbx = new JCheckBox("Run as Map Reduce job", true);
      private final JLabel numMapsLbl = new JLabel("Number of Maps: ");
      private final JTextField numMapsField = new JTextField(5);
      
      /**
       * Creates the panel
       */
      public HadoopPane() {
         super(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.HORIZONTAL;
         c.weightx = 1;
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 2;
         this.add(chkbx, c);
         
         c.fill = GridBagConstraints.NONE;
         c.gridwidth = 1;
         c.weightx = 0;
         c.gridx = 3;
         c.gridy = 0;
         this.add(numMapsLbl,c);
         
         c.fill = GridBagConstraints.HORIZONTAL;
         c.weightx = 0.5;
         c.gridx = 4;
         c.gridy = 0;
         this.add(numMapsField, c);
         
         this.setPreferredSize(new Dimension(400, 40));
         this.setBorder(new EtchedBorder());
      }
   }
   
}
