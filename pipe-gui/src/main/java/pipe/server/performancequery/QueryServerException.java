package pipe.server.performancequery;

public class QueryServerException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -9216545206586468498L;

	public QueryServerException(final String msg) {
		super(msg);
	}

	public QueryServerException(final String msg, final Throwable e) {
		super(msg, e);
	}
}
