/**
 * 
 */
package pipe.common;

import pipe.modules.interfaces.QueryConstants;

import java.awt.Color;
import java.io.Serializable;

/**
 * @author dazz
 * 
 */
public enum EvaluationStatus implements Serializable
{
	EVALNOTSUPPORTED, EVALNOTSTARTED, EVALINPROGRESS, EVALCOMPLETE, EVALFAILED;

	public Color toColor()
	{
		switch (this)
		{
			case EVALNOTSUPPORTED :
				return QueryConstants.EVALUATION_NOT_SUPPORTED_COLOUR;
			case EVALNOTSTARTED :
				return QueryConstants.EVALUATION_NOT_STARTED_YET_COLOUR;
			case EVALINPROGRESS :
				return QueryConstants.EVALUATION_IN_PROGRESS_COLOUR;
			case EVALCOMPLETE :
				return QueryConstants.EVALUATION_COMPLETE_COLOUR;
			case EVALFAILED :
				return QueryConstants.EVALUATION_FAILED_COLOUR;
			default :
				return Color.WHITE;
		}
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case EVALNOTSUPPORTED :
				return QueryConstants.EVALNOTSUPPORTED;
			case EVALCOMPLETE :
				return QueryConstants.EVALCOMPLETE;
			case EVALINPROGRESS :
				return QueryConstants.EVALINPROGRESS;
			case EVALNOTSTARTED :
				return QueryConstants.EVALNOTSTARTED;
			case EVALFAILED :
				return QueryConstants.EVALFAILED;
			default :
				return "";
		}
	}
}
