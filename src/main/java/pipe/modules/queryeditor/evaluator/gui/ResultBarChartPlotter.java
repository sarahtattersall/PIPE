/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pipe.common.LoggingHelper;
import pipe.modules.queryeditor.evaluator.QueryAnalysisException;
import pipe.modules.queryresult.FilePointsResultWrapper;
import pipe.modules.queryresult.ResultWrapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author dazz
 * 
 */
public class ResultBarChartPlotter extends ResultPlotter
{

    private final ActionListener	cvsListener	= new ActionListener()
												{
													public void actionPerformed(ActionEvent arg0)
													{
														XYDataset graphData;

														graphData = ResultBarChartPlotter.this.chart.getXYPlot()
																									.getDataset();

														int size = graphData.getItemCount(0);

														FileWriter fw = null;
														StringBuffer content = new StringBuffer();

														File saveFile;

														JFileChooser fc = new JFileChooser();

														int returnVal = fc.showSaveDialog(ResultBarChartPlotter.this.resultsPanel);
														if (returnVal == JFileChooser.APPROVE_OPTION)
														{
															saveFile = fc.getSelectedFile();
														}
														else
														{
															return;
														}

														try
														{
															fw = new FileWriter(saveFile);

															for (int i = 0; i < size; i++)
															{
																content.append(graphData.getXValue(0, i));
																content.append(",");
																content.append(graphData.getYValue(0, i));
																content.append(",\n");
															}
															String content1 = content.toString();

															fw.write(content1);
															fw.close();
														}
														catch (IOException e)
														{
															String msg = "Couldn't save file, problem writing file!";
															EvaluatorGuiLoggingHandler.logger.log(	Level.WARNING,
																									msg,
																									e);
															JOptionPane.showMessageDialog(	ResultBarChartPlotter.this.resultsPanel,
																							msg,
																							"File Writing Error",
																							JOptionPane.WARNING_MESSAGE);
														}
													}
												};

	private final ActionListener	pngListener	= new ActionListener()
												{
													public void actionPerformed(ActionEvent eve)
													{

														RenderedImage graphImage;

														graphImage = ResultBarChartPlotter.this.chart.createBufferedImage(	800,
																															600);

														File saveFile;
														JFileChooser fc = new JFileChooser();
														int returnVal = fc.showSaveDialog(ResultBarChartPlotter.this.resultsPanel);
														if (returnVal == JFileChooser.APPROVE_OPTION)
														{
															saveFile = fc.getSelectedFile();
														}
														else
														{
															return;
														}
														try
														{
															ImageIO.write(graphImage, "png", saveFile);
														}
														catch (IOException e)
														{
															EvaluatorGuiLoggingHandler.logger.warning(LoggingHelper.getStackTrace(e));
														}
													}
												};

	public ResultBarChartPlotter() {
		super();
	}

	@Override
	public JComponent getChart(final ResultWrapper wrapper) throws QueryAnalysisException
	{
		if (wrapper instanceof FilePointsResultWrapper)
		{
			final FilePointsResultWrapper w = (FilePointsResultWrapper) wrapper;
			// Generate a set of points from the results
			final XYSeries points = this.getXYSeries(w.getPoints(), w.getPlotName());

			// Create PDF graph panel
            XYDataset dataSet = new XYSeriesCollection(points);
			dataSet = new XYBarDataset(dataSet, 1);

			this.chart = ChartFactory.createXYBarChart(	"Steady State Distribution",
														"State",
														false,
														"Frequency",
														(XYBarDataset) dataSet,
														PlotOrientation.VERTICAL,
														false,
														false,
														false);

			final XYPlot plot = (XYPlot) this.chart.getPlot();
			final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
			domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

			this.chart.setBackgroundPaint(Color.white);
			this.chartPanel = new ChartPanel(this.chart);
			this.graphPanel = new JPanel();
			this.graphPanel.add(this.chartPanel, "Dist");

			// Create the results panel
			this.resultsPanel.removeAll(); // clear if previously used
			this.resultsPanel.add(this.graphPanel, Component.CENTER_ALIGNMENT);

			// Create button panel then add
			final JPanel buttons = new JPanel();

			final JButton saveImageBtn = new JButton("Save Graph");
			saveImageBtn.addActionListener(this.pngListener);
			saveImageBtn.setMnemonic(KeyEvent.VK_S);

			final JButton saveCordBtn = new JButton("Save Points");
			saveCordBtn.addActionListener(this.cvsListener);
			saveCordBtn.setMnemonic(KeyEvent.VK_C);

			buttons.add(saveImageBtn);
			buttons.add(saveCordBtn);

			this.resultsPanel.add(buttons, Component.CENTER_ALIGNMENT);

			return this.resultsPanel;
		}
		else
		{
			throw new QueryAnalysisException("Unexpected ResultWrapper Type used");
		}
	}

}
