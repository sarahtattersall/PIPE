/*
 * MinimalSiphons.java
 */
package pipe.modules.minimalSiphons;

import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.interfaces.IModule;
import pipe.utilities.math.Matrix;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.PetriNetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Vector;


/**
 * MinimalSiphons computes minimal siphons and minimals traps of a Petri Net.
 * This module implements the algorithm presented in:
 * R. Cordone, L. Ferrarini, L. Piroddi, "Some Results on the Computation of
 * Minimal Siphons in Petri Nets"; Proceedings of the 42nd IEEE Conference on
 * Decision and Control, pp 3754-3759, Maui, Hawaii (USA), December 2003.
 * TODO:
 * a) Try to eliminate extra check for siphons to be mininal, without this
 * extra check non minimal siphons are computed.
 * b) Optimize!
 *
 * @author Pere Bonet
 */
public class MinimalSiphons implements IModule {


    /* */
    private static final String MODULE_NAME = "Minimal Siphons And Minimal Traps";

    /**
     * Generate button click handler
     */
    private final ActionListener analyseButtonClick = new ActionListener() {

        public void actionPerformed(ActionEvent arg0) {
            PetriNetView sourceDataLayer = sourceFilePanel.getDataLayer();
            String s = "<h2>Minimal Siphons and Minimal Traps</h2>";

            if (sourceDataLayer == null) {
                return;
            }

            if (!sourceDataLayer.hasPlaceTransitionObjects()) {
                s += "No Petri net objects defined!";
            } else {
                try {

                    PNMLWriter.saveTemporaryFile(sourceDataLayer, this.getClass().getName());

                    s += analyse(sourceDataLayer);
                    results.setEnabled(true);
                } catch (OutOfMemoryError oome) {
                    System.gc();
                    results.setText("");
                    s = "Memory error: " + oome.getMessage();

                    s += "<br>Not enough memory. Please use a larger heap size." + "<br>" + "<br>Note:"
                            + "<br>The Java heap size can be specified with the -Xmx option."
                            + "<br>E.g., to use 512MB as heap size, the command line looks like this:"
                            + "<br>java -Xmx512m -classpath ...\n";
                    results.setText(s);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    s = "<br>Error" + e.getMessage();
                    results.setText(s);
                    return;
                }
            }
            results.setText(s);
        }
    };

    private PetriNetView _pnmlData;

    private PetriNetChooserPanel sourceFilePanel;

    private ResultsHTMLPane results;

    /**
     * @return The module name
     */
    public String getName() {
        return MODULE_NAME;
    }

    /**
     * Call the methods that find the minimal traps and siphons.
     */
    public void start() {
        // Check if this net is a CGSPN. If it is, then this
        // module won't work with it and we must convert it.
        //        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        //        if(pnmlData.getTokenViews().size() > 1)
        //        {
        //            Expander expander = new Expander(pnmlData);
        //            pnmlData = expander.unfoldOld();
        //        }
        //        // Keep a reference to the p-n for other methods in this class
        //        this._pnmlData = pnmlData;
        //
        //        // Build interface
        //        EscapableDialog guiDialog =
        //                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);
        //
        //        // 1 Set layout
        //        Container contentPane = guiDialog.getContentPane();
        //        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        //
        //        // 2 Add file browser
        //        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        //        contentPane.add(sourceFilePanel);
        //
        //        // 3 Add results pane
        //        results = new ResultsHTMLPane(pnmlData.getPNMLName());
        //        contentPane.add(results);
        //
        //        // 4 Add button
        //        contentPane.add(new ButtonBar("Generate", analyseButtonClick,
        //                                      guiDialog.getRootPane()));
        //
        //        // 5 Make window fit contents' preferred size
        //        guiDialog.pack();
        //
        //        // 6 Move window to the middle of the screen
        //        guiDialog.setLocationRelativeTo(null);
        //
        //        guiDialog.setVisible(true);
    }

    /**
     * @param pnmlData
     * @return
     */
    String analyse(PetriNetView pnmlData) throws Exception {

        Date start_time = new Date(); // start timer for program execution

        String output = "<h3>Minimal siphons</h3>";
        // compute siphons
        Vector<boolean[]> siphons = findAllMinimalSiphons(new PetriNet(
                pnmlData.getActiveTokenView().getForwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces()),
                pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces())),
                new SetOfPlaces(pnmlData.numberOfPlaces()));
        output += toString(siphons);

        output += "<h3>Minimal traps</h3>";
        // now, compute traps switching forwards and backwards incidence matrices
        Vector<boolean[]> traps = findAllMinimalSiphons(new PetriNet(
                pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces()),
                pnmlData.getActiveTokenView().getForwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces())),
                new SetOfPlaces(pnmlData.numberOfPlaces()));
        output += toString(traps);

        Date stop_time = new Date();
        double etime = (stop_time.getTime() - start_time.getTime()) / 1000.;
        System.out.println("Minimal Siphons output: " + output + "<br>Analysis time: " + etime + "s");
        return output + "<br>Analysis time: " + etime + "s";
    }

    private String toString(Vector<boolean[]> vector) {
        String s = "";

        if (vector.size() == 0) {
            return "None found.<br>";
        }

        for (boolean[] element : vector) {
            s += "{";
            for (int i = 0; i < element.length; i++) {
                s += element[i] ? _pnmlData.getPlace(i).getName() + ", " : "";
            }
            // replace the last occurance of ", "
            StringBuilder b = new StringBuilder(s);
            if (s.contains(",")) {
                b.replace(s.lastIndexOf(","), s.lastIndexOf(",") + 1, "");
                s = b.toString();
            }
            //s = s.substring(0, (s.length() - 2)) + "}<br>";
            s = s + "}<br>";
        }
        return s;
    }

    /**
     * @param pnmlData
     * @return
     */
    public Vector<boolean[]> getMinimalSiphons(PetriNetView pnmlData) throws Exception {
        return findAllMinimalSiphons(new PetriNet(
                pnmlData.getActiveTokenView().getForwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces()),
                pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces())),
                new SetOfPlaces(pnmlData.numberOfPlaces()));
    }

    /**
     * @param pnmlData
     * @return
     */
    public Vector<boolean[]> getMinimalTraps(PetriNetView pnmlData) throws Exception {
        return findAllMinimalSiphons(new PetriNet(
                pnmlData.getActiveTokenView().getBackwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces()),
                pnmlData.getActiveTokenView().getForwardsIncidenceMatrix(pnmlData.getModel().getArcs(),
                        pnmlData.getModel().getTransitions(), pnmlData.getModel().getPlaces())),
                new SetOfPlaces(pnmlData.numberOfPlaces()));
    }

    /**
     * findAllMinimalSiphons()
     * Finds all minimal siphons in a given Petri Net that contain a specific
     * set of places.
     *
     * @param g      A Petri Net
     * @param Ptilde A set of places that each siphon must contain
     *               returns       A vector containg all minimal siphons found
     * @return
     */
    Vector<boolean[]> findAllMinimalSiphons(PetriNet g, SetOfPlaces Ptilde) {
        Vector<boolean[]> E; // contains all minimal siphons found
        SetOfPlaces S = new SetOfPlaces(Ptilde.size()); // a siphon

        // Step 1
        E = new Vector();

        // Step 2
        // check if there is a place with an empty pre-set
        int p = g.placeWithEmptyInputSet();
        while (Ptilde.isEmpty() && (p != -1)) {

            // Step 3
            S.add(p);
            E.add(S.getPlaces());
            // the Petri Net can be reduced by eliminating p
            g = reduceNet(g, Ptilde.getPlacesMinus(p));
            // check if there is another place with an empty pre-set
            p = g.placeWithEmptyInputSet();
        }

        // Step 4
        // Find a generic siphon
        SetOfPlaces Stilde = findSiphon(g, Ptilde);

        // Step 5
        if (Stilde.isEmpty()) {
            // No siphon has been found, return E
            return E;
        }

        // Step 6
        // Find a minimal siphon contained in the generic siphon
        S = findMinimalSiphon(g, Stilde, Ptilde);

        // problem!
        // here should be simply E.add(S.places());
        // but without this extra check, non minimal siphons were computed.
        // So, a we find a new minimal siphon (S2) contained in the generic siphon
        // with no place constraints and we check if S contains S2
        SetOfPlaces S2 = new SetOfPlaces(Ptilde.size());
        S2 = findMinimalSiphon(g, Stilde, new SetOfPlaces(Ptilde.size()));
        if (!S.containsSet(S2)) {
            // S is minimal!
            E.add(S.getPlaces());
        }

        // Step 7
        SetOfPlaces Pnew = S.minus(Ptilde); //Pnew = S - P~
        SetOfPlaces Pold = new SetOfPlaces(S.size()); // Pold = {}

        // Step 8
        // Decompose the problem, and for each sub-problem (corresponding to a
        // specific sub-net and some place constraints) apply the same procedure
        // from Step 1.
        PetriNet gp;
        Vector<boolean[]> Ep;
        while (!Pnew.isEmpty()) {

            //Step 9
            int place = Pnew.removeTransition();
            gp = reduceNet(g, g.P.getPlacesMinus(place));
            Ep = findAllMinimalSiphons(gp, Ptilde.union(Pold));
            E.addAll(Ep);
            Pold.add(place);
        }
        return E;
    }

    /**
     * findSiphon()
     * Finds a generic siphon in a givenPetri Net g. This siphon will contain the
     * given set of places Ptilde.
     *
     * @param g      A Petri Net
     * @param Ptilde A place constraint for the resultant siphon
     * @return
     * @returns A generic siphon which contains the given set of places
     */
    private SetOfPlaces findSiphon(PetriNet g, final SetOfPlaces Ptilde) {

        do {
            boolean[] placePreSet;

            // Step 1
            // check if exists a place that is element of the union of P and Ptilde
            // such that exists a transition t in its pre-set that is not an element
            // of P's post-set
            for (int place = 0; place < g.P.size(); place++) {
                if (g.P.contains(place) && Ptilde.contains(place)) {
                    // check each place that belongs to P and to ~P
                    placePreSet = g.getPlacePreSet(place);
                    for (int transition = 0; transition < placePreSet.length; transition++) {
                        if (placePreSet[transition] && !g.PPostSetcontains(transition)) {
                            // There are no possible siphons that contain Ptilde, so
                            // findSiphon ends with empty outcome
                            //                     System.out.println("findSiphon returns an empty siphon");//dbg
                            return new SetOfPlaces(Ptilde.size(), false);
                        }
                    }
                }
            }

            // Step 2
            // check if there is a place which can be discarded
            int placeToEliminate = eliminablePlace_FS(g, Ptilde);
            if (placeToEliminate != -1) {
                // Step 3
                // perform the Petri Net reduction and go to Step 1
                g = reduceNet(g, g.P.getPlacesMinus(placeToEliminate));
            } else {
                //            g.P.debug("findSiphon result is ");//dbg
                return new SetOfPlaces(g.P);
            }
        } while (true);
    }

    // Step 2) of algorithm "FindSiphon"
    private int eliminablePlace_FS(final PetriNet g, final SetOfPlaces Ptilde) {
        boolean[] placePreSet;

        // check if exists a place that is element of P minus Ptilde
        // such that exists a transition t in its pre-set that is not an element
        // of P's post-set
        for (int place = 0; place < Ptilde.size(); place++) {
            if (g.P.contains(place) && !Ptilde.contains(place)) {
                placePreSet = g.getPlacePreSet(place);
                for (int transition = 0; transition < placePreSet.length; transition++) {
                    if (placePreSet[transition] && !g.PPostSetcontains(transition)) {
                        return place; //
                    }
                }
            }
        }
        return -1; // no place can be eliminated
    }

    /**
     * findMinimalSiphon()
     * Computes a mininal siphon in Petri Net g such that is contained in Stilde
     * and contains Ptilde, if exists.
     *
     * @param g      A Petri Net
     * @param Stilde A general siphon
     * @param Ptilde A set of places that the minimal siphon must contain
     * @return A minimal siphon
     */
    private SetOfPlaces findMinimalSiphon(PetriNet g, final SetOfPlaces Stilde, final SetOfPlaces Ptilde) {
        SetOfPlaces StildeCopy = new SetOfPlaces(Stilde);
        int placeToEliminate;
        boolean[] placePostSet;
        boolean[] transitionPostSet;
        boolean[] transitionPreSet;

        // Step 1
        do {
            placeToEliminate = eliminablePlace_FMS(g, StildeCopy, Ptilde);
            if (placeToEliminate != -1) {
                // Step 2
                // placeToEliminate can be removed from the given siphon
                StildeCopy.remove(placeToEliminate);
            }
        } while (placeToEliminate != -1);

        do {

            // Step 3
            if (g.P.containsSet(StildeCopy)) {
                g = reduceNet(g, StildeCopy.getPlaces());
            }

            // Step 4
            SetOfPlaces Pnew = g.P.minus(Ptilde);

            int newPlaceToEliminate;
            PetriNet gp;
            SetOfPlaces Sp;

            do {
                // Step 5
                if (Pnew.isEmpty()) {
                    //               StildeCopy.debug("[FIND_MINIMAL_SIPHON] ~S"); //dbg
                    return StildeCopy;
                }

                // Step 6
                newPlaceToEliminate = Pnew.removeTransition();
                gp = reduceNet(g, g.P.getPlacesMinus(newPlaceToEliminate));
                Sp = findSiphon(gp, Ptilde);
            } while (Sp.isEmpty());
            StildeCopy = Sp;
        } while (true);
    }

    // Step 1) of algorithm "FindMinimalSiphon"
    // returns the index of a place that can be eliminated or -1 if there is no
    // such place
    private int eliminablePlace_FMS(final PetriNet g, final SetOfPlaces Stilde, final SetOfPlaces Ptilde) {

        int placeToEliminate = -1;
        boolean[] placePostSet;
        boolean[] transitionPostSet;
        boolean[] transitionPreSet;

        for (int place = 0; place < Ptilde.size(); place++) {
            if (g.P.contains(place) && !Ptilde.contains(place) &&
                    Stilde.contains(place)) {
                // place 'place' is an element of the set P minus Ptilde
                placePostSet = g.getPlacePostSet(place);
                boolean eliminable = true;
                for (int transition = 0; transition < placePostSet.length; transition++) {
                    if (placePostSet[transition]) {
                        transitionPreSet = g.getTransitionPreSet(transition);
                        transitionPostSet = g.getTransitionPostSet(transition);

                        boolean containsCurrenPlace = false;
                        if ((transitionPreSet[place]) && Stilde.contains(place)) {
                            for (int currentPlace = 0; currentPlace < transitionPreSet.length; currentPlace++) {
                                if ((transitionPreSet[currentPlace]) &&
                                        Stilde.contains(currentPlace) &&
                                        currentPlace != place) {
                                    // transition pre-set intersection Stilde contains
                                    // place 'place'
                                    containsCurrenPlace = true;
                                    break;
                                }
                            }
                        }

                        boolean tPostSetIntersectionStildeIsEmpty = true;
                        for (int currentPlace = 0; currentPlace < transitionPostSet.length; currentPlace++) {
                            if (transitionPostSet[currentPlace] && Stilde.contains(currentPlace)) {
                                tPostSetIntersectionStildeIsEmpty = false;
                                // transition post-set intersection Stilde is not empty
                                break;
                            }
                        }

                        if (!containsCurrenPlace && !tPostSetIntersectionStildeIsEmpty) {
                            // place is not eliminable
                            eliminable = false;
                            break;
                        }
                    }
                }
                if (eliminable) {
                    return place; // eliminable place
                }
            }
        }
        return -1; // there is no eliminable place
    }

    /**
     * reduceNet()
     * Simplifies a given PetriNetViewComponent g discarding all places not in Ptilde and the
     * arcs connected with them.
     *
     * @param g      A Petri Net
     * @param Ptilde A set of places
     * @return
     * @returns A simplified Petri Net
     */
    private PetriNet reduceNet(final PetriNet g, final boolean[] Ptilde) {
        PetriNet gtilde = new PetriNet(g); // result

        int transitionCount = g.T.size();
        boolean[] transitionPreSet;
        boolean[] transitionPostSet;

        // for each transition in T, check if it can be discarded
        for (int transition = 0; transition < transitionCount; transition++) {
            if (g.T.contains(transition)) {
                transitionPreSet = g.getTransitionPreSet(transition);
                transitionPostSet = g.getTransitionPostSet(transition);
                boolean remove = true;
                for (int place = 0; place < Ptilde.length; place++) {
                    if ((transitionPreSet[place] || transitionPostSet[place]) && Ptilde[place]) {
                        // the intersection Ptilde and the union of the transition's
                        // pre-set and the transition's post-set is not empty, so this
                        // transition can't be discarded
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    gtilde.T.remove(transition);
                }
            }
        }

        // discard each place p that isn't an element of the set of places Ptilde
        // and its connecting arcs.
        for (int place = 0; place < Ptilde.length; place++) {
            if (!Ptilde[place]) {
                gtilde.reduce(place);
            }
        }

        //      System.out.println("\nreduceNet:"); //dbg
        //      gtilde.debug(); //dbg
        return gtilde;
    }

    // used for debug
    private void print(String string, boolean[] b) {
        System.out.println(string);
        for (boolean aB : b) {
            System.out.print(aB + " ");
        }
        System.out.println();
    }

    // helper class.
    // TODO: optimize it
    public class PetriNet {
        final SetOfPlaces P;                     // set of places

        final SetOfTransitions T;                // set of transitions

        final Matrix _forwardsIncidenceMatrix;  // input incidence matrix

        final Matrix _backwardsIncidenceMatrix; // output incidence matrix

        boolean[] PPostSet;                // union of the post-set of each place


        // constructor
        public PetriNet(int[][] _forwardsIncidenceMatrix, int[][] _backwardsIncidenceMatrix) {
            this._forwardsIncidenceMatrix = new Matrix(_forwardsIncidenceMatrix);
            this._backwardsIncidenceMatrix = new Matrix(_backwardsIncidenceMatrix);
            P = new SetOfPlaces(this._forwardsIncidenceMatrix.getRowDimension(), true);
            T = new SetOfTransitions(this._forwardsIncidenceMatrix.getColumnDimension(), true);
            PPostSet = computePPostSet(P, T, this._forwardsIncidenceMatrix);
        }

        // computes the union of each transition post-set
        private boolean[] computePPostSet(SetOfPlaces P, SetOfTransitions T, Matrix forwardsIncidenceMatrix) {
            boolean[] result = new boolean[T.size()];

            for (int i = 0; i < result.length; i++) {
                result[i] = false;
            }

            for (int transition = 0; transition < result.length; transition++) {
                result[transition] = false;
                for (int place = 0; place < P.size(); place++) {
                    if (forwardsIncidenceMatrix.get(place, transition) > 0) {
                        result[transition] = true;
                        break;
                    }
                }
            }
            return result;
        }


        // constructor
        private PetriNet(PetriNet g) {
            this(g._forwardsIncidenceMatrix, g._backwardsIncidenceMatrix);
        }


        // constructor
        private PetriNet(Matrix _forwardsIncidenceMatrix, Matrix _backwardsIncidenceMatrix) {
            this._forwardsIncidenceMatrix = _forwardsIncidenceMatrix.copy();
            this._backwardsIncidenceMatrix = _backwardsIncidenceMatrix.copy();
            P = new SetOfPlaces(this._forwardsIncidenceMatrix.getRowDimension(), true);
            T = new SetOfTransitions(this._forwardsIncidenceMatrix.getColumnDimension(), true);
            PPostSet = computePPostSet(P, T, this._forwardsIncidenceMatrix);
        }

        // returns the index of a place which its pre-set is empty
        private int placeWithEmptyInputSet() {
            boolean[] placePreSet;
            boolean hasEmptyPreSet;

            for (int place = 0; place < P.size(); place++) {
                if (!P.contains(place)) {
                    continue;
                }
                placePreSet = this.getPlacePreSet(place);
                hasEmptyPreSet = true;
                for (boolean aPlacePreSet : placePreSet) {
                    if (aPlacePreSet) {
                        hasEmptyPreSet = false;
                        break;
                    }
                }
                if (hasEmptyPreSet) {
                    return place;
                }
            }
            return -1;
        }

        // returns the pre-set of a given place
        private boolean[] getPlacePreSet(int place) {
            int[] column = _forwardsIncidenceMatrix.getColumn(place);
            boolean[] result = new boolean[column.length];

            for (int i = 0; i < column.length; i++) {
                result[i] = (column[i] > 0);
            }
            return result;
        }

        // returns the post-set of a given place
        private boolean[] getPlacePostSet(int place) {
            int[] column = _backwardsIncidenceMatrix.getColumn(place);
            boolean[] result = new boolean[column.length];

            for (int i = 0; i < column.length; i++) {
                result[i] = (column[i] > 0);
            }
            return result;
        }

        // returns the pre-set of a given transition
        private boolean[] getTransitionPreSet(int transition) {
            int[] row = _backwardsIncidenceMatrix.getRow(transition);
            boolean[] result = new boolean[row.length];

            for (int i = 0; i < row.length; i++) {
                result[i] = (row[i] > 0);
            }
            return result;
        }

        // returns the post-set of a given transition
        private boolean[] getTransitionPostSet(int transition) {
            int[] column = _forwardsIncidenceMatrix.getRow(transition);
            boolean[] result = new boolean[column.length];

            for (int i = 0; i < column.length; i++) {
                result[i] = (column[i] > 0);
            }
            return result;
        }

        // return the union of each transition post-set
        private boolean PPostSetcontains(int transition) {
            return PPostSet[transition];
        }

        // removes a place from P and clears its columns in forwards and backwards
        // incidence matrices
        private void reduce(int place) {
            P.remove(place);
            _forwardsIncidenceMatrix.clearColumn(place);
            _backwardsIncidenceMatrix.clearColumn(place);
            // P's post-set must be computed again!
            PPostSet = computePPostSet(P, T, _backwardsIncidenceMatrix);
        }

        // prints info for debug
        private void debug() {
            P.debug("P");
            T.debug("T");
            System.out.println("");
            System.out.print("Forwards Incidence Matrix");
            _forwardsIncidenceMatrix.print(_forwardsIncidenceMatrix.getColumnDimension(), 0);
            System.out.print("Backwards Incidence Matrix");
            _backwardsIncidenceMatrix.print(_backwardsIncidenceMatrix.getColumnDimension(), 0);

            System.out.print("P PostSet = { ");
            for (int i = 0; i < PPostSet.length; i++) {
                System.out.print(PPostSet[i] ? i + " " : "");
            }
            System.out.println("}");
        }

    }

    // helper class.
    // TODO: optimize it
    public class SetOfPlaces {
        final boolean[] P; // set of places


        // constructor
        public SetOfPlaces(int length) {
            this(length, false);
        }


        // constructor
        SetOfPlaces(int lenght, boolean flag) {
            P = new boolean[lenght];

            for (int place = 0; place < lenght; place++) {
                P[place] = flag;
            }
        }


        // constructor
        SetOfPlaces(SetOfPlaces set) {
            P = new boolean[set.size()];

            System.arraycopy(set.P, 0, P, 0, P.length);
        }

        // returns size of P
        private int size() {
            return P.length;
        }

        // returns true if each element of P is false
        private boolean isEmpty() {
            for (boolean aP : P) {
                if (aP) {
                    return false;
                }
            }
            return true;
        }

        // sets to true position t of P
        private void add(int t) {
            P[t] = true;
        }

        // returns a copy of P
        private boolean[] getPlaces() {
            return P.clone();
        }

        // returns a copy of P with position i set to false
        private boolean[] getPlacesMinus(int i) {
            boolean[] result = P.clone();
            result[i] = false;
            return result;
        }

        // returns a set of places S so that S = P - Ptilde
        private SetOfPlaces minus(SetOfPlaces Ptilde) {
            SetOfPlaces result = new SetOfPlaces(P.length);
            for (int i = 0; i < result.size(); i++) {
                result.P[i] = P[i];
                if (Ptilde.P[i]) {
                    result.P[i] = false;
                }
            }
            return result;
        }

        // returns index of the first position of P that is true and removes it
        // from P; if P is empty, returns -1
        private int removeTransition() {
            for (int place = 0; place < P.length; place++) {
                if (P[place]) {
                    P[place] = false;
                    return place;
                }
            }
            return -1;
        }

        // returns a set of places S so that S = P U Pold
        private SetOfPlaces union(SetOfPlaces Pold) {
            SetOfPlaces result = new SetOfPlaces(Pold.size());
            for (int i = 0; i < result.size(); i++) {
                result.P[i] = P[i] || Pold.P[i];
            }
            return result;
        }

        // sets to false position i of P
        private void remove(int i) {
            P[i] = false;
        }

        // returns true if P strictly contains Stilde
        private boolean containsSet(SetOfPlaces Stilde) {
            boolean containsSet = false;
            for (int place = 0; place < P.length; place++) {
                if (Stilde.contains(place) && !this.contains(place)) {
                    return false;
                }
                if (!Stilde.contains(place) && this.contains(place)) {
                    containsSet = true; //OK, Stilde and this are not equal
                }
            }
            return containsSet;
        }

        //returns true if position i of P is true
        private boolean contains(int i) {
            return P[i];
        }

        // used for debug purposes
        private void debug(String s) {
            System.out.print(s + " = { ");
            for (int j = 0; j < P.length; j++) {
                System.out.print(P[j] ? j + " " : "");
            }
            System.out.println("}");
        }

    }

    // helper class.
    // TODO: optimize it
    private class SetOfTransitions {
        final boolean[] T; // set of places


        // constructor
        SetOfTransitions(int length, boolean flag) {
            T = new boolean[length];

            for (int j = 0; j < length; j++) {
                T[j] = flag;
            }
        }

        // returns size of T
        private int size() {
            return T.length;
        }

        //returns true if position i of T is true
        private boolean contains(int i) {
            return T[i];
        }

        // sets to false position i of T
        private void remove(int transition) {
            T[transition] = false;
        }

        // used for debug purposes
        private void debug(String s) {
            System.out.print(s + " = { ");
            for (int j = 0; j < T.length; j++) {
                System.out.print(T[j] ? j + " " : "");
            }
            System.out.println("}");
        }

    }
}
