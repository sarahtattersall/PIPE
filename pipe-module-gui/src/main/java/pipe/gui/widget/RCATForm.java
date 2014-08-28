package pipe.gui.widget;

import pipe.gui.rcat.BuildingBlock;
import pipe.gui.rcat.BuildingBlockCreator;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.io.PetriNetReader;
import uk.ac.imperial.pipe.models.petrinet.*;


import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View class for the RCAT Module
 * @author Tanvi Potdar
 *
 */
public class RCATForm {
    /**
     * main JPanel
     */
    private JPanel primPanel;
    /**
     * radio button to choose to use existing petri net
     */
    private JRadioButton useExisting;
    /**
     * radio buttons to enable the user to load petri nets from file
     */
    private JRadioButton loadPetriNetFromRadioButton;
    /**
     * text field into which the petri net name appears
     */
    private JTextField loadpn;
    /**
     * JButton to calculate rates for passive transitions in the Building Block
     */
    private JButton calculatePassiveTransitionRates;
    /**
     * JButton to split appropriate petri netz into Building Blocks
     */
    private JButton splitBB;
    private JLabel petriNetTextLabel;
    private JPanel resultsP;
    /**
     * Text area to print results
     */
    private JTextArea evalTextArea;
    private JLabel resultsLabel;
    private JTextArea ratesTextArea;
    private final FileDialog loadPetriNetFromFile;
    /**
     *     petri net loaded via the load dialog
     */
    private PetriNet lastloaded;
    /**
     *     default petri net/ existing petri net
     */
    private PetriNet defaultPetriNet;
    /**
     * petri net being evaluated currently
     */
    private PetriNet petriNet;

    private static final Logger LOGGER = Logger.getLogger(RCATForm.class.getName());

    /**
     * Sets up the load Petri net options with the "use current Petri net" disabled
     * @param loadDialog creates file dialog to select petri net
     */
    public RCATForm(FileDialog loadDialog){
        this.loadPetriNetFromFile = loadDialog;
        useExisting.setEnabled(false);
        setUp();
    }
    /**
     * Sets up the load Petri net options with "use existing Petri net" set to
     * the petriNet parameter
     *
     * @param petriNet   current Petri net
     * @param loadDialog
     */
    public RCATForm(PetriNet petriNet, FileDialog loadDialog) {
        defaultPetriNet = petriNet;
        this.loadPetriNetFromFile = loadDialog;
        setUp();
    }

    /**
     * Defines action listeners for the radio and J-buttons:
     * 1.load petri net from file
     * 2.split petri net into building blocks
     * 3.calculate rates for passive transitions
     */
    public void setUp(){
        loadPetriNetFromRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        splitBB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPetriNet();
                splitIntoBuildingBlocks();
            }
        });
        calculatePassiveTransitionRates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePassiveTransitionRates();
            }
        });

    }

    /**
     * Converts the list of building blocks into String
     * @param buildingBlocks
     * @return list of building blocks in string form
     */
    public String toString(Collection<BuildingBlock> buildingBlocks){
        StringBuilder stringBuilder = new StringBuilder();
        for(BuildingBlock buildingBlock: buildingBlocks){
            for(Connectable c: buildingBlock.getConnectables()){
                stringBuilder.append(" "+c.getId()+" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();

    }

    /**
     * checks if the petri net satisfies POSPN conditions
     * @param petriNet is the petri net in use
     * @return true if the petri net satisfies POSPN conditions and false
     * if it doesn't
     */
    //TODO: Fill in conditions for POSPN
    public boolean checkIfPOSPN(PetriNet petriNet){
        return false;
    }

    /**
     * calls the BuildingBlockCreator class to split the current PN into Building Blocks
     * sets text area to the places in each building block
     * tests if a PN satisfies RCAT by checking if it can be split into Building Blocks
     */
    private void splitIntoBuildingBlocks() {
        try {
            Collection<BuildingBlock> listOfBuildingBlocks = new BuildingBlockCreator().splitIntoBuildingBlocks(petriNet);
            if(checkIfPOSPN(petriNet)==true){
                evalTextArea.setText("This Petri Net cannot be split into Building Blocks because it is not a POSPN. " +
                       "\n"+ "In a POSPN, for every input transition, there exists an output transition. " + "\n" +
                "This Petri Net does not satisfy those conditions and hence its rate equations cannot be solved by RCAT."
                );
            }
            else evalTextArea.setText("Here are the building blocks of this Petri Net"+"\n"+toString(listOfBuildingBlocks)
                    + "\n"+ "Each Building Block satisfies the conditions:" + "\n" + "1.Every transition is an input or an output transition"
            + "\n" + "2. For every input transition, there exists an output transition" + "\n" + "3. The Petri Net has to be completely connected."
            + "\n" +"As a result, the rate equations of this Petri Net can be solved by RCAT.");
        }
        catch (PetriNetComponentException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the petriNet variable to the petriNet in use(existing vs loaded)
     */
    private void setPetriNet() {
        petriNet =  useExisting.isSelected() ? defaultPetriNet : lastloaded;
    }


    /**
     * Gets the current petri net
     * @return petri net in use
     */
    public PetriNet getPetriNet(){
        return petriNet;
    }

    /**
     * Opens the file dialog and saves the selected Petri net into lastLoadedPetriNet
     * for use when calculating the state space exploration
     */
    private void loadData() {
        useExisting.setSelected(false);
        loadPetriNetFromRadioButton.setSelected(true);
        loadPetriNetFromFile.setMode(FileDialog.LOAD);
        loadPetriNetFromFile.setTitle("Select petri net");
        loadPetriNetFromFile.setVisible(true);
        File[] files = loadPetriNetFromFile.getFiles();
        if (files.length > 0) {
            File path = files[0];
            try {
                loadpn.setText(path.getName());
                PetriNetReader petriNetIO = new PetriNetIOImpl();
                lastloaded = petriNetIO.read(path.getAbsolutePath());
            } catch (JAXBException | FileNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    /**
     * returns the Primary Panel in the UI
     * @return primary panel
     */
    public JPanel getPrimPanel(){
        return primPanel;
    }

    /**
     * calculates the rates for passive transitions
     */
    public void calculatePassiveTransitionRates(){
        StringBuilder outputProductForm = new StringBuilder();
        Collection<StringBuilder> stringBuilders = new HashSet<>();
        try {
            Collection<BuildingBlock> listOfBuildingBlocks = new BuildingBlockCreator().splitIntoBuildingBlocks(petriNet);
            for(BuildingBlock buildingBlock: listOfBuildingBlocks){
                if(buildingBlock.getPlaces().size()==1){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(generateSSPDForMM1(buildingBlock));
                    stringBuilders.add(stringBuilder);
                }
                if(buildingBlock.getPlaces().size()>1){
                    stringBuilders.addAll(ProductForm(buildingBlock));
                    outputProductForm.append(ProductForm(buildingBlock));
                }

            }
        } catch (PetriNetComponentException e) {
            e.printStackTrace();
        }

        ratesTextArea.setText(stringBuilders.toString());

    }

    /**
     * Returns all the arcs in the building block provided
     * @param buildingBlock
     * @return all the arcs in the building block
     */
    public Collection<Arc> getBuildingBlockArcs(BuildingBlock buildingBlock){
        Collection<Arc> arcs = new HashSet<>();
        for(Arc arc: petriNet.getArcs()){
            for(Connectable c: buildingBlock.getConnectables()){
                if(c.equals(arc.getSource())||c.equals(arc.getTarget())){
                    arcs.add(arc);
                }
            }
        }
        return arcs;
    }

    /**
     * Returns all the inbound arcs(from place to transition) in the building block
     * @param buildingBlock
     * @return all inbound arcs
     */
    public Collection<InboundArc> getInboundArcsInBuildingBlock(BuildingBlock buildingBlock){
        Collection<InboundArc> inboundArcsinBuildingBlock = new HashSet<>();
        for(Arc arc: getBuildingBlockArcs(buildingBlock)){
            for(Transition transition: buildingBlock.getTransitions()){
                for(Place place: buildingBlock.getPlaces()){
                    if(transition.equals(arc.getTarget())&&place.equals(arc.getSource())){
                        inboundArcsinBuildingBlock.add((InboundArc)arc);
                    }
                }
            }
        }
        return inboundArcsinBuildingBlock;
    }

    /**
     * Returns all outbound arcs(from transition to place) in the building block
     * @param buildingBlock
     * @return all outbound arcs
     */
    public Collection<OutboundArc> getOutboundArcsInBuildingBlock(BuildingBlock buildingBlock){
        Collection<OutboundArc> outboundArcsinBuildingBlock = new HashSet<>();
        for(Arc arc: getBuildingBlockArcs(buildingBlock)){
            for(Transition transition: buildingBlock.getTransitions()){
                for(Place place: buildingBlock.getPlaces()){
                    if(transition.equals(arc.getSource())&&place.equals(arc.getTarget())){
                        outboundArcsinBuildingBlock.add((OutboundArc)arc);
                    }
                }
            }
        }
        return outboundArcsinBuildingBlock;
    }

    /**
     * Generates the Steady State Probability Distribution for a building block
     * with one place, i.e., an MM1 queue
     * @param buildingBlock
     * @return the sspd equation for an MM1 queue in the form of a string
     */
    public String generateSSPDForMM1(BuildingBlock buildingBlock){
        String output = new String();
        StringBuilder numerator = new StringBuilder();
        StringBuilder denominator = new StringBuilder();
            if(buildingBlock.getTransitions().size()==2) {
                for (OutboundArc outboundArc : getOutboundArcsInBuildingBlock(buildingBlock)) {
                    numerator.append(outboundArc.getSource().getId());
                }
                for (InboundArc inboundArc : getInboundArcsInBuildingBlock(buildingBlock)) {
                    denominator.append(inboundArc.getTarget().getId()+"\n");
                }
            }
            else if(buildingBlock.getTransitions().size()>2) {
                for (OutboundArc outboundArc : getOutboundArcsInBuildingBlock(buildingBlock)) {
                    numerator.append(outboundArc.getSource().getId() + "+");
                }
                for (InboundArc inboundArc : getInboundArcsInBuildingBlock(buildingBlock)) {
                    denominator.append(inboundArc.getTarget().getId() + "+"+"\n");
                }
            }
        output =  "x_"+numerator+"="+"x_"+denominator;
        return output;
    }

    /**
     * generates the product form rate equations for building blocks
     * @param buildingBlock
     * @return rate equations
     */
    public Collection<StringBuilder> ProductForm(BuildingBlock buildingBlock){
        StringBuilder stringBuilder = new StringBuilder(),rate = new StringBuilder();
        Collection<StringBuilder> stringBuilders = new HashSet<>(),rates = new HashSet<>();
        Collection<Transition> inputTransitions = new HashSet<>(), outputTransitions=new HashSet<>();
        for(Arc arc: getOutboundArcsInBuildingBlock(buildingBlock)){
            inputTransitions.add((Transition)arc.getSource());
        }
        for(Arc arc: getInboundArcsInBuildingBlock(buildingBlock)){
            outputTransitions.add((Transition)arc.getTarget());

        }
        for(Transition i_transition:inputTransitions){
            for(Transition o_transition: outputTransitions){
                if(petriNet.outboundArcs(i_transition).size()==petriNet.inboundArcs(o_transition).size()){
                    for(Arc arc: petriNet.outboundArcs(i_transition)){
                        for (Arc arc1: petriNet.inboundArcs(o_transition)){
                            if(arc.getTarget().equals(arc1.getSource())){
                                stringBuilder.append(i_transition.getId()+"/"+o_transition.getId()+" ");
                                stringBuilders.add(stringBuilder);
                                rate.append("x_"+i_transition.getId()+" = "+"x_"+o_transition.getId()+"\n");
                                rates.add(rate);
                            }
                        }
                    }
                }
            }
        }
        return rates;
    }





    }














