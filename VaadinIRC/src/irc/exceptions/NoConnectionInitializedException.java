package irc.exceptions;

/**
 * When no connection has been initialized, this gets thrown.
 * @author Aleksi Postari
 *
 */
public class NoConnectionInitializedException extends Exception
{
	/**
	 * Constructor to initialize the exception.
	 */
	public NoConnectionInitializedException()
	{
		super();
	}
	
	@Override
	public String getMessage()
	{
		return "No IRC connection has been initialized.";
	}
}