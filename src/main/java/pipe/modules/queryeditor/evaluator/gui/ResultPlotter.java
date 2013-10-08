/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import pipe.modules.queryeditor.evaluator.QueryAnalysisException;
import pipe.modules.queryresult.ResultWrapper;
import pipe.modules.queryresult.XYCoordinate;
import pipe.modules.queryresult.XYCoordinates;

import javax.swing.*;

/**
 * @author dazz
 * 
 */
public abstract class ResultPlotter implements EvaluatorGuiLoggingHandler
{
	final Box		resultsPanel	= Box.createVerticalBox();
	JFreeChart	chart;
	ChartPanel	chartPanel;

	JPanel		graphPanel;

	protected JButton		switchViewBtn;


	// currently
	// showing a
	// PDF view

	public abstract JComponent getChart(ResultWrapper w) throws QueryAnalysisException;

	XYSeries getXYSeries(final XYCoordinates coords, final String plotName)
	{
		final XYSeries series = new XYSeries(plotName);
		for (final XYCoordinate c : coords.getPoints())
		{
			series.add(c.getX(), c.getY());
		}
		return series;
	}
}
