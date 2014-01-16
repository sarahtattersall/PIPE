package pipe.views.builder;

import pipe.models.component.annotation.Annotation;
import pipe.views.viewComponents.AnnotationNote;

public class AnnotationNodeBuilder {
    final Annotation annotation;

    public AnnotationNodeBuilder(Annotation annotation) {
        this.annotation = annotation;
    }

    public AnnotationNote build() {
        int x = new Double(annotation.getX()).intValue();
        int y = new Double(annotation.getY()).intValue();
        int width = new Double(annotation.getWidth()).intValue();
        int height = new Double(annotation.getHeight()).intValue();

        AnnotationNote annotationNote =
                new AnnotationNote(annotation.getText(), x, y, width, height, annotation.hasBoarder());
        annotationNote.setModel(annotation);
        return annotationNote;
    }
}
