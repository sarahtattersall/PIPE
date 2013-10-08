package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import parser.ExprEvaluator;
import parser.MarkingDividedByNumberException;
import pipe.views.ArcView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TokenView;

public class ArcFunctionEditor extends JPanel{
	private PetriNetView _pnmldata;
	private EscapableDialog _rootPane;
	private ArcView _arcView;
	private TokenView token;
	private ArcWeightEditorPanel awep;
	public ArcFunctionEditor(String id, ArcWeightEditorPanel awep, EscapableDialog guiDialog,PetriNetView pnmldata, ArcView _arcView,TokenView tc) {
		this.awep=awep;
		_pnmldata=pnmldata;
		_rootPane=guiDialog;
		this._arcView=_arcView;
		this.token=tc;
		init(_pnmldata);
	}
	
	private void init(PetriNetView pnmldata){
		final JTextArea function = new JTextArea();

//		if(!_arcView.getWeightExpr(token.getID()).equals("-1")){
//			function.setText(_arcView.getWeightExpr(token.getID()));
//		}else{
//			function.setText("");
//		}
		function.setText(_arcView.getWeightFunctionOfTokenClass(token.getID()));
		
		JScrollPane scrollPane = new JScrollPane(function);
		scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Weight expression input:"));

		JPanel north = new JPanel();
		north.setBorder(javax.swing.BorderFactory.createTitledBorder("Places input:"));


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
					if(parser.parseAndEvalExpr(func,token.getID())!=-1){// && !parser.parseAndEvalExpr(func).){
						//_arcView.setWeightFunctionByID(token.getID(), func);
						awep.setWeight(func, token.getID());
						//awep.updateWeight();
						//_arcView.setfunctionalWeightExpr(token.getID(), func);
						//_arcView.setWeightType(token.getID(), "F");
					}else if (parser.parseAndEvalExpr(func,token.getID()) == -2){
						JOptionPane.showMessageDialog(null,
								"Please make sure division and floating numbers are "+
						"surrounded by ceil() or floor()");
						return;
					}else{
						System.err.println("Error in functional rates expression.");
						String message = " Expression is invalid. Please check your function.";
		                String title = "Error";
		                JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
		                return;
					}
					exit();
				}  catch (MarkingDividedByNumberException e) {
					JOptionPane.showMessageDialog(null,
							"Marking-dependent arc weight divided by number not supported.\r\n"
							+"Since this may cause non-integer arc weight.");
					return;
				}catch (Exception e) {
					e.printStackTrace();
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
//				JTextArea ta = new JTextArea(10, 10);
//
//				JScrollPane jsp = new JScrollPane(ta,
//						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//				ta.append("+\r\n-\r\n*\r\n/\r\nmax()\r\nmin()\r\n");
//				ta.setEditable(false);
//				jsp.getViewport().add(ta);
//				JOptionPane
//						.showMessageDialog((Component) evt.getSource(), jsp);
				
				
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
