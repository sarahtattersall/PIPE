package xml2xls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Queue Nets Coverter Class
 *
 */
class QueueNetConverter extends NetConverter {

   /**
    * Constructor.
    *
    * @param pnFileName        PetriNetViewComponent Output XML file
    * @param xlsFileName       Excel XLS file
    * @throws java.io.FileNotFoundException
    */
   public QueueNetConverter(String pnFileName, String xlsFileName) throws FileNotFoundException {
      super(pnFileName, xlsFileName);
   }


   /**
    * Method called to perform a validation.t
    *
    * @return      <code>TRUE</code> if it's valid.
    *              <code>FASLE</code> otherwise.
    */
   public boolean validateQN() {
      try {
         File xsdFile = new File(Thread.currentThread().getContextClassLoader().
                 getResource("schema/pmif-ExOutput.xsd").toURI());
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
    * It's recommended to call validateQN() before.
    *
    * @throws javax.xml.parsers.ParserConfigurationException
    * @throws org.xml.sax.SAXException
    * @throws FileNotFoundException
    * @throws java.io.IOException
    */
   public void convert() throws ParserConfigurationException, SAXException, IOException {
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
               this.outputWorkload(childNode);
               this.outputNode(childNode);
               this.outputNodeWorkload(childNode);
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


   private void outputWorkload(Node solutionNode) {
      boolean header = false;
      ArrayList<String> workloadNames = new ArrayList<String>();
      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputWorkload")) {
            if (!header) {
               xls.writeCell("OutputWorkload", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("WorkloadName", 0, 1, true);
               xls.writeCell("Throughput", 1, 1, true);
               xls.writeCell("ResponseTime", 2, 1, true);
               xls.writeCell("TimeUnits", 3, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            boolean found = false;
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               if (workloadNames.contains(attribute.getValue())) {
                  found = true;
               }
            }
            if (!found) {
               for (int i = 0; i < attrs.getLength(); i++) {
                  Attr attribute = (Attr) attrs.item(i);
                  String value = attribute.getValue();
                  if (attribute.getName().equals("WorkloadName")) {
                     xls.writeCell(value, 0, 0, true);
                     workloadNames.add(value);
                     Node childNode3 = childNode2.getNextSibling();
                     while (childNode3 != null) {
                        if (childNode3.getNodeName().equals("OutputWorkload")) {
                           NamedNodeMap attrs2 = childNode3.getAttributes();
                           for (int j = 0; j < attrs2.getLength(); j++) {
                              Attr attribute2 = (Attr) attrs2.item(j);
                              if (attribute2.getName().equals("WorkloadName")) {
                                 String value2 = attribute2.getValue();
                                 if (value2.equals(value)) {
                                    for (int k = 0; k < attrs2.getLength(); k++) {
                                       Attr attribute3 = (Attr) attrs2.item(k);
                                       String value3 = attribute3.getValue();
                                       if (attribute3.getName().equals("Throughput")) {
                                          xls.writeCell(value3, 1, 0, false);
                                       }
                                       if (attribute3.getName().equals("ResponseTime")) {
                                          xls.writeCell(value3, 2, 0, false);
                                       }
                                       if (attribute3.getName().equals("TimeUnits")) {
                                          xls.writeCell(value3, 3, 0, true);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                        childNode3 = childNode3.getNextSibling();
                     }
                  }
                  if (attribute.getName().equals("Throughput")) {
                     xls.writeCell(value, 1, 0, false);
                  }
                  if (attribute.getName().equals("ResponseTime")) {
                     xls.writeCell(value, 2, 0, false);
                  }
                  if (attribute.getName().equals("TimeUnits")) {
                     xls.writeCell(value, 3, 0, true);
                  }
                  if (i == attrs.getLength() - 1) {
                     xls.newRow(1);
                  }
               }
            }

         }
         childNode2 = nextChild2;
      }
   }


   private void outputNode(Node solutionNode) {
      boolean header = false;
      ArrayList<String> serverIDs = new ArrayList<String>();

      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputNode")) {
            if (!header) {
               xls.writeCell("OutputNode", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("ServerID", 0, 1, true);
               xls.writeCell("Throughput", 1, 1, true);
               xls.writeCell("Utilization", 2, 1, true);
               xls.writeCell("QueueLength", 3, 1, true);
               xls.writeCell("ServiceTime", 4, 1, true);
               xls.writeCell("TimeUnits", 5, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            boolean found = false;
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               if (serverIDs.contains(attribute.getValue())) {
                  found = true;
               }
            }
            if (!found) {
               for (int i = 0; i < attrs.getLength(); i++) {
                  Attr attribute = (Attr) attrs.item(i);
                  String value = attribute.getValue();
                  if (attribute.getName().equals("ServerID")) {
                     xls.writeCell(value, 0, 0, true);
                     serverIDs.add(value);
                     Node childNode3 = childNode2.getNextSibling();
                     while (childNode3 != null) {
                        if (childNode3.getNodeName().equals("OutputNode")) {
                           NamedNodeMap attrs2 = childNode3.getAttributes();
                           for (int j = 0; j < attrs2.getLength(); j++) {
                              Attr attribute2 = (Attr) attrs2.item(j);
                              if (attribute2.getName().equals("ServerID")) {
                                 String value2 = attribute2.getValue();
                                 if (value2.equals(value)) {
                                    for (int k = 0; k < attrs2.getLength(); k++) {
                                       Attr attribute3 = (Attr) attrs2.item(k);
                                       String value3 = attribute3.getValue();
                                       if (attribute3.getName().equals("Throughput")) {
                                          xls.writeCell(value3, 1, 0, false);
                                       }
                                       if (attribute3.getName().equals("Utilization")) {
                                          xls.writeCell(value3, 2, 0, false);
                                       }
                                       if (attribute3.getName().equals("QueueLength")) {
                                          xls.writeCell(value3, 3, 0, false);
                                       }
                                       if (attribute3.getName().equals("ServiceTime")) {
                                          xls.writeCell(value3, 4, 0, false);
                                       }
                                       if (attribute3.getName().equals("TimeUnits")) {
                                          xls.writeCell(value3, 5, 0, true);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                        childNode3 = childNode3.getNextSibling();
                     }
                  }
                  if (attribute.getName().equals("Throughput")) {
                     xls.writeCell(value, 1, 0, false);
                  }
                  if (attribute.getName().equals("Utilization")) {
                     xls.writeCell(value, 2, 0, false);
                  }
                  if (attribute.getName().equals("QueueLength")) {
                     xls.writeCell(value, 3, 0, false);
                  }
                  if (attribute.getName().equals("ServiceTime")) {
                     xls.writeCell(value, 4, 0, false);
                  }
                  if (attribute.getName().equals("TimeUnits")) {
                     xls.writeCell(value, 5, 0, true);
                  }
                  if (i == attrs.getLength() - 1) {
                     xls.newRow(1);
                  }
               }
            }
         }
         childNode2 = nextChild2;
      }
   }


   private void outputNodeWorkload(Node solutionNode) {
      boolean header = false;
      ArrayList<String> sw = new ArrayList<String>();

      for (Node childNode2 = solutionNode.getFirstChild(); childNode2 != null;) {
         Node nextChild2 = childNode2.getNextSibling();
         if (childNode2.getNodeName().equals("OutputNodeWorkload")) {
            if (!header) {
               xls.writeCell("OutputNodeWorkload", 0, 1, true);
               xls.newRow(1);
               xls.writeCell("ServerID", 0, 1, true);
               xls.writeCell("WorkloadName", 1, 1, true);
               xls.writeCell("Throughput", 2, 1, true);
               xls.writeCell("ResidenceTime", 3, 1, true);
               xls.writeCell("Utilization", 4, 1, true);
               xls.writeCell("QueueLength", 5, 1, true);
               xls.writeCell("ServiceTime", 6, 1, true);
               xls.writeCell("TimeUnits", 7, 1, true);
               xls.newRow(1);
               header = true;
            }
            NamedNodeMap attrs = childNode2.getAttributes();
            boolean found = false;
            for (int i = 0; i < attrs.getLength(); i++) {
               Attr attribute = (Attr) attrs.item(i);
               if (attribute.getName().equals("ServerID")) {
                  for (int j = 0; j < attrs.getLength(); j++) {
                     Attr attribute2 = (Attr) attrs.item(j);
                     if (attribute2.getName().equals("WorkloadName")) {
                        if (sw.contains(attribute.getValue() + attribute2.getValue())) {
                           found = true;
                        }
                     }
                  }
               }
            }
            if (!found) {
               if (attrs.getNamedItem("ServerID") != null &&
                       attrs.getNamedItem("WorkloadName") != null) {
                  String serverID = attrs.getNamedItem("ServerID").getNodeValue();
                  String workloadName = attrs.getNamedItem("WorkloadName").getNodeValue();
                  sw.add(serverID + workloadName);
                  xls.writeCell(attrs.getNamedItem("ServerID").getNodeValue(), 0, 0, true);
                  xls.writeCell(attrs.getNamedItem("WorkloadName").getNodeValue(), 1, 0, true);
                  if (attrs.getNamedItem("Throughput") != null) {
                     xls.writeCell(attrs.getNamedItem("Throughput").getNodeValue(), 2, 0, false);
                  }

                  if (attrs.getNamedItem("ResidenceTime") != null) {
                     xls.writeCell(attrs.getNamedItem("ResidenceTime").getNodeValue(), 3, 0, false);
                  }
                  if (attrs.getNamedItem("Utilization") != null) {
                     xls.writeCell(attrs.getNamedItem("Utilization").getNodeValue(), 4, 0, false);
                  }
                  if (attrs.getNamedItem("QueueLength") != null) {
                     xls.writeCell(attrs.getNamedItem("QueueLength").getNodeValue(), 5, 0, false);
                  }
                  if (attrs.getNamedItem("ServiceTime") != null) {
                     xls.writeCell(attrs.getNamedItem("ServiceTime").getNodeValue(), 6, 0, false);
                  }
                  if (attrs.getNamedItem("TimeUnits") != null) {
                     xls.writeCell(attrs.getNamedItem("TimeUnits").getNodeValue(), 7, 0, true);
                  }

                  Node childNode3 = childNode2.getNextSibling();
                  while (childNode3 != null) {
                     if (childNode3.getNodeName().equals("OutputNodeWorkload")) {
                        NamedNodeMap attrs2 = childNode3.getAttributes();
                        if (attrs2.getNamedItem("ServerID").getNodeValue().equals(serverID) &&
                                attrs2.getNamedItem("WorkloadName").getNodeValue().equals(workloadName)) {
                           if (attrs2.getNamedItem("Throughput") != null) {
                              xls.writeCell(attrs2.getNamedItem("Throughput").getNodeValue(), 2, 0, false);
                           }
                           if (attrs2.getNamedItem("ResidenceTime") != null) {
                              xls.writeCell(attrs2.getNamedItem("ResidenceTime").getNodeValue(), 3, 0, false);
                           }
                           if (attrs2.getNamedItem("Utilization") != null) {
                              xls.writeCell(attrs2.getNamedItem("Utilization").getNodeValue(), 4, 0, false);
                           }
                           if (attrs2.getNamedItem("QueueLength") != null) {
                              xls.writeCell(attrs2.getNamedItem("QueueLength").getNodeValue(), 5, 0, false);
                           }
                           if (attrs2.getNamedItem("ServiceTime") != null) {
                              xls.writeCell(attrs2.getNamedItem("ServiceTime").getNodeValue(), 6, 0, false);
                           }
                           if (attrs2.getNamedItem("TimeUnits") != null) {
                              xls.writeCell(attrs2.getNamedItem("TimeUnits").getNodeValue(), 7, 0, true);
                           }
                        }
                     }
                     childNode3 = childNode3.getNextSibling();
                  }

                  xls.newRow(1);
               }
            }
         }
         childNode2 = nextChild2;
      }
   }
}