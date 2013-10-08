package pipe.calculations;


import pipe.io.ImmediateAbortException;
import pipe.io.NewReachabilityGraphFileHeader;
import pipe.io.NewStateRecord;
import pipe.io.NewTransitionRecord;
import pipe.modules.rta.AnalyseResponse;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Class that generates a reachability graph of a Petri net. Generates the full graph -
 * does NOT eliminate vanishing states.
 *
 * @author (Oliver Haggarty) August 2007 (Much of this code is taken from the StateSpaceGenerator class)
 */
public class LargeStateSpaceGen
{
    private static final int NUMHASHROWS = 46567;
    private static final boolean PROBABILITY = true;
    private static final boolean RATE = false;

    //  Array storing the transitions fired
    private static final Stack transitions = new Stack();

    /**
     * Main entry point to class - generates the reachability graph file
     *
     * @param pnmlData   PIPE2 data structure representing Petri Net
     * @param reachGraph File to contain reachability graph
     * @throws ImmediateAbortException
     * @throws java.io.IOException
     */
    public static void generate(PetriNetView pnmlData, File reachGraph) throws ImmediateAbortException,
            IOException
    {
        Queue statesToExplore = new Queue();

        int stateNo = 0, numTransFired = 0, numTransitions = 0, numStates = 0;

        State currentState;
        RtaMarking currentMarking;
        //Stack that stores a list of states that can be reached from current state in one
        //transition
        Stack<RtaMarking> successorStates;
        //Home-made hashmap to store a hash of states already explored
        LinkedList[] exploredStates = new LinkedList[NUMHASHROWS];
        // This list is used to temporarily store details
        // of the arcs between states on the reachability graph.
        LinkedList localarcs = new LinkedList();

        successorStates = new Stack<RtaMarking>();

        // Temporary files for storing tangible states and
        // the transitions between them. They are later
        // combined into one file by the createRGFile method

        FileChannel opfc, esofc;
        MappedByteBuffer outputBuf;
        MappedByteBuffer esoBuf;
        StringBuilder sb = new StringBuilder(System.getProperty("java.io.tmpdir"));
        System.out.println(System.getProperty("java.io.tmpdir"));
        sb.append(System.getProperty("file.separator"));
        sb.append("pipeTmpFiles");
        sb.append(System.getProperty("file.separator"));
        sb.append("graph.rg");
        File intermediate = new File(sb.toString());

        if(intermediate.exists())
        {
            //System.out.println("About to delete " + intermediate);
            if(!intermediate.delete())
            {
                System.err.println("Could not delete intermediate file.");
            }
        }


        if(reachGraph.exists())
        {
            //System.out.println("About to delete " + reachGraph);
            if(!reachGraph.delete())
            {
                System.err.println("Could not delete reachGraph file.");
            }
        }

        //Open pertinent files using a MappedButeBuffer
        try
        {
            opfc = new RandomAccessFile(intermediate.toString(), "rw").getChannel();
            outputBuf = opfc.map(FileChannel.MapMode.READ_WRITE, 0, AnalyseResponse.getBufferLength());
            esofc = new RandomAccessFile(reachGraph.toString(), "rw").getChannel();
            esoBuf = esofc.map(FileChannel.MapMode.READ_WRITE, 0, AnalyseResponse.getBufferLength());
            NewReachabilityGraphFileHeader header = new NewReachabilityGraphFileHeader();
            header.write(esoBuf);
        }
        catch(IOException e)
        {
            throw new IOException(e.getMessage());
        }

        numStates++;

        //Get current/initial marking of PetriNetViewComponent and decide whether tangible or vanishing
        //Expects an array of ints. getCurrentMarkingVector now returns a linked list.
        //Code below generates an int array based on the linked list.
        LinkedList<MarkingView>[] markings = pnmlData.getCurrentMarkingVector();
        int[] markingsArray = new int[markings.length];
        for(int i = 0; i < markings.length; i++)
        {
            markingsArray[i] = markings[i].getFirst().getCurrentMarking();
        }

        //ok
        //Should be the same as GetCurrentMarkingVector in Imp
        for(int j : markingsArray)
        {
            System.out.println(j);
        }


        currentState = new State(markingsArray);
        currentMarking = new RtaMarking(currentState, stateNo, isTangible(pnmlData, currentState));

        //System.out.println("InMarking: " + storeCurrentMarking.getID() + "Tan:" + storeCurrentMarking.getisTangible());

        int statearraysize = currentMarking.getState().length;//number of places in net
        //ok
        System.out.println("state length: " + statearraysize);
        statesToExplore.enqueue(currentMarking);//add state to list that need to be explored
        addExplored(currentMarking, exploredStates, esoBuf, true);//add state to list of states that has been explored and copy state into reachability graph file
        State sprime;
        //int debug = 0;
        while(!statesToExplore.isEmpty())
        {
            //System.out.println("Debug iteration" + debug++);
            //Explore a state in the queue
            RtaMarking mk = (RtaMarking) statesToExplore.dequeue();

            //System.out.println("OutMarking: " + mk.getID() + " Tan:" + mk.getisTangible());
            RtaMarking mkprime;
            //find out which states can be entered from this state in one transition
            //add them to successorStates list
            numTransFired += fire(pnmlData, mk, successorStates, false);//false means that immediate transitions are catered for
            //Go through list of successor states and explore
            while(!successorStates.isEmpty())
            {
                sprime = successorStates.pop();
                if(!explored(sprime, exploredStates))
                {//We haven't explored this state yet
                    //Add it to queue for exploration
                    numStates++;
                    mkprime = new RtaMarking(sprime, numStates - 1, isTangible(pnmlData, sprime));
                    statesToExplore.enqueue(mkprime);//add to list to be explored
                    //System.out.println("InMarking: " + mkprime.getID() + "Tan:" + mkprime.getisTangible());
                    addExplored(mkprime, exploredStates, esoBuf, true);//do the other necessaries
                }
                else
                {//We've already explored this state
                    int id = identifyState(sprime, exploredStates);//Check thats definite
                    if(id == -1)
                        throw new ImmediateAbortException("Could not identify previously explored tangible state.");
                    mkprime = new RtaMarking(sprime, id);
                    //Don't need to add to list to explore as already have!
                    //mkprime.setIsTangible(isTangible(_pnmlData, mkprime));
                }
                //Store the transitions that lead fromt the current state to this one
                numTransitions += transition(mkprime, rate(pnmlData, mk, sprime), localarcs);
            }
            // Write all the arcs for the reachability graph to file
            writeTransitions(mk, localarcs, outputBuf, true);
            // Clear the list so can start again with the next set of arcs
            localarcs = new LinkedList();
        }
        try
        {
            outputBuf.rewind();
            opfc.close();
        }
        catch(IOException e1)
        {
            System.out.println("\nCould not close intermediate file.");
        }
        //System.out.println("\nGenerate Ends, " + numStates + " states found with " + numTransitions + " arcs.");
        //Now we have all the intermediate file complete, we must create the final reachability
        //graph file by combining these
        createRGFile(intermediate, esoBuf, statearraysize, numStates, numTransitions, true);
        try
        {
            esoBuf.rewind();
            esofc.close();
        }
        catch(IOException e1)
        {
            System.out.println("\nCould not close eso file.");
        }
        if(intermediate.exists())
        {
            if(!intermediate.delete())
            {
                System.out.println("\nCould not delete intermediate file.");
            }
        }

    }
    //#####################################################################################
    //
    //	HELPER FUNCTIONS TAKEN FROM PIPE>DATALAYER.CALCULATIONS.STATESPACEGENERATOR
    //	MAY NEED OPTIMISING LATER
    //#####################################################################################

    /**
     * Writes all the enabled transitions from one marking to the intermediate file
     *
     * @param mk      The marking whose transitions from we are concerned with
     * @param arclist A list of arcs that leave the marking from and are enabled
     * @param dataBuf A MappedByteBuffer connected to an intermediate file where transitions are stored
     * @param writeTransitionsNo
     * @throws pipe.io.ImmediateAbortException
     */
    private static void writeTransitions(RtaMarking mk, LinkedList arclist, MappedByteBuffer dataBuf, boolean writeTransitionsNo) throws ImmediateAbortException
    {
        NewTransitionRecord newTransition;
        Iterator iterator = arclist.iterator();
        ArcListElement current;
        while(iterator.hasNext())
        {
            current = (ArcListElement) iterator.next();

            if(writeTransitionsNo)
            {
                newTransition = new NewTransitionRecord(mk.getIDNum(), current.getTo(), current.getRate(), current.transitionNo, mk.getisTangible());
                newTransition.write(dataBuf);
                //System.out.println("From: " + from.getIDNum() + ":(" + from + ")" + " To: " + current.getTo() + " via " + current.transitionNo+ " Rate: " + current.getRate() + " Tan: " + from.getisTangible());

            }
            //Don't need this next bit as always want to write transition number
            /* else {
                   newTransition = new NewTransitionRecord(from.getIDNum(), current.getTo(), current.getRate());
                   try{
                       newTransition.write(dataBuf);
                       //System.out.println("From: " + from.getIDNum() + ":(" + from + ")" + " To: " + current.getTo() + " Rate: " + current.getRate());
                   } catch (IOException e){
                       System.err.println("IO error when writing transitions to file.");
                       throw new ImmediateAbortException();
                   }

               }*/
        }
    }

    /**
     * addExplored()
     * Adds a compressed version of a tangible state to the
     * explored states hashtable and also writes the full
     * state to a file for later use.
     *
     * @param newstate The explored state to be added
     * @param es       A reference to the hashtable
     * @param opfile   The file to write the state to
     * @param vanishingStates
     * @throws java.io.IOException
     */
    private static void addExplored(RtaMarking newstate, LinkedList[] es, MappedByteBuffer opfile, boolean vanishingStates) throws IOException
    {
        LinkedList hashrow = es[newstate.hashCode() % NUMHASHROWS];
        if(hashrow == null)
        {
            // This hashcode hasn't come up before so we need
            // to set up the linked list first
            es[newstate.hashCode() % NUMHASHROWS] = new LinkedList();
            hashrow = es[newstate.hashCode() % NUMHASHROWS];
        }
        hashrow.add(new CompressedState(newstate.hashCode2(), newstate.getIDNum()));
        // Now also write this state to disk for later use
        NewStateRecord sr = new NewStateRecord(newstate);
        if(vanishingStates)
                sr.write(opfile);
            else
                sr.write(opfile);
    }

    private static void createRGFile(File transource, MappedByteBuffer destination, int statesize, int states, int transitions, boolean withTransitions)
    {
        FileChannel tifc;
        MappedByteBuffer transinputBuf;
        NewStateRecord currentstate = new NewStateRecord();
        NewTransitionRecord currenttran = new NewTransitionRecord();
        NewReachabilityGraphFileHeader header;
        try
        {
            tifc = new RandomAccessFile(transource, "r").getChannel();
            transinputBuf = tifc.map(FileChannel.MapMode.READ_ONLY, 0, AnalyseResponse.getBufferLength());

            long offset = destination.position();
            // Now copy over all the transitions
            //System.out.println("Creating reachability graph, please wait...");
            for(int count = 0; count < transitions; count++)
            {
                //	System.out.print("Recording arc " + (count+1) + " of " + transitions +".\r");
                if(withTransitions)
                {
                    currenttran.read(transinputBuf);
                    currenttran.write(destination);
                }
                else
                {
                    currenttran.read(transinputBuf);
                    currenttran.write(destination);
                }

            }
            //System.out.println("");
            // Make a note of the transition record size
            // and fill in all the details in the file header.
            int recordsize = currenttran.getRecordSize();
            destination.rewind(); // Go back to the start of the file
            header = new NewReachabilityGraphFileHeader(states, statesize, transitions, recordsize, offset);
            header.write(destination);
            destination.force();

            System.out.println(destination);
            // Done so close all the files.


            transinputBuf.rewind();
            tifc.close();
        }
        catch(EOFException e)
        {
            System.err.println("EOFException");
        }
        catch(IOException e)
        {
            System.out.println("Could not create output file.");
            e.getMessage();
        }
    }


    /**
     * fire()
     * Determines all the states resulting from firing enabled transitions
     * in the state passed as an argument
     *
     * @param pnmlData
     * @param vs   The state to determine successors from
     * @param succ A stack in which to store successors
     * @param immediateTransition
     * @return
     */
    private static int fire(PetriNetView pnmlData, State vs, Stack succ, boolean immediateTransition)
    {
        int transCount = pnmlData.numberOfTransitions();
        int transitionsfired = 0;
        int[] newstate;
        boolean[] enabledTransitions = getTransitionEnabledStatusArray(pnmlData, vs.getState(), immediateTransition);
        for(int index = 0; index < transCount; index++)
        {
            if(enabledTransitions[index])
            { // If the current transition is enabled
                newstate = fireTransition(pnmlData, vs.getState(), index);
                succ.push(new State(newstate));
                transitionsfired++;
                transitions.push(new Integer(index));

            }
        }
        return transitionsfired;
    }

    /**
     * fireTransition()
     * <p/>
     * Produces a new markup vector to simulate the firing of a transition.
     * Destroys the number of tokens shown in CMinus for a given place and
     * transition, and creates the number of tokens shown in CPlus.
     *
     * @param pnmlData   The petri net data model
     * @param marking    The state/marking to fire from
     * @param transIndex Which transition to fire
     * @return The new marking/state vector resulting from the fired transition
     * @author Matthew Cook, James Bloom and Clare Clark (original code)
     * Nadeem Akharware (optimisation)
     */
    private static int[] fireTransition(PetriNetView pnmlData, int[] marking, int transIndex)
    {
        int count;               //index for 'for loop'
        int CMinusValue;         //Value from C- matrix
        int CPlusValue;          //Value from C+ matrix

        int[][] CMinus = pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getArcsArrayList(), pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());
        int[][] CPlus = pnmlData.getActiveTokenView().getForwardsIncidenceMatrix(pnmlData.getArcsArrayList(), pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());

        //Create marking array to return
        int[] newmarking = new int[marking.length];

        for(count = 0; count < marking.length; count++)
        {
            CMinusValue = CMinus[count][transIndex];
            CPlusValue = CPlus[count][transIndex];
            newmarking[count] = marking[count] - CMinusValue + CPlusValue;
        }
        return newmarking;
    }

    /**
     * areTransitionsEnabled()
     * Calculate which transitions are enabled given a specific marking.
     *
     * @param pnmlData
     * @param marking
     * @param immediateTransitions
     * @return boolean[] - an array of booleans specifying which transitions are enabled in the specified marking
     * @author Matthew Cook (original code), Nadeem Akharware (optimisation)
     */
    private static boolean[] getTransitionEnabledStatusArray(PetriNetView pnmlData, int[] marking, boolean immediateTransitions)
    {
        int transCount = pnmlData.numberOfTransitions();
        boolean[] result = new boolean[transCount];
        boolean hasTimed = false;
        boolean hasImmediate = false;

        TransitionView[] transArray = pnmlData.getTransitionViews();

        int[][] CMinus = pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getArcsArrayList(), pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());
        int placeCount = pnmlData.numberOfPlaces();

        // Initialise the result array
        for(int t = 0; t < transCount; t++)
        {
            result[t] = true;
        }

        for(int i = 0; i < transCount; i++)
        {
            for(int j = 0; j < placeCount; j++)
            {
                if(marking[j] < CMinus[j][i])
                {
                    result[i] = false;
                    //System.err.println("Set transition enabled status to false " + i + "of " + transCount);
                }
            }
        }

        /*
           * Now make sure that if any of the enabled transitions are
           * immediate transitions, only they can fire as this must then
           * be a vanishing state.
           */
        if(!immediateTransitions)
        {
            for(int i = 0; i < transCount; i++)
            {
                if(result[i])
                {
                    if(transArray[i].isTimed())
                    {
                        hasTimed = true;
                    }
                    else
                    {
                        hasImmediate = true;
                    }
                }
            }
            if(hasTimed && hasImmediate)
            {
                for(int i = 0; i < transCount; i++)
                {
                    if(transArray[i].isTimed())
                    {
                        result[i] = false;
                    }
                }
            }
        }

        /*System.out.println("areTransitionsEnabled:");
          printArray(result);*/
        return result;
    }

    /**
     * isTangible()
     * Tests whether the state passed as an argument is tangible or vanishing.
     *
     * @param pnmlData
     * @param marking
     * @return
     */
    private static boolean isTangible(PetriNetView pnmlData, State marking)
    {
        TransitionView[] trans = pnmlData.getTransitionViews();
        int numTrans = trans.length;
        boolean hasTimed = false;
        boolean hasImmediate = false;
        boolean[] enabledTransitions = getTransitionEnabledStatusArray(pnmlData, marking.getState(), false);
        for(int i = 0; i < numTrans; i++)
        {
            if(enabledTransitions[i])
            { // If the transition is enabled
                if(trans[i].isTimed())
                {
                    //If any immediate transtions exist, the state is vanishing
                    //as they will fire immediately
                    hasTimed = true;
                }
                else if(!trans[i].isTimed())
                {
                    hasImmediate = true;
                }
            }
        }
        return hasTimed && !hasImmediate;

    }

    private static double prob(PetriNetView pnmlData, State v, State vprime)
    {
        return rateorprob(pnmlData, v, vprime, PROBABILITY);
    }

    private static double rate(PetriNetView pnmlData, State s, State sprime)
    {
        return rateorprob(pnmlData, s, sprime, RATE);
    }

    /**
     * Records the fact that there is a transition firing sequence
     * from whatever the current tangible state is to the tangible
     * state sprime with an effective transition firing rate r.
     * Note it does not need to know what the current tangible state
     * actually is, it just needs a reference to the list of arcs
     * from that state.
     *
     * @param mkprime
     * @param r
     * @param arclist A linked list of arcs from the current
     *                tangible state.
     * @return
     */
    private static int transition(RtaMarking mkprime, double r, LinkedList arclist)
    {
        ArcListElement current;
        if(arclist.size() > 0)
        {
            Iterator iterator = arclist.iterator();
            current = (ArcListElement) iterator.next();
            while((current.getTo() != mkprime.getIDNum()) && iterator.hasNext())
            {
                current = (ArcListElement) iterator.next();
            }
            if(current.getTo() == mkprime.getIDNum())
            {
                double rate = current.getRate();
                current.setRate(r + rate);
                return 0;
            }
            else
            {
                current = new ArcListElement(mkprime.getIDNum(), r, (Integer) transitions.pop());
                arclist.add(current);
                return 1;
            }
        }
        else
        {
            // This must be a new arc
            current = new ArcListElement(mkprime.getIDNum(), r, (Integer) transitions.pop());
            arclist.add(current);
            return 1;
        }
    }

    /**
     * rateorprob()
     * Calculate the PROBABILITY of a transition from a VANISHING state to another
     * state or the RATE of transition from a TANGIBLE state to another state.
     * Works out the transitions enabled to fire at a particular
     * marking, transitions that can be reached from a particular marking and the
     * intersection of the two.  Then sums the firing rates of the intersection
     * and divides it by the sum of the firing rates of the enabled transitions.
     *
     * @param pnmlData
     * @param s
     * @param sprime
     * @param rp
     * @return double - the probability
     * @author Matthew Cook (original code), Nadeem Akharware (adaption and
     * optimisation)
     */

    private static double rateorprob(PetriNetView pnmlData, State s, State sprime, boolean rp)
    {

        int[] marking1 = s.getState();
        int[] marking2 = sprime.getState();
        int markSize = marking1.length;
        int[][] incidenceMatrix = pnmlData.getActiveTokenView().getIncidenceMatrix(pnmlData.getArcsArrayList(), pnmlData.getTransitionsArrayList(), pnmlData.getPlacesArrayList());
        int transCount = pnmlData.numberOfTransitions();
        boolean[] marking1EnabledTransitions = getTransitionEnabledStatusArray(pnmlData, marking1, false); //get list of transitions enabled at marking1
        boolean[] matchingTransition = new boolean[transCount];


        //**************************************************** *************************************************
        for(int j = 0; j < transCount; j++)
        {
            matchingTransition[j] = true;  //initialise matrix of potential transition values to true
        }
        //*****************************************************************************************************
        //get transition needed to fire to get from marking1 to marking2
        for(int i = 0; i < transCount; i++)
        {
            for(int k = 0; k < markSize; k++)
            {
                //if the sum of the incidence matrix and marking 1 doesn't equal marking 2,
                //set that candidate transition possibility to be false
                if((marking1[k] + incidenceMatrix[k][i]) != marking2[k])
                {
                    matchingTransition[i] = false;
                }
            }
        }

        // If the state marking1 is tangible (i.e. we must be calculating a rate),
        // all transitions will be timed, so all can be considered
        // in the calculation.
        // Otherwise, reset the enabled status of timed transitions to false,
        // as immediate transitions will always fire first.
        if(rp)
        {
            TransitionView[] transitionViews = pnmlData.getTransitionViews();
            for(int i = 0; i < transCount; i++)
            {
                if(transitionViews[i].isTimed())
                {
                    marking1EnabledTransitions[i] = false;
                }
            }
        }

        //*****************************************************************************************************
        //check if there are any potential transitions from marking 1 to marking 2 and whether they are
        // enabled or not.
        boolean enabledAndMatching = false;
        for(int i = 0; i < transCount; i++)
        {
            if(matchingTransition[i])
            {
                if(marking1EnabledTransitions[i])
                {
                    enabledAndMatching = true;
                }
            }
        }
        if(!enabledAndMatching)
        {
            return 0.0;
        }

        //******************************************************************************************************
        //work out the sum of firing weights of input transitions
        double candidateTransitionWeighting = 0.0;
        for(int i = 0; i < transCount; i++)
        {
            if((matchingTransition[i]) && (marking1EnabledTransitions[i]))
            {
                candidateTransitionWeighting += pnmlData.getTransitionViews()[i].getRate();
            }
        }
        if(!rp)
        {
            return candidateTransitionWeighting;
        }
        else
        {
            //*****************************************************************************************************
            //work out the sum of firing weights of enabled transitions
            double enabledTransitionWeighting = 0.0;
            for(int i = 0; i < transCount; i++)
            {
                if(marking1EnabledTransitions[i])
                {
                    enabledTransitionWeighting += pnmlData.getTransitionViews()[i].getRate();
                }
            }
            return (candidateTransitionWeighting / enabledTransitionWeighting);
        }
    }

    /**
     * explored()
     * Tests whether the state passed as an argument has already
     * been explored.
     *
     * @param test The state to look for
     * @param es
     * @return
     */
    private static boolean explored(State test, LinkedList[] es)
    {
        LinkedList hashrow = es[test.hashCode() % NUMHASHROWS];
        if(hashrow == null) // This row has nothing in it yet
            return false;    // so must be an unexplored state
        Iterator iterator = hashrow.iterator();
        CompressedState current;
        for(Object aHashrow : hashrow)
        {
            current = (CompressedState) iterator.next();
            if(test.hashCode2() == current.getHashCode2())
                return true;
        }
        return false;
    }

    /**
     * identifyState()
     * Takes a state that we know has been explored before
     * and works out what id number that state has been
     * given using the explored states hashtable.
     *
     * @param test The state to be identified
     * @param es   The hashtable to look it up in
     * @return The id number of that state (-1 indicates an error)
     */
    private static int identifyState(State test, LinkedList[] es)
    {
        LinkedList hashrow = es[test.hashCode() % NUMHASHROWS];
        Iterator iterator = hashrow.iterator();
        CompressedState current;
        for(Object aHashrow : hashrow)
        {
            current = (CompressedState) iterator.next();
            if(test.hashCode2() == current.getHashCode2())
                return current.getID();
        }
        return -1;
    }


}

