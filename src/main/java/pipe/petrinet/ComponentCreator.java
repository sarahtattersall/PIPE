package pipe.petrinet;

import org.w3c.dom.Element;

public interface ComponentCreator<T> {
    public T create(Element element);
}
