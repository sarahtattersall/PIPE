package pipe.views;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pipe.common.dataLayer.StateGroup;
import pipe.controllers.PetriNetController;
import pipe.exceptions.TokenLockedException;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.models.*;
import pipe.models.interfaces.IObserver;
import pipe.petrinet.*;
import pipe.utilities.Copier;
import pipe.utilities.math.RandomNumberGenerator;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.builder.*;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.Parameter;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.Observable;


/*

 * @author yufei wang(minor change)
 * 		Steve Doubleday (Oct 2013):  refactored to use TokenSetController for access to TokenViews
 */
public class PetriNetView extends Observable implements Cloneable, IObserver, Serializable, Observer {
    protected Map<Place, PlaceView> _placeViews = new HashMap<Place, PlaceView>();
    private Map<Transition, TransitionView> _transitionViews = new HashMap<Transition, TransitionView>();
    private Map<Arc, ArcView> _arcViews = new HashMap<Arc, ArcView>();
    private Map<Arc, InhibitorArcView> _inhibitorViews = new HashMap<Arc, InhibitorArcView>();
    private Map<Annotation, AnnotationNote> _labels = new HashMap<Annotation, AnnotationNote>();
    private Set<RateParameter> _rateParameters = new HashSet<RateParameter>();


    private Vector<Vector<String>> functionRelatedPlaces;

    private LinkedList<MarkingView>[] _initialMarkingVector;
    private LinkedList<MarkingView>[] _currentMarkingVector;
    private int[] _capacityMatrix;
    private int[] _priorityMatrix;
    private boolean[] _timedMatrix;
    private LinkedList<MarkingView>[] _markingVectorAnimationStorage;
    private static boolean _currentMarkingVectorChanged = true;
    private Hashtable _arcsMap;
    private Hashtable _inhibitorsMap;
    private ArrayList _stateGroups;
    private final HashSet _rateParameterHashSet = new HashSet();
    private PetriNet _model;
    private TokenSetController _tokenSetController = new TokenSetController();


    public PetriNetView(String pnmlFileName) {
        _model = new PetriNet();
        _model.registerObserver(this);
        PNMLTransformer transform = new PNMLTransformer();
        File temp = new File(pnmlFileName);
        _model.setPnmlName(temp.getName());
        createFromPNML(transform.transformPNML(pnmlFileName));
    }

    public PetriNetView(PetriNetController petriNetController, PetriNet model) {
        _tokenSetController.addObserver(this);
        _model = model;
        model.registerObserver(this);
        _model.registerObserver(this);
    }


    @SuppressWarnings("unchecked")
    public PetriNetView clone() {
        PetriNetView newClone;
        try {
            newClone = (PetriNetView) super.clone();
            newClone._placeViews = deepCopy(_placeViews);
            newClone._transitionViews = deepCopy(_transitionViews);
            newClone._arcViews = deepCopy(_arcViews);
            newClone._inhibitorViews = deepCopy(_inhibitorViews);
            newClone._labels = deepCopy(_labels);
            newClone._tokenSetController =
                    (TokenSetController) Copier.deepCopy(_tokenSetController); //TODO test this SJD
//            newClone._tokenViews = (LinkedList<TokenView>) Copier.deepCopy(_tokenViews); // SJD
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
        return newClone;
    }

    public boolean updateOrReplaceTokenViews(LinkedList<TokenView> tokenViews) throws TokenLockedException {
        return _tokenSetController.updateOrReplaceTokenViews(tokenViews);
    }

    public LinkedList<TokenView> getTokenViews() {
        return (LinkedList<TokenView>) _tokenSetController.getTokenViews();
    }

    public LinkedList<TokenView> getAllTokenViews() {
        return (LinkedList<TokenView>) _tokenSetController.getAllTokenViews();
    }

    public TokenView getActiveTokenView() {
        return _tokenSetController.getActiveTokenView();
    }

    public void setActiveTokenView(TokenView tc) {
        _tokenSetController.setActiveTokenView(tc.getID());
        updatePlaceViewsWithActiveToken(tc);
    }

    protected void updatePlaceViewsWithActiveToken(TokenView tc) {
        for (PlaceView p : _placeViews.values()) {
            p.setActiveTokenView(tc);
        }
    }

    public void lockTokenClass(String id) {
        lockToken(id, true);
    }

    public void unlockTokenClass(String id) {
        lockToken(id, false);
    }

    private void lockToken(String id, boolean lock) {
        TokenView tc = _tokenSetController.getTokenView(id);
        if (tc != null) {
            if (lock) {
                tc.incrementLock();
            } else {
                tc.decrementLock();
            }
        }
    }

    public int positionInTheList(String tokenClassID, LinkedList<MarkingView> markingViews) {
        int size = markingViews.size();
        for (int i = 0; i < size; i++) {
            MarkingView m = markingViews.get(i);
            if (m.getToken().getID().equals(tokenClassID)) {
                return i;  // SJD update
            }
        }
        return -1;
    }

    public TokenView getTokenClassFromID(String id) {
        return _tokenSetController.getTokenView(id);
    }

    //TODO: need to clone values!
    private static <K extends PetriNetComponent, V extends PetriNetViewComponent>  Map<K,V> deepCopy(Map<K,V> original) {
        Map<K,V> result =  new HashMap<K, V>();
        for (Map.Entry<K, V> entry : original.entrySet())
        {
            result.put(entry.getKey(), entry.getValue());
        }

//        ArrayList result = (ArrayList) original.clone();
//        ListIterator listIter = result.listIterator();
//
//        while (listIter.hasNext()) {
//            PetriNetViewComponent pnObj = (PetriNetViewComponent) listIter.next();
//            listIter.set(pnObj.clone());
//        }
        return result;
    }

    private void addPlace(PlaceView placeView) {
        boolean unique = true;

        if (placeView != null) {
            if (placeView.getId() != null && placeView.getId().length() > 0) {
                for (PlaceView _placeView : _placeViews.values()) {
                    if (placeView.getId().equals(_placeView.getId())) {
                        unique = false;
                    }
                }
            } else {
                String id = null;
                if (_placeViews != null && _placeViews.size() > 0) {
                    int no = _placeViews.size();
                    do {
                        for (PlaceView _placeView : _placeViews.values()) {
                            id = "P" + no;
                            if (_placeView != null) {
                                if (id.equals(_placeView.getId())) {
                                    unique = false;
                                    no++;
                                } else {
                                    unique = true;
                                }
                            }
                        }
                    } while (!unique);
                } else {
                    id = "P0";
                }

                if (id != null) {
                    placeView.setId(id);
                } else {
                    placeView.setId("error");
                }
            }
            placeView.setActiveTokenView(_tokenSetController.getActiveTokenView());
//            placeView.setActiveTokenView(_activeTokenView); // SJD
//            _placeViews.add(placeView);
            setChanged();
            setMatrixChanged();
            notifyObservers(placeView);
        }
    }

    private void addAnnotation(AnnotationNote labelInput) {
        boolean unique = true;
//        _labels.add(labelInput);
        setChanged();
        notifyObservers(labelInput);
    }

    private void addAnnotation(RateParameter rateParameterInput) {
        boolean unique = true;
//        _rateParameters.add(rateParameterInput);
        setChanged();
        notifyObservers(rateParameterInput);
    }

    private void addTransition(TransitionView transitionViewInput) {
        boolean unique = true;

        if (transitionViewInput != null) {
            if (transitionViewInput.getId() != null && transitionViewInput.getId().length() > 0) {
                for (TransitionView _transitionView : _transitionViews.values()) {
                    if (transitionViewInput.getId().equals(_transitionView.getId())) {
                        unique = false;
                    }
                }
            } else {
                String id = null;
                if (_transitionViews != null && _transitionViews.size() > 0) {
                    int no = _transitionViews.size();
                    do {
                        for (TransitionView _transitionView : _transitionViews.values()) {
                            id = "T" + no;
                            if (_transitionView != null) {
                                if (id.equals(_transitionView.getId())) {
                                    unique = false;
                                    no++;
                                } else {
                                    unique = true;
                                }
                            }
                        }
                    } while (!unique);
                } else {
                    id = "T0";
                }

                if (id != null) {
                    transitionViewInput.setId(id);
                } else {
                    transitionViewInput.setId("error");
                }
            }
//            _transitionViews.add(transitionViewInput);
            setChanged();
            setMatrixChanged();
            notifyObservers(transitionViewInput);
        }
    }

    public void addArc(NormalArcView arcViewInput) {
        boolean unique = true;

        if (arcViewInput != null) {
            if (arcViewInput.getId() != null && arcViewInput.getId().length() > 0) {
                for (ArcView _arcView : _arcViews.values()) {
                    if (arcViewInput.getId().equals(_arcView.getId())) {
                        unique = false;
                    }
                }
            } else {
                String id = null;
                if (_arcViews != null && _arcViews.size() > 0) {
                    int no = _arcViews.size();
                    do {
                        for (ArcView _arcView : _arcViews.values()) {
                            id = "A" + no;
                            if (_arcView != null) {
                                if (id.equals(_arcView.getId())) {
                                    unique = false;
                                    no++;
                                } else {
                                    unique = true;
                                }
                            }
                        }
                    } while (!unique);
                } else {
                    id = "A0";
                }
                if (id != null) {
                    arcViewInput.setId(id);
                } else {
                    arcViewInput.setId("error");
                }
            }
//            _arcViews.add(arcViewInput);
            addArcToArcsMap(arcViewInput);

            setChanged();
            setMatrixChanged();
            notifyObservers(arcViewInput);
        }
    }

    public void addArc(InhibitorArcView inhibitorArcViewInput) {
        boolean unique = true;

        if (inhibitorArcViewInput != null) {
            if (inhibitorArcViewInput.getId() != null && inhibitorArcViewInput.getId().length() > 0) {
                for (InhibitorArcView _inhibitorView : _inhibitorViews.values()) {
                    if (inhibitorArcViewInput.getId().equals(_inhibitorView.getId())) {
                        unique = false;
                    }
                }
            } else {
                String id = null;
                if (_inhibitorViews != null && _inhibitorViews.size() > 0) {
                    int no = _inhibitorViews.size();
                    do {
                        for (InhibitorArcView _inhibitorView : _inhibitorViews.values()) {
                            id = "I" + no;
                            if (_inhibitorView != null) {
                                if (id.equals(_inhibitorView.getId())) {
                                    unique = false;
                                    no++;
                                } else {
                                    unique = true;
                                }
                            }
                        }
                    } while (!unique);
                } else {
                    id = "I0";
                }
                if (id != null) {
                    inhibitorArcViewInput.setId(id);
                } else {
                    inhibitorArcViewInput.setId("error");
                }
            }
//            _inhibitorViews.add(inhibitorArcViewInput);
            addInhibitorArcToInhibitorsMap(inhibitorArcViewInput);

            setChanged();
            setMatrixChanged();
            // notifyObservers(arcInput.getBounds());
            notifyObservers(inhibitorArcViewInput);
        }
    }

    private void addArcToArcsMap(NormalArcView arcViewInput) {
        ConnectableView source = arcViewInput.getSource();
        ConnectableView target = arcViewInput.getTarget();
        ArrayList newList;

        if (source != null) {
            if (_arcsMap.get(source) != null) {
                ((ArrayList) _arcsMap.get(source)).add(arcViewInput);
            } else {
                newList = new ArrayList();
                newList.add(arcViewInput);

                _arcsMap.put(source, newList);
            }
        }

        if (target != null) {
            if (_arcsMap.get(target) != null) {
                ((ArrayList) _arcsMap.get(target)).add(arcViewInput);
            } else {
                newList = new ArrayList();
                newList.add(arcViewInput);
                _arcsMap.put(target, newList);
            }
        }
    }

    private void addInhibitorArcToInhibitorsMap(InhibitorArcView inhibitorArcViewInput) {
        ConnectableView source = inhibitorArcViewInput.getSource();
        ConnectableView target = inhibitorArcViewInput.getTarget();
        ArrayList newList;

        if (source != null) {
            if (_inhibitorsMap.get(source) != null) {
                ((ArrayList) _inhibitorsMap.get(source)).add(inhibitorArcViewInput);
            } else {
                newList = new ArrayList();
                newList.add(inhibitorArcViewInput);
                _inhibitorsMap.put(source, newList);
            }
        }

        if (target != null) {
            if (_inhibitorsMap.get(target) != null) {
                ((ArrayList) _inhibitorsMap.get(target)).add(inhibitorArcViewInput);
            } else {
                newList = new ArrayList();
                newList.add(inhibitorArcViewInput);
                _inhibitorsMap.put(target, newList);
            }
        }
    }

    public void addStateGroup(StateGroup stateGroupInput) {
        boolean unique = true;
        String id;
        int no = _stateGroups.size();

        if (stateGroupInput.getId() != null && stateGroupInput.getId().length() > 0) {
            id = stateGroupInput.getId();

            for (Object _stateGroup : _stateGroups) {
                if (id.equals(((StateGroup) _stateGroup).getId())) {
                    unique = false;
                }
            }
        } else {
            unique = false;
        }

        if (!unique) {
            id = "SG" + no;
            for (int i = 0; i < _stateGroups.size(); i++) {
                if (id.equals(((StateGroup) _stateGroups.get(i)).getId())) {
                    id = "SG" + ++no;
                    i = 0;
                }
            }
            stateGroupInput.setId(id);
        }
        _stateGroups.add(stateGroupInput);
    }

    private void addToken(TokenView tokenViewInput) {
        boolean unique = true;

        if (tokenViewInput != null) {
            if (tokenViewInput.getID() != null && tokenViewInput.getID().length() > 0) {
//                for(TokenView _tokenView : _tokenViews)
                for (TokenView _tokenView : _tokenSetController.getAllTokenViews()) {
                    if (tokenViewInput.getID().equals(_tokenView.getID())) {
                        unique = false;
                    }
                }
            } else {
                String id = null;
//                	if(_tokenViews != null && _tokenViews.size() > 0)
                if (_tokenSetController.getAllTokenViews() != null &&
                        _tokenSetController.getAllTokenViews().size() > 0) {
//                    int no = _tokenViews.size();
                    int no = _tokenSetController.getAllTokenViews().size();
                    do {
//                    	for(TokenView _tokenView : _tokenViews)
                        for (TokenView _tokenView : _tokenSetController.getAllTokenViews()) {
                            id = "token" + no;
                            if (_tokenView != null) {
                                if (id.equals(_tokenView.getID())) {
                                    unique = false;
                                    no++;
                                } else {
                                    unique = true;
                                }
                            }
                        }
                    } while (!unique);
                } else {
                    id = "token0";
                }

                if (id != null) {
                    tokenViewInput.setID(id);
                } else {
                    tokenViewInput.setID("error");
                }
            }
            try {
                _tokenSetController.updateOrAddTokenView(tokenViewInput);
            } catch (TokenLockedException e) {
                e.printStackTrace();  // should not happen when PetriNet is first being populated
            }
            setChanged();
            setMatrixChanged();
            notifyObservers(tokenViewInput);
        }
    }

    public void addPetriNetObject(PetriNetViewComponent pn) {
        if (pn instanceof NormalArcView) {
            addArcToArcsMap((NormalArcView) pn);
            addArc((NormalArcView) pn);
        } else if (pn instanceof InhibitorArcView) {
            addInhibitorArcToInhibitorsMap((InhibitorArcView) pn);
            addArc((InhibitorArcView) pn);
        } else if (pn instanceof PlaceView) {
            addPlace((PlaceView) pn);
        } else if (pn instanceof TransitionView) {
            addTransition((TransitionView) pn);
        } else if (pn instanceof AnnotationNote) {
//            _labels.add((AnnotationNote) pn);
        } else if (pn instanceof RateParameter) {
            _rateParameters.add((RateParameter) pn);
            _rateParameterHashSet.add(pn.getName());
        }
    }

    public void removePetriNetObject(PetriNetViewComponent pn) {
        ArrayList attachedArcs;

        try {

            if (pn instanceof ConnectableView) {

                if (_arcsMap.get(pn) != null) {

                    attachedArcs = ((ArrayList) _arcsMap.get(pn));
                    for (int i = attachedArcs.size() - 1; i >= 0; i--) {
                        ((ArcView) attachedArcs.get(i)).delete();
                    }
                    _arcsMap.remove(pn);
                }

                if (_inhibitorsMap.get(pn) != null) {

                    attachedArcs = ((ArrayList) _inhibitorsMap.get(pn));

                    for (int i = attachedArcs.size() - 1; i >= 0; i--) {
                        ((ArcView) attachedArcs.get(i)).delete();
                    }
                    _inhibitorsMap.remove(pn);
                }
            } else if (pn instanceof NormalArcView) {

                ConnectableView attached = ((ArcView) pn).getSource();

                if (attached != null) {
                    ArrayList a = (ArrayList) _arcsMap.get(attached);
                    if (a != null) {
                        a.remove(pn);
                    }

                    attached.removeFromArc((ArcView) pn);
                    if (attached instanceof TransitionView) {
                        ((TransitionView) attached).removeArcCompareObject((ArcView) pn);
                        attached.updateConnected();
                    }
                }

                attached = ((ArcView) pn).getTarget();
                if (attached != null) {
                    if (_arcsMap.get(attached) != null) {
                        ((ArrayList) _arcsMap.get(attached)).remove(pn);
                    }

                    attached.removeToArc((ArcView) pn);
                    if (attached instanceof TransitionView) {
                        ((TransitionView) attached).removeArcCompareObject((ArcView) pn);
                        attached.updateConnected();
                    }
                }
            } else if (pn instanceof InhibitorArcView) {

                ConnectableView attached = ((ArcView) pn).getSource();

                if (attached != null) {
                    ArrayList a = (ArrayList) _inhibitorsMap.get(attached);
                    if (a != null) {
                        a.remove(pn);
                    }

                    attached.removeFromArc((ArcView) pn);
                    if (attached instanceof TransitionView) {
                        ((TransitionView) attached).removeArcCompareObject((ArcView) pn);
                    }
                }

                attached = ((ArcView) pn).getTarget();

                if (attached != null) {
                    if (_inhibitorsMap.get(attached) != null) {
                        ((ArrayList) _inhibitorsMap.get(attached)).remove(pn);
                    }

                    attached.removeToArc((ArcView) pn);
                    if (attached instanceof TransitionView) {
                        ((TransitionView) attached).removeArcCompareObject((ArcView) pn);
                    }
                }
            } else if (pn instanceof RateParameter) {
                _rateParameterHashSet.remove(pn.getName());
            }

            setChanged();
            setMatrixChanged();
            notifyObservers(pn);
        } catch (NullPointerException npe) {
            System.out.println("NullPointerException [debug]\n" + npe.getMessage());
            throw npe;
        }
        //_changeArrayList = null;
    }

    public void removeStateGroup(StateGroup SGObject) {
        _stateGroups.remove(SGObject);
    }

    public boolean stateGroupAlreadyExists(String stateName) {
        Iterator<StateGroup> i = _stateGroups.iterator();
        while (i.hasNext()) {
            StateGroup stateGroup = i.next();
            String stateGroupName = stateGroup.getName();
            if (stateName.equals(stateGroupName)) {
                return true;
            }
        }
        return false;
    }

    public Iterator returnTransitions() {
        return _transitionViews.values().iterator();
    }

    public Iterator getPetriNetObjects() {
        ArrayList all = new ArrayList(_placeViews.values());
        all.addAll(_transitionViews.values());
        all.addAll(_arcViews.values());
        all.addAll(_labels.values());
        // tokensArray removed
        all.addAll(_rateParameters);

        return all.iterator();
    }

    public boolean hasPlaceTransitionObjects() {
        return (_placeViews.size() + _transitionViews.size()) > 0;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#createMatrixes()
      */
    public void createMatrixes() {
        for (TokenView tc : _tokenSetController.getTokenViews()) {
            tc.createIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
            tc.createInhibitionMatrix(_inhibitorViews.values(), _transitionViews.values(), _placeViews.values());
        }
        createInitialMarkingVector();
        createCurrentMarkingVector();
        createCapacityVector();
    }

    /**
     * Creates Initial Marking Vector from current Petri-Net
     */
    private void createInitialMarkingVector() {
        int placeSize = _placeViews.size();
        _initialMarkingVector = new LinkedList[placeSize];
        for (int placeNo = 0; placeNo < placeSize; placeNo++) {
            _initialMarkingVector[placeNo] = _placeViews.get(placeNo).getInitialMarkingView();
        }
    }

    /**
     * Creates Current Marking Vector from current Petri-Net
     */
    private void createCurrentMarkingVector() {
        int placeSize = _placeViews.size();

        _currentMarkingVector = new LinkedList[placeSize];
        for (int placeNo = 0; placeNo < placeSize; placeNo++) {
            _currentMarkingVector[placeNo] = _placeViews.get(placeNo).getCurrentMarkingView();
        }
    }


    /**
     * Creates Capacity Vector from current Petri-Net
     */
    private void createCapacityVector() {
        int placeSize = _placeViews.size();

        _capacityMatrix = new int[placeSize];
        for (int placeNo = 0; placeNo < placeSize; placeNo++) {
            _capacityMatrix[placeNo] = _placeViews.get(placeNo).getCapacity();
        }
    }

    /**
     * Creates Timed Vector from current Petri-Net
     */
    private void createTimedVector() {
        int transitionSize = _transitionViews.size();

        _timedMatrix = new boolean[transitionSize];
        for (int transitionNo = 0; transitionNo < transitionSize; transitionNo++) {
            _timedMatrix[transitionNo] = _transitionViews.get(transitionNo).isTimed();
        }
    }

    /**
     * Creates Priority Vector from current Petri-Net
     */
    private void createPriorityVector() {
        int transitionSize = _transitionViews.size();

        _priorityMatrix = new int[transitionSize];
        for (int transitionNo = 0; transitionNo < transitionSize; transitionNo++) {
            _priorityMatrix[transitionNo] = _transitionViews.get(transitionNo).getPriority();
        }
    }

    public void storeCurrentMarking() {
        int placeSize = _placeViews.size();
        _markingVectorAnimationStorage = new LinkedList[placeSize];
        for (int placeNo = 0; placeNo < placeSize; placeNo++) {
            _markingVectorAnimationStorage[placeNo] =
                    Copier.mediumCopy(_placeViews.get(placeNo).getCurrentMarkingView());
        }
    }

    public void restorePreviousMarking() {
        if (_markingVectorAnimationStorage != null) {
            int placeSize = _placeViews.size();
            for (int placeNo = 0; placeNo < placeSize; placeNo++) {
                PlaceView placeView = _placeViews.get(placeNo);
                if (placeView != null) {
                    placeView.setCurrentMarking(_markingVectorAnimationStorage[placeNo]);
                    setChanged();
                    notifyObservers(placeView);
                    setMatrixChanged();
                }
            }
        }
    }

    public void fireTransition(TransitionView transitionView) {
        if (transitionView != null) {
            if (transitionView.isEnabled() && _placeViews != null) {
                //TODO: fix this
                int transitionNo = 0; //_transitionViews.values().indexOf(transitionView);
                createMatrixes();
                for (int placeNo = 0; placeNo < _placeViews.size(); placeNo++) {
                    for (MarkingView markingView : _placeViews.get(placeNo).getCurrentMarkingView()) {

                        TokenView tokenView = markingView.getToken();
                        int oldMarkingPositionInTheList =
                                positionInTheList(tokenView.getID(), _currentMarkingVector[placeNo]);
                        int oldMarking =
                                _currentMarkingVector[placeNo].get(oldMarkingPositionInTheList).getCurrentMarking();
                        //  tokenView.createIncidenceMatrix(_arcViews, _transitionViews, _placeViews);

                        int markingToBeAdded = tokenView.
                                getIncidenceMatrix().
                                get(placeNo, transitionNo);
                        markingView.setCurrentMarking(oldMarking + markingToBeAdded);
                    }
                    _placeViews.get(placeNo).repaint();
                }
            }
        }
        setMatrixChanged();
    }

    public TransitionView getRandomTransition() {

        setEnabledTransitions();
        // All the enabled _transitions are of the same type:
        // a) all are immediate _transitions; or
        // b) all are timed _transitions.

        ArrayList enabledTransitions = new ArrayList();
        double rate = 0;
        for (TransitionView transitionView : _transitionViews.values()) {
            if (transitionView.isEnabled()) {
                enabledTransitions.add(transitionView);
                rate += transitionView.getRate();
            }
        }

        // if there is only one enabled transition, return this transition
        if (enabledTransitions.size() == 1) {
            return (TransitionView) enabledTransitions.get(0);
        }

        double random = RandomNumberGenerator.getRandomNumber();
        double x = 0;
        for (Object enabledTransition : enabledTransitions) {
            TransitionView t = (TransitionView) enabledTransition;

            x += t.getRate() / rate;

            if (random < x) {
                return t;
            }
        }

        // no enabled transition found, so no transition can be fired
        return null;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getEnabledTransitions()
      */
    public ArrayList<TransitionView> getEnabledTransitions() {
        setEnabledTransitions();
        // All the enabled _transitions are of the same type:
        // a) all are immediate _transitions; or
        // b) all are timed _transitions.

        ArrayList enabledTransitions = new ArrayList();
        for (TransitionView transitionView : _transitionViews.values()) {
            if (transitionView.isEnabled()) {
                enabledTransitions.add(transitionView);
            }
        }

        return enabledTransitions;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#fireTransitionBackwards(pipe.views.Transition)
      */

    public void fireTransitionBackwards(TransitionView transitionView) {
        if (transitionView != null) {
            setEnabledTransitionsBackwards();
            if (transitionView.isEnabled() && _placeViews != null) {
                //TODO: fix this
                int transitionNo = 0;// _transitionViews.values().indexOf(transitionView);
                for (int placeNo = 0; placeNo < _placeViews.size(); placeNo++) {
                    for (MarkingView m : _placeViews.get(placeNo).getCurrentMarkingView()) {
                        int oldMarkingPos = positionInTheList(m.getToken().getID(), _currentMarkingVector[placeNo]);
                        int oldMarking = _currentMarkingVector[placeNo].get(oldMarkingPos).getCurrentMarking();
                        int markingToBeSubtracted = m.getToken()
                                //.getIncidenceMatrix()
                                .getPreviousIncidenceMatrix().get(placeNo, transitionNo);
                        m.setCurrentMarking(oldMarking - markingToBeSubtracted);
                    }
                    _placeViews.get(placeNo).repaint();
                }
            }
        }
        setMatrixChanged();
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#resetEnabledTransitions()
      */
    public void resetEnabledTransitions() {
        for (TransitionView transitionView : _transitionViews.values()) {
            transitionView.setEnabled(false);
            setChanged();
            notifyObservers(transitionView);
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#isTransitionEnabled(java.util.LinkedList, int)
      */
    public boolean isTransitionEnabled(LinkedList<MarkingView>[] markings, int transition) {
        int transCount = this.numberOfTransitions();
        int placeCount = this.numberOfPlaces();
        boolean[] result = new boolean[transCount];
        int[][] CMinus;

        // initialise matrix to true
        for (int k = 0; k < transCount; k++) {
            result[k] = true;
        }
        for (int i = 0; i < transCount; i++) {
            for (int j = 0; j < placeCount; j++) {
                boolean allTokenClassesEnabled = true;
                for (MarkingView m : markings[j]) {
                    CMinus = (m.getToken()).getBackwardsIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
                    if (m.getCurrentMarking() < CMinus[j][i]) {
                        result[i] = false;
                        break;
                    }
                }
            }
        }

        return result[transition];
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#areTransitionsEnabled(java.util.LinkedList)
      */
    public final boolean[] areTransitionsEnabled(LinkedList<MarkingView>[] markings) {
        createMatrixes();
        int transitionCount = numberOfTransitions();
        int placeCount = numberOfPlaces();
        boolean[] result = new boolean[transitionCount];
        boolean hasTimed = false;
        boolean hasImmediate = false;

        int maxPriority = 0;
        for (int i = 0; i < transitionCount; i++) {
            if (_transitionViews.get(i).isInfiniteServer()) {
                if (getEnablingDegree(_transitionViews.get(i)) == 0) {
                    result[i] = false;
                    continue;
                } else {
                    result[i] = true;
                    continue;
                }
            }
            result[i] = true; // inicialitzam a enabled
            for (int j = 0; j < placeCount; j++) {
                boolean allTokenClassesEnabled = true;
                int totalMarkings = 0;
                int totalForwardIncidenceMarkings = 0;
                int totalBackwardIncidenceMarkings = 0;
                for (MarkingView m : markings[j]) {
                    if (m.getToken().isEnabled()) {
                        totalMarkings += m.getCurrentMarking();
                        totalForwardIncidenceMarkings += (m.getToken()).getForwardsIncidenceMatrix().get(j, i);
                        totalBackwardIncidenceMarkings += (m.getToken()).getBackwardsIncidenceMatrix().get(j, i);
                        if ((m.getCurrentMarking() < (m.getToken()).getBackwardsIncidenceMatrix().get(j, i)) &&
                                (m.getCurrentMarking() != -1)) {
                            allTokenClassesEnabled = false;
                            break;
                        }
                        // inhibitor arcs
                        if (m.getToken().getInhibitionMatrix().get(j, i) > 0 &&
                                m.getCurrentMarking() >= m.getToken().getInhibitionMatrix().get(j, i)) {
                            // an inhibitor arc prevents the firing of this
                            // transition so
                            // the transition is not enabled
                            allTokenClassesEnabled = false;
                            break;
                        }
                    }
                }
                // capacities
                if (allTokenClassesEnabled && (_capacityMatrix[j] > 0) &&
                        (totalMarkings + totalForwardIncidenceMarkings - totalBackwardIncidenceMarkings >
                                _capacityMatrix[j])) { // firing this transition would break a capacity
                    // restriction so the transition is not enabled
                    allTokenClassesEnabled = false;
                }


                if (!allTokenClassesEnabled) {
                    result[i] = false;
                    break;
                }
            }

            // we look for the highest priority of the enabled _transitions
            if (result[i]) {
                TransitionView t = _transitionViews.get(i);
                if (t.isTimed()) {
                    hasTimed = true;
                } else {
                    hasImmediate = true;
                    if (t.getPriority() > maxPriority) {
                        maxPriority = t.getPriority();
                    }
                }
            }
        }

        // Now make sure that if any of the enabled _transitions are immediate
        // _transitions, only they can fire as this must then be a vanishing
        // state.
        // - disable the immediate _transitions with lower priority.
        // - disable all timed _transitions if there is an immediate transition
        // enabled.
        for (int i = 0; i < transitionCount; i++) {
            TransitionView t = _transitionViews.get(i);
            if (!t.isTimed() && t.getPriority() < maxPriority) {
                result[i] = false;
            }
            if (hasTimed && hasImmediate) {
                if (t.isTimed()) {
                    result[i] = false;
                }
            }
        }

        // print("areTransitionsEnabled: ",result);//debug
        return result;
    }

    // }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#setEnabledTransitionsBackwards()
      */
    public void setEnabledTransitionsBackwards() {

        if (_currentMarkingVectorChanged) {
            createMatrixes();
        }

        boolean[] enabledTransitions =
                getTransitionEnabledStatusArray(this.getTransitionViews(), this.getCurrentMarkingVector(), true,
                        this.getCapacityMatrix(), this.numberOfPlaces(), this.numberOfTransitions());

        for (int i = 0; i < enabledTransitions.length; i++) {
            TransitionView transitionView = _transitionViews.get(i);
            if (enabledTransitions[i] != transitionView.isEnabled()) {
                transitionView.setEnabled(enabledTransitions[i]);
                setChanged();
                notifyObservers(transitionView);
            }
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#setEnabledTransitions()
      */
    public void setEnabledTransitions() {

        if (_currentMarkingVectorChanged) {
            createMatrixes();
        }

        boolean[] enabledTransitions =
                getTransitionEnabledStatusArray(this.getTransitionViews(), this.getCurrentMarkingVector(), false,
                        this.getCapacityMatrix(), this.numberOfPlaces(), this.numberOfTransitions());

        for (int i = 0; i < enabledTransitions.length; i++) {
            TransitionView transitionView = _transitionViews.get(i);
            if (enabledTransitions[i] != transitionView.isEnabled()) {
                transitionView.setEnabled(enabledTransitions[i]);
                setChanged();
                notifyObservers(transitionView);
            }
        }
    }

    private boolean[] getTransitionEnabledStatusArray(

            final TransitionView[] transArray, final LinkedList<MarkingView>[] markings, boolean backwards,/*
                                                                     * final
																	 * int[][]
																	 * CMinus,
																	 * final
																	 * int[][]
																	 * CPlus,
																	 * final
																	 * int[][]
																	 * inhibition
																	 * ,
																	 */
            final int capacities[], final int placeCount, final int transitionCount) {
        boolean[] result = new boolean[transitionCount];
        boolean hasTimed = false;
        boolean hasImmediate = false;

        int maxPriority = 0;

        for (int i = 0; i < transitionCount; i++) {
            if (_transitionViews.get(i).isInfiniteServer()) {
                if (getEnablingDegree(_transitionViews.get(i)) == 0) {
                    result[i] = false;
                    continue;
                } else {
                    result[i] = true;
                    continue;
                }
            }


            result[i] = true; // inicialitzam a enabled
            for (int j = 0; j < placeCount; j++) {
                boolean allTokenClassesEnabled = true;
                int totalMarkings = 0;
                int totalCPlus = 0;
                int totalCMinus = 0;
                for (MarkingView m : markings[j]) {
                    int[][] CMinus;
                    int[][] CPlus;
                    int[][] inhibition;
                    if (backwards) {
                        CMinus = m.getToken().getForwardsIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
                        CPlus = m.getToken().getBackwardsIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
                    } else {
                        CPlus = m.getToken().getForwardsIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
                        CMinus = m.getToken().getBackwardsIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
                    }
                    inhibition = m.getToken().getInhibitionMatrix(_inhibitorViews.values(), _transitionViews.values(), _placeViews.values());

                    if ((m.getCurrentMarking() < CMinus[j][i]) && (m.getCurrentMarking() != -1)) {
                        allTokenClassesEnabled = false;
                        break;
                    }
                    // capacities
                    totalMarkings += m.getCurrentMarking();
                    totalCPlus += (m.getToken()).getForwardsIncidenceMatrix().get(j, i);
                    totalCMinus += (m.getToken()).getBackwardsIncidenceMatrix().get(j, i);

                    if (allTokenClassesEnabled && (_capacityMatrix[j] > 0) &&
                            (totalMarkings + totalCPlus - totalCMinus >
                                    _capacityMatrix[j])) { // firing this transition would break a capacity
                        // restriction so the transition is not enabled
                        allTokenClassesEnabled = false;
                    }

                    // inhibitor arcs
                    if (inhibition[j][i] > 0 && m.getCurrentMarking() >= inhibition[j][i]) {
                        // an inhibitor arc prevents the firing of this
                        // transition
                        // so
                        // the transition is not enabled
                        allTokenClassesEnabled = false;
                        break;
                    }
                }

                if (!allTokenClassesEnabled) {
                    result[i] = false;
                    break;
                }
            }
            // we look for the highest priority of the enabled _transitions
            if (result[i]) {
                if (transArray[i].isTimed()) {
                    hasTimed = true;
                } else {
                    hasImmediate = true;
                    if (transArray[i].getPriority() > maxPriority) {
                        maxPriority = transArray[i].getPriority();
                    }
                }
            }

        }
        // Now make sure that if any of the enabled _transitions are immediate
        // _transitions, only they can fire as this must then be a vanishing
        // state.
        // - disable the immediate _transitions with lower priority.
        // - disable all timed _transitions if there is an immediate transition
        // enabled.
        for (int i = 0; i < transitionCount; i++) {
            if (!transArray[i].isTimed() && transArray[i].getPriority() < maxPriority) {
                result[i] = false;
            }
            if (hasTimed && hasImmediate) {
                if (transArray[i].isTimed()) {
                    result[i] = false;
                }
            }
        }

        // print("areTransitionsEnabled: ",result);//debug
        return result;
    }

    /**
     * Empty all attributes, turn into empty Petri-Net
     */
    private void emptyPNML() {
        _model.resetPNML();
        _placeViews = null;
        _transitionViews = null;
        _arcViews = null;
        _labels = null;
        _rateParameters = null;
        _initialMarkingVector = null;
        _arcsMap = null;
        _tokenSetController = null;
//        initializeMatrices();
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#places()
      */
    public PlaceView[] places() {
        PlaceView[] returnArray = new PlaceView[_placeViews.size()];

        for (int i = 0; i < _placeViews.size(); i++) {
            returnArray[i] = _placeViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlacesArrayList()
      */
    public Collection<PlaceView> getPlacesArrayList() {
        return _placeViews.values();
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#numberOfPlaces()
      */
    public int numberOfPlaces() {
        if (_placeViews == null) {
            return 0;
        } else {
            return _placeViews.size();
        }
    }

    /* wjk added 03/10/2007 */
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#marking()
      */
    public LinkedList<MarkingView>[] marking() {
        LinkedList<MarkingView>[] result = new LinkedList[_placeViews.size()];

        for (int i = 0; i < _placeViews.size(); i++) {
            result[i] =
                    (LinkedList<MarkingView>) Copier.deepCopy(((PlaceView) _placeViews.get(i)).getCurrentMarkingView());
        }
        return result;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#labels()
      */
    public AnnotationNote[] labels() {
        AnnotationNote[] returnArray = new AnnotationNote[_labels.size()];

        for (int i = 0; i < _labels.size(); i++) {
            returnArray[i] = _labels.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#markingRateParameters()
      */
    public RateParameter[] markingRateParameters() {
        RateParameter[] returnArray = new RateParameter[_rateParameters.size()];
        _rateParameters.toArray(returnArray);
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitions()
      */
    public TransitionView[] getTransitionViews() {
        TransitionView[] returnArray = new TransitionView[_transitionViews.size()];

        for (int i = 0; i < _transitionViews.size(); i++) {
            returnArray[i] = _transitionViews.get(i);
        }
        return returnArray;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionsArrayList()
      */
    public Collection<TransitionView> getTransitionsArrayList() {
        return _transitionViews.values();
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#numberOfTransitions()
      */
    public int numberOfTransitions() {
        if (_transitionViews == null) {
            return 0;
        } else {
            return _transitionViews.size();
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#arcs()
      */
    public ArcView[] arcs() {
        ArcView[] returnArray = new ArcView[_arcViews.size()];

        for (int i = 0; i < _arcViews.size(); i++) {
            returnArray[i] = _arcViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getArcsArrayList()
      */
    public Collection<ArcView> getArcsArrayList() {
        return _arcViews.values();
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#inhibitors()
      */
    public InhibitorArcView[] inhibitors() {
        InhibitorArcView[] returnArray = new InhibitorArcView[_inhibitorViews.size()];

        for (int i = 0; i < _inhibitorViews.size(); i++) {
            returnArray[i] = _inhibitorViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getInhibitorsArrayList()
      */
    public Collection<InhibitorArcView> getInhibitorsArrayList() {
        return _inhibitorViews.values();
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionById(java.lang.String)
      */


    public LinkedList<MarkingView>[] getInitialMarkingVector() {
        //if(_initialMarkingVectorChanged)
        createInitialMarkingVector();
        return _initialMarkingVector;
    }

    public LinkedList<MarkingView>[] getCurrentMarkingVector() {
        createCurrentMarkingVector();
//        if(_currentMarkingVectorChanged)
//        {
//            createCurrentMarkingVector();
//        }
        return _currentMarkingVector;
    }


    public void setCurrentMarkingVector(int[] is) {
        int placeSize = _placeViews.size();

        for (int placeNo = 0; placeNo < placeSize; placeNo++) {
            _placeViews.get(placeNo).getCurrentMarkingView().getFirst().setCurrentMarking(is[placeNo]);
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getCapacityMatrix()
      */
    public int[] getCapacityMatrix() {
        createCapacityVector();
        return _capacityMatrix;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPriorityMatrix()
      */
    public int[] getPriorityMatrix() {
        createPriorityVector();
        return _priorityMatrix;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTimedMatrix()
      */
    public boolean[] getTimedMatrix() {
        createTimedVector();
        return _timedMatrix;
    }

    private void setMatrixChanged() { // Steve Doubleday:  checks both enabled and disabled TokenViews; do we just need the enabled ones?
        for (TokenView tc : _tokenSetController.getAllTokenViews()) {
            if (tc.getForwardsIncidenceMatrix() != null) {
                tc.getForwardsIncidenceMatrix().matrixChanged = true;
            }
            if (tc.getBackwardsIncidenceMatrix() != null) {
                tc.getBackwardsIncidenceMatrix().matrixChanged = true;
            }
            if (tc.getIncidenceMatrix() != null) {
                tc.getIncidenceMatrix().matrixChanged = true;
            }
            if (tc.getInhibitionMatrix() != null) {
                tc.getInhibitionMatrix().matrixChanged = true;
            }
        }
        _currentMarkingVectorChanged = true;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#createFromPNML(org.w3c.dom.Document)
      */
    public void createFromPNML(Document PNMLDoc) {
        if (ApplicationSettings.getApplicationView() != null) {
            // Notifies used to indicate new instances.
            ApplicationSettings.getApplicationModel().setMode(Constants.CREATING);
        }

        newDisplayMethod(PNMLDoc);

        if (ApplicationSettings.getApplicationView() != null) {
            ApplicationSettings.getApplicationModel().restoreMode();
        }
    }

    private void displayTokens(Collection<Token> tokens) {
        for (Token token : tokens) {
            TokenViewBuilder builder = new TokenViewBuilder(token);
            addToken(builder.build());
        }
    }

    private void newDisplayMethod(Document PNMLDoc) {
        CreatorStruct struct = new CreatorStruct(new PlaceCreator(), new TransitionCreator(), new ArcCreator(),
                new AnnotationCreator(), new RateParameterCreator(), new TokenCreator(), new StateGroupCreator());
        PetriNetReader reader = new PetriNetReader(struct);
        _model = reader.createFromFile(PNMLDoc);
        update();
    }

    private void displayStateGroups(Collection<StateGroup> stateGroups) {

        for (StateGroup group : stateGroups) {
            addStateGroup(group);
        }
    }

    private void displayAnnotations(Collection<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            AnnotationNodeBuilder builder = new AnnotationNodeBuilder(annotation);
            addAnnotation(builder.build());
        }
    }

    private void displayRateParameters(Collection<RateParameter> rateParameters) {
        for (RateParameter parameter : rateParameters) {
            addAnnotation(parameter);
        }
    }

    private void displayArcs(Collection<Arc> arcs) {
        //TODO: Quick and dirty hack, tidy up to not use instanceof!
        for (Arc arc : arcs) {
            if (arc instanceof NormalArc) {
                NormalArc normalArc = NormalArc.class.cast(arc);
                NormalArcViewBuilder builder = new NormalArcViewBuilder(normalArc);
                NormalArcView view = builder.build();
                addArc(view);
                //TODO: Add back in:
                //checkForInverseArc(view);
            } else {

                InhibitorArc inhibitorArc = InhibitorArc.class.cast(arc);
                InhibitorArcViewBuilder builder = new InhibitorArcViewBuilder(inhibitorArc);
                addArc(builder.build());
            }
        }
    }

    private void displayPlaces(Collection<Place> places) {

        for (Place place : places) {
            PlaceView view;
            if (_placeViews.containsKey(place))
            {
                view = _placeViews.get(place);
                view.update(this, place);
            } else
            {
                PlaceViewBuilder builder = new PlaceViewBuilder(place);
                view = builder.build();
                _placeViews.put(place, view);
            }
            view.setActiveTokenView(_tokenSetController.getActiveTokenView());
//            placeView.setActiveTokenView(_activeTokenView); // SJD
//            _placeViews.add(view);
            setChanged();
            setMatrixChanged();
            notifyObservers(view);
        }
    }

    private void displayTransitions(Collection<Transition> transitions) {
        for (Transition transition : transitions) {
            TransitionView view;
            if (_transitionViews.containsKey(transition))
            {
                view = _transitionViews.get(transition);
                view.update();
            } else
            {
                TransitionViewBuilder builder = new TransitionViewBuilder(transition);
                view = builder.build();
                _transitionViews.put(transition, view);
            }

            setChanged();
            setMatrixChanged();
            notifyObservers(view);
        }

    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getStateGroups()
      */
    public StateGroup[] getStateGroups() {
        StateGroup[] returnArray = new StateGroup[_stateGroups.size()];
        for (int i = 0; i < _stateGroups.size(); i++) {
            returnArray[i] = (StateGroup) _stateGroups.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getStateGroupsArray()
      */
    public ArrayList<StateGroup> getStateGroupsArray() {
        return this._stateGroups;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#print()
      */
    public void print() {
        System.out.println("No of Places = " + _placeViews.size() + "\"");
        System.out.println("No of Transitions = " + _transitionViews.size() + "\"");
        System.out.println("No of Arcs = " + _arcViews.size() + "\"");
        System.out.println("No of Labels = " + _labels.size() + "\" (Model View Controller Design Pattern)");
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#existsRateParameter(java.lang.String)
      */
    public boolean existsRateParameter(String name) {
        return _rateParameterHashSet.contains(name);
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#changeRateParameter(java.lang.String, java.lang.String)
      */
    public boolean changeRateParameter(String oldName, String newName) {
        if (_rateParameterHashSet.contains(newName)) {
            return false;
        }
        _rateParameterHashSet.remove(oldName);
        _rateParameterHashSet.add(newName);
        return true;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#hasTimedTransitions()
      */
    public boolean hasTimedTransitions() {
        TransitionView[] transitionViews = this.getTransitionViews();
        int transCount = transitionViews.length;

        for (int i = 0; i < transCount; i++) {
            if (transitionViews[i].isTimed()) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#hasImmediateTransitions()
      */
    public boolean hasImmediateTransitions() {
        TransitionView[] transitionViews = this.getTransitionViews();
        int transCount = transitionViews.length;

        for (int i = 0; i < transCount; i++) {
            if (!transitionViews[i].isTimed()) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#isTangibleState(java.util.LinkedList)
      */
    public boolean isTangibleState(LinkedList<MarkingView>[] marking) {
        TransitionView[] trans = this.getTransitionViews();
        int numTrans = trans.length;
        boolean hasTimed = false;
        boolean hasImmediate = false;

        for (int i = 0; i < numTrans; i++) {
            if (this.isTransitionEnabled(marking, i)) {
                if (trans[i].isTimed()) {
                    // If any immediate transtions exist, the state is vanishing
                    // as they will fire immediately
                    hasTimed = true;
                } else if (!trans[i].isTimed()) {
                    hasImmediate = true;
                }
            }
        }
        return (hasTimed && !hasImmediate);
    }

    private void checkForInverseArc(NormalArcView newArcView) {
        Iterator iterator = newArcView.getSource().getConnectToIterator();

        ArcView anArcView;
        while (iterator.hasNext()) {
            anArcView = (ArcView) iterator.next();
            if (anArcView.getTarget() == newArcView.getSource() && anArcView.getSource() == newArcView.getTarget()) {
                if (anArcView.getClass() == NormalArcView.class) {
                    if (!newArcView.hasInverse()) {
                        ((NormalArcView) anArcView).setInverse(newArcView, Constants.JOIN_ARCS);
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionName(int)
      */
    public String getTransitionName(int i) {
        return _transitionViews.get(i).getName();
    }

    // Function to check the structure of the Petri Net to ensure that if tagged
    // arcs are included then they obey the restrictions on how they can be used
    // (i.e. a transition may only have one input tagged Arc and one output
    // tagged Arc and if it has one it must have the other).
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#validTagStructure()
      */
    public boolean validTagStructure() {
        ArrayList inputArcsArray = new ArrayList();
        ArrayList outputArcsArray = new ArrayList();

        TransitionView currentTrans;
        NormalArcView currentArcView;

        boolean taggedNet = false;
        boolean taggedTransition;
        boolean taggedInput;
        boolean taggedOutput;
        boolean validStructure = true;
        String checkResult;
        int noTaggedInArcs;
        int noTaggedOutArcs;

        checkResult = "Tagged structure validation result:\n";

        if (_transitionViews != null && _transitionViews.size() > 0) {
            // we need to check all the arcs....
            for (TransitionView _transitionView : _transitionViews.values()) {
                currentTrans = _transitionView;
                taggedTransition = false;
                taggedInput = false;
                taggedOutput = false;
                // invalidStructure = false;
                noTaggedInArcs = 0;
                noTaggedOutArcs = 0;
                inputArcsArray.clear();
                outputArcsArray.clear();

                // we must:
                // i) find the arcs attached to this transition
                // ii) determine whether they are input arcs or output arcs
                // iii) check that if there is one tagged input arc there is
                // also
                // one output arc

                if (_arcViews != null && _arcViews.size() > 0) {
                    for (ArcView _arcView : _arcViews.values()) {
                        currentArcView = (NormalArcView) _arcView;
                        if (currentArcView.getSource() == currentTrans) {
                            outputArcsArray.add(currentArcView);
                            if (currentArcView.isTagged()) {
                                taggedNet = true;
                                taggedTransition = true;
                                taggedOutput = true;
                                noTaggedOutArcs++;
                                if (noTaggedOutArcs > 1) {
                                    checkResult = checkResult + "  Transition " + currentTrans.getName() +
                                            " has more than one" + " tagged output arc\n";
                                    validStructure = false;
                                }
                            }
                        } else if (currentArcView.getTarget() == currentTrans) {
                            inputArcsArray.add(currentArcView);
                            if (currentArcView.isTagged()) {
                                taggedNet = true;
                                taggedTransition = true;
                                taggedInput = true;
                                noTaggedInArcs++;
                                if (noTaggedInArcs > 1) {
                                    checkResult = checkResult + "  Transition " + currentTrans.getName() +
                                            " has more than one" + " tagged input arc\n";
                                    validStructure = false;
                                }
                            }
                        }
                    }
                }

                // we have now built lists of input arcs and output arcs and
                // verified that there is at most one of each.
                // we must check, however, that if there is a tagged input there
                // is
                // a tagged output and vice-versa
                if (taggedTransition) {
                    if ((taggedInput && !taggedOutput) || (!taggedInput && taggedOutput)) {
                        checkResult = checkResult + "  Transition " + currentTrans.getName() +
                                " does not have matching tagged arcs\n";
                        validStructure = false;
                    }
                }
            }
        }

        // if we reach the end with validStructure still true then everything
        // must
        // be OK!
        if (validStructure) {
            checkResult = "Tagged structure validation result:\n  Tagged arc structure is valid\n";
            JOptionPane.showMessageDialog(null, checkResult, "Validation Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, checkResult, "Validation Results", JOptionPane.ERROR_MESSAGE);
        }

        // System.out.println(checkResult);

        return validStructure;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#checkTransitionIDAvailability(java.lang.String)
      */
    public boolean checkTransitionIDAvailability(String newName) {
        for (TransitionView _transitionView : _transitionViews.values()) {
            if (_transitionView.getId().equals(newName)) {
                // ID/name isn't available
                return false;
            }
        }
        // ID/name is available
        return true;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#checkPlaceIDAvailability(java.lang.String)
      */
    public boolean checkPlaceIDAvailability(String newName) {
        for (PlaceView _placeView : _placeViews.values()) {
            if (_placeView.getId().equals(newName)) {
                // ID/name isn't available
                return false;
            }
        }
        // ID/name is available
        return true;
    }


    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlaceIndex(java.lang.String)
      */
    public int getPlaceIndex(String placeName) {
        int index = -1;
        for (int i = 0; i < _placeViews.size(); i++) {
            if (_placeViews.get(i).getId().equals(placeName)) {
                index = i;
                break;
            }
        }

        return index;
    }

    // Added for passage time analysis of tagged nets
    /*use to check if structure contain any tagged token or tagged arc, then the structure
      * needs to be validated before animation
      */
    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#hasValidatedStructure()
      */
    public boolean hasValidatedStructure() {

        boolean tagged = false;

        for (ArcView _arcView : _arcViews.values()) {
            if (_arcView.isTagged()) {
                tagged = true;
            }
        }

        if (tagged && _model.isValidated()) {
            return true;
        } else {
            return !tagged;
        }


    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#setValidate(boolean)
      */
    public void setValidate(boolean valid) {
        _model.setValidated(valid);
    }


    /**
     * Updates view by displaying relevant information
     */
    @Override
    public void update() {

        displayPlaces(_model.getPlaces());
        displayTokens(_model.getTokens());
        displayTransitions(_model.getTransitions());
        displayArcs(_model.getArcs());
        displayRateParameters(_model.getRateParameters());
        displayAnnotations(_model.getAnnotations());
        displayStateGroups(_model.getStateGroups());
    }


    public TransitionView getTransitionById(String transitionID) {
        TransitionView returnTransitionView = null;

        if (_transitionViews != null) {
            if (transitionID != null) {
                for (TransitionView _transitionView : _transitionViews.values()) {
                    if (transitionID.equalsIgnoreCase(_transitionView.getId())) {
                        returnTransitionView = _transitionView;
                    }
                }
            }
        }
        return returnTransitionView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransitionByName(java.lang.String)
      */
    public TransitionView getTransitionByName(String transitionName) {
        TransitionView returnTransitionView = null;

        if (_transitionViews != null) {
            if (transitionName != null) {
                for (TransitionView _transitionView : _transitionViews.values()) {
                    if (transitionName.equalsIgnoreCase(_transitionView.getName())) {
                        returnTransitionView = _transitionView;
                    }
                }
            }
        }
        return returnTransitionView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getTransition(int)
      */
    public TransitionView getTransition(int transitionNo) {
        TransitionView returnTransitionView = null;

        if (_transitionViews != null) {
            if (transitionNo < _transitionViews.size()) {
                returnTransitionView = _transitionViews.get(transitionNo);
            }
        }
        return returnTransitionView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlaceById(java.lang.String)
      */
    public PlaceView getPlaceById(String placeID) {
        PlaceView returnPlaceView = null;

        if (_placeViews != null) {
            if (placeID != null) {
                for (PlaceView _placeView : _placeViews.values()) {
                    if (placeID.equalsIgnoreCase(_placeView.getId())) {
                        returnPlaceView = _placeView;
                    }
                }
            }
        }
        return returnPlaceView;
    }

    /* (non-Javadoc)
      * @see pipe.models.interfaces.IPetriNet#getPlaceByName(java.lang.String)
      */
    public PlaceView getPlaceByName(String placeName) {
        PlaceView returnPlaceView = null;

        if (_placeViews != null) {
            if (placeName != null) {
                for (PlaceView _placeView : _placeViews.values()) {
                    if (placeName.equalsIgnoreCase(_placeView.getName())) {
                        returnPlaceView = _placeView;
                    }
                }
            }
        }
        return returnPlaceView;
    }

    public PlaceView getPlace(int placeNo) {
        PlaceView returnPlaceView = null;

        if (_placeViews != null) {
            if (placeNo < _placeViews.size()) {
                returnPlaceView = _placeViews.get(placeNo);
            }
        }
        return returnPlaceView;
    }

    public ConnectableView getPlaceTransitionObject(String ptoId) {
        if (ptoId != null) {
            if (getPlaceById(ptoId) != null) {
                return getPlaceById(ptoId);
            } else if (getTransitionById(ptoId) != null) {
                return getTransitionById(ptoId);
            }
        }
        return null;
    }

    public String getPNMLName() {
        return _model.getPnmlName();
    }

    /**
     * @return
     * @author yufeiwang
     */
    public void backUpPlaceViewsMarking() {
        for (PlaceView place : _placeViews.values()) {
            place.backUpMarking();
        }
    }

    public void restorePlaceViewsMarking() {
        for (PlaceView place : _placeViews.values()) {
            place.restoreMarking();
        }
        for (ArcView arc : _arcViews.values()) {
            //arc.updateArcWeight();
            arc.repaint();
        }
        for (TransitionView tran : _transitionViews.values()) {
            tran.update();
        }
    }


    /**
     * @author yufeiwang
     */
    public void setFunctionalExpressionRelatedPlaces() {
        functionRelatedPlaces = new Vector<Vector<String>>();
        for (PlaceView place : _placeViews.values()) {
            Vector<String> temp = new Vector<String>();
            temp.add(place.getName());
            temp.add("no");
            functionRelatedPlaces.add(temp);
        }
        //first we check all arc weights
        for (ArcView arc : _arcViews.values()) {
            LinkedList<MarkingView> weights = arc.getWeightSimple();
            for (MarkingView weight : weights) {
                String temp = weight.getCurrentFunctionalMarking();
                for (PlaceView place : _placeViews.values()) {
                    if (temp.toLowerCase().contains(place.getName().toLowerCase())) {
                        setFuncRelatedPlace(place.getName().toLowerCase(), "yes");
                    }
                }
            }
        }
    }

    /**
     * @param name
     * @param yesorno
     * @author yufeiwang
     */
    public void setFuncRelatedPlace(String name, String yesorno) {
        for (Vector<String> place : functionRelatedPlaces) {
            if (place.get(0).toLowerCase().equals(name.toLowerCase())) {
                place.set(1, yesorno);
            }
        }
    }

    public boolean isPlaceFunctionalRelated(String name) {
        boolean result = false;

        for (Vector<String> place : functionRelatedPlaces) {
            if (place.get(0).toLowerCase().equals(name.toLowerCase())) {
                result = (place.get(1).equals("yes"));
            }
        }
        return result;

    }

    public int getEnabledTokenClassNumber() {
        return _tokenSetController.getTokenViews().size();
    }

    public boolean hasFunctionalRatesOrWeights() {
        for (ArcView arc : _arcViews.values()) {
            LinkedList<MarkingView> weights = arc.getWeightSimple();
            for (MarkingView weight : weights) {
                try {
                    Integer.parseInt(weight.getCurrentFunctionalMarking());
                } catch (Exception e) {
                    return true;
                }
            }
        }
        for (TransitionView tran : _transitionViews.values()) {
            try {
                Double.parseDouble(tran.getRateExpr());
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    public int getEnablingDegree(TransitionView tran) {

        int enablingDegree = Integer.MAX_VALUE;

        Iterator to = tran.getConnectToIterator();
        while (to.hasNext()) {
            ArcView arcTo = ((ArcView) to.next());
            PlaceView source = ((PlaceView) arcTo.getSource());

            LinkedList<MarkingView> weight = arcTo.getWeight();
            LinkedList<MarkingView> sourceMarking = source.getCurrentMarkingView();
            for (int i = 0; i < weight.size(); i++) {
                int current = sourceMarking.get(i).getCurrentMarking();

                for (MarkingView w : weight) {
                    if (w.getToken().getID().equals(sourceMarking.get(i).getToken().getID())) {
                        if (w.getCurrentMarking() == 0) {
                            enablingDegree = 0;
                        } else {
                            int ed = (int) Math.floor(current / w.getCurrentMarking());
                            if (ed < enablingDegree) {
                                enablingDegree = ed;
                            }
                        }

                    }
                }
            }
        }
        return enablingDegree;
    }

    public void deletePlace(String id) {
        for (int i = 0; i < _placeViews.size(); i++) {
            if (_placeViews.get(i).getId().equals(id)) {
                _placeViews.remove(i);
            }
        }

    }

    public void deleteTransition(String id) {
        for (int i = 0; i < _transitionViews.size(); i++) {
            if (_transitionViews.get(i).getId().equals(id)) {
                _transitionViews.remove(i);
            }
        }

    }

    public void deleteArc(String id) {
        for (int i = 0; i < _arcViews.size(); i++) {
            if (_arcViews.get(i).getId().equals(id)) {
                _arcViews.remove(i);
            }
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if ((arg0.equals(_tokenSetController)) && (arg1 instanceof TokenView)) {
            updatePlaceViewsWithActiveToken((TokenView) arg1);
        }
    }

    public PetriNet getModel() {
        return _model;
    }
}