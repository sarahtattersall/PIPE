package pipe.exceptions;

public class TokenLockedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public TokenLockedException()
	{
	}

	public TokenLockedException(String arg0)
	{
		super(arg0);
	}

	public TokenLockedException(Throwable arg0)
	{
		super(arg0);
	}

	public TokenLockedException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
