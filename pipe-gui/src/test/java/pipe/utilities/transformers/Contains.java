package pipe.utilities.transformers;

import org.mockito.ArgumentMatcher;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

public class Contains {
    private Contains() {}

    public static ContainsAction thisAction(UndoableEdit edit) {
        return new ContainsAction(edit);
    }

    public static class ContainsAction extends ArgumentMatcher<UndoableEditEvent> {


        private ContainsAction(UndoableEdit expectedEdit) {
            this.expectedEdit = expectedEdit;
        }

        private UndoableEdit expectedEdit;

        @Override
        public boolean matches(Object argument) {
            UndoableEditEvent event = (UndoableEditEvent) argument;
            UndoableEdit edit = event.getEdit();

            return edit.equals(expectedEdit);
        }
    }
}
