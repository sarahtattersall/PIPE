/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.QueryAnalysisException;
import pipe.modules.queryresult.FilePointsResultWrapper;
import pipe.modules.queryresult.NodeAnalyserResultWrapper;
import pipe.modules.queryresult.PointsResultWrapper;
import pipe.modules.queryresult.TextFileResultWrapper;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;
import pipe.server.performancequery.nodeanalyser.NodeAnalyserHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

/**
 * @author dazz
 * 
 */
class ResultProvider implements EvaluatorGuiLoggingHandler
{

	public static final String	SSPTextTabName			= "Steady State Probability Results";
	private static final String	SSPHistoTabName			= "Steady State Probability distribution";
	private static final String	PTDGraphTabName			= "Passage Time Density Result";
	public static final String	MomentTabName			= "Moment Results";
	public static final String	FRTabName				= "Firing Rate Results";
	private static final String	DistTabName				= "Cumulative distribution";
	public static final String	PercentileName			= "Percentile Results";
	public static final String	ProbInIntervalTabName	= "ProbInInterval Results";

	private static boolean		hasSequential			= false;

	public static void setupAutomaticResult(final QueryOperationNode node)
	{
		try
		{
			if (node.getNodeType().compareTo(PetriNetNode.SEQUENTIAL) <= 0)
			{
				ResultProvider.hasSequential |= node.getNodeType() == PetriNetNode.SEQUENTIAL;
				switch (node.getResult().getOrginalType())
				{
					case PERCENTILE :
					case DISTRIBUTION :
					case PASSAGETIMEDENSITY :
					case PROBININTERVAL :
					case CONVOLUTION :
					{
						ResultProvider.setupGraphTab(node);
						break;
					}
					case PROBINSTATES :

						break;
					case MOMENT :
					{
						ResultProvider.setupTextTab(node, ResultProvider.MomentTabName);
						break;
					}
					case FIRINGRATE :
					{
						ResultProvider.setupTextTab(node, ResultProvider.FRTabName);
						break;
					}
					case STEADYSTATEPROB :
					{
						ResultProvider.setupSSPBarChartTab(node);
						break;
					}
					case ININTERVAL :
					case DISCON :
					case ARITHCOMP :
					case ARITHOP :
					case NEGATION :
					case BOOL :
					case NUM :
					case RANGE :
					{
						final ProgressWindow window = QueryManager.getProgressWindow();
						final NodeAnalyserResultWrapper w = (NodeAnalyserResultWrapper) node.getResult();
						final String result = QueryConstants.successfulResultStringStart +
												NodeAnalyserHelper.getValue(w.getResult());
						window.setProgressBarText(result);
						break;
					}
					default :
						throw new QueryAnalysisException(node.getResult().getType() +
															":Can't support this kind of result presentation on result node currently..");
				}
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(	Level.WARNING,
													"Attempt to display Result node result failed",
													e);
		}
		catch (final InvalidNodeAnalyserException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(	Level.WARNING,
													"Attempt to display Result node result failed",
													e);
		}
	}

	public static void setupFiringRate(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getResult().getOrginalType() == PetriNetNode.FIRINGRATE)
			{
				final JPopupMenu menu = new JPopupMenu();
				final TextFileResultWrapper result = (TextFileResultWrapper) node.getResult();

				final JMenuItem num = new JMenuItem(String.valueOf(result.getNumResult()));

				menu.add(num);

				final JMenuItem popup = new JMenuItem(new TabOpenFileText(node));
				popup.setText("View Text File");
				menu.add(popup);

				menu.setVisible(true);

				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			else
			{
				throw new QueryAnalysisException("Argument Node isn't of type FR");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't create Histogram", e);
		}
	}

	public static void setupGraphTab(final QueryOperationNode node)
	{
		try
		{
			Component area = null;
			String name = null;
			switch (node.getResult().getOrginalType())
			{
				case PASSAGETIMEDENSITY :
				case PROBININTERVAL :
					name = ResultProvider.PTDGraphTabName;
					break;
				case PERCENTILE :
				case DISTRIBUTION :
					name = ResultProvider.DistTabName;
					break;
				default :
					throw new QueryAnalysisException("Argument Node isn't of type PTD");
			}
			final int hash = node.getResult().hashCode() + name.hashCode();
			final ProgressWindow window = QueryManager.getProgressWindow();
			if (!window.allComponents.containsKey(hash))
			{
				final ResultPlotter myPlotter = new ResultGraphPlotter();
				area = myPlotter.getChart(node.getResult());
				window.allComponents.put(hash, area);
			}
			else
			{
				area = window.allComponents.get(hash);
			}
			window.addTab(area, name);
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't create Graph", e);
		}
	}

	public static void setupMoment(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getResult().getOrginalType() == PetriNetNode.MOMENT)
			{
				final JPopupMenu menu = new JPopupMenu();
				final TextFileResultWrapper result = (TextFileResultWrapper) node.getResult();

				final JMenuItem num = new JMenuItem(String.valueOf(result.getNumResult()));

				menu.add(num);

				final JMenuItem popup = new JMenuItem(new TabOpenFileText(node));
				popup.setText("View Text File");
				menu.add(popup);

				menu.setVisible(true);

				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			else
			{
				throw new QueryAnalysisException("Argument Node isn't of type Moment");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't show file text", e);
		}
	}

	public static void setupNodeAnalyser(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getResult().getOrginalType().usesNodeAnalyser())
			{
				final JPopupMenu menu = new JPopupMenu();
				final NodeAnalyserResultWrapper w = (NodeAnalyserResultWrapper) node.getResult();
				final String result = NodeAnalyserHelper.getValue(w.getResult());
				menu.add(result);
				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			else
			{
				throw new QueryAnalysisException("Argument node isn't implemented to use node Analyser");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Could create NodeAnalyser popup", e);
		}
		catch (final InvalidNodeAnalyserException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Could create NodeAnalyser popup", e);
		}
	}

	public static void setupPercentile(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getResult().getOrginalType() == PetriNetNode.PERCENTILE)
			{
				final JPopupMenu menu = new JPopupMenu();
				final PointsResultWrapper result = (PointsResultWrapper) node.getResult();

				final JMenuItem num = new JMenuItem(String.valueOf(result.getNumResult()));

				menu.add(num);

				final JMenuItem popup = new JMenuItem(new TabOpenFileText(node));
				popup.setText("View Text File");
				menu.add(popup);

				final JMenuItem popup2 = new JMenuItem(new TabOpenGraph(node));
				popup2.setText("View distribution");
				menu.add(popup2);

				menu.setVisible(true);

				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			else
			{
				throw new QueryAnalysisException("Argument Node isn't of type Percentile");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't show file text", e);
		}
	}

	public static void setupProbInInterval(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getResult().getOrginalType() == PetriNetNode.PROBININTERVAL)
			{
				final JPopupMenu menu = new JPopupMenu();
				final PointsResultWrapper result = (PointsResultWrapper) node.getResult();

				final JMenuItem num = new JMenuItem(String.valueOf(result.getNumResult()));

				menu.add(num);

				final JMenuItem popup = new JMenuItem(new TabOpenFileText(node));
				popup.setText("View Text File");
				menu.add(popup);

				final JMenuItem popup2 = new JMenuItem(new TabOpenGraph(node));
				popup2.setText("View distribution");
				menu.add(popup2);

				menu.setVisible(true);

				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			else
			{
				throw new QueryAnalysisException("Argument Node isn't of type ProbInInterval");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't show file text", e);
		}
	}

	public static void setupResult(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getNodeType().compareTo(PetriNetNode.RESULT) <= 0 && !ResultProvider.hasSequential)
			{
				switch (node.getResult().getOrginalType())
				{
					case DISTRIBUTION :
					case PASSAGETIMEDENSITY :
					case CONVOLUTION :
					{
						ResultProvider.setupGraphTab(node);
						break;
					}
					case PROBININTERVAL :
						ResultProvider.setupProbInInterval(node, event);
						break;
					case PROBINSTATES :

						break;
					case MOMENT :
					{
						ResultProvider.setupMoment(node, event);
						break;
					}
					case PERCENTILE :
					{
						ResultProvider.setupPercentile(node, event);
						break;
					}
					case FIRINGRATE :
					{
						ResultProvider.setupFiringRate(node, event);
						break;
					}
					case STEADYSTATEPROB :
					{
						ResultProvider.setupSSP(node, event);
						break;
					}
					case ININTERVAL :
					case DISCON :
					case ARITHCOMP :
					case ARITHOP :
					case NEGATION :
					case BOOL :
					case NUM :
					case RANGE :
					{
						ResultProvider.setupNodeAnalyser(node, event);
						break;
					}
					default :
						throw new QueryAnalysisException(node.getResult().getType() +
															":Can't support this kind of result presentation on result node currently..");
				}
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(	Level.WARNING,
													"Attempt to display Result node result failed",
													e);
		}
	}

	public static void setupSSP(final QueryOperationNode node, final MouseEvent event)
	{
		try
		{
			if (node.getResult().getOrginalType() == PetriNetNode.STEADYSTATEPROB)
			{
				final JPopupMenu menu = new JPopupMenu();
				final FilePointsResultWrapper result = (FilePointsResultWrapper) node.getResult();

				final JMenuItem num = new JMenuItem(String.valueOf(result.getNumResult()));

				menu.add(num);

				final JMenuItem popup1 = new JMenuItem(new TabOpenFileText(node));
				popup1.setText("View Text File");
				menu.add(popup1);

				final JMenuItem popup2 = new JMenuItem(new TabOpenHistogram(node));
				popup2.setText("View distribution");
				menu.add(popup2);

				menu.setVisible(true);

				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			else
			{
				throw new QueryAnalysisException("Argument Node isn't of type SSP");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't create Histogram", e);
		}
	}

	public static void setupSSPBarChartTab(final QueryOperationNode node)
	{
		try
		{
			if (node.getResult().getOrginalType() == PetriNetNode.STEADYSTATEPROB)
			{
				Component area = null;
				final String name = ResultProvider.SSPHistoTabName;
				final int hash = node.getResult().hashCode() + name.hashCode();
				final ProgressWindow window = QueryManager.getProgressWindow();
				if (!window.allComponents.containsKey(hash))
				{
					final ResultPlotter myPlotter = new ResultBarChartPlotter();
					area = myPlotter.getChart(node.getResult());
					window.allComponents.put(hash, area);
				}
				else
				{
					area = QueryManager.getProgressWindow().allComponents.get(hash);
				}
				window.addTab(area, name);
			}
			else
			{
				throw new QueryAnalysisException("Argument Node isn't of type SSP");
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't create Histogram", e);
		}
	}

	public static void setupTextTab(final QueryOperationNode node, final String name)
	{
		try
		{
			Component area = null;
			final int hash = node.getResult().hashCode() + name.hashCode();
			if (!QueryManager.getProgressWindow().allComponents.containsKey(hash))
			{
				if (node.getResult().getOrginalType().compareTo(PetriNetNode.PERCENTILE) <= 0 &&
					node.getResult().getOrginalType().compareTo(PetriNetNode.PROBININTERVAL) >= 0)
				{
					area = new JTextArea(((TextFileResultWrapper) node.getResult())	.getFileString()
																					.toString());
				}
				else
				{
					area = new JTextArea(NodeAnalyserHelper.getValue(node.getResult().getResult()));
				}
				QueryManager.getProgressWindow().allComponents.put(hash, area);
			}
			else
			{
				area = QueryManager.getProgressWindow().allComponents.get(hash);
			}
			QueryManager.getProgressWindow().addTab(area, name);
		}
		catch (final InvalidNodeAnalyserException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Could create text tab", e);
		}
	}
}
