/*
 * Created on 28-Jun-2005
 */
package pipe.modules.gspn;

import pipe.calculations.StateList;
import pipe.calculations.StateSpaceGenerator;
import pipe.calculations.SteadyStateSolver;
import pipe.exceptions.MarkingNotIntegerException;
import pipe.exceptions.StateSpaceTooBigException;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.ImmediateAbortException;
import pipe.modules.interfaces.IModule;
import pipe.utilities.Expander;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author Nadeem
 *         <p/>
 *         This class enables steady state analysis of GSPN's created using
 *         PIPE2
 *         
 * @author yufeiwang (modification for marking-dependent arc and transition)
 */
public class GSPNNew extends GSPN implements IModule {

	private static final String MODULE_NAME = "GSPN Analysis";
	private final File output = new File("GSPN_Analysis.html");

	public void start() {
		// Check if this net is a CGSPN. If it is, then this
		// module won't work with it and we must convert it.
//		PetriNetView pnmlData = ApplicationSettings.getApplicationView()
//				.getCurrentPetriNetView();
//		//if (pnmlData.getTokenViews().size() > 1) {
//		if(pnmlData.getEnabledTokenClassNumber() > 1){
//			Expander expander = new Expander(pnmlData);
//			pnmlData = expander.unfoldOld();
//			JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black)",
//					"Information", JOptionPane.INFORMATION_MESSAGE);
//		}
//		// Build interface
//		EscapableDialog guiDialog = new EscapableDialog(
//				ApplicationSettings.getApplicationView(), MODULE_NAME, true);
//
//		// 1 Set layout
//		Container contentPane = guiDialog.getContentPane();
//		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
//
//		// 2 Add file browser
//		sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
//		contentPane.add(sourceFilePanel);
//
//		// 3 Add results pane
//		results = new ResultsHTMLPane(pnmlData.getPNMLName());
//		contentPane.add(results);
//
//		// 4 Add button's
//		contentPane.add(new ButtonBar("Analyse GSPN", runAnalysis, guiDialog
//				.getRootPane()));
//
//		// 5 Make window fit contents' preferred size
//		guiDialog.pack();
//
//		// 6 Move window to the middle of the screen
//		guiDialog.setLocationRelativeTo(null);
//
//		try {
//			guiDialog.setVisible(true);
//		} catch (NullPointerException e) {
//		}

	}

	/**
	 * Analyse button click handler
	 */
	private final ActionListener runAnalysis = new ActionListener() {

		public void actionPerformed(final ActionEvent arg0) {

			if (arg0.getSource() instanceof JButton) {
				((JButton) arg0.getSource()).setEnabled(false);
			}
			results.setText(""); // clear

			SwingWorker sw = new SwingWorker() {

				@Override
				protected Object doInBackground() {

					long start = new Date().getTime();
					long efinished;
					long ssdfinished;
					long allfinished;
					double explorationtime;
					double steadystatetime;
					double totaltime;

					//PetriNetView sourceDataLayer = sourceFilePanel
						//	.getDataLayer();
					PetriNetView sourceDataLayer= ApplicationSettings.getApplicationView().getCurrentPetriNetView();

					// This will be used to store the reachability graph data
					File reachabilityGraph = new File("results.rg");

					// This will be used to store the steady state distribution
					double[] pi;

					String s = "<h2>GSPN Steady State Analysis Results</h2>";

					if (sourceDataLayer == null) {
						JOptionPane.showMessageDialog(null,
								"Please, choose a source net", "Error",
								JOptionPane.ERROR_MESSAGE);
						return null;
					}

					results.setVisibleProgressBar(true);
					results.setIndeterminateProgressBar(false);

					for (int i = 0; i < 1; i++) {
						if (!sourceDataLayer.hasTimedTransitions()) {
							s += "This Petri net has no timed transitions, "
									+ "so GSPN analysis cannot be performed.";
							results.setText(s);
						} else {

							PNMLWriter.saveTemporaryFile(sourceDataLayer, this
									.getClass().getName());

							try {
								results.setStringProgressBar("State Space exploration...");
								results.setIndeterminateProgressBar(true);
								
								//sourceDataLayer.setFunctionalExpressionRelatedPlaces();
								//sourceDataLayer.backUpPlaceViewsMarking();
									StateSpaceGenerator.generate(sourceDataLayer,
											reachabilityGraph, results);
								//sourceDataLayer.restorePlaceViewsMarking();
								efinished = new Date().getTime();
								System.gc();

								results.setIndeterminateProgressBar(false);
								results.setStringProgressBar("Solving the steady state ...");
								results.setIndeterminateProgressBar(true);

								pi = SteadyStateSolver.solve(reachabilityGraph);
								ssdfinished = new Date().getTime();
								System.gc();

								results.setIndeterminateProgressBar(false);
								results.setStringProgressBar("Computing and formating resutls ...");
								results.setIndeterminateProgressBar(true);

								// Now format and display the results nicely
								s += displayResults(sourceDataLayer,
										reachabilityGraph, pi);
								allfinished = new Date().getTime();
								explorationtime = (efinished - start) / 1000.0;
								steadystatetime = (ssdfinished - efinished) / 1000.0;
								totaltime = (allfinished - start) / 1000.0;
								DecimalFormat f = new DecimalFormat();
								f.setMaximumFractionDigits(5);
								s += "<br>State space exploration took "
										+ f.format(explorationtime) + "s";
								s += "<br>Solving the steady state distribution took "
										+ f.format(steadystatetime) + "s";
								s += "<br>Total time was "
										+ f.format(totaltime) + "s";

								results.setEnabled(true);
								results.setText(s);//
								System.gc();
							} catch (OutOfMemoryError e) {
								System.gc();
								results.setText("");
								s += "Memory error: " + e.getMessage();

								s += "<br>Not enough memory. Please use a larger heap size."
										+ "<br>"
										+ "<br>Note:"
										+ "<br>The Java heap size can be specified with the -Xmx option."
										+ "<br>E.g., to use 512MB as heap size, the command line looks like this:"
										+ "<br>java -Xmx512m -classpath ...\n";
								results.setText(s);
								return null;
							} catch (ImmediateAbortException e) {
								s += "<br>Error: " + e.getMessage();
								results.setText(s);
								return null;
//							} catch (TimelessTrapException e) {
//								s += "<br>" + e.getMessage();
//								results.setText(s);
//								return null;
//							} catch (IOException e) {
//								s += "<br>" + e.getMessage();
//								results.setText(s);
//								return null;
							} catch (MarkingNotIntegerException e) {
								JOptionPane.showMessageDialog(null,
										"Functional arc weight cannot have non-integer value. Please check again.");
								sourceDataLayer.restorePlaceViewsMarking();
								return null;
							
							}catch(Exception e){
								e.printStackTrace();
							}finally {
								
							}

							if (reachabilityGraph.exists()) {
								if (!reachabilityGraph.delete()) {
									System.err
											.println("Could not delete intermediate file.");
								}
							}
							pi = null;
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

	/**
	 * displayResults() Takes the reachability graph file and the steady state
	 * distribution and produces nicely formatted output showing these results
	 * plus more results based on them.
	 * 
	 * @param sourceDataLayer
	 *            The GSPN model data
	 * @param rgfile
	 *            The reachability graph
	 * @param pi
	 *            The steady state distribution
	 * @return
	 */
	private String displayResults(PetriNetView sourceDataLayer, File rgfile,
			double[] pi) {
		StateList tangiblestates; // This will hold all the tangible states

		StringBuilder s = new StringBuilder();
		// If there are greater than MAXSTATESTODISPLAY tangible state,
		// the results will be summarised
		final boolean FULLMODE = false; // Indicates that all results will be
										// displayed
		final boolean SUMMARYMODE = true; // Indicates that only a summary of
											// the results should be displayed
		final int MAXSTATESTODISPLAY = 100;
		boolean mode = FULLMODE;

		// First load all the tangible states into memory
		try {
			tangiblestates = new StateList(rgfile, false);
		} catch (IOException e) {
			// s += e.getMessage();
			s.append(e.getMessage());
			return s.toString();
		} catch (StateSpaceTooBigException e) {
			// s += e.getMessage();
			s.append(e.getMessage());
			return s.toString();
		}
		// Now decide whether to summarise the results

		if (tangiblestates.size() > MAXSTATESTODISPLAY) {
			mode = SUMMARYMODE;
		}

		try {

			FileWriter outFile = new FileWriter(output);
			PrintWriter out = new PrintWriter(outFile);
			/*
			 * FileWriter fw = new FileWriter("someFile.txt"); BufferedWriter bw
			 * = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw);
			 */
			out.println("<html><head>" + ResultsHTMLPane.HTML_STYLE
					+ "</head><body>");
			out.println("<h2>GSPN Steady State Analysis Results</h2><br>");

			if (mode) {
				String states = "<br>" + "There are " + tangiblestates.size()
						+ " tangible states. ";
				s.append(states);
				out.println(states);
				s.append("Only a summary of the results will be displayed.");
				s.append("<br> For complete results see ").append(
						output.getAbsolutePath());
				s.append("<br>");
			}

			if (mode) {
				renderTangibleStates(sourceDataLayer, tangiblestates, out);
				renderPi(pi, tangiblestates, out);
			} else {
				String tangibles = renderTangibleStates(sourceDataLayer,
						tangiblestates);
				s.append(tangibles);
				out.println(tangibles);
				String p = renderPi(pi, tangiblestates);
				s.append("<br>").append(p);
				out.println("<br>" + p);
			}

			double[] averages = averageTokens(pi, tangiblestates);
			if (averages != null) {
				String avrgs = renderAverages(sourceDataLayer, averages);
				s.append("<br>").append(avrgs);
				out.println("<br>" + avrgs);
			}

			double[][] tokendist = tokenDistribution(pi, tangiblestates);
			if (tokendist != null) {
				String distribution = renderTokenDistribution(sourceDataLayer,
						tokendist);
				s.append("<br>").append(distribution);
				out.println("<br>" + distribution);
			}

			double[] throughput = getFastTransitionThroughput(sourceDataLayer,
					tangiblestates, pi);
			if (throughput != null) {
				String tThroughput = renderTimedTransitionThroughput(
						sourceDataLayer, throughput);
				s.append("<br>").append(tThroughput);
				out.println("<br>" + tThroughput);
			}

			double[] sojournTimes = calcSojournTime(sourceDataLayer,
					tangiblestates);
			if (sojournTimes != null) {
				if (mode) {
					renderSojournTimes(sojournTimes, tangiblestates, out);
				} else {
					String sTimes = renderSojournTimes(sojournTimes,
							tangiblestates);
					s.append("<br>").append(sTimes);
					out.println("<br>" + sTimes);
				}

			}

			out.println("</body></html>");
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("Error writing to file");
		}

		return s.toString();
	}

	/**
	 * renderTangibleStates() Turns all the tangible states into a table in the
	 * form of a long string with embedded HTML ready for display.
	 * 
	 * @param sourceDataLayer
	 *            The GSPN model data
	 * @param tangiblestates
	 *            The list of tangible states
	 * @return
	 */
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

	private void renderTangibleStates(PetriNetView sourceDataLayer,
			StateList tangiblestates, PrintWriter out) {

		// add headers to table
		writeTableHeader("Set of Tangible States", out);

		int markSize = tangiblestates.get(0).length;
		int states = tangiblestates.size();
		if (states == 0 || markSize == 0) {
			out.println("n/a");
		} else {
			ArrayList result = new ArrayList();

			// add empty position in order to get the table well formated
			result.add("");

			PlaceView[] placeViews = sourceDataLayer.places();// pendent++
			for (int i = 0; i < markSize; i++) {
				result.add(placeViews[i].getName());
				// result.add("<A NAME= 'M" + i + "'></A>");
			}

			// add
			for (int i = 0; i < tangiblestates.size(); i++) {
				result.add(tangiblestates.getID(i) + "<A NAME= 'M" + i
						+ "'></A>");
				for (int j = 0; j < markSize; j++) {
					result.add(Integer.toString(tangiblestates.get(i)[j]));
				}

			}
			writeTable(result.toArray(), markSize + 1, false, true, true, true,
					out);
			closeTable(out);
		}

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

	// Format lists of doubles nicely
	private void renderList(double[] data, StateList list, PrintWriter out) {
		if (list.size() == 0) {
			out.println("n/a");
			return;

		}

		ArrayList result = new ArrayList();
		// add headers to table
		result.add("Marking");
		result.add("Value");

		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(5);
		for (int i = 0; i < list.size(); i++) {
			result.add("<A HREF='#" + list.getID(i) + "'>"
					+ list.getID(i).toUpperCase() + "</A>");
			result.add(f.format(data[i]));
		}

		writeTable(result.toArray(), 2, false, true, true, true, out);
	}

	private void writeTableHeader(String text, PrintWriter out) {
		out.println("<table cellspacing=\"2\" border=\"0\">");
		out.println("<tr><td class=\"colhead\">" + text + "</td>");
		out.println("</tr>");
		out.println("<tr><td class=\"cell\">");
	}

	private void closeTable(PrintWriter out) {
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
	}

	/**
	 * renderPi() Turns the steady state distribution vector into a table in the
	 * form of a long string with embedded HTML ready for display.
	 * 
	 * @param pi
	 * @param states
	 * @return
	 */
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
		writeTableHeader("Steady State Distribution of Tangible States", out);
		renderList(pi, states, out);
		closeTable(out);
	}

	/**
	 * averageTokens() Determines the average number of tokens on each place at
	 * steady state.
	 * 
	 * @param pi
	 * @param states
	 *            The list of tangible states
	 * @return An array containing the average number of tokens on each place.
	 */
	private double[] averageTokens(double[] pi, StateList states) {
		int numstates = states.size();
		if (numstates == 0) {
			return null;
		}

		int numplaces = states.get(0).length;
		double[] averages = new double[numplaces];
		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(1);

		// Set all the averages to zero to begin with
		for (int place = 0; place < numplaces; place++) {
			averages[place] = 0.0;
		}

		// Now work out the average number of tokens on a place
		System.out
				.println("Calculating average number of tokens on a place...");
		for (int marking = 0; marking < numstates; marking++) {
			for (int place = 0; place < numplaces; place++) {
				averages[place] += (states.get(marking))[place] * pi[marking];
			}
			// System.out.print(f.format(((double)marking/numstates)*100) +
			// "% done.\r");

		}
		System.out.println("100.0% done.");
		return averages;
	}

	// <Marc>
	private class NoTimedTransitionsException extends Exception {
	}

	/*
	 * It returns an array with two Objects. The first Object is a double[]
	 * which contains Pi, and the second one is an StateList which contains the
	 * tangible states.
	 * 
	 * @param sourceDataLayer The GSPN model data
	 * 
	 * @return Pi and the tangible states list
	 */
	public Object[] getPiAndTangibleStates(PetriNetView sourceDataLayer) throws OutOfMemoryError, MarkingNotIntegerException {
		Object result[] = new Object[2];
		File reachabilityGraph = new File("results.rg");
		try {
			StateSpaceGenerator.generate(sourceDataLayer, reachabilityGraph,
					null);
			result[0] = SteadyStateSolver.solve(reachabilityGraph);
			result[1] = new StateList(reachabilityGraph, false);
		} catch (StateSpaceTooBigException e) {
			System.out.println(e);
			return null;
//		} catch (TimelessTrapException e) {
//			System.out.println(e);
//			return null;
		} catch (ImmediateAbortException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		} catch (Exception e) {
            //TODO: WAHT EXCEPTION
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

        return result;
	}

	/*
	 * It returns a double array containg the average number of tokens for each
	 * place. The order is the same as in the array returned by places() method
	 * in PetriNet class, so you can match each place with its average number of
	 * tokens.
	 * 
	 * @param sourceDataLayer The GSPN model data
	 * 
	 * @return an array with the average number of tokens for each place.
	 * 
	 * @throws NoTimedTransitionException if there is no timed transition in the
	 * net.
	 */
	public double[] getAverageTokens(PetriNetView sourceDataLayer, double pi[],
			StateList tangiblestates) throws NoTimedTransitionsException, OutOfMemoryError, MarkingNotIntegerException {
		File reachabilityGraph = new File("results.rg");

		if (!sourceDataLayer.hasTimedTransitions()) {
			throw new NoTimedTransitionsException();
		}

		try {
			if (pi == null || tangiblestates == null) {
				StateSpaceGenerator.generate(sourceDataLayer,
						reachabilityGraph, null);
				if (pi == null) {
					pi = SteadyStateSolver.solve(reachabilityGraph);
				}

				if (tangiblestates == null) {
					// First load all the tangible states into memory
					tangiblestates = new StateList(reachabilityGraph, false);
				}

			}
			return averageTokens(pi, tangiblestates);

		} catch (StateSpaceTooBigException e) {
			System.out.println(e);
			return null;
//		} catch (TimelessTrapException e) {
//			System.out.println(e);
//			return null;
		} catch (ImmediateAbortException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		} catch (Exception e) {
            //TODO: WAHT EXCEPTION
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

    }

	/*
	 * It returns a two-dimensional array containing the token distribution for
	 * each place. The order is the same as in the array returned by places()
	 * method in PetriNet class, so result[i] would be the token distribution
	 * for place i, and result[i][j] would be the probability of having j tokens
	 * in place i.
	 * 
	 * @param sourceDataLayer The GSPN model data
	 * 
	 * @return an array with the token distribution for each place.
	 * 
	 * @throws NoTimedTransitionException if there is no timed transition in the
	 * net.
	 */
	public double[][] getTokenDistribution(PetriNetView sourceDataLayer,
			double pi[], StateList tangiblestates)
			throws NoTimedTransitionsException, OutOfMemoryError, MarkingNotIntegerException {
		File reachabilityGraph = new File("results.rg");
		if (!sourceDataLayer.hasTimedTransitions()) {
			throw new NoTimedTransitionsException();
		}

		try {
			if (pi == null || tangiblestates == null) {
				StateSpaceGenerator.generate(sourceDataLayer,
						reachabilityGraph, null);
				if (pi == null) {
					pi = SteadyStateSolver.solve(reachabilityGraph);
				}

				if (tangiblestates == null) {
					// First load all the tangible states into memory
					tangiblestates = new StateList(reachabilityGraph, false);
				}

			}
			return tokenDistribution(pi, tangiblestates);

		} catch (StateSpaceTooBigException e) {
			System.out.println(e);
			return null;
//		} catch (TimelessTrapException e) {
//			System.out.println(e);
//			return null;
		} catch (ImmediateAbortException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		} catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

    }

	/*
	 * It returns a two-dimensional array containing the token distribution for
	 * each place. The order is the same as in the array returned by places()
	 * method in PetriNet class, so result[i] would be the token distribution
	 * for place i, and result[i][j] would be the probability of having j tokens
	 * in place i.
	 * 
	 * @param sourceDataLayer The GSPN model data
	 * 
	 * @return an array with the token distribution for each place.
	 * 
	 * @throws NoTimedTransitionException if there is no timed transition in the
	 * net.
	 */
	public double[] getThroughput(PetriNetView sourceDataLayer, double pi[],
			StateList tangiblestates) throws NoTimedTransitionsException, OutOfMemoryError, MarkingNotIntegerException {
		File reachabilityGraph = new File("results.rg");
		if (!sourceDataLayer.hasTimedTransitions()) {
			throw new NoTimedTransitionsException();
		}

		try {
			if (pi == null || tangiblestates == null) {
				StateSpaceGenerator.generate(sourceDataLayer,
						reachabilityGraph, null);
				if (pi == null) {
					pi = SteadyStateSolver.solve(reachabilityGraph);
				}

				if (tangiblestates == null) {
					// First load all the tangible states into memory
					tangiblestates = new StateList(reachabilityGraph, false);
				}

			}
			return getFastTransitionThroughput(sourceDataLayer, tangiblestates,
					pi);

		} catch (StateSpaceTooBigException e) {
			System.out.println(e);
			return null;
//		} catch (TimelessTrapException e) {
//			System.out.println(e);
//			return null;
		} catch (ImmediateAbortException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		} catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

    }

	// </Marc>

	/**
	 * renderAverages() Turns the array containing the average number of tokens
	 * on a place into a table in the form of a long string with embedded HTML
	 * ready for display.
	 * 
	 * @param pnmldata
	 * @param data
	 * @return
	 */
	private String renderAverages(PetriNetView pnmldata, double[] data) {
		return ResultsHTMLPane.makeTable(
				new String[] {
						"Average Number of Tokens on a Place",
						renderLists(data, pnmldata.places(), new String[] {
								"Place", "Number of Tokens" }) }, 1, false,
				false, true, false);
	}

	/**
	 * tokenDistribution() Calculates the steady state probability of there
	 * being n tokens at place p
	 * 
	 * @param pi
	 * @param states
	 * @return
	 */
	private double[][] tokenDistribution(double[] pi, StateList states) {
		int numstates = states.size();
		if (numstates == 0) {
			return null;
		}

		int numplaces = states.get(0).length;
		int highestnumberoftokens = 1;

		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(1);

		// The following ArrayList tallies up the probabilities.
		// Each element in the ArrayList is an array of doubles.
		// Each row in the array of doubles represents a particular number of
		// tokens on that place and the value at that row is the probability of
		// having that many tokens.
		ArrayList counter = new ArrayList(numplaces);

		double[] copy; // A temporary array for storing intermediate results
		double[][] result;

		// Each place will have at least 0 or 1 tokens on it at some point so
		// initialise each ArrayList with at a double array of at least two
		// elements
		for (int p = 0; p < numplaces; p++) {
			counter.add(p, new double[] { 0, 0 });
		}

		System.out.println("Calculating token distribution...");
		// Now go through each tangible state and each place in that state. For
		// each place, determine the number of tokens on it and add that states
		// steady state distribution to the appropriate tally for that place and
		// number of tokens in the ArrayList. Also keep track of the highest
		// number of tokens for any place as we will need to know it for the
		// final
		// results array.
		for (int marking = 0; marking < numstates; marking++) {
			int[] current = states.get(marking);
			for (int p = 0; p < numplaces; p++) {
				if (current[p] > highestnumberoftokens) {
					highestnumberoftokens = current[p];
				}

				if (current[p] > (((double[]) counter.get(p)).length - 1)) {
					copy = new double[current[p] + 1];
					System.arraycopy(counter.get(p), 0, copy, 0,
							((double[]) counter.get(p)).length);
					// Zero all the remainder of the array
					for (int i = ((double[]) counter.get(p)).length; i < copy.length; i++) {
						copy[i] = 0.0;
					}
					// Start off the tally for this number of tokens on place p

					copy[current[p]] = pi[marking];
					// Get rid of the old smaller array
					counter.remove(p);
					// Put the new one in instead
					counter.add(p, copy);
				} else {
					copy = (double[]) counter.get(p);
					copy[current[p]] += pi[marking];
				}

			}
			// System.out.print(f.format(((double)marking/numstates) * 100) +
			// "% done.  \r");
		}
		System.out.println("100.0% done.  ");

		// Now put together our results array
		result = new double[numplaces][highestnumberoftokens + 1];
		for (int p = 0; p < numplaces; p++) {
			System.arraycopy(counter.get(p), 0, result[p], 0,
					((double[]) counter.get(p)).length);
			if (((double[]) counter.get(p)).length < highestnumberoftokens) {
				for (int i = ((double[]) counter.get(p)).length; i < highestnumberoftokens; i++) {
					result[p][i] = 0.0;
				}

			}
		}
		return result;
	}

	/**
	 * renderTokenDistribution() Turns the array containing the token
	 * distributionns into a table in the form of a long string with embedded
	 * HTML ready for display.
	 * 
	 * @param pnmlData
	 * @param tokendist
	 * @return
	 */
	private String renderTokenDistribution(PetriNetView pnmlData,
			double[][] tokendist) {
		return ResultsHTMLPane
				.makeTable(
						new String[] {
								"Token Probability Density",
								renderProbabilityDensity(pnmlData.places(),
										tokendist) }, 1, false, false, true,
						false);
	}

	private String renderProbabilityDensity(PlaceView[] placeViews,
			double[][] probabilities) {
		if (probabilities.length == 0) {
			return "n/a";
		}

		int rows = probabilities.length;
		int cols = probabilities[0].length;
		ArrayList result = new ArrayList();
		// add headers to table
		result.add("");
		for (int i = 0; i < cols; i++) {
			result.add("&micro;=" + i);
		}

		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(5);
		for (int i = 0; i < rows; i++) {
			result.add(placeViews[i].getName());
			for (int j = 0; j < cols; j++) {
				result.add(f.format(probabilities[i][j]));
			}

		}

		return ResultsHTMLPane.makeTable(result.toArray(), cols + 1, false,
				true, true, true);
	}

	private double[] getFastTransitionThroughput(PetriNetView pnmldata,
			StateList list, double[] pi) {
		int length = list.size();
		
		pnmldata.storeCurrentMarking();
		TransitionView[] transitionViews = pnmldata.getTransitionViews();
		double[] result = new double[transitionViews.length];

		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(1);
		System.out.println("Calculating transition throughput...");

		for (int marking = 0; marking < length; marking++) {
			double specifiedTransitionRate;
			
			int[] markingArray = list.get(marking);
			pnmldata.setCurrentMarkingVector(markingArray);
			pnmldata.createMatrixes();
			LinkedList<MarkingView>[] markings = new LinkedList[markingArray.length];
			for (int i = 0; i < markingArray.length; i++) {
				LinkedList<MarkingView> newMarkingViewList = new LinkedList<MarkingView>();
				MarkingView m = new MarkingView(pnmldata.getActiveTokenView(),
						markingArray[i] + "");
				newMarkingViewList.add(m);
				markings[i] = newMarkingViewList;
			}
			boolean[] transStatus = pnmldata.areTransitionsEnabled(markings);
			for (int tran = 0; tran < transitionViews.length; tran++) {
				if (transStatus[tran]) {
					specifiedTransitionRate = transitionViews[tran].getRate();
					//System.out.println(specifiedTransitionRate +" * "+ pi[marking]);
					result[tran] += (specifiedTransitionRate * pi[marking]);
				}
				// System.out.print(f.format(((double)marking/length) * 100) +
				// "% done.  \r");

			}
		}
		System.out.println("100.0% done.  ");
		pnmldata.restorePreviousMarking();
		return result;
	}

	// Format throughput data nicely.
	private String renderTTThroughput(PetriNetView pnmldata, double[] data) {
		if (data.length == 0) {
			return "n/a";
		}

		int transCount = data.length;
		ArrayList result = new ArrayList();
		// add headers to table
		result.add("Transition");
		result.add("Throughput");
		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(5);
		for (int i = 0; i < transCount; i++) {
			if (pnmldata.getTransitionViews()[i].isTimed()) {
				result.add(pnmldata.getTransitionViews()[i].getName());
				result.add(f.format(data[i]));
			}

		}

		return ResultsHTMLPane.makeTable(result.toArray(), 2, false, true,
				true, true);
	}

	private String renderTimedTransitionThroughput(PetriNetView pnmldata,
			double[] throughput) {
		return ResultsHTMLPane.makeTable(
				new String[] { "Throughput of Timed Transitions",
						renderTTThroughput(pnmldata, throughput) }, 1, false,
				false, true, false);
	}

	private String renderSojournTimes(double[] data, StateList list) {
		return ResultsHTMLPane.makeTable(new String[] {
				"Sojourn times for tangible states", renderLists(data, list) },
				1, false, false, true, false);
	}

	private void renderSojournTimes(double[] data, StateList list,
			PrintWriter out) {
		// add headers to table
		writeTableHeader("Sojourn times for tangible states", out);
		renderList(data, list, out);
		closeTable(out);
		ResultsHTMLPane.makeTable(new String[] {
				"Sojourn times for tangible states", renderLists(data, list) },
				1, false, false, true, false);
	}

	public String getName() {
		return MODULE_NAME;
	}

	/**
	 * This function determines the sojourn time for each state in a specified
	 * set of states.
	 *
	 * @return double[] - the array of sojourn times for each specific state
	 * @param pnmldata
	 * @param tangibleStates
	 */
	private double[] calcSojournTime(PetriNetView pnmldata,
			StateList tangibleStates) {
		int numStates = tangibleStates.size();
		int numTrans = pnmldata.getTransitionViews().length;
		TransitionView[] trans = pnmldata.getTransitionViews();
		double[] sojournTime = new double[numStates];

		pnmldata.storeCurrentMarking();
		for (int i = 0; i < numStates; i++) {
			int[] tangibleStateArray = tangibleStates.get(i);
			pnmldata.setCurrentMarkingVector(tangibleStateArray);
			pnmldata.createMatrixes();
			LinkedList<MarkingView>[] markings = new LinkedList[tangibleStateArray.length];
			for (int j = 0; j < tangibleStateArray.length; j++) {
				LinkedList<MarkingView> newMarkingViewList = new LinkedList<MarkingView>();
				MarkingView m = new MarkingView(pnmldata.getActiveTokenView(),
						tangibleStateArray[j]);
				newMarkingViewList.add(m);
				markings[j] = newMarkingViewList;
			}

			boolean[] transStatus = pnmldata.areTransitionsEnabled(markings);
			double weights = 0;
			for (int j = 0; j < numTrans; j++) {
				if (transStatus[j]) {
					weights += trans[j].getRate();
				}

			}
			sojournTime[i] = 1 / weights;
		}

		pnmldata.restorePreviousMarking();
		return sojournTime;
	}
}
