/*
 * Created on 29-Jun-2005
 */
package pipe.calculations;

import pipe.exceptions.MarkingNotIntegerException;
import pipe.exceptions.TimelessTrapException;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.ImmediateAbortException;
import pipe.io.ReachabilityGraphFileHeader;
import pipe.io.StateRecord;
import pipe.io.TransitionRecord;
import pipe.utilities.math.Matrix;
import pipe.views.ArcView;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import java.io.*;
import java.util.*;

/**
 * @author Nadeem
 * @author Will Master - minor changes 02/2007
 * @author Edwin Chung/Matthew Worthington/Will Master - changes to accomodate
 *         requirements of reachability graph module, overloading generate
 *         method to produce both tangible and non tangible states of net.
 * @author Pere Bonet - fixed a bug in generate method caused by a wrong
 *         calculation of the probability of a transition from a vanishing state
 *         to another state
 * @author yufeiwang - modified so that this calculation could adapt to functional 
 * 			arc weights and transition rates.
 * 
 *         This class is used to generate the state space of a General
 *         Stochastic Petri-Net model. It uses an algorithm developed by Dr.
 *         William Knottenbelt in his MSc. thesis "Generalised Markovian
 *         Analysis of Timed Transition Systems" June 1996 Dept. of Computer
 *         Science, University of Cape Town. The algorithm eliminates vanishing
 *         states 'on the fly' and creates the infinitessimal generator matrix
 *         'Q' at the same time.
 * 
 *         There is just one public method and it is static. It returns the
 *         state space array and the matrix Q.
 */
public class StateSpaceGenerator {

	private static final boolean DEBUG = false;
	private static final boolean RATE = false;
	private static final boolean PROBABILITY = true;
	private static final int NUMHASHROWS = 46567;

	// Array storing the transitions fired
	private static final Stack transitions = new Stack();
	private static boolean[] enabledTransitions;
	private static int[][] _incidenceMatrixBeforeFire;

	public static void generate(PetriNetView pnmlData, File reachGraph)
			throws OutOfMemoryError, ImmediateAbortException, IOException, MarkingNotIntegerException {

		// back up place markings
		//pnmlData.backUpPlaceViewsMarking();
		pnmlData.setFunctionalExpressionRelatedPlaces();
		pnmlData.storeCurrentMarking();
		
		LinkedList<MarkingView>[] markings = pnmlData.getCurrentMarkingVector();
		int[] marking = new int[markings.length];
		for (int i = 0; i < markings.length; i++) {
			marking[i] = markings[i].getFirst().getCurrentMarking();
		}
		State currentMarking = new State(marking);
		int statearraysize = currentMarking.getState().length;

		Queue statesQueue = new Queue();// States waiting to be explored
		Stack tansuccessor = new Stack();
		// Objects for temporarily storing states that haven't been identified
		// as
		// tangible or vanishing
		State sprime = null;
		MarkingState currentState = null; // Used in some loops
		MarkingState s = null; // Used in some loops

		// A record of all explored states. The actual states themselves are not
		// stored here. Instead, just their two hashcodes are used to represent
		// them, one as a key to the hashtable row, the other as the entry in a
		// list at each hashtable row.
		LinkedList[] exploredStates = new LinkedList[NUMHASHROWS];

		// This list is used to temporarily store details of the arcs between
		// states on the reachability graph.
		LinkedList localarcs = new LinkedList();

		// Counters used for creating the reachability graph file
		int numStates = 0;
		int numTransitions = 0;
		int numtransitionsfired = 0;

		// Temporary files for storing tangible states and the transitions
		// between
		// them. They are later combined into one file by the createRGFile
		// method.
		RandomAccessFile outputFile;
		RandomAccessFile esoFile;
		File intermediate = new File("graph.irg");

		if (intermediate.exists()) {
			if (!intermediate.delete()) {
				System.err.println("Could not delete intermediate file.");
			}
		}

		try {
			outputFile = new RandomAccessFile(intermediate, "rw");
			esoFile = new RandomAccessFile(reachGraph, "rw");
			// Write a blank file header as a place holder for later
			ReachabilityGraphFileHeader header = new ReachabilityGraphFileHeader();
			header.write(esoFile);
		} catch (IOException e) {
			System.out.println("Could not create intermediate files.");
			return;
		}

		pnmlData.createMatrixes();

		currentState = new MarkingState(currentMarking, numStates, isTangible(
				pnmlData, currentMarking));
		currentState.setPlaceMarking(currentMarking.getPlaceMarking());
		numStates++;
		statesQueue.enqueue(currentState);
		addExplored(currentState, exploredStates, esoFile, true);

		while (!statesQueue.isEmpty()) {

			s = (MarkingState) statesQueue.dequeue();

			numtransitionsfired += fire(pnmlData, s, tansuccessor);
			while (!tansuccessor.isEmpty()) {
				sprime = (State) tansuccessor.pop();
				if (!explored(sprime, exploredStates)) {
					currentState = new MarkingState(sprime, numStates,
							isTangible(pnmlData, sprime));
					currentState.setPlaceMarking(sprime.getPlaceMarking());
					numStates++;
					statesQueue.enqueue(currentState);
					addExplored(currentState, exploredStates, esoFile, true);
				} else {
					int id = identifyState(sprime, exploredStates);
					if (id == -1) {
						throw new ImmediateAbortException(
								"Could not identify previously explored tangible state.");
					}
					currentState = new MarkingState(sprime, id);
					currentState.setPlaceMarking(sprime.getPlaceMarking());
				}
				numTransitions += transition(currentState,
						rate(pnmlData, s, sprime), localarcs);
//				for (int index = 0; index < pnmlData.numberOfTransitions(); index++) {
//					if (enabledTransitions[index]) {
//						setTokenAfterFiringTransition(index);
//					}
//				}
			}
			// Write all the arcs for the reachability graph to file
			writeTransitions(s, localarcs, outputFile, true);
			// Clear the list so can start again with the next set of arcs
			localarcs.clear();

			// REVISAR-LIMITS
			if (numTransitions > Constants.MAX_NODES) {
				// System.out.println("numTransitions: " + numTransitions);
				throw new OutOfMemoryError("The net generates in excess of "
						+ Constants.MAX_NODES + " states");
			}
		}
		try {
			outputFile.close();
		} catch (IOException e1) {
			System.err.println("\nCould not close intermediate file.");
		}
		System.out.println("\nGenerate Ends, " + numStates
				+ " states found with " + numTransitions + " arcs.");
		createRGFile(intermediate, esoFile, statearraysize, numStates,
				numTransitions, true);

		if (DEBUG) {
			/* Write a csv file indicating the Hashtable distribution */
			FileWriter htdist = new FileWriter("HashTableDist.csv");
			for (int row = 0; row < NUMHASHROWS; row++) {
				htdist.write(Integer.toString(row) + ",");
				if (exploredStates[row] == null) {
					htdist.write("0\n");
				} else {
					htdist.write(Integer.toString(exploredStates[row].size())
							+ "\n");
				}
			}
			htdist.close();
			System.out
					.println("Finished writing hashtable distribution to file.");
		}

		if (intermediate.exists()) {
			if (!intermediate.delete()) {
				System.err.println("\nCould not delete intermediate file.");
			}
		}
		pnmlData.restorePreviousMarking();
	}

	/**
	 * generate() This static method generates the statespace from a GSPN It
	 * uses a hashtable so that it can quickly check whether a state has already
	 * been explored.
	 * 
	 * @param pnmlData
	 * @param reachGraph
	 * @param resultspane
	 * @throws TimelessTrapException
	 * @throws ImmediateAbortException
	 * @throws IOException
	 * @author Nadeem 30/06/2005
	 * @throws OutOfMemoryError
	 * @throws MarkingNotIntegerException 
	 */
	public static void generate(PetriNetView pnmlData, File reachGraph,
			ResultsHTMLPane resultspane) throws OutOfMemoryError,
			TimelessTrapException, ImmediateAbortException, IOException, MarkingNotIntegerException {

		pnmlData.backUpPlaceViewsMarking();
		pnmlData.setFunctionalExpressionRelatedPlaces();
		// This is used to catch timeless traps. It's the maximum number
		// of attempts to try and get successors to vanishing states.
		final int MAX_TRIES = 100000;
		LinkedList<MarkingView>[] markings = pnmlData.getCurrentMarkingVector();
		int[] marking = new int[markings.length];
		for (int i = 0; i < markings.length; i++) {
			marking[i] = markings[i].getFirst().getCurrentMarking();
		}
		State currentMarking = new State(marking);
		int statearraysize = currentMarking.getState().length;

		// Tangible states waiting to be explored
		Queue tangibleStates = new Queue();
		// Vanishing states waiting to be explored and then eliminated
		Stack vanishingStates = new Stack();
		// Temporary stacks for storing states resulting from a transition
		// firing
		Stack vansuccessor = new Stack();
		Stack tansuccessor = new Stack();
		// Objects for temporarily storing states that haven't been identified
		// as
		// tangible or vanishing
		State vprime = null;
		State sprime = null;
		MarkingState tangible = null; // Used in some loops
		MarkingState s = null; // Used in some loops
		VanishingState v = null; // Used in some loops

		// A record of all explored states. The actual states themselves are not
		// stored here. Instead, just their two hashcodes are used to represent
		// them, one as a key to the hashtable row, the other as the entry in a
		// list at each hashtable row.
		LinkedList[] exploredStates = new LinkedList[NUMHASHROWS];

		// This list is used to temporarily store details of the arcs between
		// states on the reachability graph.
		LinkedList localarcs = new LinkedList();

		// These are used when calculating effective transition rates from
		// vanishing states to tangible states
		double p;
		double pprime;
		double epsilon = 0.0000001;

		// Counters used for creating the reachability graph file
		int numtangiblestates = 0;
		int numtransitions = 0;
		int numtransitionsfired = 0;

		// The following two variables form a crude progress monitor

		// Temporary files for storing tangible states and the transitions
		// between
		// them. They are later combined into one file by the createRGFile
		// method
		RandomAccessFile outputFile;
		RandomAccessFile esoFile;
		File intermediate = new File("graph.irg");

		if (intermediate.exists()) {
			if (!intermediate.delete()) {
				System.err.println("Could not delete intermediate file.");
			}
		}

		try {
			outputFile = new RandomAccessFile(intermediate, "rw");
			esoFile = new RandomAccessFile(reachGraph, "rw");
			// Write a blank file header as a place holder for later
			ReachabilityGraphFileHeader header = new ReachabilityGraphFileHeader();
			header.write(esoFile);
		} catch (IOException e) {
			System.out.println("Could not create intermediate files.");
			return;
		}

		pnmlData.createMatrixes();

		/*
		 * Phase I Initialise the tangibleStates stack with initial tangible
		 * states.
		 */
		System.out.println("Beginning Phase I: "
				+ "Determining initial tangible states...");

		// Start state space exploration
		if (isTangible(pnmlData, currentMarking)) {
			tangible = new MarkingState(currentMarking, numtangiblestates);
			tangible.setPlaceMarking(pnmlData.getCurrentMarkingVector());
			tangibleStates.enqueue(tangible);
			addExplored(tangible, exploredStates, esoFile, false);
			numtangiblestates++;
		} else {
			int attempts = 0; // This is a counter used to detect timeless traps
			VanishingState vs = new VanishingState(currentMarking, 1.0);
			vs.setPlaceMarking(pnmlData.getCurrentMarkingVector());
			vanishingStates.push(vs);
			while ((!vanishingStates.isEmpty()) && (attempts != MAX_TRIES)) {
				attempts++;
				v = (VanishingState) vanishingStates.pop();
				/**
				 * 
				 */
				pnmlData.setCurrentMarkingVector(v.getState());
				p = v.getRate();
				// System.out.println("p: " + p);//debug
				// numtransitionsfired += fire(_pnmlData, v, vansuccessor);

				LinkedList<MarkingView>[] state = new LinkedList[v.getState().length];
				for (int i = 0; i < v.getState().length; i++) {
					LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
					MarkingView m = new MarkingView(pnmlData.getTokenViews()
							.getFirst(), v.getState()[i]);
					mlist.add(m);
					state[i] = mlist;
				}
				boolean[] enabledTransitions = pnmlData
						.areTransitionsEnabled(state);

				int transition = fireFirstEnabledTransition(pnmlData,
						enabledTransitions, v, vansuccessor);

				while (transition != -1) {
					// while (!vansuccessor.isEmpty()) {
					vprime = (State) vansuccessor.pop();
					
					/**
					 * 
					 */
					pnmlData.setCurrentMarkingVector(vprime.getState());
					if (isTangible(pnmlData, vprime)) {
						if (!explored(vprime, exploredStates)) {
							tangible = new MarkingState(vprime,
									numtangiblestates);
							tangible.setPlaceMarking(vprime.getPlaceMarking());
							tangibleStates.enqueue(tangible);
							addExplored(tangible, exploredStates, esoFile,
									false);
							numtangiblestates++;
						}
					} else {
						pprime = p * prob(pnmlData, v, vprime, transition);
						if (pprime > epsilon) {
							VanishingState vsp =new VanishingState(vprime,
									pprime);
							vsp.setPlaceMarking(vprime.getPlaceMarking());
							vanishingStates.push(vsp);
						}
					}
					transition = fireFirstEnabledTransition(pnmlData,
							enabledTransitions, v, vansuccessor);
				}
			}
			if (attempts == MAX_TRIES) {
				try {
					outputFile.close();
				} catch (IOException e1) {
					System.err.println("Could not close intermediate file.");
				}
				throw new TimelessTrapException();
			}
		}
		/* Phase I ends */

		/*
		 * Phase II Perform state space exploration, eliminating vanishing
		 * states
		 */
		System.out.println("Beginning Phase II: Exploring state space...");

		// Continue state space exploration
		while (!tangibleStates.isEmpty()) {
			/*
			 * if (progress == UPDATEAFTER) { progress = 0; // Reset the counter
			 * System.out.print(numtangiblestates +
			 * " tangible states generated and " + numtransitionsfired +
			 * " transitions fired.\r"); } else{ progress++; }
			 */

			s = (MarkingState) tangibleStates.dequeue();
		//	pnmlData.setCurrentMarkingVector(s.getPlaceMarking());
			// System.out.println("numtransitionsfired abans de fire: " +
			// numtransitionsfired);//debug
			pnmlData.createMatrixes();
			numtransitionsfired += fire(pnmlData, s, tansuccessor);
			//updatePlaceMarkings();
			// System.out.println("numtransitionsfired despres de fire: " +
			// numtransitionsfired);//debug
			while (!tansuccessor.isEmpty()) {
				sprime = (State) tansuccessor.pop();
				if (isTangible(pnmlData, sprime)) {
					if (!explored(sprime, exploredStates)) {
						tangible = new MarkingState(sprime, numtangiblestates);
						
						tangible.setPlaceMarking(sprime.getPlaceMarking());
						tangibleStates.enqueue(tangible);
						addExplored(tangible, exploredStates, esoFile, false);
						numtangiblestates++;
					} else {
						int id = identifyState(sprime, exploredStates);
						if (id == -1) {
							throw new ImmediateAbortException(
									"Could not identify "
											+ "previously explored tangible state.");
						}
						tangible = new MarkingState(sprime, id);
						tangible.setPlaceMarking(sprime.getPlaceMarking());
					}
					
					
					//here we calculate the rate, so we need place markings to be updated before this
					numtransitions += transition(tangible,
							rate(pnmlData, s, sprime), localarcs);
				} else {
					int attempts = 0;
					VanishingState vs = new VanishingState(sprime, rate(
							pnmlData, s, sprime));
					vs.setPlaceMarking(sprime.getPlaceMarking());
					vanishingStates.push(vs);
					while ((!vanishingStates.isEmpty())
							&& (attempts != MAX_TRIES)) {
						attempts++;
						v = (VanishingState) vanishingStates.pop();
						p = v.getRate();

						LinkedList<MarkingView>[] state = new LinkedList[v
								.getState().length];
						for (int i = 0; i < v.getState().length; i++) {
							LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
							MarkingView m = new MarkingView(pnmlData
									.getTokenViews().getFirst(),
									v.getState()[i]);
							mlist.add(m);
							state[i] = mlist;
						}
						boolean[] enabledTransitions = pnmlData
								.areTransitionsEnabled(state);

						int transition = fireFirstEnabledTransition(pnmlData,
								enabledTransitions, v, vansuccessor);
						//updatePlaceMarkings();
						while (transition != -1) {
							vprime = (State) vansuccessor.pop();
							pprime = p * prob(pnmlData, v, vprime, transition);
							if (isTangible(pnmlData, vprime)) {
								if (!explored(vprime, exploredStates)) {
									tangible = new MarkingState(vprime,
											numtangiblestates);
									tangible.setPlaceMarking(vprime.getPlaceMarking());
									tangibleStates.enqueue(tangible);
									addExplored(tangible, exploredStates,
											esoFile, false);
									numtangiblestates++;
								} else {
									int id = identifyState(vprime,
											exploredStates);
									if (id == -1) {
										throw new ImmediateAbortException(
												"Could not "
														+ "identify previously explored tangible "
														+ "state.");
									}
									tangible = new MarkingState(vprime, id);
									tangible.setPlaceMarking(vprime.getPlaceMarking());
								}
								numtransitions += transition(tangible, pprime,
										localarcs);
							} else {
								if (pprime > epsilon) {
									vanishingStates.push(new VanishingState(
											vprime, pprime));
								}
							}

							transition = fireFirstEnabledTransition(pnmlData,
									enabledTransitions, v, vansuccessor);
						//	updatePlaceMarkings();
						}
					}
					if (attempts == MAX_TRIES) {
						try {
							outputFile.close();
						} catch (IOException e1) {
							System.err
									.println("Could not close intermediate file.");
						}
						throw new TimelessTrapException();
					}
				}
			}
			
			
			// Write all the arcs for the reachability graph to file
			writeTransitions(s, localarcs, outputFile, false);
			// Clear the list so can start again with the next set of arcs
			localarcs = new LinkedList();
		}
		try {
			outputFile.close();
		} catch (IOException e1) {
			System.err.println("\nCould not close intermediate file.");
		}
		System.out.println("\nGenerate Ends, " + numtangiblestates
				+ " tangible states found with " + numtransitions + " arcs.");
		createRGFile(intermediate, esoFile, statearraysize, numtangiblestates,
				numtransitions, false);

		if (DEBUG) {
			/* Write a csv file indicating the Hashtable distribution */
			FileWriter htdist = new FileWriter("HashTableDist.csv");
			for (int row = 0; row < NUMHASHROWS; row++) {
				htdist.write(Integer.toString(row) + ",");
				if (exploredStates[row] == null) {
					htdist.write("0\n");
				} else {
					htdist.write(Integer.toString(exploredStates[row].size())
							+ "\n");
				}
			}
			htdist.close();
			System.out
					.println("Finished writing hashtable distribution to file.");
		}

		if (intermediate.exists()) {
			if (!intermediate.delete()) {
				System.err.println("\nCould not delete intermediate file.");
			}
		}
		pnmlData.restorePlaceViewsMarking();
	}
	
	/**
	 * isTangible() Tests whether the state passed as an argument is tangible or
	 * vanishing.
	 * 
	 * @param pnmlData
	 * @param marking
	 * @return
	 * 
	 * modified 
	 */
	private static boolean isTangible(PetriNetView pnmlData, State marking) {
		
		/**
		 * 
		 */
		pnmlData.setCurrentMarkingVector(marking.getState());
		TransitionView[] trans = pnmlData.getTransitionViews();
		int numTrans = trans.length;
		boolean hasTimed = false;
		boolean hasImmediate = false;
		boolean hasEnabledTransitions = false;

		LinkedList<MarkingView>[] state = new LinkedList[marking.getState().length];
		for (int i = 0; i < marking.getState().length; i++) {
			LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
			MarkingView m = new MarkingView(
					pnmlData.getTokenViews().getFirst(), marking.getState()[i]);
			mlist.add(m);
			state[i] = mlist;
		}

		boolean[] enabledTransitions = pnmlData.areTransitionsEnabled(state);

		for (int i = 0; i < numTrans; i++) {
			if (enabledTransitions[i]) { // If the transition is enabled
				if (trans[i].isTimed()) {
					// If any immediate transtions exist, the state is vanishing
					// as they will fire immediately
					hasTimed = true;
					hasEnabledTransitions=true;
				} else if (!trans[i].isTimed()) {
					hasImmediate = true;
					hasEnabledTransitions=true;
					// return false;
				}
			}
		}
		if(!hasEnabledTransitions){
			return true;
		}
		return (hasTimed && !hasImmediate);
	}

	/**
	 * fire() Determines all the states resulting from firing enabled
	 * transitions in the state passed as an argument
	 * 
	 * @param pnmlData
	 * @param vs
	 *            The state to determine successors from
	 * @param succ
	 *            A stack in which to store successors
	 * @return
	 * @throws MarkingNotIntegerException 
	 */
	private static int fire(PetriNetView pnmlData, State vs, Stack succ) throws MarkingNotIntegerException {
		int transCount = pnmlData.numberOfTransitions();

		int transitionsfired = 0;
		int[] newstate = null;

		LinkedList<MarkingView>[] state = new LinkedList[vs.getState().length];
		for (int i = 0; i < vs.getState().length; i++) {
			LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
			MarkingView m = new MarkingView(
					pnmlData.getTokenViews().getFirst(), vs.getState()[i]);
			mlist.add(m);
			state[i] = mlist;
		}
		pnmlData.setCurrentMarkingVector(vs.getState());
		//boolean[] enabledTransitions = pnmlData.areTransitionsEnabled(state);
		enabledTransitions = pnmlData.areTransitionsEnabled(state);
		for (int index = 0; index < transCount; index++) {
			if (enabledTransitions[index]) {
				
				/**
				 * 
				 */
				pnmlData.setCurrentMarkingVector(vs.getState());
				
				newstate = fireTransition(pnmlData, vs.getState(), index);
				State s = new State(newstate);
				s.setPlaceMarking(pnmlData.getCurrentMarkingVector());
				// System.out.println("fired transition " + index);//debug
				succ.push(s);
				
				/**
				 * 
				 */
				pnmlData.setCurrentMarkingVector(vs.getState());
				//pnmlData.restorePreviousMarking();
				// System.out.println("generat estat: " + (new
				// State(newstate).toString()));//debug
				transitionsfired++;
				transitions.push(new Integer(index));
			}
		}
		return transitionsfired;
	}

	/**
	 * fireFirstEnabledTransition() Determines the state resulting from firing
	 * enabled transitions in the state passed as an argument
	 * 
	 * @param pnmlData
	 * @param enabledTransitions
	 * @param vs
	 *            The state to determine successors from
	 * @param succ
	 *            A stack in which to store successors
	 * @return
	 * @throws MarkingNotIntegerException 
	 */
	private static int fireFirstEnabledTransition(PetriNetView pnmlData,
			boolean[] enabledTransitions, State vs, Stack succ) throws MarkingNotIntegerException {

		for (int index = 0; index < enabledTransitions.length; index++) {
			if (enabledTransitions[index]) {
				
				/**
				 * 
				 */
				pnmlData.setCurrentMarkingVector(vs.getState());
				// If the current transition is enabled
				int[] newstate = fireTransition(pnmlData, vs.getState(), index);
				State s = new State(newstate);
				s.setPlaceMarking(pnmlData.getCurrentMarkingVector());
				// System.out.println("fired transition " + index);//debug
				succ.push(s);
				
				/**
				 * 
				 */
				pnmlData.setCurrentMarkingVector(vs.getState());
				//pnmlData.restorePreviousMarking();
				// System.out.println("generat estat: " + (new
				// State(newstate).toString()));//debug
				transitions.push(new Integer(index));
				enabledTransitions[index] = false;
				return index;
			}
		}
		return -1;
	}

	/**
	 * fireTransition() Produces a new markup vector to simulate the firing of a
	 * transition. Destroys the number of tokens shown in CMinus for a given
	 * place and transition, and creates the number of tokens shown in CPlus.
	 * 
	 * @author Matthew Cook, James Bloom and Clare Clark (original code) Nadeem
	 *         Akharware (optimisation)
	 * @param pnmlData
	 *            The petri net data model
	 * @param marking
	 *            The state/marking to fire from
	 * @param transIndex
	 *            Which transition to fire
	 * @return The new marking/state vector resulting from the fired transition
	 * @throws MarkingNotIntegerException 
	 */
	private static int[] fireTransition(PetriNetView pnmlData, int[] marking,
			int transIndex) throws MarkingNotIntegerException {
		
		
		
		int CMinusValue; // Value from C- matrix
		int CPlusValue; // Value from C+ matrix
		//
		int[][] CMinus = pnmlData
				.getTokenViews()
				.getFirst()
				.getBackwardsIncidenceMatrix(pnmlData.getArcsArrayList(),
						pnmlData.getTransitionsArrayList(),
						pnmlData.getPlacesArrayList());
		int[][] CPlus = pnmlData
				.getTokenViews()
				.getFirst()
				.getForwardsIncidenceMatrix(pnmlData.getArcsArrayList(),
						pnmlData.getTransitionsArrayList(),
						pnmlData.getPlacesArrayList());

		// Create marking array to return
		int[] newmarking = new int[marking.length];

		for (int count = 0; count < marking.length; count++) {
			CMinusValue = CMinus[count][transIndex];
			CPlusValue = CPlus[count][transIndex];
			if(CMinusValue==-1 || CPlusValue==-1){
				throw new MarkingNotIntegerException();
				
			}
			newmarking[count] = marking[count] - CMinusValue + CPlusValue;
		}
			
		setTokenAfterFiringTransition(transIndex);
		return newmarking;
	}
	
	

	public static void setTokenAfterFiringTransition(int transitionId) {

		PetriNetView dataLayer = ApplicationSettings.getApplicationView()
				.getCurrentPetriNetView();
		Collection<TransitionView> trans = dataLayer.getTransitionsArrayList();
        //TODO: Fix this
		TransitionView tran = null; //trans.get(transitionId);
		Iterator to = tran.getConnectToIterator();
		Iterator from = tran.getConnectFromIterator();
		if (to.hasNext()) {
			ArcView arcTo = ((ArcView) to.next());
			PlaceView source = ((PlaceView) arcTo.getSource());
			if (dataLayer.isPlaceFunctionalRelated(source.getName())) {
				LinkedList<MarkingView> weight = arcTo.getWeight();
				LinkedList<MarkingView> sourceMarking = source
						.getCurrentMarkingView();
				for (int i = 0; i < weight.size(); i++) {
					int current = sourceMarking.get(i).getCurrentMarking();
					int change=0;
					for(MarkingView w: weight){
						if(w.getToken().getID().equals(sourceMarking.get(i).getToken().getID())){
							change = w.getCurrentMarking();
						}
					}
					//int newMarking = current - weight.get(i).getCurrentMarking();
					int newMarking = current - change;
					sourceMarking.get(i).setCurrentMarking(newMarking);

				}
			}

			// arcTo.updateArcWeight();
		}
		if (from.hasNext()) {
			ArcView arcFrom = ((ArcView) from.next());
			PlaceView targetPlace = ((PlaceView) arcFrom.getTarget());

			if (dataLayer.isPlaceFunctionalRelated(targetPlace.getName())) {
				LinkedList<MarkingView> weight = arcFrom.getWeight();
				LinkedList<MarkingView> sourceMarking = targetPlace
						.getCurrentMarkingView();
				for (int i = 0; i < weight.size(); i++) {
					int current = sourceMarking.get(i).getCurrentMarking();
					int change=0;
					for(MarkingView w: weight){
						if(w.getToken().getID().equals(sourceMarking.get(i).getToken().getID())){
							change = w.getCurrentMarking();
						}
					}
				//	int newMarking = current + weight.get(i).getCurrentMarking();
					int newMarking = current - change;
					sourceMarking.get(i).setCurrentMarking(newMarking);
				}
			}

			// arcFrom.updateArcWeight();
		}
	}

	/**
	 * explored() Tests whether the state passed as an argument has already been
	 * explored.
	 * 
	 * @param test
	 *            The state to look for
	 * @param es
	 * @param exploredStates
	 *            The hashtable to check for the state
	 * @return
	 */
	private static boolean explored(State test, LinkedList[] es) {
		LinkedList hashrow = es[test.hashCode() % NUMHASHROWS];

		if (hashrow == null) {
			// This row has nothing in it yet so must be an unexplored state
			return false;
		}
		Iterator iterator = hashrow.iterator();
		CompressedState current;
		for (Object aHashrow : hashrow) {
			current = (CompressedState) iterator.next();
			if (test.hashCode2() == current.getHashCode2()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * identifyState() Takes a state that we know has been explored before and
	 * works out what id number that state has been given using the explored
	 * states hashtable.
	 * 
	 * @param test
	 *            The state to be identified
	 * @param es
	 *            The hashtable to look it up in
	 * @return The id number of that state (-1 indicates an error)
	 */
	private static int identifyState(State test, LinkedList[] es) {
		LinkedList hashrow = es[test.hashCode() % NUMHASHROWS];
		Iterator iterator = hashrow.iterator();
		CompressedState current;

		for (Object aHashrow : hashrow) {
			current = (CompressedState) iterator.next();
			if (test.hashCode2() == current.getHashCode2()) {
				return current.getID();
			}
		}
		return -1;
	}

	/**
	 * addExplored() Adds a compressed version of a tangible state to the
	 * explored states hashtable and also writes the full state to a file for
	 * later use.
	 * 
	 * @param newstate
	 *            The explored state to be added
	 * @param es
	 *            A reference to the hashtable
	 * @param opfile
	 *            The file to write the state to
	 * @param vanishingStates
	 */
	private static void addExplored(MarkingState newstate, LinkedList[] es,
			RandomAccessFile opfile, boolean vanishingStates) {
		LinkedList hashrow = es[newstate.hashCode() % NUMHASHROWS];

		if (hashrow == null) {
			// This hashcode hasn't come up before so we need to set up the
			// linked list first
			es[newstate.hashCode() % NUMHASHROWS] = new LinkedList();
			hashrow = es[newstate.hashCode() % NUMHASHROWS];
		}
		hashrow.add(new CompressedState(newstate.hashCode2(), newstate
				.getIDNum()));
		// Now also write this state to disk for later use
		StateRecord sr = new StateRecord(newstate);
		// System.out.println("afegit: " + sr.toString());//debug
		try {
			if (vanishingStates) {
				sr.write(opfile, newstate.getIsTangible());
			} else {
				sr.write(opfile);
			}
		} catch (IOException e) {
			System.err
					.println("IO problem while writing explored states to file.");
		}
	}

	/**
	 * rate() Calculate the RATE of transition from a TANGIBLE state to another
	 * state. Works out the transitions enabled to fire at a particular marking,
	 * transitions that can be reached from a particular marking and the
	 * intersection of the two. Then sums the firing rates of the intersection
	 * and divides it by the sum of the firing rates of the enabled transitions.
	 * 
	 * @author Matthew Cook (original code), Nadeem Akharware (adaption and
	 *         optimisation), Pere Bonet (minor changes)
	 * @param pnmlData
	 * @param s
	 * @param sprime
	 * @param v
	 * @param vprime
	 * @return double - the probability
	 */
	private static double rate(PetriNetView pnmlData, State s, State sprime) {
		int[] marking1 = s.getState();
		int[] marking2 = sprime.getState();
		int markSize = marking1.length;
		//pnmlData.createMatrixes();
	//	int[][] incidenceMatrix = getPreviousIncidentMatrix();
		
		pnmlData.setCurrentMarkingVector(s.getState());
		pnmlData.createMatrixes();
	//	System.out.println("token after setting: "+pnmlData.getPlacesArrayList().get(0).getCurrentMarkingView().getFirst().getCurrentMarking());
//		System.out.println();
//		System.out.println(Arrays.toString(s.getState()));
//		System.out.println("current marking: ["+ pnmlData.getCurrentMarkingVector()[0].getFirst().getCurrentMarking());
//		int[][] incidenceMatrix = pnmlData
//				.getTokenViews()
//				.getFirst()
//				.getIncidenceMatrix(pnmlData.getArcsArrayList(),
//						pnmlData.getTransitionsArrayList(),
//						pnmlData.getPlacesArrayList());
		//int[][] im = pnmlData.getTokenViews().getFirst().getIncidenceMatrix();
//		System.out.println("state s marking: "+ Arrays.toString(s.getState()));
//		System.out.println("state s marking: "+ Arrays.toString(sprime.getState()));
//		System.out.println("rate1: "+pnmlData.getTransitionViews()[0].getRate());
//		System.out.println("rate2: "+pnmlData.getTransitionViews()[1].getRate());
//		System.out.println();
		Matrix incidenceMatrix1 = pnmlData
				.getTokenViews()
				.getFirst()
				.getIncidenceMatrix();
		int transCount = pnmlData.numberOfTransitions();
		// get list of transitions enabled at marking1
		LinkedList<MarkingView>[] state = new LinkedList[marking1.length];
		for (int i = 0; i < marking1.length; i++) {
			LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
			MarkingView m = new MarkingView(
					pnmlData.getTokenViews().getFirst(), marking1[i]);
			mlist.add(m);
			state[i] = mlist;
		}

		boolean[] marking1EnabledTransitions = pnmlData
				.areTransitionsEnabled(state);
		boolean[] matchingTransition = new boolean[transCount];
		// get transition needed to fire to get from marking1 to marking2
		for (int i = 0; i < transCount; i++) {
			// initialise value of potential transition to true
			matchingTransition[i] = true;
			for (int k = 0; k < markSize; k++) {
				// if the sum of the incidence matrix and marking 1 doesn't
				// equal
				// marking 2, set that candidate transition possibility to be
				// false
			//	incidenceMatrix1.get(i, k);
			//	System.out.println(incidenceMatrix1.get(i, k)+" <-matrix  "+incidenceMatrix[k][i]);
				//System.out.println("marking 1: "+marking1[k]+" + "+incidenceMatrix1.get(i, k)+" = marking 2 "+marking2[k]);
				if ((marking1[k] + incidenceMatrix1.get(k, i)) != marking2[k]) {
					matchingTransition[i] = false;
					break;
				}
			}
		}

		// check if there are any potential transitions from marking 1 to
		// marking
		// 2 and whether they are enabled or not.
		boolean enabledAndMatching = false;
		for (int i = 0; i < transCount; i++) {
			if (matchingTransition[i]) {
				if (marking1EnabledTransitions[i]) {
					enabledAndMatching = true;
					break; // we have found an enabled and matching transition
				}
			}
		}
		if (!enabledAndMatching) {
			return 0.0;
		}
		// work out the sum of firing weights of input transitions
		double candidateTransitionWeighting = 0.0;
		for (int i = 0; i < transCount; i++) {
			if ((matchingTransition[i]) && (marking1EnabledTransitions[i])) {
				candidateTransitionWeighting +=pnmlData.getTransitionViews()[i].getRate(); //getPreviousTransitionRates(i);
						
			}
		}
		
		//System.out.println("current marking: ["+ pnmlData.getCurrentMarkingVector()[0].getFirst().getCurrentMarking()+", "+pnmlData.getCurrentMarkingVector()[1].getFirst().getCurrentMarking()+"]");
		//System.out.println("rate from "+Arrays.toString(s.getState())+ " to "+ Arrays.toString(sprime.getState())+" is " + candidateTransitionWeighting);
		return candidateTransitionWeighting;
	}

	/**
	 * prob() Calculate the PROBABILITY of a transition from a VANISHING state
	 * to another state. Works out the transitions enabled to fire at a
	 * particular marking, transitions that can be reached from a particular
	 * marking and the intersection of the two. Then sums the firing rates of
	 * the intersection and divides it by the sum of the firing rates of the
	 * enabled transitions.
	 * 
	 * @author Matthew Cook (original code), Nadeem Akharware (adaption and
	 *         optimisation), Pere Bonet (minor changes)
	 * @param pnmlData
	 * @param s
	 * @param sprime
	 * @param firedTransition
	 * @param v
	 * @param vprime
	 * @return double - the probability
	 */
	private static double prob(PetriNetView pnmlData, State s, State sprime,
			int firedTransition) {
		
		int[] marking1 = s.getState();
		int[] marking2 = sprime.getState();
		int markSize = marking1.length;
//		int[][] incidenceMatrix =getPreviousIncidentMatrix();
		
		
		/**
		 * 
		 */
		pnmlData.setCurrentMarkingVector(s.getState());
		int[][] incidenceMatrix = pnmlData
				.getTokenViews()
				.getFirst()
				.getIncidenceMatrix(pnmlData.getArcsArrayList(),
						pnmlData.getTransitionsArrayList(),
						pnmlData.getPlacesArrayList());
		int transCount = pnmlData.numberOfTransitions();
		// get list of transitions enabled at marking1
		LinkedList<MarkingView>[] state = new LinkedList[marking1.length];
		for (int i = 0; i < marking1.length; i++) {
			LinkedList<MarkingView> mlist = new LinkedList<MarkingView>();
			MarkingView m = new MarkingView(
					pnmlData.getTokenViews().getFirst(), marking1[i]);
			mlist.add(m);
			state[i] = mlist;
		}
		boolean[] marking1EnabledTransitions = pnmlData
				.areTransitionsEnabled(state);
		boolean[] matchingTransition = new boolean[transCount];

		// get transition needed to fire to get from marking1 to marking2
		for (int i = 0; i < transCount; i++) {
			// initialise value of potential transition to true
			matchingTransition[i] = true;
			for (int k = 0; k < markSize; k++) {
				// if the sum of the incidence matrix and marking 1 doesn't
				// equal
				// marking 2, set that candidate transition possibility to be
				// false
				if ((marking1[k] + incidenceMatrix[k][i]) != marking2[k]) {
					matchingTransition[i] = false;
					break;
				}
			}
		}

		// check if there are any potential transitions from marking 1 to
		// marking
		// 2 and whether they are enabled or not.
		boolean enabledAndMatching = false;
		for (int i = 0; i < transCount; i++) {
			if (matchingTransition[i]) {
				if (marking1EnabledTransitions[i]) {
					enabledAndMatching = true;
					break; // we have found an enabled and matching transition
				}
			}
		}
		if (!enabledAndMatching) {
			return 0.0;
		}

		// work out the sum of firing weights of enabled transitions
		double enabledTransitionWeighting = 0.0;
		for (int i = 0; i < transCount; i++) {
			if (marking1EnabledTransitions[i]) {
				enabledTransitionWeighting += pnmlData.getTransitionViews()[i]
						.getRate();
				//enabledTransitionWeighting += getPreviousTransitionRates(i);
			}
		}
		double candidateTransitionWeighting = pnmlData.getTransitionViews()[firedTransition].getRate();//getPreviousTransitionRates(firedTransition);
		
//		//System.out.println("prob from "+Arrays.toString(s.getState())+ " to "+ Arrays.toString(sprime.getState())+" is " + candidateTransitionWeighting / enabledTransitionWeighting);
		return (candidateTransitionWeighting / enabledTransitionWeighting);
	}

	/**
	 * Records the fact that there is a transition firing sequence from whatever
	 * the current tangible state is to the tangible state sprime with an
	 * effective transition firing rate r. Note it does not need to know what
	 * the current tangible state actually is, it just needs a reference to the
	 * list of arcs from that state.
	 * 
	 * @param sprime
	 * @param r
	 * @param arclist
	 *            A linked list of arcs from the current tangible state.
	 * @return
	 */
	private static int transition(MarkingState sprime, double r,
			LinkedList arclist) {
		ArcListElement current;
		if (arclist.size() > 0) {
			Iterator iterator = arclist.iterator();
			current = (ArcListElement) iterator.next();
			while ((current.getTo() != sprime.getIDNum()) && iterator.hasNext()) {
				current = (ArcListElement) iterator.next();
			}
			if (current.getTo() == sprime.getIDNum()) {
				double rate = current.getRate();
				current.setRate(r + rate);
				return 0;
			} else {
				current = new ArcListElement(sprime.getIDNum(), r,
						(Integer) transitions.pop());
				arclist.add(current);
				return 1;
			}
		} else {
			// This must be a new arc
			current = new ArcListElement(sprime.getIDNum(), r,
					(Integer) transitions.pop());
			arclist.add(current);
			return 1;
		}
	}
	
//	private static void storeIncidentMatrixBeforeFiring(){
//		PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
//		_incidenceMatrixBeforeFire = pnmlData
//				.getTokenViews()
//				.getFirst()
//				.getIncidenceMatrix(pnmlData.getArcsArrayList(),
//						pnmlData.getTransitionsArrayList(),
//						pnmlData.getPlacesArrayList());
//	}
//	
//	private static int[][] getPreviousIncidentMatrix(){
//		return _incidenceMatrixBeforeFire;
//	}
//
//	private static Vector<Double> previousRate=new Vector<Double>();
//	
//	private static void storeTransitionRatesBeforeFiring(){
//		ArrayList<TransitionView> trans = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTransitionsArrayList();
//		previousRate.setSize(trans.size());
//		for(int i=0;i<trans.size();i++){
//			previousRate.set(i,trans.get(i).getRate());
//		}
//	}
//	private static Double getPreviousTransitionRates(int i){
//		return previousRate.get(i);
//	}
	
	/**
	 * writeTransitions() Records all the arcs in the reachability graph from
	 * state 'from'.
	 * 
	 * @param from
	 *            The tangible state which all the arcs in the linked list are
	 *            from.
	 * @param arclist
	 *            The list of arcs.
	 * @param dataFile
	 *            The file that reachability graph data needs to be written to.
	 * @param writeTransitionsNo
	 * @throws ImmediateAbortException
	 */
	private static void writeTransitions(MarkingState from, LinkedList arclist,
			RandomAccessFile dataFile, boolean writeTransitionsNo)
			throws ImmediateAbortException {
		TransitionRecord newTransition;
		Iterator iterator = arclist.iterator();
		ArcListElement current;

		while (iterator.hasNext()) {
			current = (ArcListElement) iterator.next();
			if (writeTransitionsNo) {
				newTransition = new TransitionRecord(from.getIDNum(),
						current.getTo(), current.getRate(),
						current.transitionNo);
				try {
					newTransition.write1(dataFile);
					// System.out.println("From: " + from.getIDNum() + ":(" +
					// from + ")" + " To: " + current.getTo() + " via " +
					// current.transitionNo+ " Rate: " + current.getRate());
				} catch (IOException e) {
					System.err
							.println("IO error when writing transitions to file.");
					throw new ImmediateAbortException();
				}
			} else {
				newTransition = new TransitionRecord(from.getIDNum(),
						current.getTo(), current.getRate());
				try {
					newTransition.write(dataFile);
					// System.out.println("From: " + from.getIDNum() + ":(" +
					// from + ")" + " To: " + current.getTo() + " Rate: " +
					// current.getRate());
				} catch (IOException e) {
					System.err
							.println("IO error when writing transitions to file.");
					throw new ImmediateAbortException();
				}
			}
		}
	}

	/**
	 * createRGFile() Creates a reachability graph file containing all the
	 * tangible states that were found during state space exploration and also
	 * all the transitions between them.
	 * 
	 * @param transource
	 *            A file containing all the transitions (arcs) between tangible
	 *            states.
	 * @param destination
	 *            The file to create as a reachability graph file. The file
	 *            should already contain a list of all the tangible states and
	 *            be in the correct position for writing the record of all the
	 *            transitions between them.
	 * @param statesize
	 *            The size of each state array
	 * @param states
	 *            The number of tangible states found
	 * @param transitions
	 *            The number of transitions recorded
	 * @param withTransitions
	 */
	private static void createRGFile(File transource,
			RandomAccessFile destination, int statesize, int states,
			int transitions, boolean withTransitions) {
		RandomAccessFile transinputFile;
		TransitionRecord currenttran = new TransitionRecord();
		ReachabilityGraphFileHeader header;
		try {
			transinputFile = new RandomAccessFile(transource, "r");

			// The destination file actually already exists with a blank file
			// header as a placeholder and all the tangible states written in
			// order. The file pointer should already be at the end of the file
			// (i.e. after the last tangible state that's been written to the
			// file.
			// Make a note of the file pointer as this is where the transition
			// records begin.
			long offset = destination.getFilePointer();
			// Now copy over all the transitions
			System.out.println("Creating reachability graph, please wait...");
			for (int count = 0; count < transitions; count++) {
				// System.out.print("Recording arc " + (count+1) + " of " +
				// transitions +".\r");
				if (withTransitions) {
					currenttran.read1(transinputFile);
					currenttran.write1(destination);
				} else {
					currenttran.read(transinputFile);
					currenttran.write(destination);
				}
			}

			// Make a note of the transition record size and fill in all the
			// details in the file header.
			int recordsize = currenttran.getRecordSize();
			destination.seek(0); // Go back to the start of the file
			header = new ReachabilityGraphFileHeader(states, statesize,
					transitions, recordsize, offset);
			header.write(destination);

			// Done so close all the files.
			transinputFile.close();
			destination.close();
		} catch (EOFException e) {
			System.err.println("EOFException");
		} catch (IOException e) {
			System.out.println("Could not create output file.");
			e.getMessage();
		}
	}

	private static void printArray(boolean[] array) {
		int rows = array.length;
		System.out.print("Elements as follows: ");
		for (int i = 0; i < rows; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	private static void printArray(int[] array) {
		int rows = array.length;
		System.out.print("Elements as follows: ");
		for (int i = 0; i < rows; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	private static void printArray(State state) {
		int[] array = state.getState();
		printArray(array);
	}

}
