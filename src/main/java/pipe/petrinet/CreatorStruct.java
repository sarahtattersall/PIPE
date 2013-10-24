package pipe.petrinet;

public class CreatorStruct {
    public final PlaceCreator placeCreator;
    public final TransitionCreator transitionCreator;
    public final ArcCreator arcCreator;
    public final AnnotationCreator annotationCreator;
    public final RateParameterCreator rateParameterCreator;
    public final TokenCreator tokenCreator;
    public final StateGroupCreator stateGroupCreator;

    public CreatorStruct(PlaceCreator placeCreator,
                         TransitionCreator transitionCreator,
                         ArcCreator arcCreator,
                         AnnotationCreator annotationCreator,
                         RateParameterCreator rateParameterCreator,
                         TokenCreator tokenCreator,
                         StateGroupCreator stateGroupCreator) {
        this.placeCreator = placeCreator;
        this.transitionCreator = transitionCreator;
        this.arcCreator = arcCreator;
        this.annotationCreator = annotationCreator;
        this.rateParameterCreator = rateParameterCreator;
        this.tokenCreator = tokenCreator;
        this.stateGroupCreator = stateGroupCreator;
    }

}
