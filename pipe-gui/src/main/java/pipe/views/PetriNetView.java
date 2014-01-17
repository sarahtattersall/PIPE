package pipe.views;

import pipe.common.dataLayer.StateGroup;
import pipe.controllers.PetriNetController;
import pipe.exceptions.TokenLockedException;
import pipe.gui.Constants;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.*;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.petrinet.transformer.PNMLTransformer;
import pipe.utilities.Copier;
import pipe.views.builder.AnnotationNodeBuilder;
import pipe.views.builder.PlaceViewBuilder;
import pipe.views.builder.TokenViewBuilder;
import pipe.views.builder.TransitionViewBuilder;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;


/*

 * @author yufei wang(minor change)
 * 		Steve Doubleday (Oct 2013):  refactored to use TokenSetController for access to TokenViews
 */
public class PetriNetView extends Observable implements Cloneable, Serializable, Observer {
    private static boolean _currentMarkingVectorChanged = true;
    private final HashSet _rateParameterHashSet = new HashSet();
    private final PetriNetController petriNetController;
    protected Map<Place, PlaceView> _placeViews = new HashMap<Place, PlaceView>();
    private Map<Transition, TransitionView> _transitionViews = new HashMap<Transition, TransitionView>();
    private Map<Arc, ArcView> _arcViews = new HashMap<Arc, ArcView>();
    private Map<Arc, InhibitorArcView> _inhibitorViews = new HashMap<Arc, InhibitorArcView>();
    private Map<Annotation, AnnotationNote> _labels = new HashMap<Annotation, AnnotationNote>();
    private Set<RateParameter> _rateParameters = new HashSet<RateParameter>();
    private Vector<Vector<String>> functionRelatedPlaces;
    private List<MarkingView>[] _initialMarkingVector;
    private List<MarkingView>[] _currentMarkingVector;
    private int[] _capacityMatrix;
    private int[] _priorityMatrix;
    private boolean[] _timedMatrix;
    private List<MarkingView>[] _markingVectorAnimationStorage;
    private Hashtable _arcsMap = new Hashtable();
    private Hashtable _inhibitorsMap = new Hashtable();
    private ArrayList<StateGroup> _stateGroups = new ArrayList<StateGroup>();
    private PetriNet _model;
    private TokenSetController _tokenSetController = new TokenSetController();


    public PetriNetView(String pnmlFileName) {
        _model = new PetriNet();
        PNMLTransformer transform = new PNMLTransformer();
        File temp = new File(pnmlFileName);
        _model.setPnmlName(temp.getName());
//        createFromPNML(transform.transformPNML(pnmlFileName));
        petriNetController = null;
    }

    /**
     * Updates view by displaying relevant information
     */
    public void update() {
//        removeAllDeletedModels();
//        displayPlaces(_model.getPlaces());
//        displayTokens(_model.getTokens());
//        displayTransitions(_model.getTransitions());
//        displayArcs(_model.getArcs());
//                displayRateParameters(_model.getRateParameters());
//        displayAnnotations(_model.getAnnotations());
        //        displayStateGroups(_model.getStateGroups());
    }

    private void displayTokens(Collection<Token> tokens) {
        for (Token token : tokens) {
            TokenViewBuilder builder = new TokenViewBuilder(token);
            addToken(builder.build());
        }
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
            notifyObservers(tokenViewInput);
        }
    }

    private void displayAnnotations(Collection<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            AnnotationNodeBuilder builder = new AnnotationNodeBuilder(annotation);
            addAnnotation(builder.build());
        }
    }

    private void addAnnotation(AnnotationNote labelInput) {
        boolean unique = true;
        //        _labels.add(labelInput);
        setChanged();
        notifyObservers(labelInput);
    }

    private void displayArcs(Collection<Arc<? extends Connectable, ? extends Connectable>> arcs) {
        //        for (Arc<? extends Connectable, ? extends Connectable> arc : arcs) {
        //            ArcView view;
        //            if (_arcViews.containsKey(arc)) {
        //                view = _arcViews.get(arc);
        //                view.update();
        //            } else if (arc.getType().equals(ArcType.NORMAL)) {
        //                NormalArcViewBuilder builder = new NormalArcViewBuilder(arc, petriNetController);
        //                view = builder.build();
        //                _arcViews.put(arc, view);
        //                //TODO: Add back in:
        //                //checkForInverseArc(view);
        //            } else {
        //                InhibitorArcViewBuilder builder = new InhibitorArcViewBuilder(arc, petriNetController);
        //                view = builder.build();
        //                _arcViews.put(arc, view);
        //            }
        //
        ////            addArcToArcsMap(view);
        //            setChanged();
        //            notifyObservers(view);
        //        }
    }

    private void displayPlaces(Collection<Place> places) {
        System.out.println("DISPLAY PLACES");
        for (Place place : places) {
            PlaceView view;
            if (_placeViews.containsKey(place)) {
                view = _placeViews.get(place);
                view.update(this, place);
            } else {
                PlaceViewBuilder builder = new PlaceViewBuilder(place, petriNetController);
                view = builder.build();
                _placeViews.put(place, view);
            }
            view.setActiveTokenView(_tokenSetController.getActiveTokenView());
            setChanged();
            notifyObservers(view);

        }
    }

    private void displayTransitions(Collection<Transition> transitions) {
        for (Transition transition : transitions) {
            TransitionView view;
            if (_transitionViews.containsKey(transition)) {
                view = _transitionViews.get(transition);
                view.update();
            } else {
                TransitionViewBuilder builder = new TransitionViewBuilder(transition, petriNetController);
                view = builder.build();
                _transitionViews.put(transition, view);
            }

            setChanged();
            notifyObservers(view);
        }

    }

    /**
     * Removes any models that have been deleted from the petrinet
     */
    private void removeAllDeletedModels() {
        removeNoLongerThereComponents(_placeViews, _model.getPlaces());
        removeNoLongerThereComponents(_transitionViews, _model.getTransitions());
        //TODO: TOKENS
        //        removeNoLongerThereComponents(_arcViews, _model.getArcs());
        removeNoLongerThereComponents(_labels, _model.getAnnotations());
        //TODO: Rate params, state groups

    }

    /**
     * Removes a component view if it is no longer in components.
     * That is if it has been deleted from the model.
     */
    private <M extends PetriNetComponent, V extends AbstractPetriNetViewComponent<M>>
    void removeNoLongerThereComponents(Map<M, V> componentsToViews, Collection<M> components) {
        final Iterator<Map.Entry<M, V>> itr = componentsToViews.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<M, V> entry = itr.next();
            if (!components.contains(entry.getKey())) {
                PetriNetViewComponent component = entry.getValue();

                component.delete();
                setChanged();
                notifyObservers(component);

                itr.remove();
            }
        }
    }

    public PetriNetView(PetriNetController petriNetController, PetriNet model) {
        _tokenSetController.addObserver(this);
        _model = model;
        this.petriNetController = petriNetController;
    }

    private void displayRateParameters(Collection<RateParameter> rateParameters) {
        for (RateParameter parameter : rateParameters) {
            addAnnotation(parameter);
        }
    }

    private void addAnnotation(RateParameter rateParameterInput) {
        boolean unique = true;
        //        _rateParameters.add(rateParameterInput);
        setChanged();
        notifyObservers(rateParameterInput);
    }

    private void displayStateGroups(Collection<StateGroup> stateGroups) {

        for (StateGroup group : stateGroups) {
            addStateGroup(group);
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

    @SuppressWarnings("unchecked")
    public PetriNetView clone() {
        PetriNetView newClone;
        try {
            newClone = (PetriNetView) super.clone();
            newClone._placeViews = deepCopy(_placeViews);
            newClone._transitionViews = deepCopy(_transitionViews);
            //            newClone._arcViews = deepCopy(_arcViews);
            //            newClone._inhibitorViews = deepCopy(_inhibitorViews);
            newClone._labels = deepCopy(_labels);
            newClone._tokenSetController =
                    (TokenSetController) Copier.deepCopy(_tokenSetController); //TODO test this SJD
            //            newClone._tokenViews = (LinkedList<TokenView>) Copier.deepCopy(_tokenViews); // SJD
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
        return newClone;
    }

    //TODO: need to clone values!
    private static <K extends PetriNetComponent, V extends AbstractPetriNetViewComponent<K>> Map<K, V> deepCopy(
            Map<K, V> original) {
        Map<K, V> result = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : original.entrySet()) {
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
        //TODO: IMPROVE WHERE THIS HAPPENS
        petriNetController.selectToken(tc.getModel());
        updatePlaceViewsWithActiveToken(tc);
    }

    protected void updatePlaceViewsWithActiveToken(TokenView tc) {
        for (PlaceView p : _placeViews.values()) {
            p.setActiveTokenView(tc);
        }
    }

    public int positionInTheList(String tokenClassID, List<MarkingView> markingViews) {
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

    public void addPetriNetObject(AbstractPetriNetViewComponent pn) {
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
            notifyObservers(placeView);
        }
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
      * @see pipe.interfaces.IPetriNet#createMatrixes()
      */
    public void createMatrixes() {
        //        for (TokenView tc : _tokenSetController.getTokenViews()) {
        //            tc.createIncidenceMatrix(_arcViews.values(), _transitionViews.values(), _placeViews.values());
        //            tc.createInhibitionMatrix(_inhibitorViews.values(), _transitionViews.values(), _placeViews.values());
        //        }
        //        createInitialMarkingVector();
        //        createCurrentMarkingVector();
        //        createCapacityVector();
    }

    public void storeCurrentMarking() {
        int placeSize = _model.getPlaces().size();
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
                }
            }
        }
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
      * @see pipe.interfaces.IPetriNet#places()
      */
    public PlaceView[] places() {
        PlaceView[] returnArray = new PlaceView[_placeViews.size()];

        for (int i = 0; i < _placeViews.size(); i++) {
            returnArray[i] = _placeViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getPlacesArrayList()
      */
    public Collection<PlaceView> getPlacesArrayList() {
        return _placeViews.values();
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#numberOfPlaces()
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
      * @see pipe.interfaces.IPetriNet#marking()
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
      * @see pipe.interfaces.IPetriNet#labels()
      */
    public AnnotationNote[] labels() {
        AnnotationNote[] returnArray = new AnnotationNote[_labels.size()];

        for (int i = 0; i < _labels.size(); i++) {
            returnArray[i] = _labels.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#markingRateParameters()
      */
    public RateParameter[] markingRateParameters() {
        RateParameter[] returnArray = new RateParameter[_rateParameters.size()];
        _rateParameters.toArray(returnArray);
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getTransitionById(java.lang.String)
      */

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getTransitionsArrayList()
      */
    public Collection<TransitionView> getTransitionsArrayList() {
        return _transitionViews.values();
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#numberOfTransitions()
      */
    public int numberOfTransitions() {
        if (_transitionViews == null) {
            return 0;
        } else {
            return _transitionViews.size();
        }
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#arcs()
      */
    public ArcView[] arcs() {
        ArcView[] returnArray = new ArcView[_arcViews.size()];

        for (int i = 0; i < _arcViews.size(); i++) {
            returnArray[i] = _arcViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getArcsArrayList()
      */
    public Collection<ArcView> getArcsArrayList() {
        return _arcViews.values();
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#inhibitors()
      */
    public InhibitorArcView[] inhibitors() {
        InhibitorArcView[] returnArray = new InhibitorArcView[_inhibitorViews.size()];

        for (int i = 0; i < _inhibitorViews.size(); i++) {
            returnArray[i] = _inhibitorViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getInhibitorsArrayList()
      */
    public Collection<InhibitorArcView> getInhibitorsArrayList() {
        return _inhibitorViews.values();
    }

    public List<MarkingView>[] getInitialMarkingVector() {
        //if(_initialMarkingVectorChanged)
        createInitialMarkingVector();
        return _initialMarkingVector;
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

    public List<MarkingView>[] getCurrentMarkingVector() {
        createCurrentMarkingVector();
        //        if(_currentMarkingVectorChanged)
        //        {
        //            createCurrentMarkingVector();
        //        }
        return _currentMarkingVector;
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

    public void setCurrentMarkingVector(int[] is) {
        int placeSize = _placeViews.size();

        for (int placeNo = 0; placeNo < placeSize; placeNo++) {
            _placeViews.get(placeNo).getCurrentMarkingView().get(0).setCurrentMarking(is[placeNo]);
        }
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getCapacityMatrix()
      */
    public int[] getCapacityMatrix() {
        createCapacityVector();
        return _capacityMatrix;
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

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getPriorityMatrix()
      */
    public int[] getPriorityMatrix() {
        createPriorityVector();
        return _priorityMatrix;
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

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getTimedMatrix()
      */
    public boolean[] getTimedMatrix() {
        createTimedVector();
        return _timedMatrix;
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

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getStateGroups()
      */
    public StateGroup[] getStateGroups() {
        StateGroup[] returnArray = new StateGroup[_stateGroups.size()];
        for (int i = 0; i < _stateGroups.size(); i++) {
            returnArray[i] = (StateGroup) _stateGroups.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getStateGroupsArray()
      */
    public ArrayList<StateGroup> getStateGroupsArray() {
        return this._stateGroups;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#print()
      */
    public void print() {
        System.out.println("No of Places = " + _placeViews.size() + "\"");
        System.out.println("No of Transitions = " + _transitionViews.size() + "\"");
        System.out.println("No of Arcs = " + _arcViews.size() + "\"");
        System.out.println("No of Labels = " + _labels.size() + "\" (Model View Controller Design Pattern)");
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#existsRateParameter(java.lang.String)
      */
    public boolean existsRateParameter(String name) {
        return _rateParameterHashSet.contains(name);
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#changeRateParameter(java.lang.String, java.lang.String)
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
      * @see pipe.interfaces.IPetriNet#hasTimedTransitions()
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
      * @see pipe.interfaces.IPetriNet#getTransitions()
      */
    public TransitionView[] getTransitionViews() {
        TransitionView[] returnArray = new TransitionView[_transitionViews.size()];

        for (int i = 0; i < _transitionViews.size(); i++) {
            returnArray[i] = _transitionViews.get(i);
        }
        return returnArray;
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#hasImmediateTransitions()
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
      * @see pipe.interfaces.IPetriNet#getTransitionName(int)
      */
    public String getTransitionName(int i) {
        return _transitionViews.get(i).getName();
    }

    // Function to check the structure of the Petri Net to ensure that if tagged
    // arcs are included then they obey the restrictions on how they can be used
    // (i.e. a transition may only have one input tagged Arc and one output
    // tagged Arc and if it has one it must have the other).
    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#validTagStructure()
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
                        if (currentArcView.getSource().equals(currentTrans)) {
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
                        } else if (currentArcView.getTarget().equals(currentTrans)) {
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
      * @see pipe.interfaces.IPetriNet#checkTransitionIDAvailability(java.lang.String)
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
      * @see pipe.interfaces.IPetriNet#checkPlaceIDAvailability(java.lang.String)
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
      * @see pipe.interfaces.IPetriNet#getPlaceIndex(java.lang.String)
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
      * @see pipe.interfaces.IPetriNet#hasValidatedStructure()
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
      * @see pipe.interfaces.IPetriNet#setValidate(boolean)
      */
    public void setValidate(boolean valid) {
        _model.setValidated(valid);
    }

    /* (non-Javadoc)
      * @see pipe.interfaces.IPetriNet#getTransitionByName(java.lang.String)
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
      * @see pipe.interfaces.IPetriNet#getTransition(int)
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
      * @see pipe.interfaces.IPetriNet#getPlaceByName(java.lang.String)
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
      * @see pipe.interfaces.IPetriNet#getPlaceById(java.lang.String)
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
            List<MarkingView> weights = arc.getWeightSimple();
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

    //TODO: WORK OUT IF THIS IS STILL AN ISSUE?
    public boolean hasFunctionalRatesOrWeights() {
        //        for (ArcView arc : _arcViews.values()) {
        //            List<MarkingView> weights = arc.getWeightSimple();
        //            for (MarkingView weight : weights) {
        //                try {
        //                    Integer.parseInt(weight.getCurrentFunctionalMarking());
        //                } catch (Exception e) {
        //                    return true;
        //                }
        //            }
        //        }
        //        for (TransitionView tran : _transitionViews.values()) {
        //            try {
        //                Double.parseDouble(tran.getRateExpr());
        //            } catch (Exception e) {
        //                return true;
        //            }
        //        }
        return false;
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

    //TODO: STUBBED METHOD DELETE
    public boolean[] areTransitionsEnabled(final LinkedList<MarkingView>[] state) {
        return new boolean[0];  //To change body of created methods use File | Settings | File Templates.
    }
}