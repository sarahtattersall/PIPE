package pipe.petrinet.writer.reflectionCreator;


import com.google.common.base.Joiner;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.PetriNetComponent;
import pipe.models.Pnml;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ElementCreator {

    private final List<TypeHandler> handlers = new LinkedList<TypeHandler>();

    public ElementCreator() {
        handlers.add(new MapHandler());
        handlers.add(new ColorHandler());
        handlers.add(new DefaultHandler());
    }

    public <T extends PetriNetComponent> Element createElement(T component, Document document) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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

            for (TypeHandler handler : handlers) {
                if (handler.canHandle(fieldValue)) {
                    handler.handle(pnml, fieldValue, element);
                    return;
                }
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
