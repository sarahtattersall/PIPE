package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.jeval.Evaluator;





import parser.ExprEvaluator;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

/**
 * @author yufei wang
 *
 * 
 */

//for layout managers and more
public class TransitionFunctionEditor extends JPanel {

	private PetriNetView _pnmldata;
	private EscapableDialog _rootPane;
	private TransitionView _transitionView;
	private TransitionEditorPanel _editor;
	
	public TransitionFunctionEditor(TransitionEditorPanel transitionEditorPanel, EscapableDialog guiDialog,PetriNetView pnmldata, TransitionView _transitionView) {
		_editor=transitionEditorPanel;
		_pnmldata=pnmldata;
		_rootPane=guiDialog;
		this._transitionView=_transitionView;
		init(_pnmldata);
	}
	
	private void init(PetriNetView pnmldata){
		
		final JTextArea function = new JTextArea();
		function.setText(_transitionView.getRateExpr());
		
		JScrollPane scrollPane = new JScrollPane(function);
		scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Rate expression input:"));

		
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.setBorder(javax.swing.BorderFactory.createTitledBorder("Places and transitions input:"));

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
					if(func==null || func.equals("")){
						exit();
						return;
					}
					ExprEvaluator parser = new ExprEvaluator();
					if(parser.parseAndEvalExprForTransition(func)!=null){
						_editor.setRate(func);
						//_transitionView.setRate(func);
					}
					exit();
				} catch (Exception e) {
					System.err.println("Error in functional rates expression.");
					String message = " Expression is invalid. Please check your function.";
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
				   JOptionPane.showMessageDialog(null, help);//, title, JOptionPane.YES_NO_OPTION);
			}

		});
		south.add(okbutton);
		south.add(helpbutton);

		places.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {  
				JComboBox cb = (JComboBox)evt.getSource();
		        String placeName = (String)cb.getSelectedItem();
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
