/**
 * QueryExporter
 * 
 * - Exports query trees to other formats, as well as printing.
 * 
 * @author Tamas Suto
 * @date 09/05/07
 */

package pipe.modules.queryeditor.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import pipe.gui.Grid;
import pipe.gui.widgets.FileBrowser;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;


public class PerformanceTreeExporter {

	public static final int PNG = 1;
	public static final int EPS = 2;
	public static final int PRINTER = 3;


	public static void exportQueryView(QueryView g,int format) {
		boolean gridEnabled = Grid.isEnabled();
		String filename = null;

		if(g.getComponentCount() == 0) 
			return;

		if(QueryManager.getFile() != null) {
			filename = QueryManager.getFile().getAbsolutePath();
			// change file extension
			int dotpos = filename.lastIndexOf('.');
			if(dotpos > filename.lastIndexOf(System.getProperty("file.separator"))) {
				// dot is for extension
				filename = filename.substring(0,dotpos+1);
				switch(format) {
				case PNG:        
					filename += "png"; 
					break;
				case EPS: 
					filename += "eps";  
					break;
				}
			}
		}

		// Stuff to make it export properly
		g.updatePreferredSize();
		PerformanceTreeObject.ignoreSelection(true);
		
		if(gridEnabled)
			Grid.disableGrid();

		try {
			switch(format) {
			case PNG:
				filename = new FileBrowser("PNG image","png",filename).saveFile();
				if(filename != null)
					toPNG(g,filename);
				break;
			case EPS:
				filename = new FileBrowser("ExtendedPostScript file","eps",filename).saveFile();
				if(filename != null)
					toPostScript(g,filename);
				break;
			case PRINTER:
				toPrinter(g);
				break;
			}
		} catch (Exception e) {
			// There was some problem with the action
			JOptionPane.showMessageDialog(QueryManager.getEditor(),
					"There were errors performing the requested action:\n"+e,
					"Error",JOptionPane.ERROR_MESSAGE
			);
		}

		if(gridEnabled)
			Grid.enableGrid();
		
		PerformanceTreeObject.ignoreSelection(false);
		g.repaint();

    }
	
	public static void exportMacroView(MacroView g,int format) {
		boolean gridEnabled = Grid.isEnabled();
		String filename = null;

		if(g.getComponentCount() == 0) 
			return;

		if(QueryManager.getFile() != null) {
			filename = QueryManager.getFile().getAbsolutePath();
			// change file extension
			int dotpos = filename.lastIndexOf('.');
			if(dotpos > filename.lastIndexOf(System.getProperty("file.separator"))) {
				// dot is for extension
				filename = filename.substring(0,dotpos+1);
				switch(format) {
				case PNG:        
					filename += "png"; 
					break;
				case EPS: 
					filename += "eps";  
					break;
				}
			}
		}

		// Stuff to make it export properly
		g.updatePreferredSize();
		PerformanceTreeObject.ignoreSelection(true);
		
		if(gridEnabled)
			Grid.disableGrid();

		try {
			switch(format) {
			case PNG:
				filename = new FileBrowser("PNG image","png",filename).saveFile();
				if(filename != null)
					toPNG(g,filename);
				break;
			case EPS:
				filename = new FileBrowser("ExtendedPostScript file","eps",filename).saveFile();
				if(filename != null)
					toPostScript(g,filename);
				break;
			case PRINTER:
				toPrinter(g);
				break;
			}
		} catch (Exception e) {
			// There was some problem with the action
			JOptionPane.showMessageDialog(QueryManager.getEditor(),
					"There were errors performing the requested action:\n"+e,
					"Error",JOptionPane.ERROR_MESSAGE
			);
		}

		if(gridEnabled)
			Grid.enableGrid();
		
		PerformanceTreeObject.ignoreSelection(false);
		g.repaint();

    }
	
	/**
	 * Exports a Performance Tree query to an EPS file
	 * @param g
	 * @param filename
	 * @throws PrintException
	 * @throws IOException
	 */
	private static void toPostScript(Object g, String filename) throws PrintException, IOException {
		// Input document type
		DocFlavor flavour = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		// Output stream MIME type
		String psMimeType = DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();

		// Look up a print service factory that can handle this job
		StreamPrintServiceFactory[] factories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavour, psMimeType);
		if (factories.length == 0) throw new RuntimeException("No suitable factory found for export to EPS");

		FileOutputStream f = new FileOutputStream(filename);
		// Get a print service from the factory, create a print job and print
		factories[0].getPrintService(f).createPrintJob().print(
				new SimpleDoc(g, flavour, null), 
				new HashPrintRequestAttributeSet()
		);
		f.close();
	}

	/**
	 * Exports a Performance Tree query to a PNG file
	 * @param g
	 * @param filename
	 * @throws IOException
	 */
	private static void toPNG(JComponent g, String filename) throws IOException {
		Iterator i=ImageIO.getImageWritersBySuffix("png");
		
		if(!i.hasNext()) 
			throw new RuntimeException("No ImageIO exporters can handle PNG");

		File f = new File(filename);
		BufferedImage img = new BufferedImage(g.getPreferredSize().width,g.getPreferredSize().height,BufferedImage.TYPE_3BYTE_BGR);
		g.print(img.getGraphics());
		ImageIO.write(img,"png",f);
	}

	private static void toPrinter(Object g) throws PrintException {
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavour = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavour, pras);
		
		if(printService.length == 0)
			throw new PrintException("\nUnable to locate a compatible printer service.\nTry exporting to PostScript.");
		
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		PrintService service = ServiceUI.printDialog(null, 200, 200, printService, defaultService, flavour, pras);
		
		if (service != null) {
			DocPrintJob job = service.createPrintJob();
			DocAttributeSet das = new HashDocAttributeSet();
			Doc doc = new SimpleDoc(g, flavour, das);
			job.print(doc, pras);
		}
	}

}