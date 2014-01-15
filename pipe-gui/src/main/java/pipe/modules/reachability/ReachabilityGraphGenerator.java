package pipe.modules.reachability;

import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import pipe.calculations.StateSpaceGenerator;
import pipe.calculations.myTree;
import pipe.exceptions.MarkingNotIntegerException;
import pipe.exceptions.TimelessTrapException;
import pipe.exceptions.TreeTooBigException;
import pipe.extensions.jpowergraph.*;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.*;
import pipe.io.*;
import pipe.modules.interfaces.IModule;
import pipe.utilities.Expander;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Matthew Worthington / Edwin Chung / Will Master
 *         Created module to produce the reachability graph representation of a Petri
 *         net. This module makes use of modifications that were made to the state space
 *         generator to produce a list of possible states (both tangible and non
 *         tangible) which are then transformed into a dot file.
 *         The file is then translated into its graphical layout using
 *         www.research.att.com hosting of Graphviz. It should therefore be noted that
 *         a live internet connection is required. (Feb/March,2007)
 */
public class ReachabilityGraphGenerator
implements IModule
{

	private static final String MODULE_NAME = "Reachability/Coverability Graph";
	private PetriNetChooserPanel sourceFilePanel;
	private static ResultsHTMLPane results;
	private final EscapableDialog guiDialog =
			new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

	private static final Checkbox checkBox1 =
			new Checkbox("Display initial state(S0) in a different shape", false);

	private static String dataLayerName;


	public void start()
	{
//		PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
//		// Check if this net is a CGSPN. If it is, then this
//		// module won't work with it and we must convert it.
//		if(pnmlData.getEnabledTokenClassNumber() > 1){
////		if(pnmlData.getTokenViews().size() > 1)
//			Expander expander = new Expander(pnmlData);
//			pnmlData = expander.unfoldOld();
//			JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black)",
//					"Information", JOptionPane.INFORMATION_MESSAGE);
//		}
//		// Build interface
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
//		contentPane.add(new ButtonBar("Generate Reachability/Coverability Graph", generateGraph,
//				guiDialog.getRootPane()));
//		contentPane.add(checkBox1);
//
//		// 5 Make window fit contents' preferred size
//		guiDialog.pack();
//
//		// 6 Move window to the middle of the screen
//		guiDialog.setLocationRelativeTo(null);
//
//		checkBox1.setState(false);
//		guiDialog.setModal(false);
//		guiDialog.setVisible(false);
//		guiDialog.setVisible(true);
	}


	private final ActionListener generateGraph = new ActionListener()
	{

		public void actionPerformed(ActionEvent arg0)
		{
			long start = new Date().getTime();
			long gfinished;
			long allfinished;
			double graphtime;
			double constructiontime;
			double totaltime;

			//data layer corrected, so that we could have the correct calculation
			PetriNetView sourcePetriNetView = ApplicationSettings.getApplicationView().getCurrentPetriNetView();//sourceFilePanel.getDataLayer();
			dataLayerName = sourcePetriNetView.getPNMLName();

			// This will be used to store the reachability graph data
			File reachabilityGraph = new File("results.rg");

			// This will be used to store the steady state distribution
			String s = "<h2>Reachability/Coverability Graph Results</h2>";

			if(sourcePetriNetView == null)
			{
				JOptionPane.showMessageDialog(null, "Please, choose a source net",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if(!sourcePetriNetView.hasPlaceTransitionObjects())
			{
				s += "No Petri net objects defined!";
			}
			else
			{
				try
				{

					PNMLWriter.saveTemporaryFile(sourcePetriNetView,
							this.getClass().getName());

					String graph = "Reachability graph";

					boolean generateCoverability = false;
					try
					{
						StateSpaceGenerator.generate(sourcePetriNetView, reachabilityGraph);
					}
					catch(OutOfMemoryError e)
					{
						// net seems to be unbounded, let's try to generate the
						// coverability graph
						generateCoverability = true;
					}

                    List<MarkingView>[] markings = sourcePetriNetView.getCurrentMarkingVector();
					int[] currentMarking = new int[markings.length];
					for(int i = 0; i < markings.length; i++)
					{
						currentMarking[i] = markings[i].get(0).getCurrentMarking();
					}

					// TODO: reachability graph and coverability graph are the same
					// when the net is bounded so we could just generate the
					// coverability graph
					if(generateCoverability)
					{
						myTree tree = new myTree(sourcePetriNetView,
								currentMarking,
								reachabilityGraph);
						graph = "Coverability graph";
					}

					gfinished = new Date().getTime();
					System.gc();
					generateGraph(reachabilityGraph, sourcePetriNetView,
							generateCoverability);
					allfinished = new Date().getTime();
					graphtime = (gfinished - start) / 1000.0;
					constructiontime = (allfinished - gfinished) / 1000.0;
					totaltime = (allfinished - start) / 1000.0;
					DecimalFormat f = new DecimalFormat();
					f.setMaximumFractionDigits(5);
					s += "<br>Generating " + graph + " took " +
							f.format(graphtime) + "s";
					s += "<br>Constructing it took " +
							f.format(constructiontime) + "s";
					s += "<br>Total time was " + f.format(totaltime) + "s";
					results.setEnabled(true);
				}
				catch(OutOfMemoryError e)
				{
					System.gc();
					results.setText("");
					s = "Memory error: " + e.getMessage();

					s += "<br>Not enough memory. Please use a larger heap size."
							+ "<br>"
							+ "<br>Note:"
							+ "<br>The Java heap size can be specified with the -Xmx option."
							+ "<br>E.g., to use 512MB as heap size, the command line looks like this:"
							+ "<br>java -Xmx512m -classpath ...\n";
					results.setText(s);
					return;
				}
				catch(StackOverflowError e)
				{
					s += "StackOverflow Error";
					results.setText(s);
					return;
				}
				catch(ImmediateAbortException e)
				{
					s += "<br>Error: " + e.getMessage();
					results.setText(s);
					return;
				}
				catch(TimelessTrapException e)
				{
					s += "<br>" + e.getMessage();
					results.setText(s);
					return;
				}
				catch(IOException e)
				{
					s += "<br>" + e.getMessage();
					results.setText(s);
					return;
				}
				catch(TreeTooBigException e)
				{
					s += "<br>" + e.getMessage();
					results.setText(s);
					return;
				}
				
				catch (MarkingNotIntegerException e) {
					JOptionPane.showMessageDialog(null,
							"Weighting cannot be less than 0. Please re-enter");
					sourcePetriNetView.restorePlaceViewsMarking();
					return;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					s += "<br>Error" + e.getMessage();
					results.setText(s);
					return;
				}
				finally
				{
					if(reachabilityGraph.exists())
					{
						reachabilityGraph.delete();
					}
				}
			}
			results.setText(s);
		}
	};


	public String getName()
	{
		return MODULE_NAME;
	}


	private void generateGraph(File rgFile, PetriNetView dataLayer,
			boolean coverabilityGraph)
					throws Exception
					{
		DefaultGraph graph = createGraph(rgFile, dataLayer, coverabilityGraph);
		GraphFrame frame = new GraphFrame();
		PlaceView[] placeView = dataLayer.places();
		String legend = "";
		if(placeView.length > 0)
		{
			legend = "{" + placeView[0].getName();
		}
		for(int i = 1; i < placeView.length; i++)
		{
			legend += ", " + placeView[i].getName();
		}
		legend += "}";
		frame.constructGraphFrame(graph, legend);
		frame.toFront();
		frame.setIconImage((
				new ImageIcon(Thread.currentThread().getContextClassLoader().
						getResource(ApplicationSettings.getImagePath() + "icon.png")).getImage()));
		frame.setTitle(dataLayerName);
					}


	private static DefaultGraph createGraph(File rgFile, PetriNetView dataLayer,
			boolean coverabilityGraph) throws IOException
			{
		DefaultGraph graph = new DefaultGraph();

		ReachabilityGraphFileHeader header = new ReachabilityGraphFileHeader();
		RandomAccessFile reachabilityFile;
		try
		{
			reachabilityFile = new RandomAccessFile(rgFile, "r");
			header.read(reachabilityFile);
		}
		catch(IncorrectFileFormatException e1)
		{
			System.err.println("createGraph: " +
					"incorrect file format on state space file");
			return graph;
		}
		catch(IOException e1)
		{
			System.err.println("createGraph: unable to read header file");
			return graph;
		}

		if((header.getNumStates() + header.getNumTransitions()) > 400)
		{
			throw new IOException("There are " + header.getNumStates() + " states with "
					+ header.getNumTransitions() + " arcs. The graph is too big to be displayed properly.");
		}

		ArrayList nodes = new ArrayList();
		ArrayList edges = new ArrayList();
		ArrayList loopEdges = new ArrayList();
		ArrayList loopEdgesTransitions = new ArrayList();
		String label;
		String marking;

		int stateArraySize = header.getStateArraySize();
		StateRecord record = new StateRecord();
		record.read1(stateArraySize, reachabilityFile);
		label = "S0";
		marking = record.getMarkingString();
		if(record.getTangible())
		{
			if(checkBox1.getState())
			{
				nodes.add(coverabilityGraph
						? new PIPEInitialState(label, marking)
				: new PIPEInitialTangibleState(label, marking));
			}
			else
			{
				nodes.add(coverabilityGraph
						? new PIPEState(label, marking)
				: new PIPETangibleState(label, marking));
			}
		}
		else
		{
			if(checkBox1.getState())
			{
				nodes.add(coverabilityGraph
						? new PIPEInitialState(label, marking)
				: new PIPEInitialVanishingState(label, marking));
			}
			else
			{
				nodes.add(coverabilityGraph
						? new PIPEState(label, marking)
				: new PIPEVanishingState(label, marking));
			}
		}

		for(int count = 1; count < header.getNumStates(); count++)
		{
			record.read1(stateArraySize, reachabilityFile);
			label = "S" + count;
			marking = record.getMarkingString();
			if(record.getTangible())
			{
				nodes.add(coverabilityGraph
						? new PIPEState(label, marking)
				: new PIPETangibleState(label, marking));
			}
			else
			{
				nodes.add(coverabilityGraph
						? new PIPEState(label, marking)
				: new PIPEVanishingState(label, marking));
			}
		}

		reachabilityFile.seek(header.getOffsetToTransitions());
		int numberTransitions = header.getNumTransitions();
		for(int transitionCounter = 0; transitionCounter < numberTransitions;
				transitionCounter++)
		{
			TransitionRecord transitions = new TransitionRecord();
			transitions.read1(reachabilityFile);

			int from = transitions.getFromState();
			int to = transitions.getToState();
			if(from != to)
			{
				edges.add(new TextEdge(
						(DefaultNode) (nodes.get(from)),
						(DefaultNode) (nodes.get(to)),
						dataLayer.getTransitionName(transitions.getTransitionNo())));
			}
			else
			{
				if(loopEdges.contains(nodes.get(from)))
				{
					int i = loopEdges.indexOf(nodes.get(from));

					loopEdgesTransitions.set(i,
							loopEdgesTransitions.get(i) + ", " +
									dataLayer.getTransitionName(transitions.getTransitionNo()));
				}
				else
				{
					loopEdges.add(nodes.get(from));
					loopEdgesTransitions.add(
							dataLayer.getTransitionName(transitions.getTransitionNo()));
				}
			}
		}

		for(int i = 0; i < loopEdges.size(); i++)
		{
			edges.add(new PIPELoopWithTextEdge((DefaultNode) (loopEdges.get(i)),
					(String) (loopEdgesTransitions.get(i))));
		}

		graph.addElements(nodes, edges);

		return graph;
			}
}
