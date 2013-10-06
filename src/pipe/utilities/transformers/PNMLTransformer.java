package pipe.utilities.transformers;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;


/**
 * Create pnmlTransformer object, which deals with getting information from the 
 * XML file, then passes this back to PetriNet constructor for construction
 * @author Ben Kirby, 10 Feb 2007
 */
public class PNMLTransformer {
   
   
   /** Create a Transformer*/
   public PNMLTransformer() {
   }
   
   
   /** 
    * Transform a PNML file into a Document which is returned and used to
    * construct the PetriNet
    * @returns Document from which PetriNet can be built
    * @param filename URI location of PNML
    * @throws TransformerException
    * @return
    */
   public Document transformPNML(String filename) 
   {
      return transformPNMLStreamSource(new StreamSource(filename));
   }

   /** 
    * Transform a StreamSource into a Document which is returned and used to
    * construct the PetriNet.  
    * Typical use:  new StreamSource(new ByteArrayInputStream(XMLString.getBytes()))
    * @returns Document from which PetriNet can be built
    * @param StreamSource containing XML 
    * @throws TransformerException
    * @return
    */
   	// Steve Doubleday:  added to simplify testing; pass in a test Net as a String rather than doing file I/O
	public Document transformPNMLStreamSource(StreamSource source)
			throws TransformerFactoryConfigurationError
	{
		File outputObjectArrayListFile = null;
	      Document document = null;
	      
	
	      try {
	         // Create Transformer with XSL Source File
	         StreamSource xsltSource = new StreamSource(
	                 Thread.currentThread().getContextClassLoader().getResourceAsStream(
	                 "xslt" + System.getProperty("file.separator")
	                 + "GenerateObjectList.xsl"));
	         Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
	         
	         // TRY TO DO ALL IN MEMORT TO REDUCE READ-WRITE DELAYS
	         
	         // Output for XSLT Transformation
	         outputObjectArrayListFile = new File(System.getProperty("java.io.tmpdir") + 
	                 System.getProperty("file.separator") + "ObjectList.xml"); 
	         outputObjectArrayListFile.deleteOnExit();
	         StreamResult result = new StreamResult(outputObjectArrayListFile);
	         transformer.transform(source, result);
	
	         // Get DOM for transformed document
	         document = getDOM(outputObjectArrayListFile);    
	      }
	      catch (TransformerException e) {
	         System.out.println("TransformerException thrown in " +
	                 "loadPNML(String filename) : PetriNet Class : models Package");
	         e.printStackTrace(System.err);
	      }
	
	//		Delete transformed file
	      if (outputObjectArrayListFile != null) {
	         outputObjectArrayListFile.delete();
	      }
	      return document;
	      //BK - surely I want to make everything exact?? - ignore below in favour of catches above
	      //	 Maxim - make it throw out any exception it gets to the caller. Debugging message left in for now.
	      //} catch (Exception e) {
	      //  throw new RuntimeException(e);
	      //}	    
	}
   
   /**
    * Return a DOM for the PNML File pnmlFile
    * @param pnmlFile File Object for PNML of Petri-Net
    * @return A DOM for the File Object for PNML of Petri-Net
    * @throws ParserConfigurationException
    * @throws IOException
    * @throws SAXException
    */
   private Document getDOM(File pnmlFile)
   {
      Document document = null;
      
      try {
         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         documentBuilderFactory.setIgnoringElementContentWhitespace(true);
// POSSIBLY ADD VALIDATING
// documentBuilderFactory.setValidating(true);
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         document = documentBuilder.parse(pnmlFile);
      } catch (ParserConfigurationException e) {
         System.err.println("javax.xml.parsers.ParserConfigurationException thrown in getDom(String pnmlFileName) : PetriNet Class : models Package");
      } catch (IOException e) {
         System.err.println("ERROR: File may not be present or have the correct attributes");
         System.err.println("java.io.IOException thrown in getDom(String pnmlFileName) : PetriNet Class : models Package" + e);
      } catch (SAXException e) { 
         System.err.println("org.xml.sax.SAXException thrown in getDom(String pnmlFileName) : PetriNet Class : models Package" + e);
         System.err.println("Workaround: delete the xmlns attribute from the PNML root node.  Probably not ideal, to be fixed when time allows.");
      }
      return document;
   }



}
