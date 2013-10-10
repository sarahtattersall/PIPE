/*
 * Created on Feb 10, 2004
 *
 * Class used in state space and GSPN modules to generate trees and arrays of
 * potential state spaces
 */
package pipe.calculations;

import pipe.exceptions.TreeTooBigException;
import pipe.io.ImmediateAbortException;
import pipe.io.ReachabilityGraphFileHeader;
import pipe.io.TransitionRecord;
import pipe.utilities.math.Matrix;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * @author Matthew
 * @author Pere Bonet (coverability graph)
 */
public class myTree
{

    public boolean foundAnOmega = false;         // bounded
    public boolean moreThanOneToken = false;     // safe
    public boolean noEnabledTransitions = false; // deadlock

    public myNode root;              // root of the tree
    public int nodeCount = 0;        // Total number of nodes in tree
    public final Matrix _CPlus;
    public final Matrix _CMinus;          // incidence matrices of petri net
    public final Matrix _inhibition;      // inhibition matrix of petri net
    public final int transitionCount;      // number of transitions in net
    public final int placeCount;           // number of places in the net
    public final int[] capacity;           // vector of capacities restrictions
    private final int[] priority;           // vector of transition priorities
    private final boolean[] timed;          // true if transition is timed
    public int[] pathToDeadlock;     // Gives transitions to deadlock
    public final boolean tooBig = false;   // Set if the tree gets too large
    private int edges = 0;            // Counter for edges
    public int states = 0;           // Counter for states

    public final PetriNetView _petriNetView;

    //Tree Constructor
    public myTree(PetriNetView petriNetView, int[] treeRoot) throws TreeTooBigException
    {

        _petriNetView = petriNetView;

        _CPlus = new Matrix(petriNetView.getTokenViews().getFirst().getForwardsIncidenceMatrix(
                petriNetView.getArcsArrayList(), petriNetView.getTransitionsArrayList(), petriNetView.getPlacesArrayList()));
        _CMinus = new Matrix(petriNetView.getTokenViews().getFirst().getBackwardsIncidenceMatrix(
                petriNetView.getArcsArrayList(), petriNetView.getTransitionsArrayList(), petriNetView.getPlacesArrayList()));
        _inhibition = new Matrix(petriNetView.getTokenViews().getFirst().getInhibitionMatrix(
                petriNetView.getInhibitorsArrayList(), petriNetView.getTransitionsArrayList(), petriNetView.getPlacesArrayList()));
        capacity = petriNetView.getCapacityMatrix();
        priority = petriNetView.getPriorityMatrix();
        timed = petriNetView.getTimedMatrix();

        //Find number transitions in net from incidence matrix dimensions
        transitionCount = _CMinus.getColumnDimension();

        //Find number of places in net from incidence matrix dimensions
        placeCount = _CMinus.getRowDimension();

        //Create root of tree by calling Node constructor
        root = new myNode(treeRoot, root, this, 1);

        //Check for safeness. If any of places have > 1 token set variable.
        this.moreThanOneToken = isSafe(treeRoot);

        //Call expansion function on root of tree
        root.RecursiveExpansion();
    }


    //Tree Constructor
    public myTree(PetriNetView data, int[] treeRoot, File reachabilityGraph)
            throws TreeTooBigException, ImmediateAbortException
    {

        _petriNetView = data;

        _CPlus = new Matrix(data.getTokenViews().getFirst().getForwardsIncidenceMatrix(
                data.getArcsArrayList(), data.getTransitionsArrayList(), data.getPlacesArrayList()));
        _CMinus = new Matrix(data.getTokenViews().getFirst().getBackwardsIncidenceMatrix(
                data.getArcsArrayList(), data.getTransitionsArrayList(), data.getPlacesArrayList()));
        _inhibition = new Matrix(data.getTokenViews().getFirst().getInhibitionMatrix(
                data.getInhibitorsArrayList(), data.getTransitionsArrayList(), data.getPlacesArrayList()));

        capacity = data.getCapacityMatrix();
        priority = data.getPriorityMatrix();
        timed = data.getTimedMatrix();

        //Find number transitions in net from incidence matrix dimensions
        transitionCount = _CMinus.getColumnDimension();

        //Find number of places in net from incidence matrix dimensions
        placeCount = _CMinus.getRowDimension();

        //Create root of tree by calling Node constructor
        root = new myNode(treeRoot, root, this, 1);

        //Check for safeness. If any of places have > 1 token set variable.
        this.moreThanOneToken = isSafe(treeRoot);

        // Temporary files for storing tangible states and the transitions
        // between them. They are later combined into one file by the createCGFile
        // method
        RandomAccessFile outputFile;
        RandomAccessFile esoFile;
        File intermediate = new File("graph.irg");

        if(intermediate.exists())
        {
            if(!intermediate.delete())
            {
                System.err.println("Could not delete intermediate file.");
            }
        }

        try
        {
            outputFile = new RandomAccessFile(intermediate, "rw");
            esoFile = new RandomAccessFile(reachabilityGraph, "rw");
            // Write a blank file header as a place holder for later
            ReachabilityGraphFileHeader header = new ReachabilityGraphFileHeader();
            header.write(esoFile);
            //Call expansion function on root of tree
            createCoverabilityGraph(outputFile, esoFile);
            outputFile.close();
        }
        catch(IOException e)
        {
            System.err.println("Could not create intermediate files.");
            return;
        }

        createCGFile(intermediate, esoFile, treeRoot.length, states, edges);

        if(intermediate.exists())
        {
            if(!intermediate.delete())
            {
                System.err.println("Could not delete intermediate file.");
            }
        }
    }


    private boolean isSafe(final int[] treeRoot)
    {
        for(int aTreeRoot : treeRoot)
        {
            if(aTreeRoot > 1)
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Function: void RecursiveExpansion()
     * Undertakes a recursive expansion of the tree
     * Called on root node from within the tree constructor.
     * @param outputFile
     * @param esoFile
     * @throws pipe.exceptions.TreeTooBigException
     * @throws pipe.io.ImmediateAbortException
     */
    private void createCoverabilityGraph(RandomAccessFile outputFile,
                                         RandomAccessFile esoFile) throws TreeTooBigException,
            ImmediateAbortException
    {
        int transIndex;                       //Index to count transitions
        int[] newMarkup;                      //markup used to create new node
        boolean aTransitionIsEnabled = false; //To check for deadlock

        //Attribute used for assessing whether a node has occured before
        boolean repeatedNode;

        boolean allOmegas;


        LinkedList<MarkingView>[] state = new LinkedList[root.markup.length];
        for(int i = 0; i < root.markup.length; i++)
        {
            LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
            MarkingView m = new MarkingView(_petriNetView.getTokenViews().getFirst(), root.markup[i]+"");
            mlist.add(m);
            state[i] = mlist;
        }

        boolean[] enabledTransitions = _petriNetView.areTransitionsEnabled(state);

        //emmagatzemar estat nou
        writeNode(root.id, root.markup, esoFile, true);
        states++;

        ArrayList<myNode> unprocessednodes = new ArrayList();

        unprocessednodes.add(root);
        myNode currentNode;
        while(!unprocessednodes.isEmpty())
        {


            currentNode = unprocessednodes.get(0);
            unprocessednodes.remove(0);

            state = new LinkedList[currentNode.markup.length];
            for(int i = 0; i < currentNode.markup.length; i++)
            {
                LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
                MarkingView m = new MarkingView(_petriNetView.getTokenViews().getFirst(), currentNode.markup[i]+"");
                mlist.add(m);
                state[i] = mlist;
            }

            enabledTransitions =
                    _petriNetView.areTransitionsEnabled(state);
            //For each transition
            for(int i = 0; i < enabledTransitions.length; i++)
            {
                if(enabledTransitions[i])
                {
                    //Set transArray of to true for this index
                    currentNode.transArray[i] = true;

                    //System.out.println("\n Transition " + i + " Enabled" ); //debug

                    //currentNode.print("\n currentNode.markup is :", currentNode.markup);//debug

                    //Fire transition to produce new markup vector
                    newMarkup = fire(i, currentNode.markup);

                    //print("\n newMarkup: ", newMarkup);//debug

                    //Create a new node using the new markup vector and attach it to
                    //the current node as a child.
                    currentNode.children[i] =
                            new myNode(newMarkup, currentNode, this, currentNode.depth + 1);

                    /* Now need to (a) check if any omegas (represented by -1) need to
                    * be inserted in the markup vector of the new node, and (b) if the
                    * resulting markup vector has appeared anywhere else in the tree.
                    * We must do (a) before (b) as we need to know if the new node
                    * contains any omegas to be able to compare it with existing nodes.
                    */
                    allOmegas = currentNode.children[i].InsertOmegas();

                    //print("\n New Markup (after omegas) is :", newMarkup);//debug
                    //System.out.println("afegit fill a la posicio " + i +
                    //        "; profunditat" + (currentNode.depth +1));

                    repeatedNode = (this.root).FindMarkup(currentNode.children[i]);
                    //repeatedNode = (this.root).FindMarkup(currentNode.children[i], this.nodes);

                    //REVISAR-LIMITS
                    /*if (this.nodeCount >= Constants.MAX_NODES && !this.tooBig) {
                       System.out.println("too big");
                       this.tooBig = true;
                       throw new TreeTooBigException();
                    }*/

                    if((!repeatedNode) /*&& (!allOmegas)*/)
                    {
                        writeNode(currentNode.children[i].id, currentNode.children[i].markup, esoFile, true);
                        this.states++;
                        unprocessednodes.add(currentNode.children[i]);
                        this.edges++;
                        //
                        if(currentNode.children[i].previousInstance != null)
                        {
                            writeEdge(currentNode.id, currentNode.children[i].previousInstance.id, 0, i, currentNode.markup, outputFile);
                        }
                        else
                        {
                            writeEdge(currentNode.id, currentNode.children[i].id, 0, i, currentNode.markup, outputFile);
                        }
                    }
                    else
                    {
                        //emmagatzemar nova transicio
                        // es null si hi ha omega?
                        if(currentNode.children[i].previousInstance != null)
                        {
                            writeEdge(currentNode.id, currentNode.children[i].previousInstance.id, 0, i, currentNode.markup, outputFile);
                            this.edges++;
                        }
                    }
                }
            }
        }
    }

    private int[] fire(int transIndex, int[] markup)
    {
        int CMinusValue;               //Value from C- matrix
        int CPlusValue;                //Value from C+ matrix

        //Create marking array to return
        int[] marking = new int[this.placeCount];

        //System.out.println("\nFire transition " + transIndex);
        for(int count = 0; count < this.placeCount; count++)
        {
            CMinusValue = (this._CMinus).get(count, (transIndex));
            CPlusValue = (this._CPlus).get(count, (transIndex));

            if(markup[count] != -1)
            {
                marking[count] = markup[count] - CMinusValue + CPlusValue;
            }
            else
            {
                marking[count] = markup[count];
            }
        }

        //print( "Markup: ", marking); //debug

        //Return this new marking to RecursiveExpansion function
        return marking;
    }


    /**
     * writeEdge()
     * Records all the arcs in the reachability graph from state 'from'.
     *
     * @param from               The tangible state which all the arcs in the linked
     *                           list are from.
     * @param to
     * @param rate
     * @param transitionNo
     * @param markup
     * @param dataFile           The file that reachability graph data needs to be
     *                           written to.
     * @throws ImmediateAbortException
     */
    private void writeEdge(int from, int to, double rate,
                           int transitionNo, int[] markup, RandomAccessFile dataFile)
            throws ImmediateAbortException
    {
        TransitionRecord newTransition =
                new TransitionRecord(from, to, rate, transitionNo);
//      System.out.println("writeEdge: from _s" + from + " to _s" + to + "; t =" + transitionNo); //debug
        try
        {
            newTransition.write1(dataFile);
        }
        catch(IOException e)
        {
            System.err.println("IO error when writing transitions to file.");
            throw new ImmediateAbortException();
        }
    }


    /**
     * addExplored()
     * Adds a compressed version of a tangible state to the explored states
     * hashtable and also writes the full state to a file for later use.
     *
     * @param newstate The explored state to be added
     * @param es       A reference to the hashtable
     * @param stateId
     * @param marking
     * @param opfile   The file to write the state to
     * @param vanishingStates
     */
    private void writeNode(int stateId, int[] marking, RandomAccessFile opfile,
                           boolean vanishingStates)
    {

//      System.out.print("writeNode: _s" + stateId + " {" ); // debug
        try
        {
            opfile.writeInt(stateId);
            for(int aMarking : marking)
            {
                opfile.writeInt(aMarking);
//            System.out.print("" + marking[i]); // debug
            }
//         System.out.println("}"); // debug
            opfile.writeBoolean(vanishingStates);
        }
        catch(IOException e)
        {
            System.err.println("IO problem while writing explored states to file.");
        }
    }


    //Temp function for debugging - delete when done.
    public void print(String s, boolean[] array)
    {
        int size = array.length;

        System.out.println(s);
        for(int i = 0; i < size; i++)
        {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }


    //Temp function for debugging - delete when done.
    public void print(String s, int[] array)
    {
        int size = array.length;

        System.out.println(s);
        for(int i = 0; i < size; i++)
        {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    private static void createCGFile(File transource,
                                     RandomAccessFile destination, int statesize, int states,
                                     int transitions)
    {
        RandomAccessFile transinputFile;
        TransitionRecord currenttran = new TransitionRecord();
        ReachabilityGraphFileHeader header;

        try
        {
            transinputFile = new RandomAccessFile(transource, "r");

            // The destination file actually already exists with a blank file
            // header as a placeholder and all the tangible states written in
            // order. The file pointer should already be at the end of the file
            // (i.e. after the last tangible state that's been written to the file.
            // Make a note of the file pointer as this is where the transition
            // records begin.
            long offset = destination.getFilePointer();
            // Now copy over all the transitions
            System.out.println("Creating coverability graph, please wait...");
            for(int count = 0; count < transitions; count++)
            {
                //System.out.print("Recording arc " + (count+1) + " of " + transitions +".\r");
                currenttran.read1(transinputFile);
                currenttran.write1(destination);
            }
            System.out.println("");
            // Make a note of the transition record size and fill in all the
            // details in the file header.
            int recordsize = currenttran.getRecordSize();
            destination.seek(0); // Go back to the start of the file
            header = new ReachabilityGraphFileHeader(states, statesize, transitions,
                                                     recordsize, offset);
            header.write(destination);

            // Done so close all the files.
            transinputFile.close();
            destination.close();
        }
        catch(EOFException e)
        {
            System.err.println("EOFException");
        }
        catch(IOException e)
        {
            System.err.println("Could not create output file.\n " + e.getMessage());
        }
    }

}
