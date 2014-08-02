package pipe.gui.widget;

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
     * Sets up the load Petri net options with "use current Petri net" set to
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
     * calls the BuildingBlockCreator class to split the current PN into Building Blocks
     * sets text area to the places in each building block
     * tests if a PN satisfies RCAT by checking if it can be split into Building Blocks
     */
    private void splitIntoBuildingBlocks() {
        try {
            Collection<Collection<Place>> listOfBuildingBlocks = new BuildingBlockCreator().splitIntoBuildingBlocks(petriNet);
            if(listOfBuildingBlocks.isEmpty()){
                //TODO: Copy conditions for RCAT
                evalTextArea.setText("This Petri Net cannot be split into building blocks as it does ....");
            }
            else evalTextArea.setText(listOfBuildingBlocks.toString());
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
     * @return
     */
    public JPanel getPrimPanel(){
        return primPanel;
    }

    /**
     * calculates the rates for passive transitions
     */
    public void calculatePassiveTransitionRates(){

    }


}
