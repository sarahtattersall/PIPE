package pipe.controllers.arcCreator;

import pipe.models.component.Connectable;
import pipe.models.component.Token;

public interface ArcActionCreator {
    public <S extends Connectable, T extends Connectable> void create(S source, T target, Token token);

    public <S extends Connectable, T extends Connectable>  boolean canCreate(S source, T target);
}
