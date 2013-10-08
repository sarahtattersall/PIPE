package pipe.modules.steadyState;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import pipe.calculations.StateList;
import pipe.calculations.StateSpaceGenerator;
import pipe.calculations.SteadyStateSolver;
import pipe.exceptions.MarkingNotIntegerException;
import pipe.exceptions.StateSpaceTooBigException;
import pipe.exceptions.TimelessTrapException;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.ImmediateAbortException;
import pipe.modules.interfaces.IModule;
import pipe.utilities.Expander;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

public class SteadyStateAnalysis implements IModule{

	private static final String MODULE_NAME = "Steady State Analysis";

	private PetriNetChooserPanel sourceFilePanel;
	private ResultsHTMLPane results;

	@Override
	public void start() {
		// Check if this net is a CGSPN. If it is, then this
		// module won't work with it and we must convert it.
		PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
		if(pnmlData.getEnabledTokenClassNumber() > 1){
			Expander expander = new Expander(pnmlData);
			pnmlData = expander.unfold();
			JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black)",
					"Information", JOptionPane.INFORMATION_MESSAGE);
		}
		if(pnmlData.getTokenViews().size() > 1)
		{
			Expander expander = new Expander(pnmlData);
			pnmlData = expander.unfold();
		}
		// Build interface
		EscapableDialog guiDialog =
				new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

		// 1 Set layout
		Container contentPane = guiDialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		// 2 Add file browser
		sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
		contentPane.add(sourceFilePanel);

		// 3 Add results pane
		results = new ResultsHTMLPane(pnmlData.getPNMLName());
		contentPane.add(results);

		// 4 Add button's
		contentPane.add(new ButtonBar("Analyse GSPN", runAnalysis,
				guiDialog.getRootPane()));

		// 5 Make window fit contents' preferred size
		guiDialog.pack();

		// 6 Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);

		try
		{
			guiDialog.setVisible(true);
		}
		catch(NullPointerException e)
		{}		
	}

	private final ActionListener runAnalysis = new ActionListener()
	{
		public void actionPerformed(final ActionEvent arg0)
		{
			if(arg0.getSource() instanceof JButton)
			{
				((JButton) arg0.getSource()).setEnabled(false);
			}
			results.setText(""); // clear
			
			SwingWorker sw = new SwingWorker() {

				protected Object doInBackground()  {
					PetriNetView sourceDataLayer= ApplicationSettings.getApplicationView().getCurrentPetriNetView();

					// This will be used to store the reachability graph data
					File reachabilityGraph = new File("results.rg");

					// This will be used to store the steady state distribution
					double[] pi;

					StringBuilder s =new StringBuilder("<h2>Steady State Analysis Results</h2>");

					if(sourceDataLayer == null)
					{
						JOptionPane.showMessageDialog(null, "Please, choose a source net",
								"Error", JOptionPane.ERROR_MESSAGE);
						return null;
					}

					results.setVisibleProgressBar(true);
					results.setIndeterminateProgressBar(false);

					for(int i = 0; i < 1; i++)
					{
						if(!sourceDataLayer.hasTimedTransitions())
						{
							s.append( "This Petri net has no timed transitions, " + "so GSPN analysis cannot be performed.");
							results.setText(s.toString());
						}
						else
						{

							PNMLWriter.saveTemporaryFile(sourceDataLayer,
									this.getClass().getName());

							try
							{
								results.setStringProgressBar("State Space exploration...");
								results.setIndeterminateProgressBar(true);
								sourceDataLayer.setFunctionalExpressionRelatedPlaces();
								StateSpaceGenerator.generate(sourceDataLayer, reachabilityGraph,
										results);
								System.gc();

								results.setIndeterminateProgressBar(false);
								results.setStringProgressBar("Solving the steady state ...");
								results.setIndeterminateProgressBar(true);

								pi = SteadyStateSolver.solve(reachabilityGraph);
								System.gc();

								results.setIndeterminateProgressBar(false);
								results.setStringProgressBar("Computing and formating resutls ...");
								results.setIndeterminateProgressBar(true);

								// Now format and display the results nicely
								//s += displayResults(sourceDataLayer, reachabilityGraph, pi);
								/*
								 * 
								 */
								StateList tangiblestates;
								try {
									tangiblestates = new StateList(reachabilityGraph, false);
								} catch (IOException e) {
									// s += e.getMessage();
									s.append(e.getMessage());
									return s.toString();
								} catch (StateSpaceTooBigException e) {
									// s += e.getMessage();
									s.append(e.getMessage());
									return s.toString();
								}
								
								String tangibles = renderTangibleStates(sourceDataLayer,
										tangiblestates);
								s.append(tangibles);
								String p = renderPi(pi, tangiblestates);
								s.append("<br>").append(p);
								/*
								 * 
								 */
								
								
								results.setEnabled(true);
								results.setText(s.toString());//
								System.gc();
								return null;
							}
							catch(OutOfMemoryError e)
							{
								System.gc();
								results.setText("");
								s.append("Memory error: " + e.getMessage());

								s.append("<br>Not enough memory. Please use a larger heap size." + "<br>" + "<br>Note:" + "<br>The Java heap size can be specified with the -Xmx option." + "<br>E.g., to use 512MB as heap size, the command line looks like this:" + "<br>java -Xmx512m -classpath ...\n");
								results.setText(s.toString());
								return null;
							}
							catch(ImmediateAbortException e)
							{
								s.append("<br>Error: " + e.getMessage());
								results.setText(s.toString());
								return null;
							}
							catch(TimelessTrapException e)
							{
								s.append("<br>" + e.getMessage());
								results.setText(s.toString());
								return null;
							}
							catch(IOException e)
							{
								s.append("<br>" + e.getMessage());
								results.setText(s.toString());
								return null;
							} catch (MarkingNotIntegerException e1) {
								JOptionPane.showMessageDialog(null,
										"Weighting cannot be less than 0. Please re-enter");
								return null;
							}
						}
					}
					return null;
				}
					@Override
					protected void done() {
						super.done();
						results.setVisibleProgressBar(false);

						if (arg0.getSource() instanceof JButton) {
							((JButton) arg0.getSource()).setEnabled(true);
						}

					}
				
				};
				sw.execute();
			}
		};

		@Override
		public String getName() {
			return MODULE_NAME;
		}
		private String renderPi(double[] pi, StateList states) {
			return ResultsHTMLPane
					.makeTable(new String[] {
							"Steady State Distribution of Tangible States",
							renderLists(pi, states) }, 1, false, false, true, false);
		}

		String renderLists(double[] data, StateList list) {
		      if (list.size() == 0) {
		         return "n/a";
		      }
		      int rows = list.size();
		      
		      ArrayList result = new ArrayList();
		      // add headers to table
		      result.add("Marking");
		      result.add("Value");
		      
		      DecimalFormat f=new DecimalFormat();
		      f.setMaximumFractionDigits(5);
		      for (int i = 0; i < rows; i++) {
		         result.add("<A HREF='#" + list.getID(i) + "'>" +
		                            list.getID(i).toUpperCase() + "</A>");
		         result.add(f.format(data[i]));
		      }
		      return ResultsHTMLPane.makeTable(
		              result.toArray(), 2, false, true, true, true);
		   }
		private void renderPi(double[] pi, StateList states, PrintWriter out) {
			// add headers to table
			out.println("<table cellspacing=\"2\" border=\"0\">");
			out.println("<tr><td class=\"colhead\">" + "Steady State Distribution of Tangible States" + "</td>");
			out.println("</tr>");
			out.println("<tr><td class=\"cell\">");
			
			if(states.size()!=0){
				ArrayList result = new ArrayList();
				// add headers to table
				result.add("Marking");
				result.add("Value");

				DecimalFormat f = new DecimalFormat();
				f.setMaximumFractionDigits(5);
				for (int i = 0; i < states.size(); i++) {
					result.add("<A HREF='#" + states.getID(i) + "'>"
							+ states.getID(i).toUpperCase() + "</A>");
					result.add(f.format(pi[i]));
				}
				writeTable(result.toArray(), 2, false, true, true, true, out);
			}
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			}
		
		
		public String renderTangibleStates(PetriNetView sourceDataLayer,
				StateList tangiblestates) {
			return ResultsHTMLPane
					.makeTable(
							new String[] {
									"Set of Tangible States",
									renderStateSpaceLinked(sourceDataLayer,
											tangiblestates) }, 1, false, false,
							true, false);
		}
		
		public String renderStateSpaceLinked(PetriNetView pnmlData, StateList data) {
		      if ((data.size() == 0) || (data.get(0).length == 0)) {
		         return "n/a";
		      }
		      int markSize = data.get(0).length;
		      ArrayList result=new ArrayList();
		      // add headers to table
		      result.add("");
		      
		      PlaceView[] placeViews = pnmlData.places();//pendent++
		      for (int i = 0; i < markSize; i++) {
		         result.add(placeViews[i].getName());
		         //result.add(_pnmlData.getPlace(i).getName());
		         //result.add("<A NAME= 'M" + i + "'></A>");
		      }
		      
		      for (int i = 0; i < data.size(); i++) {
		         result.add(data.getID(i)+ "<A NAME= 'M" + i + "'></A>");
		         for (int j = 0; j < markSize; j++) {
		            result.add(Integer.toString(data.get(i)[j]));
		         }
		      }
		      return ResultsHTMLPane.makeTable(result.toArray(), markSize + 1, false,
		              true, true, true);
		   }
		
		private static void writeTable(Object[] items, int cols, boolean showLines,
				boolean doShading, boolean columnHeaders, boolean rowHeaders,
				PrintWriter out) {
			out.println("<table border=" + (showLines ? 1 : 0) + " cellspacing=2>");
			int j = 0;
			for (int i = 0; i < items.length; i++) {
				if (j == 0) {
					out.println("<tr"
							+ (doShading ? " class="
									+ (i / cols % 2 == 1 ? "odd>" : "even>") : ">"));
				}

				out.print("<td class=");
				if (i == 0 && items[i] == "") {
					out.print("empty>");
				} else if ((j == 0) && rowHeaders) {
					out.print("rowhead>");
				} else if ((i < cols) && columnHeaders) {
					out.print("colhead>");
				} else {
					out.print("cell>");
				}

				out.println(items[i] + "</td>");
				if (++j == cols) {
					out.println("</tr>");
					j = 0;
				}

			}
			out.println("</table>");
		}

	}