package xml2xls;

import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetConverter {

   final String netFileName;
   final String xlsFileName;
   XLSCreator xls;

   NetConverter(String netFileName, String xlsFileName)
   {
      this.netFileName = netFileName;
      this.xlsFileName = xlsFileName;
   }

   boolean IsValid(File schemaFile) {
      SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

      try {
         Schema schema = factory.newSchema(schemaFile);
         Validator validator = schema.newValidator();
         try {
            Source source;
            source = new StreamSource(new File(this.netFileName));
            try {
               validator.validate(source);
            } catch (IOException ex) {
               Logger.getLogger(PetriNetConverter.class.getName()).
                       log(Level.SEVERE, null, ex);
            }
            System.out.println(this.netFileName + " is valid.");
            return true;
         } catch (SAXException ex) {
            System.out.println(this.netFileName + " is not valid because ");
            System.out.println(ex.getMessage());
            return false;
         }
      } catch (SAXException ex) {
         Logger.getLogger(PetriNetConverter.class.getName()).
                 log(Level.SEVERE, null, ex);
      }
      return true;
   }
}