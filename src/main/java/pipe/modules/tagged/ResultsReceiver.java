package pipe.modules.tagged;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.clientCommon.SocketIO;

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

/**
 * This method receives the results file from the server and displays a graph of the data 
 * 
 * @author Barry Kearns
 * @date September 2007
 */

class ResultsReceiver
{
	private final SocketIO server;
	private final JPanel resultsPanel;
	private JFreeChart pdfGraph, cdfGraph;
    private ChartPanel cdfChartPanel;
	private CardLayout graphFlip;
	private JPanel graphPanel;
	
	
	private JButton switchViewBtn;
	private boolean currentlyPDF = true; // Is the graph currently showing a PDF view


    public ResultsReceiver(SocketIO server, JPanel resultsPanel, String currentStatus)
	{
		this.server = server;
		this.resultsPanel = resultsPanel;
	}
	
	public void receive()
	{			
		String results = server.receiveFileContent();
		
		// Generate a set of points from the results
		XYSeries points = new XYSeries("Passage Time Results");
		XYSeries CDFpoints = new XYSeries("Passage Time Results (CDF)");
		boolean doCDF = false;
		String[] lines, values;
		
		// Divide the results into lines
		lines = results.split("\\r+|\n+");
		
				
		try
		{
			int i;
			
			// The first two values of each line are the X,Y cordinates			
			for(i=0; i<lines.length;i++)
			{
				// If we hit the CDF marker, PDF results have finished
				if (lines[i].equals("CDF <br>"))
				{
					doCDF = true;
					i++;
					break;
				}
					
				values = lines[i].split("\\s+");
				points.add(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
			}
			
			// If there is a set of cumulative distribution function results, add to CDFpoints
			if (doCDF)
			{
				while(i< lines.length)
				{
					values = lines[i].split("\\s+");
					CDFpoints.add(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
					i++;
				}
				
			
				// Create CDF graph panel
                XYDataset CDFDataset = new XYSeriesCollection(CDFpoints);
				cdfGraph = ChartFactory.createXYLineChart("Passage Time Results", "Time","Probability Density", CDFDataset, PlotOrientation.VERTICAL, false, false, false); 
				cdfGraph.setBackgroundPaint(Color.white); 
				cdfChartPanel = new ChartPanel(cdfGraph);
			}
		
			// Create PDF graph panel
            XYDataset PDFDataset = new XYSeriesCollection(points);
			pdfGraph = ChartFactory.createXYLineChart("Passage Time Results", "Time","Probability Density", PDFDataset, PlotOrientation.VERTICAL, false, false, false); 
			pdfGraph.setBackgroundPaint(Color.white);
            ChartPanel pdfChartPanel = new ChartPanel(pdfGraph);
			
			
			graphFlip = new CardLayout();
			graphPanel = new JPanel(graphFlip);
			graphPanel.add(pdfChartPanel, "PDF");
			
			if(doCDF)
				graphPanel.add(cdfChartPanel, "CDF");		
			
	
			//	 Create the results panel
			resultsPanel.removeAll(); // clear if previously used
			resultsPanel.setLayout(new BorderLayout());
			resultsPanel.add(graphPanel, BorderLayout.CENTER);
			
			// Create button panel then add
			JPanel buttons = new JPanel();
			
			if (doCDF)
			{
				switchViewBtn = new JButton("Show CDF");
				switchViewBtn.addActionListener(switchView);
				switchViewBtn.setMnemonic(KeyEvent.VK_V);
				
				buttons.add(switchViewBtn);
			}
			
			JButton saveImageBtn = new JButton("Save Graph");
			saveImageBtn.addActionListener(pngListener);
			saveImageBtn.setMnemonic(KeyEvent.VK_S);
			
			JButton saveCordBtn = new JButton("Save Points");
			saveCordBtn.addActionListener(cvsListener);
			saveCordBtn.setMnemonic(KeyEvent.VK_C);		
			
			buttons.add(saveImageBtn);
			buttons.add(saveCordBtn);		
			
			resultsPanel.add(buttons, BorderLayout.PAGE_END);
		}
		
		// If error occurs on the server then the results will be the error message 
		catch(Exception exp)
		{
			HTMLPane errorText = new HTMLPane("Error calculating results");
			errorText.setText(results + exp);
		
			resultsPanel.removeAll(); // clear if previously used
			resultsPanel.setLayout(new BorderLayout());
			resultsPanel.add(errorText, BorderLayout.CENTER);			
		}
	}
	
	
	private final ActionListener switchView = new ActionListener()
	{
		public void actionPerformed(ActionEvent eve)
		{
			if (currentlyPDF)
			{
				graphFlip.show(graphPanel, "CDF");
				switchViewBtn.setText("Show PDF");
			}
			else
			{
				graphFlip.show(graphPanel, "PDF");
				switchViewBtn.setText("Show CDF");				
			}
			
			currentlyPDF = !currentlyPDF; // flip bool
		}
	};
	
	
	
	private final ActionListener pngListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent eve) {
			
			RenderedImage graphImage;
			
			if (currentlyPDF)
				graphImage = pdfGraph.createBufferedImage(800, 600);
			else
				graphImage = cdfGraph.createBufferedImage(800, 600);
			
			
			File saveFile;
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(resultsPanel);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				saveFile = fc.getSelectedFile();
			}
			else
				return;
			try {
				ImageIO.write(graphImage, "png", saveFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	


	private final ActionListener cvsListener = new ActionListener() {
	  	public void actionPerformed(ActionEvent arg0) {
	  		XYDataset graphData; 
	  		
	  		if(currentlyPDF)
	  			graphData = pdfGraph.getXYPlot().getDataset();
	  		else
	  			graphData = cdfGraph.getXYPlot().getDataset();
	  			
	  		int size = graphData.getItemCount(0);
	  		
	  		FileWriter fw = null;
	  		StringBuffer content = new StringBuffer();
	  		
	  		File saveFile;
		
			JFileChooser fc = new JFileChooser();			
			
			int returnVal = fc.showSaveDialog(resultsPanel);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				saveFile = fc.getSelectedFile();
			}
			else
				return;
			
			try {
				fw = new FileWriter(saveFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			for(int i = 0; i < size; i++) {
				content.append(graphData.getXValue(0, i));
				content.append(",");
				content.append(graphData.getYValue(0, i));
				content.append(",\n");				
			}
	  		String content1 = content.toString();
	  		
	  		try {
		  		fw.write(content1);
		  		fw.close();
	  		}
	  		catch (Exception e) {
	  			e.printStackTrace();
	  		}
	  	}
	  };
}
