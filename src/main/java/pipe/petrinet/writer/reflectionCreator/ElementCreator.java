package pipe.petrinet.writer.reflectionCreator;


import com.google.common.base.Joiner;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.Pnml;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ElementCreator {

    private final List<TypeHandler> handlers = new LinkedList<TypeHandler>();
    private final Document document;

    public ElementCreator(Document document) {
        this.document = document;
        handlers.add(new MapHandler());
        handlers.add(new ColorHandler());
        handlers.add(new CollectionHandler(this));
        handlers.add(new DefaultHandler());
    }

    public <T extends PetriNetComponent> Element createElement(T component) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<? extends PetriNetComponent> clazz = component.getClass();


        String tag = getTagForClass(clazz);
        Element element = document.createElement(tag);

        for (Field field : clazz.getSuperclass().getDeclaredFields()) {
            createAttributeIfFieldAnnotated(element, field, component);
        }

        for (Field field : clazz.getDeclaredFields()) {
            createAttributeIfFieldAnnotated(element, field, component);
        }

        return element;
    }

    private  <T extends PetriNetComponent> void createAttributeIfFieldAnnotated(Element element, Field field, T component) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Pnml pnml = field.getAnnotation(Pnml.class);
        if (pnml != null) {
            Object fieldValue = PropertyUtils.getProperty(component, field.getName());
            handleObject(pnml, fieldValue, element);

        }
    }

    private void handleObject(Pnml pnmlAnnotation, Object object, Element element) {
        for (TypeHandler handler : handlers) {
            if (handler.canHandle(object)) {
                handler.handle(pnmlAnnotation, object, element);
                return;
            }
        }
    }

    private String getTagForClass(Class clazz) {
        if (clazz.getAnnotation(Pnml.class) != null) {
            Pnml pnml = (Pnml) clazz.getAnnotation(Pnml.class);
            return pnml.value();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    private static interface TypeHandler {
        boolean canHandle(Object object);
        void handle(Pnml pnmlAnnotation, Object object, Element element);
    }

    private static class MapHandler implements TypeHandler {

        @Override
        public boolean canHandle(Object object) {
            return object instanceof Map;
        }

        @Override
        public void handle(Pnml pnmlAnnotation, Object object, Element element) {
            Map map = (Map) object;
            element.setAttribute(pnmlAnnotation.value(),
                    Joiner.on(",").withKeyValueSeparator(",").join(map));
        }
    }

    private static class ColorHandler implements TypeHandler {

        @Override
        public boolean canHandle(Object object) {
            return object instanceof Color;
        }

        @Override
        public void handle(Pnml pnmlAnnotation, Object object, Element element) {
            Color color = (Color) object;
            element.setAttribute("red", String.valueOf(color.getRed()));
            element.setAttribute("green", String.valueOf(color.getGreen()));
            element.setAttribute("blue", String.valueOf(color.getBlue()));
        }
    }

    private class CollectionHandler implements TypeHandler {
        private final ElementCreator creator;

        private CollectionHandler(final ElementCreator creator) {
            this.creator = creator;
        }


        @Override
        public boolean canHandle(Object object) {
            return object instanceof Collection;
        }

        @Override
        public void handle(final Pnml pnmlAnnotation, final Object object, final Element element) {
            handlePetriNetComponents(pnmlAnnotation, object, element);
        }

        public <T extends PetriNetComponent>  void handlePetriNetComponents(Pnml pnmlAnnotation, Object object, Element element) {
            Collection<T> collection = (Collection<T>) object;
            for (T item : collection) {
                Element childElement = null;
                try {
                    childElement = creator.createElement(item);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                element.appendChild(childElement);
            }
        }
    }

    private static class DefaultHandler implements TypeHandler {
        @Override
        public boolean canHandle(Object object) {
            return true;
        }

        @Override
        public void handle(Pnml pnmlAnnotation, Object object, Element element) {
            element.setAttribute(pnmlAnnotation.value(), object.toString());
        }
    }
}
