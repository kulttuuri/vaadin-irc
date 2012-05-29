package irc.exceptions;

/**
 * When connection was needed to be terminated, this gets thrown.
 * @author Aleksi Postari
 *
 */
public class TerminateConnectionException extends Exception
{
	/** Why was the connection terminated? */
	private String reason;
	
	/**
	 * Constructor to initialize the exception.
	 */
	public TerminateConnectionException(String reason)
	{
		super();
		this.reason = reason;
	}
	
	@Override
	public String getMessage()
	{
		return reason;
	}
}
