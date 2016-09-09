package pipe.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PetriNetTab;

import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;

final class PetriNetChangeListener implements PropertyChangeListener {
	/**
	 * 
	 */
	private PipeApplicationView pipeApplicationView;

	/**
	 * @param pipeApplicationView
	 */
	PetriNetChangeListener(PipeApplicationView pipeApplicationView) {
		this.pipeApplicationView = pipeApplicationView;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	    String msg = evt.getPropertyName();
	    if (msg.equals(PetriNet.PETRI_NET_NAME_CHANGE_MESSAGE)) {
	        PetriNetName name = (PetriNetName) evt.getNewValue();
	        pipeApplicationView.updateSelectedTabName(name.getName());
	    } else if (msg.equals(PetriNet.NEW_TOKEN_CHANGE_MESSAGE) || msg.equals(
	            PetriNet.DELETE_TOKEN_CHANGE_MESSAGE)) {
	        pipeApplicationView.refreshTokenClassChoices();
	    } else  if (msg.equals(PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE)) {
            PetriNet petriNet = (PetriNet) evt.getNewValue();
            pipeApplicationView.registerNewPetriNet(petriNet);
        } else if (msg.equals(PetriNetManagerImpl.REMOVE_PETRI_NET_MESSAGE)) {
            pipeApplicationView.removeCurrentTab();
        } else if (msg.equals(PetriNetManagerImpl.NEW_INCLUDE_HIERARCHY_MESSAGE)) {
        	IncludeHierarchy include = (IncludeHierarchy) evt.getNewValue(); 
			pipeApplicationView.registerNewIncludeHierarchy(include); 
		} else if (msg.equals(PipeApplicationController.KEEP_ROOT_TAB_ACTIVE_MESSAGE)) {
			 PetriNetTab tab = (PetriNetTab) evt.getNewValue(); 
			pipeApplicationView.forceActiveTab(tab);   
		}

	}
}