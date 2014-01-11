package pipe.historyActions;

import pipe.views.GroupTransitionView;
import pipe.views.TransitionView;

import java.util.ArrayList;

/**
 * 
 * @author Alex Charalambous
 */
public class GroupTransition extends HistoryItem
{

	private final GroupTransitionView groupTransition;

	public GroupTransition(GroupTransitionView _groupTransition) {
		groupTransition = _groupTransition;
	}

	/** */
	public void undo() {
		groupTransition.ungroupTransitionsHelper();
	}

	public void redo() {
		TransitionView foldedInto = groupTransition.getFoldedInto();
		ArrayList<TransitionView> transitionViews = new ArrayList<TransitionView>();
		for(TransitionView t:groupTransition.getTransitions()){
			transitionViews.add(t);
		}
		groupTransition.getTransitions().clear();
		// Make the transition "foldedInto" group the transitions 
		// "transitions" into the group transition: "groupTransition"
		foldedInto.groupTransitionsHelper(
                transitionViews, groupTransition);
	}
}
