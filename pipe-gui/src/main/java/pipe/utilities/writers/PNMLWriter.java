package pipe.utilities.writers;

import org.w3c.dom.*;
import pipe.common.dataLayer.StateGroup;
import pipe.views.*;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.RateParameter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PNMLWriter
{
    private final PetriNetView _netViewModel;

    public PNMLWriter(PetriNetView currentNetView)
    {
        _netViewModel = currentNetView;
    }

    public void saveTo(File file, boolean saveFunctional) throws NullPointerException, IOException, DOMException
    {

        if(file == null)
        {
            throw new NullPointerException("Null file in saveTo");
        }

        Document pnDOM;
        StreamSource xsltSource = null;
        Transformer transformer = null;
        try
        {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            pnDOM = builder.newDocument();

            Element PNML = pnDOM.createElement("pnml"); // PNML Top Level Element
            pnDOM.appendChild(PNML);

            Attr pnmlAttr = pnDOM.createAttribute("xmlns"); // PNML "xmlns" Attribute
            pnmlAttr.setValue("http://www.informatik.hu-berlin.de/top/pnml/ptNetb");
            PNML.setAttributeNode(pnmlAttr);

            Element NET = pnDOM.createElement("net"); // Net Element
            PNML.appendChild(NET);
            Attr netAttrId = pnDOM.createAttribute("id"); // Net "id" Attribute
            netAttrId.setValue("Net-One");
            NET.setAttributeNode(netAttrId);
            Attr netAttrType = pnDOM.createAttribute("type"); // Net "type" Attribute
            netAttrType.setValue("P/T net");
            NET.setAttributeNode(netAttrType);

            LinkedList<TokenView> tokenclasses = _netViewModel.getTokenViews();
            for(TokenView tokenclass : tokenclasses)
            {
                if(tokenclass.isEnabled() || !tokenclass.getID().equals(""))
                    NET.appendChild(createTokenClassElement(tokenclass, pnDOM));
            }

            AnnotationNote[] labels = _netViewModel.labels();
            for(AnnotationNote label : labels)
            {
            	if(!label.isDeleted())
            		NET.appendChild(createAnnotationNoteElement(label, pnDOM));
            }

            RateParameter[] rateParameters = _netViewModel.markingRateParameters();
            for(RateParameter rateParameter : rateParameters)
            {
            	if(!rateParameter.isDeleted())
            		NET.appendChild(createDefinition(rateParameter, pnDOM));
            }

            PlaceView[] placeViews = _netViewModel.places();
            for(PlaceView placeView : placeViews)
            {
            	if(!placeView.isDeleted())
            		NET.appendChild(createPlaceElement(placeView, pnDOM));
            }

            TransitionView[] transitionViews = _netViewModel.getTransitionViews();
            for(TransitionView transitionView : transitionViews)
            {
            	if(!transitionView.isDeleted())
            		NET.appendChild(createTransitionElement(transitionView, pnDOM, saveFunctional));
            }
            

            ArcView[] arcViews = _netViewModel.arcs();
            for(ArcView arcView : arcViews)
            {
            	if(!arcView.isDeleted()){
	                Element newArc = createArcElement(arcView, pnDOM, saveFunctional);
	
	                int arcPoints = arcView.getArcPath().getArcPathDetails().length;
	                String[][] point = arcView.getArcPath().getArcPathDetails();
	                for(int j = 0; j < arcPoints; j++)
	                {
	                    newArc.appendChild(createArcPoint(point[j][0], point[j][1], point[j][2], pnDOM, j));
	                }
	                NET.appendChild(newArc);
            	}
                //newArc = null;
            }

            InhibitorArcView[] inhibitorArcViews = _netViewModel.inhibitors();
            for(InhibitorArcView inhibitorArcView : inhibitorArcViews)
            {
        		if(!inhibitorArcView.isDeleted()){
		            Element newArc = createArcElement(inhibitorArcView, pnDOM, saveFunctional);
		
		            int arcPoints = inhibitorArcView.getArcPath().getArcPathDetails().length;
		            String[][] point = inhibitorArcView.getArcPath().getArcPathDetails();
		            for(int j = 0; j < arcPoints; j++)
		            {
		                newArc.appendChild(createArcPoint(point[j][0],
		                                                  point[j][1],
		                                                  point[j][2],
		                                                  pnDOM, j));
		            }
		            NET.appendChild(newArc);
        		}
            }

            StateGroup[] stateGroups = _netViewModel.getStateGroups();
            for(StateGroup stateGroup : stateGroups)
            {
                Element newStateGroup = createStateGroupElement(stateGroup, pnDOM);

                int numConditions = stateGroup.numElements();
                String[] conditions = stateGroup.getConditions();
                for(int j = 0; j < numConditions; j++)
                {
                    newStateGroup.appendChild(createCondition(conditions[j], pnDOM));
                }
                NET.appendChild(newStateGroup);
            }
            //stateGroups = null;

            pnDOM.normalize();
            // Create Transformer with XSL Source File
            xsltSource = new StreamSource(Thread.currentThread().
                    getContextClassLoader().getResourceAsStream("xslt" +
                                                                        System.getProperty("file.separator") + "GeneratePNML.xsl"));

            transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
            // Write file and do XSLT transformation to generate correct PNML
            DOMSource source = new DOMSource(pnDOM);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        }
        catch(ParserConfigurationException e)
        {
            // System.out.println("=====================================================================================");
            System.out.println("ParserConfigurationException thrown in saveTo() " +
                                       ": dataLayerWriter Class : models Package: filename=\"" +
                                       file.getCanonicalPath() + "\" xslt=\"" +
                                       xsltSource.getSystemId() + "\" transformer=\"" +
                                       transformer.getURIResolver() + "\"");
            // System.out.println("=====================================================================================");
            // e.printStackTrace(System.err);
        }
        catch(DOMException e)
        {
            // System.out.println("=====================================================================");
            System.out.println("DOMException thrown in saveTo() " +
                                       ": dataLayerWriter Class : models Package: filename=\"" +
                                       file.getCanonicalPath() + "\" xslt=\"" +
                                       xsltSource.getSystemId() + "\" transformer=\"" +
                                       transformer.getURIResolver() + "\"");
            // System.out.println("=====================================================================");
            // e.printStackTrace(System.err);
        }
        catch(TransformerConfigurationException e)
        {
            // System.out.println("==========================================================================================");
            System.out.println("TransformerConfigurationException thrown in saveTo() " +
                                       ": dataLayerWriter Class : models Package: filename=\"" + file.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() + "\" transformer=\"" + transformer.getURIResolver() + "\"");
            // System.out.println("==========================================================================================");
            // e.printStackTrace(System.err);
        }
        catch(TransformerException e)
        {
            // System.out.println("=============================================================================");
            System.out.println("TransformerException thrown in saveTo() : dataLayerWriter Class : models Package: filename=\"" + file.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() + "\" transformer=\"" + transformer.getURIResolver() + "\"" + e);
            // System.out.println("=============================================================================");
            // e.printStackTrace(System.err);
        }
    }

    /**
     * Creates a Place Element for a PNML Petri-Net DOM
     *
     * @param inputPlaceView Input Place
     * @param document       Any DOM to enable creation of Elements and Attributes
     * @return Place Element for a PNML Petri-Net DOM
     */
    private Element createPlaceElement(PlaceView inputPlaceView, Document document)
    {
        Element placeElement = null;

        if(document != null)
        {
            placeElement = document.createElement("place");
        }

        if(inputPlaceView != null)
        {
            Integer attrValue = null;
            Double positionXInput = new Double(inputPlaceView.getX());
            Double positionYInput = new Double(inputPlaceView.getY());
            String idInput = inputPlaceView.getId();
            String nameInput = inputPlaceView.getName();
            //TODO: WE SHOULD BE USING MODEL TO WRITE!
            Double nameOffsetXInput = 0.0;
            Double nameOffsetYInput = 0.0;
            List<MarkingView> initialMarkingViewInput = inputPlaceView.getCurrentMarkingObject();
            Double markingOffsetXInput = inputPlaceView.getMarkingOffsetXObject();
            Double markingOffsetYInput = inputPlaceView.getMarkingOffsetYObject();
            Integer capacityInput = inputPlaceView.getCapacity();

            placeElement.setAttribute("positionX", (positionXInput != null ? String.valueOf(positionXInput) : ""));
            placeElement.setAttribute("positionY", (positionYInput != null ? String.valueOf(positionYInput) : ""));
            placeElement.setAttribute("name", (nameInput != null ? nameInput : (idInput != null && idInput.length() > 0 ? idInput : "")));
            placeElement.setAttribute("id", (idInput != null ? idInput : "error"));
            placeElement.setAttribute("nameOffsetX", (nameOffsetXInput != null ? String.valueOf(nameOffsetXInput) : ""));
            placeElement.setAttribute("nameOffsetY", (nameOffsetYInput != null ? String.valueOf(nameOffsetYInput) : ""));
            String markingOutput = initialMarkingViewInput.get(0).getToken().getID() + "," + initialMarkingViewInput.get(0).getCurrentMarking();
            for(int i = 1; i < initialMarkingViewInput.size(); i++)
            {
                markingOutput += "," + initialMarkingViewInput.get(i).getToken().getID() + "," + initialMarkingViewInput.get(i).getCurrentMarking();
            }
            placeElement.setAttribute("initialMarking", markingOutput);
            placeElement.setAttribute("markingOffsetX", (markingOffsetXInput != null ? String.valueOf(markingOffsetXInput) : ""));
            placeElement.setAttribute("markingOffsetY", (markingOffsetYInput != null ? String.valueOf(markingOffsetYInput) : ""));
            placeElement.setAttribute("capacity", (capacityInput != null ? String.valueOf(capacityInput) : ""));
        }
        return placeElement;
    }

    /**
     * Creates a label Element for a PNML Petri-Net DOM
     *
     * @param inputLabel input label
     * @param document   Any DOM to enable creation of Elements and Attributes
     * @return label Element for a PNML Petri-Net DOM
     */
    private Element createAnnotationNoteElement(AnnotationNote inputLabel, Document document)
    {
        Element labelElement = null;

        if(document != null)
        {
            labelElement = document.createElement("labels");
        }

        if(inputLabel != null)
        {
            int positionXInput = inputLabel.getOriginalX();
            int positionYInput = inputLabel.getOriginalY();
            int widthInput = inputLabel.getNoteWidth();
            int heightInput = inputLabel.getNoteHeight();
            String nameInput = inputLabel.getNoteText();
            boolean borderInput = inputLabel.isShowingBorder();

            labelElement.setAttribute("positionX",
                                      (positionXInput >= 0.0 ? String.valueOf(positionXInput) : ""));
            labelElement.setAttribute("positionY",
                                      (positionYInput >= 0.0 ? String.valueOf(positionYInput) : ""));
            labelElement.setAttribute("width",
                                      (widthInput >= 0.0 ? String.valueOf(widthInput) : ""));
            labelElement.setAttribute("height",
                                      (heightInput >= 0.0 ? String.valueOf(heightInput) : ""));
            labelElement.setAttribute("border", String.valueOf(borderInput));
            labelElement.setAttribute("text", (nameInput != null ? nameInput : ""));
        }
        return labelElement;
    }

    /**
     * Creates a Transition Element for a PNML Petri-Net DOM
     *
     * @param inputTransitionView Input Transition
     * @param document            Any DOM to enable creation of Elements and Attributes
     * @return Transition Element for a PNML Petri-Net DOM
     */
    /**
     * modified so that we could save functional rates and weights
     * @param inputTransitionView
     * @param document
     * @return
     */
    private Element createTransitionElement(TransitionView inputTransitionView,
                                            Document document, boolean saveFunctional)
    {
        Element transitionElement = null;

        if(document != null)
        {
            transitionElement = document.createElement("transition");
        }

        if(inputTransitionView != null)
        {
            Integer attrValue = null;
            Double positionXInput = new Double(inputTransitionView.getX());
            Double positionYInput = new Double(inputTransitionView.getY());
            //TODO: WE SHOULD BE USING MODEL TO WRITE!
            Double nameOffsetXInput = 0.0;
            Double nameOffsetYInput = 0.0;
            String idInput = inputTransitionView.getId();
            String nameInput = inputTransitionView.getName();
            //double aRate = inputTransitionView.getRate();
            String aRate;
            if(saveFunctional){
            	aRate = inputTransitionView.getRateExpr();
            	aRate = aRate.replaceAll(",", "@");
            }else{
            	aRate = inputTransitionView.getRate()+"";
            }
            boolean timedTrans = inputTransitionView.isTimed();
            boolean infiniteServer = inputTransitionView.isInfiniteServer();
            int orientation = inputTransitionView.getAngle();
            int priority = inputTransitionView.getPriority();
            String rateParameter = "";
            if(inputTransitionView.getRateParameter() != null)
            {
                rateParameter = inputTransitionView.getRateParameter().getName();
            }

            transitionElement.setAttribute("positionX",
                                           (positionXInput != null ? String.valueOf(positionXInput) : ""));
            transitionElement.setAttribute("positionY",
                                           (positionYInput != null ? String.valueOf(positionYInput) : ""));
            transitionElement.setAttribute("nameOffsetX",
                                           (nameOffsetXInput != null ? String.valueOf(nameOffsetXInput) : ""));
            transitionElement.setAttribute("nameOffsetY",
                                           (nameOffsetYInput != null ? String.valueOf(nameOffsetYInput) : ""));
            transitionElement.setAttribute("name",
                                           (nameInput != null ? nameInput : (idInput != null && idInput.length() > 0 ? idInput : "")));
            transitionElement.setAttribute("id",
                                           (idInput != null ? idInput : "error"));
            transitionElement.setAttribute("rate", aRate);
                                         //  (aRate != 1 ? String.valueOf(aRate) : "1.0"));
            transitionElement.setAttribute("timed", String.valueOf(timedTrans));
            transitionElement.setAttribute("infiniteServer",
                                           String.valueOf(infiniteServer));
            transitionElement.setAttribute("angle", String.valueOf(orientation));
            transitionElement.setAttribute("priority", String.valueOf(priority));
            transitionElement.setAttribute("parameter",
                                           (rateParameter != null ? rateParameter : ""));
        }
        return transitionElement;
    }

    /**
     * Creates a Arc Element for a PNML Petri-Net DOM
     *
     * @param inputArcView Input Arc
     * @param document Any DOM to enable creation of Elements and Attributes
     * @return Arc Element for a PNML Petri-Net DOM
     */
    /**
     * 
     * Modified so that we could write functional arcs and weights
     * 
     * @param inputArcView
     * @param document
     * @return
     */
    private Element createArcElement(ArcView inputArcView, Document document, boolean saveFunctional)
    {
        Element arcElement = null;

        if(document != null)
        {
            arcElement = document.createElement("arc");
        }

        if(inputArcView != null)
        {
            String idInput = inputArcView.getId();
            String sourceInput = inputArcView.getSource().getId();
            String targetInput = inputArcView.getTarget().getId();
            List<MarkingView> weightInput = inputArcView.getWeightSimple();
            //LinkedList<MarkingView> weightInput = inputArcView.getWeight();
            // Double inscriptionPositionXInput = inputArc.getInscriptionOffsetXObject();
            // Double inscriptionPositionYInput = inputArc.getInscriptionOffsetYObject();
            arcElement.setAttribute("id", (idInput != null ? idInput : "error"));
            arcElement.setAttribute("source", (sourceInput != null ? sourceInput : ""));
            arcElement.setAttribute("target", (targetInput != null ? targetInput : ""));
            arcElement.setAttribute("type", inputArcView.getType());
            
            if(saveFunctional){
            	if( weightInput.size() > 0 ){
            		String weightOutput = weightInput.get(0).getToken().getID() + "," + weightInput.get(0).getCurrentFunctionalMarking().replaceAll(",", "@");
            		for(int i = 1; i < weightInput.size(); i++)
    	            {
    	            	weightOutput += "," + weightInput.get(i).getToken().getID() + "," +weightInput.get(i).getCurrentFunctionalMarking().replaceAll(",", "@");//( weightInput.get(i).getCurrentFunctionalMarking().replaceAll(",", "@"));
    	            }
            		arcElement.setAttribute("inscription", weightOutput);
                }
            }else{
            	if( weightInput.size() > 0 ){
    	            String weightOutput = weightInput.get(0).getToken().getID() + "," + weightInput.get(0).getCurrentMarking();
    	            for(int i = 1; i < weightInput.size(); i++)
    	            {
    	            	weightOutput += "," + weightInput.get(i).getToken().getID() + "," + weightInput.get(i).getCurrentMarking();
    	            }
    	            arcElement.setAttribute("inscription", weightOutput);
                }
            }
//            if( weightInput.size() > 0 ){
//	            //String weightOutput = weightInput.get(0).getToken().getID() + "," + weightInput.get(0).getCurrentMarking();
//	            String weightOutput = weightInput.get(0).getToken().getID() + "," + weightInput.get(0).getCurrentFunctionalMarking();
//	            for(int i = 1; i < weightInput.size(); i++)
//	            {
//	                //weightOutput += "," + weightInput.get(i).getToken().getID() + "," + weightInput.get(i).getCurrentMarking();
//	            	weightOutput += "," + weightInput.get(i).getToken().getID() + "," + weightInput.get(i).getCurrentFunctionalMarking();
//	            }
//	            arcElement.setAttribute("inscription", weightOutput);
//            }
            // arcElement.setAttribute("inscriptionOffsetX", (inscriptionPositionXInput != null ? String.valueOf(inscriptionPositionXInput) : ""));
            // arcElement.setAttribute("inscriptionOffsetY", (inscriptionPositionYInput != null ? String.valueOf(inscriptionPositionYInput) : ""));

            if(inputArcView instanceof NormalArcView)
            {
                boolean tagged = inputArcView.isTagged();
                arcElement.setAttribute("tagged", tagged ? "true" : "false");
            }
        }
        return arcElement;
    }

    private Element createArcPoint(String x, String y, String type,
                                   Document document, int id)
    {
        Element arcPoint = null;

        if(document != null)
        {
            arcPoint = document.createElement("arcpath");
        }
        String pointId = String.valueOf(id);
        if(pointId.length() < 3)
        {
            pointId = "0" + pointId;
        }
        if(pointId.length() < 3)
        {
            pointId = "0" + pointId;
        }
        arcPoint.setAttribute("id", pointId);
        arcPoint.setAttribute("xCoord", x);
        arcPoint.setAttribute("yCoord", y);
        arcPoint.setAttribute("arcPointType", type);

        return arcPoint;
    }

    private Node createDefinition(RateParameter inputParameter, Document document)
    {
        Element labelElement = null;

        if(document != null)
        {
            labelElement = document.createElement("definitions");
        }

        if(inputParameter != null)
        {

            int positionXInput = inputParameter.getOriginalX();//getX()
            int positionYInput = inputParameter.getOriginalY();//getY()
            int widthInput = inputParameter.getNoteWidth();
            int heightInput = inputParameter.getNoteHeight();
            double valueInput = inputParameter.getValue();
            String idInput = inputParameter.getName();
            boolean borderInput = inputParameter.isShowingBorder();
            labelElement.setAttribute("defType", "real");
            labelElement.setAttribute("expression", String.valueOf(valueInput));
            labelElement.setAttribute("id", idInput);
            labelElement.setAttribute("name", idInput);
            labelElement.setAttribute("type", "text");
            labelElement.setAttribute("positionX",
                                      (positionXInput >= 0.0 ? String.valueOf(positionXInput) : ""));
            labelElement.setAttribute("positionY",
                                      (positionYInput >= 0.0 ? String.valueOf(positionYInput) : ""));

            labelElement.setAttribute("width",
                                      (widthInput >= 0.0 ? String.valueOf(widthInput) : ""));
            labelElement.setAttribute("height",
                                      (heightInput >= 0.0 ? String.valueOf(heightInput) : ""));
            labelElement.setAttribute("border", String.valueOf(borderInput));
        }
        return labelElement;
    }

    /**
     * Creates a State Group Element for a PNML Petri-Net DOM
     *
     * @param inputStateGroup Input State Group
     * @param document        Any DOM to enable creation of Elements and Attributes
     * @return State Group Element for a PNML Petri-Net DOM
     * @author Barry Kearns, August 2007
     */
    private Element createStateGroupElement(StateGroup inputStateGroup, Document document)
    {
        Element stateGroupElement = null;

        if(document != null)
        {
            stateGroupElement = document.createElement("stategroup");
        }

        if(inputStateGroup != null)
        {
            String idInput = inputStateGroup.getId();
            String nameInput = inputStateGroup.getName();

            stateGroupElement.setAttribute("name",
                                           (nameInput != null ? nameInput
                                                   : (idInput != null && idInput.length() > 0 ? idInput : "")));
            stateGroupElement.setAttribute("id", (idInput != null ? idInput : "error"));
        }
        return stateGroupElement;
    }

    /**
     * Creates a Token Element for a PNML Petri-Net DOM
     *
     * @param inputTokenView inpout Token
     * @param document   Any DOM to enable creation of Elements and Attributes
     * @return TokenClass Element for a PNML Petri-Net DOM
     */
    private Element createTokenClassElement(TokenView inputTokenView, Document document)
    {
        Element tokenClassElement = null;

        if(document != null)
        {
            tokenClassElement = document.createElement("token");
        }

        if(inputTokenView != null)
        {
            String idInput = inputTokenView.getID();
            Integer redInput = inputTokenView.getColor().getRed();
            Integer greenInput = inputTokenView.getColor().getGreen();
            Integer blueInput = inputTokenView.getColor().getBlue();
            boolean enabledInput = inputTokenView.isEnabled();

            tokenClassElement.setAttribute("id", (idInput != null ? idInput : "error"));
            tokenClassElement.setAttribute("enabled", String.valueOf(enabledInput));
            tokenClassElement.setAttribute("red", (redInput != null ? String.valueOf(redInput) : ""));
            tokenClassElement.setAttribute("green", (greenInput != null ? String.valueOf(greenInput) : ""));
            tokenClassElement.setAttribute("blue", (blueInput != null ? String.valueOf(blueInput) : ""));
        }
        return tokenClassElement;
    }

    private Element createCondition(String condition, Document document)
    {
        Element stateCondition = null;

        if(document != null)
        {
            stateCondition = document.createElement("statecondition");
        }

        stateCondition.setAttribute("condition", condition);
        return stateCondition;
    }


    public static void saveTemporaryFile(PetriNetView data, String className)
    {
        // desar la xarxa a un arxiu temporal per si hi ha cap problema
        try
        {
            //current working dir?
            File dir = new File(Thread.currentThread().getContextClassLoader().
                    getResource("").toURI());

            // temporary file
            File tempFile = File.createTempFile("petrinet[" + className + "][" +
                                                        System.nanoTime() + "]", ".pipe.petrinet.xml", dir);

            PNMLWriter saveModel = new PNMLWriter(data);

            saveModel.saveTo(tempFile, true);

            // Delete temp file when program exits. If PIPE crashes,
            // tempFile won't be deleted.
            tempFile.deleteOnExit();

            // If the directory does not exists, IOException will be thrown
            // and temporary file will not be created.
            System.out.println("Temporary file created at : " + tempFile.getPath());
        }
        catch(URISyntaxException ex)
        {
            Logger.getLogger(className).log(Level.SEVERE, null, ex);
        }
        catch(IOException ioe)
        {
            System.out.println("Exception creating temporary file : " + ioe);
        }
        catch(NullPointerException ex)
        {
            System.out.println("Exception creating temporary file : " + ex);
            Logger.getLogger(className).log(Level.SEVERE, null, ex);
        }
        catch(DOMException ex)
        {
            System.out.println("Exception creating temporary file : " + ex);
            Logger.getLogger(className).log(Level.SEVERE, null, ex);
        }
    }

}
