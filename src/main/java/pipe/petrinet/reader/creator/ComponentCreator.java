package pipe.petrinet.reader.creator;

import org.w3c.dom.Element;

public interface ComponentCreator<T> {
    public T create(Element element);
}
