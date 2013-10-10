package xml2xls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Petri Nets Coverter Class
 *
 */
class PetriNetConverter extends NetConverter {

   /**
    * Constructor.
    *
    * @param pnFileName        PetriNetViewComponent Output XML file
    * @param xlsFileName       Excel XLS file
    * @throws java.io.FileNotFoundException
    */
   public PetriNetConverter(String pnFileName, String xlsFileName)
           throws FileNotFoundException {
      super(pnFileName, xlsFileName);
   }

   /**
    * Method called to perform a validation.
    *
    * @return      <code>TRUE</code> if it's valid.
    *              <code>FALSE</code> otherwise.
    */
   public boolean validatePN() {

      try {
         File xsdFile = new File(Thread.currentThread().getContextClassLoader().
                 getResource("schema/PN-ExOutput.xsd").toURI());
         if (this.IsValid(xsdFile)) {
            return true;
         }
      } catch (URISyntaxException ex) {
         System.out.println("Failed to read schema document; URISyntaxException: " +
                 ex.getMessage());
      }

      return false;
   }

   /**
    * Starts the conversion xml-xls.
    * It's recommended to call validatePN() before.
    *
    * @throws javax.xml.parsers.ParserConfigurationException
    * @throws org.xml.sax.SAXException
    * @throws FileNotFoundException
    * @throws java.io.IOException
    */
   public void convert()
           throws ParserConfigurationException, SAXException,
           IOException {
      File file = new File(this.netFileName);
      xls = new XLSCreator(this.xlsFileName);

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);

      doc.getDocumentElement().normalize();
      //System.out.println("Root element " + doc.getDocumentElement().getNodeName());

      int solution = 0;
      if (doc.getDocumentElement().hasChildNodes()) {
         for (Node childNode = doc.getDocumentElement().getFirstChild(); childNode != null;) {
            Node nextChild = childNode.getNextSibling();
            if (childNode.getNodeName().equals("Solution")) {
               xls.newSheet(String.valueOf(solution));
               xls.newRow(0);
               xls.writeCell("Solution ID", 0, 1, true);
               String solutionName = "";
               NamedNodeMap attrs = childNode.getAttributes();
               for (int i = 0; i < attrs.getLength(); i++) {
                  Attr attribute = (Attr) attrs.item(i);
                  if (attribute.getName().equals("ID")) {
                     solutionName = attribute.getValue();
                     xls.writeCell(solutionName, 1, 1, true);
                  }
               }
               xls.newRow(1);
               // childNode is the solution node.
               this.valueUsed(childNode);
               this.variable(childNode);
               this.outputPlace(childNode);
               this.outputTransition(childNode);
               this.outputPlaceInvariants(childNode);
               this.outputTransitionInvariants(childNode);
               this.outputMinimalSiphons(childNode);
               this.outputMinimalTraps(childNode);
               this.outputStructuralProperties(childNode);
               xls.setSheetName(solution + 1, solutionName);
               solution++;
            }
            childNode = nextChild;
         }
      }
      xls.close();
   }

   private void valueUsed(Node solutionNode) {
      boolean header = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("ValueUsed")) {
            if (!header) {
               xls.writeCell("ValueUsed", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("VariableName", 0, 1, true);
               xls.writeCell("VariableValue", 1, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("VariableName")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (attribute.getName().equals("VariableValue")) {
                  xls.writeCell(value, 1, 0, false);
               }
               // both attributes are mandatory
               if (i % 2 == 1) {
                  xls.newRow(1);
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void variable(Node solutionNode) {
      boolean header = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("Variable")) {
            if (!header) {
               xls.writeCell("Variable", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("VariableName", 0, 1, true);
               xls.writeCell("VariableValue", 1, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("VariableName")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (attribute.getName().equals("VariableValue")) {
                  xls.writeCell(value, 1, 0, false);
               }
               // both attributes are mandatory
               if (i % 2 == 1) {
                  xls.newRow(1);
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputTransition(Node solutionNode) {
      boolean header = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputTransition")) {
            if (!header) {
               xls.writeCell("OutputTransition", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("TransitionID", 0, 1, true);
               xls.writeCell("Throughput", 1, 1, true);
               xls.writeCell("TimeUnits", 2, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("TransitionID")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (attribute.getName().equals("Throughput")) {
                  xls.writeCell(value, 1, 0, false);
               }
               if (attribute.getName().equals("TimeUnits")) {
                  xls.writeCell(value, 2, 0, true);
               }
               if (i == attrs.getLength() - 1) {
                  xls.newRow(1);
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputPlace(Node solutionNode) {
      boolean header = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputPlace")) {
            if (!header) {
               xls.writeCell("OutputPlace", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("PlaceID", 0, 1, true);
               xls.writeCell("AvgNumOfTokens", 1, 1, true);
               xls.writeCell("Utilization", 2, 1, true);
               xls.writeCell("TimeUnits", 3, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("PlaceID")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (attribute.getName().equals("AverageNumberOfTokens")) {
                  xls.writeCell(value, 1, 0, false);
               }
               if (attribute.getName().equals("Utilization")) {
                  xls.writeCell(value, 2, 0, false);
               }
               if (attribute.getName().equals("TimeUnits")) {
                  xls.writeCell(value, 3, 0, true);
               }
               if (i == attrs.getLength() - 1) {
                  xls.newRow(1);
               }
            }
            if (childNode2.hasChildNodes()) {
               xls.writeCell("TokenProbDensity", 0, 0, true);
               xls.writeCell("NumberOfTokens", 1, 0, true);
               xls.writeCell("Probability", 2, 0, true);
               xls.newRow(1);
               for (Node childNode3 = childNode2.getFirstChild(); childNode3 != null;) {
                  Node nextChild3 = childNode3.getNextSibling();
                  if (childNode3.getNodeName().equals("TokenProbabilityDensity")) {
                     NamedNodeMap attrs2 = childNode3.getAttributes();
                     for (int j = 0; j < attrs2.getLength(); j++) {
                        Attr attribute2 = (Attr) attrs2.item(j);
                        String value2 = attribute2.getValue();
                        if (attribute2.getName().equals("NumberOfTokens")) {
                           xls.writeCell(value2, 1, 0, false);
                        }
                        if (attribute2.getName().equals("Probability")) {
                           xls.writeCell(value2, 2, 0, false);
                        }
                        if (j == attrs2.getLength() - 1) {
                           xls.newRow(1);
                        }
                     }
                  }
                  childNode3 = nextChild3;
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputPlaceInvariants(Node solutionNode) {
      boolean header = false;
      boolean header2 = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputPlaceInvariants")) {
            if (!header) {
               xls.writeCell("OutputPlaceInvariants", 0, 1, true);
               xls.writeCell("InvariantEquations", 1, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("InvariantEquations")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (i == attrs.getLength() - 1) {
                  xls.newRow(1);
               }
            }
            if (childNode2.hasChildNodes()) {
               for (Node childNode3 = childNode2.getFirstChild(); childNode3 != null;) {
                  Node nextChild3 = childNode3.getNextSibling();
                  if (childNode3.getNodeName().equals("PlaceInvariant")) {
                     xls.writeCell("PlaceInvariant", 0, 0, true);
                     xls.newRow(1);
                     for (Node childNode4 = childNode3.getFirstChild(); childNode4 != null;) {
                        Node nextChild4 = childNode4.getNextSibling();
                        if (childNode4.getNodeName().equals("Place")) {
                           if (!header2) {
                              xls.writeCell("Place", 0, 0, true);
                              xls.writeCell("PlaceID", 1, 0, true);
                              xls.writeCell("Value", 2, 0, true);
                              xls.newRow(1);
                              header2 = true;
                           }
                           NamedNodeMap attrs2 = childNode4.getAttributes();
                           for (int j = 0; j < attrs2.getLength(); j++) {
                              Attr attribute2 = (Attr) attrs2.item(j);
                              String value2 = attribute2.getValue();
                              if (attribute2.getName().equals("PlaceID")) {
                                 xls.writeCell(value2, 1, 0, true);
                              }
                              if (attribute2.getName().equals("Value")) {
                                 xls.writeCell(value2, 2, 0, false);
                              }
                              if (j == attrs2.getLength() - 1) {
                                 xls.newRow(1);
                              }
                           }
                        }
                        childNode4 = nextChild4;
                     }
                     header2 = false;
                  }
                  childNode3 = nextChild3;
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputTransitionInvariants(Node solutionNode) {
      boolean header = false;
      boolean header2 = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputTransitionInvariants")) {
            if (!header) {
               xls.writeCell("OutputTransitionInvariants", 0, 1, true);
               xls.writeCell("InvariantEquations", 1, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("InvariantEquations")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (i == attrs.getLength() - 1) {
                  xls.newRow(1);
               }
            }
            if (childNode2.hasChildNodes()) {
               for (Node childNode3 = childNode2.getFirstChild(); childNode3 != null;) {
                  Node nextChild3 = childNode3.getNextSibling();
                  if (childNode3.getNodeName().equals("TransitionInvariant")) {
                     xls.writeCell("TransitionInvariant", 0, 0, true);
                     xls.newRow(1);
                     for (Node childNode4 = childNode3.getFirstChild(); childNode4 != null;) {
                        Node nextChild4 = childNode4.getNextSibling();
                        if (childNode4.getNodeName().equals("Transition")) {
                           if (!header2) {
                              xls.writeCell("Transition", 0, 0, true);
                              xls.writeCell("TransitionID", 1, 0, true);
                              xls.writeCell("Value", 2, 0, true);
                              xls.newRow(1);
                              header2 = true;
                           }
                           NamedNodeMap attrs2 = childNode4.getAttributes();
                           for (int j = 0; j < attrs2.getLength(); j++) {
                              Attr attribute2 = (Attr) attrs2.item(j);
                              String value2 = attribute2.getValue();
                              if (attribute2.getName().equals("TransitionID")) {
                                 xls.writeCell(value2, 1, 0, true);
                              }
                              if (attribute2.getName().equals("Value")) {
                                 xls.writeCell(value2, 2, 0, false);
                              }
                              if (j == attrs2.getLength() - 1) {
                                 xls.newRow(1);
                              }
                           }
                        }
                        childNode4 = nextChild4;
                     }
                     header2 = false;
                  }
                  childNode3 = nextChild3;
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputMinimalSiphons(Node solutionNode) {
      boolean header = false;
      boolean header2 = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputMinimalSiphons")) {
            if (!header) {
               xls.writeCell("OutputMinimalSiphons", 0, 1, true);
               xls.newRow(1);
               header = true;
            }
            if (childNode2.hasChildNodes()) {
               for (Node childNode3 = childNode2.getFirstChild(); childNode3 != null;) {
                  Node nextChild3 = childNode3.getNextSibling();
                  if (childNode3.getNodeName().equals("MinimalSiphon")) {
                     xls.writeCell("MinimalSiphon", 0, 0, true);
                     xls.newRow(1);
                     for (Node childNode4 = childNode3.getFirstChild(); childNode4 != null;) {
                        Node nextChild4 = childNode4.getNextSibling();
                        if (childNode4.getNodeName().equals("Place")) {
                           if (!header2) {
                              xls.writeCell("Place", 0, 0, true);
                              xls.writeCell("PlaceID", 1, 0, true);
                              xls.newRow(1);
                              header2 = true;
                           }
                           NamedNodeMap attrs2 = childNode4.getAttributes();
                           for (int j = 0; j < attrs2.getLength(); j++) {
                              Attr attribute2 = (Attr) attrs2.item(j);
                              String value2 = attribute2.getValue();
                              if (attribute2.getName().equals("PlaceID")) {
                                 xls.writeCell(value2, 1, 0, true);
                              }
                              if (j == attrs2.getLength() - 1) {
                                 xls.newRow(1);
                              }
                           }
                        }
                        childNode4 = nextChild4;
                     }
                     header2 = false;
                  }
                  childNode3 = nextChild3;
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputMinimalTraps(Node solutionNode) {
      boolean header = false;
      boolean header2 = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputMinimalTraps")) {
            if (!header) {
               xls.writeCell("OutputMinimalTraps", 0, 1, true);
               xls.newRow(1);
               header = true;
            }
            if (childNode2.hasChildNodes()) {
               for (Node childNode3 = childNode2.getFirstChild(); childNode3 != null;) {
                  Node nextChild3 = childNode3.getNextSibling();
                  if (childNode3.getNodeName().equals("MinimalTrap")) {
                     xls.writeCell("MinimalTrap", 0, 0, true);
                     xls.newRow(1);
                     for (Node childNode4 = childNode3.getFirstChild(); childNode4 != null;) {
                        Node nextChild4 = childNode4.getNextSibling();
                        if (childNode4.getNodeName().equals("Place")) {
                           if (!header2) {
                              xls.writeCell("Place", 0, 0, true);
                              xls.writeCell("PlaceID", 1, 0, true);
                              xls.newRow(1);
                              header2 = true;
                           }
                           NamedNodeMap attrs2 = childNode4.getAttributes();
                           for (int j = 0; j < attrs2.getLength(); j++) {
                              Attr attribute2 = (Attr) attrs2.item(j);
                              String value2 = attribute2.getValue();
                              if (attribute2.getName().equals("PlaceID")) {
                                 xls.writeCell(value2, 1, 0, true);
                              }
                              if (j == attrs2.getLength() - 1) {
                                 xls.newRow(1);
                              }
                           }
                        }
                        childNode4 = nextChild4;
                     }
                     header2 = false;
                  }
                  childNode3 = nextChild3;
               }
            }
         }
         childNode2 = nextChild2;
      }
   }

   private void outputStructuralProperties(Node solutionNode) {
      boolean header = false;
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputStructuralProperties")) {
            if (!header) {
               xls.writeCell("Property", 0, 1, true);
               xls.writeCell("Value", 1, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               String value = attribute.getValue();
               if (attribute.getName().equals("Property")) {
                  xls.writeCell(value, 0, 0, true);
               }
               if (attribute.getName().equals("Value")) {
                  xls.writeCell(value, 1, 0, true);
               }
               // both attributes are mandatory
               if (i % 2 == 1) {
                  xls.newRow(1);
               }
            }
         }
         childNode2 = nextChild2;
      }
   }
}