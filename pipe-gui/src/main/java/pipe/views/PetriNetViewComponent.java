package pipe.views;

import pipe.gui.PetriNetTab;

public interface PetriNetViewComponent {
    void delete();

    boolean isDeleted();

    /**
     * Each subclass should know how to add itself to a PetriNetTab
     * @param tab to add itself to
     */
    void addToPetriNetTab(PetriNetTab tab);


    public int getLayerOffset();
}
