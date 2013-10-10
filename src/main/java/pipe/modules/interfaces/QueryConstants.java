/**
 * QueryConstants
 * 
 * This interface is a replica of grip.gui.Constants, extended with
 * some additional constants required for query manipulation. Didn't
 * want to modify the main Constants file, since the query editor is 
 * supposed to be completely modularised and shouldn't interfere with
 * the orginial PIPE components' design.
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.interfaces;
import java.awt.Color;

public interface QueryConstants
{

	/** Performance Tree operation mode definitions */

	int					GRID								= 300;
	int					DRAW								= 301;
	int					SELECT								= 302;
	int					DRAG								= 303;
	int					DELETE								= 304;
	int					LOADING								= 305;													// creating
	// components
	// while
	// parsing
	// in a
	// PTML
	// file
	int					NODE								= 306;
	int					ARC									= 307;

	/** Performance Tree object type definitions */

	int					RESULT_NODE							= 400;
	int					SEQUENTIAL_NODE						= 401;
	int					PASSAGETIMEDENSITY_NODE				= 402;
	int					DISTRIBUTION_NODE					= 403;
	int					CONVOLUTION_NODE					= 404;
	int					PROBININTERVAL_NODE					= 405;
	int					PROBINSTATES_NODE					= 406;
	int					MOMENT_NODE							= 407;
	int					FIRINGRATE_NODE						= 408;
	int					STEADYSTATEPROB_NODE				= 409;
	int					STEADYSTATESTATES_NODE				= 410;
	int					STATESATTIME_NODE					= 411;
	int					ININTERVAL_NODE						= 412;
	int					SUBSET_NODE							= 413;
	int					DISCON_NODE							= 414;
	int					NEGATION_NODE						= 415;
	int					ARITHCOMP_NODE						= 416;
	int					ARITHOP_NODE						= 417;

	int					RANGE_NODE							= 419;
	int					STATES_NODE							= 420;
	int					ACTIONS_NODE						= 421;
	int					NUM_NODE							= 422;
	int					BOOL_NODE							= 423;
	int					STATEFUNCTION_NODE					= 424;

	int					MACRO_NODE							= 425;
	int					ARGUMENT_NODE						= 426;
	int					PERCENTILE_NODE						= 427;

	/** Node return types */

	String				MULTI_TYPE							= "Multiple return type";								// used
	// for
	// Sequential
	// node
	String				NUM_TYPE							= "Numerical value";
	String				BOOL_TYPE							= "Boolean value";
	String				STATES_TYPE							= "Set of states";
	String				ACTIONS_TYPE						= "Set of actions";
	String				RANGE_TYPE							= "Numerical range";
	String				FUNC_TYPE							= "State function";
	String				DIST_TYPE							= "Probability distribution function";
	String				DENS_TYPE							= "Probability density function";
	String				MACRO_TYPE							= "Macro (no return type yet)";
	String				ARGUMENT_TYPE						= "Argument";

	/** Node Actions Labels */
	String				arithOpChildNum1					= "num. value 1",
	arithCompChildNum1 = "num. value 1";
	String				arithOpChildNum2					= "num. value 2",
	arithCompChildNum2 = "num. value 2";

	String				convChildDensity1					= "density 1";
	String				convChildDensity2					= "density 2";
	String				disConChildBool1					= "bool value 1";
	String				disConChildBool2					= "bool value 2";
	String				distChildDensity					= "density";
	String				firingRateChildAction				= "actions";
	String				inIntervalChildNum					= "num. value";
	String				inIntervalChildRange				= "range";
	String				momentChildNum						= "moment";
	String				momentChildDensDist					= "density / distribution";
	String				percentileChildDensity				= "density / distribution";
	String				percentileChildNum					= "percentile";
	String				negChildBool						= "bool value";
	String				sSSChildStartStates					= "start states",
	probInStatesChildStartStates = "start states", pTDChildStartStates = "start states";

	String				pTDChildTargetStates				= "target states";
	String				pTDChildExcludedStates				= "excluded states";
	String				probInIntervalChildDens				= "density";
	String				probInIntervalChildRange			= "time range";
	String				probInStatesChildObservedStates		= "observed states";
	String				probInStatesChildNum				= "time instant";
	String				rangeChildFromNum					= "from";
	String				rangeChildToNum						= "to";
	String				statesAtTChildNum					= "time";
	String				sSSChildRange						= "prob. range",
	statesAtTChildRange = "prob. range";

	String				sSPChildStateFunc					= "state function";
	String				sSPChildStates						= "states";
	String				subset1								= "set 1";
	String				subset2								= "set 2";
	String				resultQuery							= "query";

	/** For drawing */

	Color				ELEMENT_LINE_COLOUR					= Color.BLACK;
	Color				ELEMENT_FILL_COLOUR					= Color.WHITE;
	Color				SELECTION_LINE_COLOUR				= new Color(0, 0, 192);
	Color				SELECTION_FILL_COLOUR				= new Color(192, 192, 255);
	Color				HIGHLIGHTED_COLOUR					= new Color(192, 0, 0);
	int					COMPONENT_DRAW_OFFSET				= 5;

	/** For nodes */

	int					NODE_WIDTH							= 65;
	int					NODE_HEIGHT							= 35;

	/** For status indicators */

	int					STATUS_INDICATOR_HEIGHT				= 15;
	public Color		EVALUATION_NOT_SUPPORTED_COLOUR		= Color.RED;
	public Color		EVALUATION_NOT_STARTED_YET_COLOUR	= Color.YELLOW;
	public Color		EVALUATION_IN_PROGRESS_COLOUR		= Color.ORANGE;
	public Color		EVALUATION_COMPLETE_COLOUR			= Color.GREEN;
	public Color		EVALUATION_FAILED_COLOUR			= Color.BLACK;

	public String		EVALNOTSUPPORTED					= "Evaluation of this node is not supported yet";
	public String		EVALNOTSTARTED						= "Evaluation not started";
	public String		EVALINPROGRESS						= "Evaluation in progress";
	public String		EVALCOMPLETE						= "Evaluation completed";
	public String		EVALFAILED							= "Evaluation failed";

	public final String	failedComplete						= "Analysis process failed to complete successfully";
	public final String	successfulComplete					= "Analysis process finished successfully";
	public final String	timeoutComplete						= "Analysis process timed-out";
	public final String	successfulResultStringStart			= "Result is ";

	/** For PerformanceTreeArcPath */

	int					ARC_CONTROL_POINT_CONSTANT			= 3;
	int					ARC_PATH_SELECTION_WIDTH			= 6;
	int					ARC_PATH_PROXIMITY_WIDTH			= 10;

	/** For Node-Arc Snap-To behaviour */

	int					NODE_PROXIMITY_RADIUS				= 25;

	/** Object layer positions for QueryView: */

	int					NODE_LAYER_OFFSET					= 30;
	int					STATUS_INDICATOR_LAYER_OFFSET		= 40;
	int					PLACE_TRANSITION_LAYER_OFFSET		= 30;
	int					ANNOTATION_LAYER_OFFSET				= 10;
	int					ARC_LAYER_OFFSET					= 20;
	int					ARC_POINT_LAYER_OFFSET				= 50;
	int					SELECTION_LAYER_OFFSET				= 90;
	int					LOWEST_LAYER_OFFSET					= 0;

	/** Scale factors for loading other queries */

	int					DISPLAY_SCALE_FACTORX				= 7;
	/** X-Axis Scale Value */
	int					DISPLAY_SCALE_FACTORY				= 7;
	/** Y-Axis Scale Value */
	int					DISPLAY_SHIFT_FACTORX				= 270;
	/** X-Axis Shift Value */
	int					DISPLAY_SHIFT_FACTORY				= 120;
	/** Y-Axis Shift Value */
}
