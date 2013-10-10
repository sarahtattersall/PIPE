/**
 *
 */
package pipe.modules.queryeditor.evaluator.gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import pipe.common.LoggingHelper;
import pipe.handlers.StringHelper;
import pipe.modules.queryeditor.evaluator.QueryAnalysisException;
import pipe.modules.queryresult.*;

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
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author dazz
 */
public class ResultGraphPlotter extends ResultPlotter
{
    protected JFreeChart cdfGraph;

    private XYDataset seriesCollection;

    private final ActionListener cvsListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent arg0)
        {
            XYDataset graphData;

            graphData = ResultGraphPlotter.this.chart.getXYPlot()
                    .getDataset();

            int size = graphData.getItemCount(0);

            FileWriter fw = null;
            StringBuffer content = new StringBuffer();

            File saveFile;

            JFileChooser fc = new JFileChooser();

            int returnVal = fc.showSaveDialog(ResultGraphPlotter.this.resultsPanel);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                saveFile = fc.getSelectedFile();
            }
            else return;

            try
            {
                fw = new FileWriter(saveFile);

                for(int i = 0; i < size; i++)
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
            catch(IOException e)
            {
                String msg = "Couldn't save file, problem writing file!";
                EvaluatorGuiLoggingHandler.logger.log(Level.WARNING,
                                                      msg,
                                                      e);
                JOptionPane.showMessageDialog(ResultGraphPlotter.this.resultsPanel,
                                              msg,
                                              "File Writing Error",
                                              JOptionPane.WARNING_MESSAGE);
            }
        }
    };

    private final ActionListener pngListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent eve)
        {

            RenderedImage graphImage;

            graphImage = ResultGraphPlotter.this.chart.createBufferedImage(800,
                                                                           600);

            File saveFile;
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(ResultGraphPlotter.this.resultsPanel);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                saveFile = fc.getSelectedFile();
            }
            else return;
            try
            {
                ImageIO.write(graphImage, "png", saveFile);
            }
            catch(IOException e)
            {
                EvaluatorGuiLoggingHandler.logger.warning(LoggingHelper.getStackTrace(e));
            }
        }
    };

    public ResultGraphPlotter()
    {
        super();
    }

    @Override
    public JComponent getChart(final ResultWrapper wrapper) throws QueryAnalysisException
    {
        if(wrapper instanceof PointsResultWrapper)
        {
            final PointsResultWrapper w = (PointsResultWrapper) wrapper;

            final XYPlot plot = this.getPlot(w);

            this.chart = new JFreeChart(plot);
            this.chart.setTitle("Passage Time Results");
            this.chart.setBackgroundPaint(Color.white);
            this.chartPanel = new ChartPanel(this.chart);

            this.graphPanel = new JPanel();
            this.graphPanel.add(this.chartPanel, "PDF");

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
        else throw new QueryAnalysisException("Unexpected ResultWrapper Type used to get chart");
    }

    private XYPlot getPlot(final PointsResultWrapper w)
    {
        // Generate a set of points from the results
        final XYSeries points = this.getXYSeries(w.getPoints(), w.getPlotName());

        // Create PDF graph panel
        this.seriesCollection = new XYSeriesCollection(points);
        final XYItemRenderer r = new StandardXYItemRenderer();
        XYPlot plot = null;

        if(w instanceof PercentileResultWrapper)
        {
            plot = this.setupPercentile((PercentileResultWrapper) w, r);
        }
        else if(w instanceof ProbInIntervalResultWrapper)
        {
            plot = this.setupProbInInterval((ProbInIntervalResultWrapper) w, r);
        }
        else
        {
            plot = new XYPlot(this.seriesCollection,
                              new NumberAxis("Time"),
                              new NumberAxis("Probability Density"),
                              r);
        }
        return plot;
    }

    private XYCoordinate getTextPlacement(final ProbInIntervalResultWrapper w)
    {
        final XYCoordinate lastCoord = w.getPoints().getMaxX();
        double xCoord, yCoord;

        final double maxXFrac = 1 / 3.0;
        final double acceptableX = lastCoord.getX() * maxXFrac;
        if(w.getLowerBound() < acceptableX)
        {
            xCoord = w.getLowerBound();
        }
        else
        {
            xCoord = acceptableX;
        }

        final double minY = w.getPoints().getMinY().getY();
        final double maxY = w.getPoints().getMaxY().getY();

        final double percentage = 0.05;
        final double yPadding = percentage * (maxY - minY);
        final double yComponent = w.getPoints().getCoordinateAtX(w.getLowerBound()).getY();

        if(maxY - yComponent <= yPadding)
        {
            yCoord = maxY - yPadding;
        }
        else if(yComponent - minY < yPadding)
        {
            yCoord = yPadding;
        }
        else
        {
            yCoord = yComponent;
        }
        return new XYCoordinate(xCoord, yCoord);
    }

    private XYPlot setupPercentile(final PercentileResultWrapper percentileWrapper, final XYItemRenderer r)
    {
        final XYPlot plot = new XYPlot(this.seriesCollection,
                                       new NumberAxis("Time"),
                                       new NumberAxis("Probability"),
                                       r);
        r.setSeriesPaint(0, Color.BLACK);

        final BasicStroke bstroke = new BasicStroke(1.0f,
                                                    BasicStroke.CAP_BUTT,
                                                    BasicStroke.JOIN_MITER,
                                                    10,
                                                    new float[]{5, 5, 5},
                                                    0);

        final XYLineAnnotation note1 = new XYLineAnnotation(percentileWrapper.getNumResult(),
                                                            0,
                                                            percentileWrapper.getNumResult(),
                                                            percentileWrapper.getPercentile() / 100,
                                                            bstroke,
                                                            Color.RED);
        final XYLineAnnotation note2 = new XYLineAnnotation(0,
                                                            percentileWrapper.getPercentile() / 100,
                                                            percentileWrapper.getNumResult(),
                                                            percentileWrapper.getPercentile() / 100,
                                                            bstroke,
                                                            Color.BLACK);

        double orientation = Math.PI / 4 * (percentileWrapper.getPercentile() < 15 ? 7 : 1);
        final XYPointerAnnotation note3 = new XYPointerAnnotation(StringHelper.getStringTH(percentileWrapper.getPercentile()) +
                                                                          " Percentile is " +
                                                                          String.valueOf(percentileWrapper.getNumResult()),
                                                                  percentileWrapper.getNumResult(),
                                                                  percentileWrapper.getPercentile() / 100,
                                                                  orientation);
        note3.setBaseRadius(35.0);
        note3.setTipRadius(10.0);
        note3.setFont(new Font("SansSerif", Font.PLAIN, 10));

        note3.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        note3.setPaint(Color.RED);
        plot.addAnnotation(note1);
        plot.addAnnotation(note2);
        plot.addAnnotation(note3);
        return plot;
    }

    private XYPlot setupProbInInterval(final ProbInIntervalResultWrapper piiWrapper, final XYItemRenderer r)
    {
        final XYPlot plot = new XYPlot(this.seriesCollection,
                                       new NumberAxis("Time"),
                                       new NumberAxis("Probability"),
                                       r);
        r.setSeriesPaint(0, Color.BLACK);

        final BasicStroke bstroke = new BasicStroke(1.0f,
                                                    BasicStroke.CAP_BUTT,
                                                    BasicStroke.JOIN_MITER,
                                                    10,
                                                    new float[]{5, 5, 5},
                                                    0);

        final ArrayList<Double> subPoints = new ArrayList<Double>();
        final XYCoordinates coords = piiWrapper.getPoints();
        for(final pipe.modules.queryresult.XYCoordinate coord : coords.getFromXToX(piiWrapper.getLowerBound(),
                                                                                   piiWrapper.getUpperBound()))
        {
            subPoints.add(coord.getX());
            subPoints.add(coord.getY());
        }
        subPoints.add(piiWrapper.getUpperBound());
        subPoints.add(0.0);

        subPoints.add(piiWrapper.getLowerBound());
        subPoints.add(0.0);
        final double[] polyPoints = new double[subPoints.size()];
        int counter = 0;
        for(final Double d : subPoints)
        {
            polyPoints[counter++] = d;
        }

        final XYPolygonAnnotation rangePolygon = new XYPolygonAnnotation(polyPoints,
                                                                         bstroke,
                                                                         Color.RED,
                                                                         Color.YELLOW);

        final XYCoordinate textPos = this.getTextPlacement(piiWrapper);

        final XYTextAnnotation text = new XYTextAnnotation("Probability passage time takes place between " +
                                                                   piiWrapper.getLowerBound() + " and " +
                                                                   piiWrapper.getUpperBound() + " = " +
                                                                   String.valueOf(piiWrapper.getNumResult()),
                                                           textPos.getX(),
                                                           textPos.getY());

        text.setFont(new Font("SansSerif", Font.PLAIN, 10));
        text.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        text.setPaint(Color.RED);

        plot.addAnnotation(rangePolygon);
        plot.addAnnotation(text);
        return plot;
    }

}
