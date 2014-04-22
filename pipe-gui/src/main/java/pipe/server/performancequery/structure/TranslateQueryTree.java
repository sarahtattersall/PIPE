package pipe.server.performancequery.structure;

import pipe.common.*;
import pipe.common.dataLayer.StateGroup;
import pipe.handlers.StringHelper;
import pipe.modules.interfaces.QueryConstants;
import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.SimpleValueNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class TranslateQueryTree implements StructureLoggingHandler
{

	private final SimplePlaces places;
	private final SimpleTransitions						transitions;
	private final ArrayList<StateGroup>					allStateGroups;
	private final HashMap<String, ArrayList<String>>	stateLabels;
	private final ArrayList<SimpleNode>					nodes;
	private final AnalysisSettings						settings;

	private final ModFileBuilder						modFiles;

	// this needs to be populated with state and count measure
	// then steady state ran once
	private PerformanceMeasure							performanceMeasures;

	// how select state groups i need from allStateGroups?
	private ArrayList<StateGroup>						srcStateGrps, targetStateGrps, exStateGrps;

	public TranslateQueryTree(	final SimplePlaces places,
								final SimpleTransitions transitions,
								final ArrayList<StateGroup> stateGroups,
								final HashMap<String, ArrayList<String>> stateLabels,
								final ArrayList<SimpleNode> nodes,
								final AnalysisSettings settings,
								final String path) {
		this.places = places;
		this.transitions = transitions;
		this.allStateGroups = stateGroups;
		this.stateLabels = stateLabels;
		this.nodes = nodes;
		this.settings = settings;
		this.performanceMeasures = new PerformanceMeasure();

		this.modFiles = new ModFileBuilder(path);

	}

	private ModFile addModFile(final OperationSubtree subtree)
	{
		return this.modFiles.addNewModfile(subtree);
	}

	private void addStateGroups(final ArrayList<StateGroup> copyToStateGroups, final ArrayList<String> labels) throws QueryServerException
	{
		if (labels != null)
		{
			for (final String label : labels)
			{
				copyToStateGroups.add(this.getStateGroup(label));
			}
		}

	}

	private void appendStartTargetExcluded()
	{
		// Add source conditions

		this.modFiles.append("\t\\sourcecondition{");
		this.stateGroups(this.srcStateGrps);
		this.modFiles.append("}\n");

		// Add target conditions

		this.modFiles.append("\t\\targetcondition{");
		this.stateGroups(this.targetStateGrps);
		this.modFiles.append("}\n");

		// Add excluded if specified!
		if (this.exStateGrps != null && !this.exStateGrps.isEmpty())
		{
			this.modFiles.append("\t\\excludedcondition{");
			this.stateGroups(this.exStateGrps);
			this.modFiles.append("}\n");
		}
	}

	private void clearStateGroups()
	{
		if (this.srcStateGrps != null)
		{
			this.srcStateGrps.clear();
		}
		if (this.targetStateGrps != null)
		{
			this.targetStateGrps.clear();
		}
		if (this.exStateGrps != null)
		{
			this.exStateGrps.clear();
		}
	}

	private void countMeasure()
	{
		for (int i = 0; i < this.performanceMeasures.counts.size(); i++)
		{
			// Set the "Transition <state id>" as the identifier
			this.modFiles	.append("\t\\countmeasure{")
							.append("Transition ")
							.append(this.performanceMeasures.counts.get(i))
							.append("}{\n");

			// Add the estimators
			this.modFiles.append("\t\t\\estimator{mean}\n");

			// set the transition we wish to count
			this.modFiles	.append("\t\t\\transition{")
							.append(this.performanceMeasures.counts.get(i))
							.append("}\n");

			this.modFiles.append("\t}\n");
		}
	}

	private void createNewPerformanceMeasures()
	{
		this.performanceMeasures = new PerformanceMeasure();
	}

	private void DoFiringTransMod(final OperationSubtree subtree) throws QueryServerException, IOException
	{
		// get firing rate actions
		// need to get correct string to reference, not sure which this is!
		final SimpleOperationNode opNode = (SimpleOperationNode) subtree.getNode();

		final String childNodeID = opNode.getChildren().get(QueryConstants.firingRateChildAction);
		if (childNodeID == null)
		{
			throw new QueryServerException("Couldn't find child node");
		}
		final SimpleNode actionNode = this.getSimpleNode(childNodeID);

		String actionName;
		if (actionNode instanceof SimpleValueNode)
		{
			actionName = ((SimpleValueNode) actionNode).getValue();
		}
		else
		{
			final String msg = "argument not of type valuenode!";
			throw new QueryServerException(msg);
		}
		// add count measure to performanceMeasures list, can include condition
		for (int i = 0; i < this.transitions.length; i++)
		{
			if (this.transitions.names[i] != null && this.transitions.names[i].equals(actionName))
			{
				actionName = this.transitions.ids[i];
			}
		}
		this.performanceMeasures.counts.add(actionName);

		this.setupSteadyStates(subtree);

		if (this.places.length > 0)
		{
			this.countMeasure();
		}

		this.createNewPerformanceMeasures();

		this.modFiles.finaliseSteadyState();
	}

	private void DoMomentTransMod(final OperationSubtree subtree) throws QueryServerException, IOException
	{
		// get moment parameters, a num node and distribution
		// need to get correct string to reference, not sure which this is!
		final SimpleOperationNode opNode = (SimpleOperationNode) subtree.getNode();
		final String numID = opNode.getChildren().get(QueryConstants.momentChildNum);
		final String densDistID = opNode.getChildren().get(QueryConstants.momentChildDensDist);

		if (numID == null | densDistID == null)
		{
			final String msg = "Child nodes not initialised!";
			throw new QueryServerException(msg);
		}
		SimpleNode numNode, densityNode;
		numNode = this.getSimpleNode(numID);
		densityNode = this.getSimpleNode(densDistID);

		String num;
		HashMap<String, String> kids;
		if (numNode instanceof SimpleValueNode)
		{
			num = ((SimpleValueNode) numNode).getValue();
		}
		else
		{
			final String msg = "argument not of type valuenode!";
			throw new QueryServerException(msg);
		}
		if (densityNode instanceof SimpleOperationNode)
		{
			kids = ((SimpleOperationNode) densityNode).getChildren();
		}
		else
		{
			final String msg = "argument not of type valuenode!";
			throw new QueryServerException(msg);
		}

		this.getPassageKids(kids);
		final ModFile m = this.addModFile(subtree);

		if (this.places.length > 0)
		{
			this.moment(num);
		}

		this.clearStateGroups();
		this.modFiles.finalise(m);
	}

	private void DoPassageTransMod(final OperationSubtree subtree) throws QueryServerException, IOException
	{
		// get start, target and excluded states
		final SimpleOperationNode opNode = (SimpleOperationNode) subtree.getNode();
		final HashMap<String, String> kids = opNode.getChildren();

		this.getPassageKids(kids);

		final ModFile m = this.addModFile(subtree);

		if (this.places.length > 0)
		{
			this.passageTime();
		}
		this.clearStateGroups();

		this.modFiles.finalise(m);
	}

	private void DoProbInStates(final OperationSubtree subtree) throws QueryServerException, IOException
	{
		// get start, target and excluded states
		final SimpleOperationNode opNode = (SimpleOperationNode) subtree.getNode();
		final HashMap<String, String> kids = opNode.getChildren();

		this.srcStateGrps = new ArrayList<StateGroup>();
		this.targetStateGrps = new ArrayList<StateGroup>();

		this.getChildStates(kids, QueryConstants.probInStatesChildStartStates, this.srcStateGrps);
		this.getChildStates(kids, QueryConstants.probInStatesChildObservedStates, this.targetStateGrps);

		final ModFile m = this.addModFile(subtree);

		if (this.places.length > 0)
		{
			this.probInStates();
		}
		this.clearStateGroups();

		this.modFiles.finalise(m);
	}

	private void DoSSPTransMod(final OperationSubtree subtree) throws QueryServerException, IOException
	{
		// get SSP params, StateFunc node and set of States
		// need to get correct string to reference, not sure which this is!
		final SimpleOperationNode opNode = (SimpleOperationNode) subtree.getNode();
		final String stateFuncID = opNode.getChildren().get(QueryConstants.sSPChildStateFunc);

		if (stateFuncID == null)
		{
			final String msg = "Child nodes not initialised!";
			throw new QueryServerException(msg);
		}

		final SimpleNode stateFuncNode = this.getSimpleNode(stateFuncID);

		String stateFuncKey;

		if (stateFuncNode instanceof SimpleValueNode)
		{
			stateFuncKey = ((SimpleValueNode) stateFuncNode).getValue();
		}
		else
		{
			final String msg = "argument not of type valuenode!";
			throw new QueryServerException(msg);
		}

		final StringBuilder replacement = new StringBuilder(stateFuncKey);
		final HashMap<String, String> nameToId = new HashMap<String, String>();
		for (int i = 0; i < this.places.length; i++)
		{
			nameToId.put(this.places.names[i], this.places.ids[i]);
		}

		this.exStateGrps = new ArrayList<StateGroup>();
		if (opNode.getChildren().get(QueryConstants.sSPChildStates) != null)
		{
			this.getChildStates(opNode.getChildren(), QueryConstants.sSPChildStates, this.exStateGrps);
		}

		if (StringHelper.hasSpecifiedLabelsAndReplace(stateFuncKey, replacement, nameToId))
		{
			// get states
			this.performanceMeasures.states.add(replacement.toString());
		}
		else
		{
			throw new QueryServerException("StateFunction isn't valid, this should have been verified on the client in EditPerformanceTreeNodeAction");
		}

		this.setupSteadyStates(subtree);

		if (this.places.length > 0)
		{
			this.stateMeasure();
		}
		this.clearStateGroups();
		this.createNewPerformanceMeasures();

		this.modFiles.finaliseSteadyState();
	}

	private void genModForModel()
	{
		this.modFiles.addToModel();

		if (this.places.length > 0)
		{
			this.model();
		}
	}

	public void genModForSubtree(final OperationSubtree subtree) throws QueryServerException, IOException
	{
		if (!this.modFiles.hasModel())
		{
			this.genModForModel();
		}
		final SimpleNode node = subtree.getNode();
		if (node instanceof SimpleOperationNode)
		{
			final SimpleOperationNode opNode = (SimpleOperationNode) node;
			final PetriNetNode nodeType = opNode.getType();
			// types of nodes we may encounter and currently support!

			switch (nodeType)
			{
				case FIRINGRATE :
					this.DoFiringTransMod(subtree);
					break;
				case MOMENT :
					this.DoMomentTransMod(subtree);
					break;
				case PASSAGETIMEDENSITY :
					this.DoPassageTransMod(subtree);
					break;
				case STEADYSTATEPROB :
					this.DoSSPTransMod(subtree);
					break;
				case PROBINSTATES :
					this.DoProbInStates(subtree);
					break;
				case CONVOLUTION :

					break;
				default :
					throw new QueryServerException("Node type:" + nodeType + " currently unsupported!");
			}

		}

	}

	private void getChildStates(final HashMap<String, String> kids,
								final String childRole,
								final ArrayList<StateGroup> group) throws QueryServerException
	{
		final String nodeID = kids.get(childRole);

		if (nodeID == null)
		{
			final String msg = "Child node not initialised!";
			throw new QueryServerException(msg);
		}

		final SimpleNode stateNode = this.getSimpleNode(nodeID);

		String stateNodeKey;
		if (stateNode instanceof SimpleValueNode)
		{
			stateNodeKey = ((SimpleValueNode) stateNode).getValue();
		}
		else
		{
			final String msg = "argument not of type valuenode!";
			throw new QueryServerException(msg);
		}

		// get state labels
		final ArrayList<String> labels = this.stateLabels.get(stateNodeKey);

		this.addStateGroups(group, labels);

		if (stateNodeKey != null && group.isEmpty())
		{
			final String msg = "state groups not populated properly";
			throw new QueryServerException(msg);
		}
	}

	private void getPassageKids(final HashMap<String, String> kids) throws QueryServerException
	{
		this.srcStateGrps = new ArrayList<StateGroup>();
		this.targetStateGrps = new ArrayList<StateGroup>();

		this.getChildStates(kids, QueryConstants.pTDChildStartStates, this.srcStateGrps);
		this.getChildStates(kids, QueryConstants.pTDChildTargetStates, this.targetStateGrps);

		// optional
		if (kids.get(QueryConstants.pTDChildExcludedStates) != null)
		{
			this.exStateGrps = new ArrayList<StateGroup>();
			this.getChildStates(kids, QueryConstants.pTDChildExcludedStates, this.exStateGrps);
		}

	}

	private SimpleNode getSimpleNode(final String nodeID)
	{
		for (final SimpleNode node : this.nodes)
		{
			if (nodeID.equals(node.getID()))
			{
				return node;
			}
		}
		return null;
	}

	private StateGroup getStateGroup(final String name) throws QueryServerException
	{
		for (final StateGroup s : this.allStateGroups)
		{
			if (name.equals(s.getName()))
			{
				return s;
			}
		}
		throw new QueryServerException("StateGroup:" + name + " not found");
	}

	private String getTransitionConditions(final int transitionNum)
	{
		String condition = "";

		final Iterator<SimpleArc> arcsTo = this.transitions.arcsTo.get(transitionNum).iterator();
		SimpleArc currArc;

		if (arcsTo.hasNext())
		{
			currArc = arcsTo.next();
			condition += currArc.placeId + " > " + (currArc.weight - 1);
		}

		while (arcsTo.hasNext())
		{
			currArc = arcsTo.next();
			condition += " && " + currArc.placeId + " > " + (currArc.weight - 1);
		}

		return condition;
	}

	private void initial()
	{
		this.modFiles.append("\t\\initial{\n");
		this.modFiles.append("\t\t");

		for (int i = 0; i < this.places.length; i++)
		{
			this.modFiles	.append(this.places.ids[i])
							.append(" = ")
							.append(this.places.marking[i])
							.append("; ");
		}

		this.modFiles.append("\n\t}\n");
	}

	/**
	 * This method is to limit the number of states that are generated to a
	 * maximum of 2 million. This ensures infinite state models terminate
	 */
	private void maxStates()
	{
		this.modFiles.append("\\generation{\n\t\\maxstates{2000000}\n}\n");
	}

	private void model()
	{
		this.modFiles.append("\\model{\n");
		this.stateVector();
		this.initial();
		this.transitions();
		this.modFiles.append("}\n\n");

		this.maxStates();
	}

	private void moment(final String maxMoment)
	{
		this.modFiles.append("\n\\moment{\n");

		this.appendStartTargetExcluded();

		// append max moment
		this.modFiles.append("\t\\max_moment{").append(maxMoment + "}\n");

		this.modFiles.append("}\n");
	}

	private void passageTime()
	{
		// Set solution type to 'sor'
		this.modFiles.append("\\solution{\n\t\\method{sor}\n}");

		this.modFiles.append("\n\\passage{\n");

		this.appendStartTargetExcluded();

		// Add time parameters
		this.modFiles.append("\t\\t_start{").append(this.settings.startTime + "}\n");
		this.modFiles.append("\t\\t_stop{").append(this.settings.endTime + "}\n");
		this.modFiles.append("\t\\t_step{").append(this.settings.timeStep + "}\n");

		this.modFiles.append("}\n");

	}

	private void probInStates()
	{
		// Set solution type to 'sor'
		this.modFiles.append("\\solution{\n\t\\method{sor}\n}");

		this.modFiles.append("\n\\transient{\n");

		this.appendStartTargetExcluded();

		// Add time parameters
		this.modFiles.append("\t\\t_start{").append(this.settings.startTime + "}\n");
		this.modFiles.append("\t\\t_stop{").append(this.settings.endTime + "}\n");
		this.modFiles.append("\t\\t_step{").append(this.settings.timeStep + "}\n");

		this.modFiles.append("}\n");

	}

	void setupSteadyStates(final OperationSubtree subtree)
	{
		this.modFiles.addToSteadyStateMod(subtree);
	}

	private void stateGroups(final ArrayList<StateGroup> stateGroups)
	{
		String[] currentCondition;
		int groupCount = 0;

		for (final StateGroup curStateGroup : stateGroups)
		{
			currentCondition = curStateGroup.getConditions();

			// for any group after the first add the OR
			if (groupCount > 0)
			{
				this.modFiles.append(" || ");
			}

			this.modFiles.append("(").append(currentCondition[0]);

			for (int i = 1; i < currentCondition.length; i++)
			{
				this.modFiles.append(" && ").append(currentCondition[i]);
			}

			this.modFiles.append(")");

			groupCount++;
		}
	}

	private void stateMeasure()
	{
		for (int i = 0; i < this.performanceMeasures.states.size(); i++)
		{
			// Set the "State <state id>" as the identifier
			this.modFiles	.append("\t\\statemeasure{")
							.append("State ")
							.append(this.performanceMeasures.states.get(i))
							.append("}{\n");

			// Add the estimators
			this.modFiles.append("\t\t\\estimator{");

			this.modFiles.append("mean variance stddev distribution ");

			this.modFiles.append("}\n");

			// set the state we wish to measure
			this.modFiles.append("\t\t\\expression{");
			if (this.exStateGrps != null && !this.exStateGrps.isEmpty())
			{
				this.stateGroups(this.exStateGrps);
				this.modFiles.append(" ? ");
			}

			this.modFiles.append(this.performanceMeasures.states.get(i));

			if (this.exStateGrps != null && !this.exStateGrps.isEmpty())
			{
				this.modFiles.append(" : ").append(StringHelper.SENTINEL);
			}

			this.modFiles.append("}\n");

			this.modFiles.append("\t}\n");
		}
	}

	private void stateVector()
	{
		this.modFiles.append("\t\\statevector{\n");
		this.modFiles.append("\t\t\\type{short}{");

		this.modFiles.append(this.places.ids[0]);

		for (int i = 1; i < this.places.length; i++)
		{
			this.modFiles.append(", " + this.places.ids[i]);
		}

		this.modFiles.append("}\n");
		this.modFiles.append("\t}\n\n");
	}

	private void transitions()
	{

		for (int i = 0; i < this.transitions.length; i++)
		{
			this.modFiles.append("\t\\transition{").append(this.transitions.ids[i]).append("}{\n");
			// Add the transition condition, i.e. all inputs to the transition
			// hold a marking
			this.modFiles.append("\t\t\\condition{").append(this.getTransitionConditions(i)).append("}\n");
			this.modFiles.append("\t\t\\action{\n");

			final LinkedList<SimpleArc> arcToTransitions = this.transitions.arcsTo.get(i);
			final LinkedList<SimpleArc> arcFromTransitions = this.transitions.arcsFrom.get(i);

			// Consider when a place is both an input and an output to the
			// transition, potentially of different weights
			// We assume that each place can have a maximum of one arc to and/or
			// from the transition
			arcToLoop : for (final SimpleArc currArc : arcToTransitions)
			{
				// Check if the current transition has the place as both an
				// output and an input,
				// if so compact to a single action of the correct weight
				for (final SimpleArc compareArc : arcFromTransitions)
				{
					if (currArc.placeId.equals(compareArc.placeId))
					{
						if (currArc.weight > compareArc.weight)
						{
							// Adjust source place's weight and remove the
							// target place
							currArc.weight -= compareArc.weight;
							arcFromTransitions.remove(compareArc);
							break;
						}
						else if (currArc.weight < compareArc.weight)
						{
							// Adjust the weight of the target place and skip
							// writing to the source place
							compareArc.weight -= currArc.weight;
							continue arcToLoop;
						}
						else
						// Equal weights cancel each other so print nothing
						{
							arcFromTransitions.remove(compareArc);
							continue arcToLoop;
						}
					}
				}

				this.modFiles.append("\t\t\tnext->").append(currArc.placeId);
				this.modFiles	.append(" = ")
								.append(currArc.placeId)
								.append(" - ")
								.append(currArc.weight)
								.append(";\n");
			}

			// Add a marking to all outputs of the transition
			for (final SimpleArc currArc : this.transitions.arcsFrom.get(i))
			{
				this.modFiles.append("\t\t\tnext->").append(currArc.placeId);
				this.modFiles	.append(" = ")
								.append(currArc.placeId)
								.append(" + ")
								.append(currArc.weight)
								.append(";\n");
			}

			this.modFiles.append("\t\t}\n");

			// Specify the rate / weight of the transition
			if (this.transitions.timed[i])
			{
				this.modFiles.append("\t\t\\rate{").append(this.transitions.rate[i]).append("}\n");
			}
			else
			{
				this.modFiles.append("\t\t\\weight{").append(this.transitions.rate[i]).append("}\n");
			}

			this.modFiles.append("\t}\n");

		}
	}
}
